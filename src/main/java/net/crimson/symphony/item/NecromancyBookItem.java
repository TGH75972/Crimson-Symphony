package net.crimson.symphony.item;
import net.crimson.symphony.entity.ai.SuicidalLavaGoal;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
public class NecromancyBookItem extends Item{
public NecromancyBookItem(Settings settings){
super(settings.maxCount(1));
}
@Override
public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
ItemStack stack = user.getStackInHand(hand);
if(user.isSneaking()){
if(!world.isClient){
int currentSpell = getActiveSpell(stack);
int nextSpell = currentSpell >= 3 ? 1 : currentSpell + 1;
setActiveSpell(stack, nextSpell);
String spellName = switch (nextSpell){
case 1 -> "Telekinetic Grasp";
case 2 -> "Suicidal Curse";
case 3 -> "Void Attraction";
default -> "Unknown";
};
user.sendMessage(Text.literal("Selected Spell: " + spellName).formatted(Formatting.DARK_PURPLE, Formatting.BOLD), true);
world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0F, 1.0F);
 }
return TypedActionResult.success(stack);
}
if(!world.isClient && world instanceof ServerWorld serverWorld){
int spell = getActiveSpell(stack);
NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
if(spell == 1){ 
if(nbt.containsUuid("GrabbedUUID")){
UUID targetId = nbt.getUuid("GrabbedUUID");
Entity rawEntity = serverWorld.getEntity(targetId);
if(rawEntity instanceof LivingEntity target){
Vec3d launchVec = user.getRotationVec(1.0F).multiply(3.5);
target.setVelocity(launchVec.x, launchVec.y, launchVec.z);
target.velocityModified = true;
serverWorld.getChunkManager().sendToOtherNearbyPlayers(target, new EntityVelocityUpdateS2CPacket(target));
serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 1.0F, 0.5F);
}
nbt.remove("GrabbedUUID");
stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
user.getItemCooldownManager().set(this, 10);
}
else{
EntityHitResult hitResult = raycastToEntity(user, 24);
if(hitResult != null && hitResult.getEntity() instanceof LivingEntity target){
nbt.putUuid("GrabbedUUID", target.getUuid());
stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, SoundCategory.PLAYERS, 1.0F, 1.0F);
}
else{
user.sendMessage(Text.literal("No target found within range!").formatted(Formatting.RED), true);
 }
}
return TypedActionResult.success(stack);
} 
else{
EntityHitResult hitResult = raycastToEntity(user, 24);
if(hitResult != null && hitResult.getEntity() instanceof LivingEntity target){
switch(spell){
case 2 -> castSuicideCurse(user, target, serverWorld);
case 3 -> castVoidAttraction(user, target, serverWorld);
 }
} 
else{
user.sendMessage(Text.literal("No target found within range!").formatted(Formatting.RED), true);
  }
 }
}
return TypedActionResult.success(stack);
}
@Override
public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
if(!world.isClient && world instanceof ServerWorld serverWorld && entity instanceof PlayerEntity player){
NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
if(nbt.containsUuid("GrabbedUUID")){
if(!selected){
nbt.remove("GrabbedUUID");
stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
return;
}
UUID targetId = nbt.getUuid("GrabbedUUID");
Entity rawEntity = serverWorld.getEntity(targetId);
if(rawEntity instanceof LivingEntity target && target.isAlive()){
if(target instanceof MobEntity mob){
mob.getNavigation().stop();
}
Vec3d targetCrosshairPos = player.getEyePos().add(player.getRotationVec(1.0F).multiply(6.0));
Vec3d finalVelocity = targetCrosshairPos.subtract(target.getPos()).multiply(0.5); 
target.setVelocity(finalVelocity.x, finalVelocity.y, finalVelocity.z);
target.velocityModified = true;
target.fallDistance = 0;
serverWorld.getChunkManager().sendToOtherNearbyPlayers(target, new EntityVelocityUpdateS2CPacket(target));
serverWorld.spawnParticles(ParticleTypes.WITCH, target.getX(), target.getBodyY(0.5), target.getZ(), 2, 0.1, 0.1, 0.1, 0.05);
}
else{
nbt.remove("GrabbedUUID");
stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
 }
   }
 } 
}
private void castSuicideCurse(PlayerEntity player, LivingEntity target, ServerWorld world){
BlockPos targetPos = target.getBlockPos();
BlockPos foundLava = null;
for(BlockPos currentPos : BlockPos.iterate(targetPos.add(-16, -8, -16), targetPos.add(16, 8, 16))){
if(world.getBlockState(currentPos).isOf(Blocks.LAVA)){
foundLava = currentPos.toImmutable();
break;
 }
}
if(foundLava != null && target instanceof MobEntity mob){
try{
Field goalSelectorField = MobEntity.class.getDeclaredField("goalSelector");
goalSelectorField.setAccessible(true);
net.minecraft.entity.ai.goal.GoalSelector goalSelector = (net.minecraft.entity.ai.goal.GoalSelector) goalSelectorField.get(mob);
goalSelector.add(0, new SuicidalLavaGoal(mob, foundLava));
player.sendMessage(Text.literal("Target cursed to seek fire!").formatted(Formatting.GOLD), true);
}
catch(Exception e){
e.printStackTrace();
 }
} 
else{
Vec3d leap = player.getRotationVec(1.0F).multiply(1.5).add(0, 0.7, 0);
target.setVelocity(leap);
target.velocityModified = true;
world.getChunkManager().sendToOtherNearbyPlayers(target, new EntityVelocityUpdateS2CPacket(target));
player.sendMessage(Text.literal("No lava nearby! Target flung away.").formatted(Formatting.GRAY), true);
   }
world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, target.getX(), target.getBodyY(0.5), target.getZ(), 20, 0.3, 0.5, 0.3, 0.05);
world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENTITY_WITHER_AMBIENT, SoundCategory.PLAYERS, 0.8F, 0.5F);
}
private void castVoidAttraction(PlayerEntity player, LivingEntity target, ServerWorld world) {
ArmorStandEntity anomaly = new ArmorStandEntity(world, target.getX(), target.getY(), target.getZ()){
private int suckTicks = 0;
@Override
public void tick(){
super.tick();
suckTicks++;
if(!this.getWorld().isClient()){
Box box = this.getBoundingBox().expand(16);
List<LivingEntity> victims = this.getWorld().getEntitiesByClass(LivingEntity.class, box, e -> e != this && e != player && !e.isSpectator());
for(LivingEntity v : victims){
Vec3d pull = this.getPos().add(0, 1, 0).subtract(v.getPos()).normalize().multiply(0.4);
v.setVelocity(pull.x, pull.y + 0.1, pull.z);
v.velocityModified = true;
if(v instanceof ServerPlayerEntity sp){
sp.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(sp));
 }
}
((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.PORTAL, this.getX(), this.getY() + 1, this.getZ(), 25, 0.5, 0.5, 0.5, 0.2);
if(suckTicks % 10 == 0){
this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.HOSTILE, 1.5F, 0.5F);
}
if(suckTicks >= 60){
((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.DRAGON_BREATH, this.getX(), this.getY() + 1, this.getZ(), 100, 1.0, 1.0, 1.0, 0.3);
this.getWorld().createExplosion(null, this.getX(), this.getY() + 1, this.getZ(), 6.0F, false, World.ExplosionSourceType.MOB);
this.discard();
 }
   }
 }
};
anomaly.setInvisible(true);
anomaly.setNoGravity(true);
anomaly.setInvulnerable(true);
world.spawnEntity(anomaly);
}
private EntityHitResult raycastToEntity(PlayerEntity player, double range){
Vec3d eyePos = player.getEyePos();
Vec3d lookVec = player.getRotationVec(1.0F).multiply(range);
Vec3d traceEnd = eyePos.add(lookVec);
Box searchBox = player.getBoundingBox().stretch(lookVec).expand(1.0);
return net.minecraft.entity.projectile.ProjectileUtil.raycast(player, eyePos, traceEnd, searchBox, entity -> !entity.isSpectator() && entity.canHit(), range * range);
}
private int getActiveSpell(ItemStack stack){
NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
return nbt.contains("ActiveSpell") ? nbt.getInt("ActiveSpell") : 1;
}
private void setActiveSpell(ItemStack stack, int spellIndex){
NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
nbt.putInt("ActiveSpell", spellIndex);
stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
  }
}
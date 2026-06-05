package net.crimson.symphony.item;
import net.crimson.symphony.entity.GrapplingHookEntity;
import net.crimson.symphony.entity.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
public class GrapplingHookItem extends Item{
public GrapplingHookItem(Settings settings){
super(settings.maxCount(1)); 
 }
@Override
public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
ItemStack stack = user.getStackInHand(hand);
if(!world.isClient){
var hooks = world.getEntitiesByClass(GrapplingHookEntity.class, user.getBoundingBox().expand(64), h -> h.getOwner() == user);
if(!hooks.isEmpty()){
GrapplingHookEntity hook = hooks.get(0);
Vec3d pullDir = hook.getPos().subtract(user.getPos()).normalize();
user.setVelocity(pullDir.multiply(3.0));
user.velocityModified = true;
user.fallDistance = 0.0F;
((ServerPlayerEntity) user).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(user));
world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST, SoundCategory.PLAYERS, 1.0F, 1.0F);
hook.discard();
user.getItemCooldownManager().set(this, 20);
} 
else{
GrapplingHookEntity hook = new GrapplingHookEntity(ModEntities.GRAPPLING_HOOK, world, user);
hook.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 5.0F, 0.5F);
world.spawnEntity(hook);
world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_WIND_CHARGE_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
  } 
}
return TypedActionResult.success(stack);
 }
   }
package net.crimson.symphony.item;
import net.crimson.symphony.component.ModDataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.joml.Vector3f;
public class SacrificialRapierItem extends SwordItem{
public SacrificialRapierItem(ToolMaterial toolMaterial, Settings settings){
super(toolMaterial, settings);
}
@Override
public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker){
World world = attacker.getWorld();
if(!world.isClient()){
ServerWorld serverWorld = (ServerWorld) world;
long currentTime = System.currentTimeMillis();
int charges = stack.getOrDefault(ModDataComponentTypes.BLOOD_CHARGES, 0);
long lastTime = stack.getOrDefault(ModDataComponentTypes.LAST_SACRIFICE_TIME, 0L);
if(currentTime - lastTime > 60000)
charges = 0;
charges++;
target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 3, false, true));
target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 60, 3, false, false));
serverWorld.spawnParticles(new DustParticleEffect(new Vector3f(0.8f, 0.0f, 0.0f), 1.5f), 
target.getX(), target.getBodyY(0.5), target.getZ(), 15, 0.3, 0.5, 0.3, 0.1);
world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.0f, 0.5f);

if(charges >= 5){
double radius = 5.0;
int explosionCount = 8;
world.playSound(null, attacker.getBlockPos(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.0f, 1.5f);
for(int i = 0; i < explosionCount; i++){
double angle = 2 * Math.PI * i / explosionCount;
double x = attacker.getX() + radius * Math.cos(angle);
double z = attacker.getZ() + radius * Math.sin(angle);
world.createExplosion(attacker, x, attacker.getY(), z, 2.0f, World.ExplosionSourceType.MOB);
 }
charges = 0;
}
stack.set(ModDataComponentTypes.BLOOD_CHARGES, charges);
stack.set(ModDataComponentTypes.LAST_SACRIFICE_TIME, currentTime);
}
return super.postHit(stack, target, attacker);
  }
}


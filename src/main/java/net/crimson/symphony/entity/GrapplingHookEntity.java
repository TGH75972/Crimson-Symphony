package net.crimson.symphony.entity;
import net.crimson.symphony.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
public class GrapplingHookEntity extends PersistentProjectileEntity implements FlyingItemEntity{
public GrapplingHookEntity(EntityType<? extends GrapplingHookEntity> entityType, World world){
super(entityType, world);
this.pickupType = PickupPermission.DISALLOWED;
}
public GrapplingHookEntity(EntityType<? extends GrapplingHookEntity> entityType, World world, LivingEntity owner){
super(entityType, owner, world, new ItemStack(ModItems.GRAPPLING_HOOK), null);
this.pickupType = PickupPermission.DISALLOWED;
}
@Override
protected SoundEvent getHitSound(){
return SoundEvents.ENTITY_WIND_CHARGE_THROW;
 }
@Override
public ItemStack getStack(){
return new ItemStack(ModItems.GRAPPLING_HOOK);
}
@Override
protected ItemStack getDefaultItemStack(){
return new ItemStack(ModItems.GRAPPLING_HOOK);
  }
}
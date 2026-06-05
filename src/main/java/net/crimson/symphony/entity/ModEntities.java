package net.crimson.symphony.entity;
import net.crimson.symphony.CrimsonSymphony;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
public class ModEntities{
public static final EntityType<GrapplingHookEntity> GRAPPLING_HOOK = Registry.register(Registries.ENTITY_TYPE,Identifier.of(CrimsonSymphony.MOD_ID, "grappling_hook"), EntityType.Builder.<GrapplingHookEntity>create(GrapplingHookEntity::new, SpawnGroup.MISC).dimensions(0.25f, 0.25f).build());
public static void registerEntities(){
 }
}
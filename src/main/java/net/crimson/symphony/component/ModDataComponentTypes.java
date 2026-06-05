package net.crimson.symphony.component;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
public class ModDataComponentTypes{
public static final ComponentType<Integer> BLOOD_CHARGES = Registry.register(Registries.DATA_COMPONENT_TYPE,Identifier.of("crimson-symphony", "blood_charges"),ComponentType.<Integer>builder().codec(Codec.INT).build());
public static final ComponentType<Long> LAST_SACRIFICE_TIME = Registry.register(Registries.DATA_COMPONENT_TYPE,Identifier.of("crimson-symphony", "last_sacrifice_time"),ComponentType.<Long>builder().codec(Codec.LONG).build());
public static final ComponentType<Integer> GRABBED_ENTITY_ID = Registry.register(Registries.DATA_COMPONENT_TYPE,Identifier.of("crimson-symphony", "grabbed_entity_id"),ComponentType.<Integer>builder().codec(Codec.INT).build());
public static void registerDataComponentTypes(){
       
 }
}
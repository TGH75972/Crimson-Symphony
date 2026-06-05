package net.crimson.symphony;
import net.crimson.symphony.component.ModDataComponentTypes;
import net.crimson.symphony.entity.ModEntities;
import net.crimson.symphony.item.ModItems;
import net.fabricmc.api.ModInitializer;

public class CrimsonSymphony implements ModInitializer{
public static final String MOD_ID = "crimson-symphony";
@Override
public void onInitialize(){
ModDataComponentTypes.registerDataComponentTypes();
ModEntities.registerEntities();
ModItems.registerModItems();
 }
}
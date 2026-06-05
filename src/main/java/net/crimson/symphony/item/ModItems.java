package net.crimson.symphony.item;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
public class ModItems{
public static final Item SACRIFICIAL_RAPIER = registerItem("sacrificial_rapier", 
new SacrificialRapierItem(ToolMaterials.NETHERITE, new Item.Settings()
.attributeModifiers(SacrificialRapierItem.createAttributeModifiers(ToolMaterials.NETHERITE, 3, -2.4f))));
public static final Item GRAPPLING_HOOK = registerItem("grappling_hook", 
new GrapplingHookItem(new Item.Settings().maxCount(1).maxDamage(256)));
public static final Item NECROMANCY_BOOK = registerItem("necromancy_book", 
new NecromancyBookItem(new Item.Settings().maxCount(1)));
private static Item registerItem(String name, Item item) {
return Registry.register(Registries.ITEM, Identifier.of("crimson-symphony", name), item);
}
public static void registerModItems(){
ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries->{
entries.add(SACRIFICIAL_RAPIER);
entries.add(GRAPPLING_HOOK);
});
ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries->{
entries.add(NECROMANCY_BOOK);
  });
 } 
}
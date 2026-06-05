package net.crimson.symphony;
import net.crimson.symphony.entity.GrapplingHookRenderer;
import net.crimson.symphony.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class CrimsonSymphonyClient implements ClientModInitializer{
@Override
public void onInitializeClient(){
EntityRendererRegistry.register(ModEntities.GRAPPLING_HOOK, GrapplingHookRenderer::new);
 }
}
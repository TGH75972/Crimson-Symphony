package net.crimson.symphony.entity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
public class GrapplingHookRenderer extends FlyingItemEntityRenderer<GrapplingHookEntity>{
public GrapplingHookRenderer(EntityRendererFactory.Context context){
super(context, 1.0f, true);
}
@Override
public void render(GrapplingHookEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light){
super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
if(entity.getOwner() instanceof PlayerEntity player){
matrices.push();
Vec3d playerPos = player.getLerpedPos(tickDelta).add(0, player.getStandingEyeHeight() * 0.8, 0);
Vec3d entityPos = entity.getLerpedPos(tickDelta);
float dx = (float) (playerPos.x - entityPos.x);
float dy = (float) (playerPos.y - entityPos.y);
float dz = (float) (playerPos.z - entityPos.z);
VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getLineStrip());
Matrix4f matrix = matrices.peek().getPositionMatrix();
buffer.vertex(matrix, 0, 0, 0).color(0, 0, 0, 255).normal(matrices.peek(), dx, dy, dz);
buffer.vertex(matrix, dx, dy, dz).color(0, 0, 0, 255).normal(matrices.peek(), dx, dy, dz);
matrices.pop();
  }
    }
}
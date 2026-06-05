package net.crimson.symphony.entity.ai;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import java.util.EnumSet;
public class SuicidalLavaGoal extends Goal{
private final MobEntity mob;
private final BlockPos lavaPos;
private double originalStepHeight = 0.6;
private int pathCheckTimer = 0;
private float oldLavaPenalty;
private float oldFirePenalty;
private float oldDangerFirePenalty;
public SuicidalLavaGoal(MobEntity mob, BlockPos lavaPos){
this.mob = mob;
this.lavaPos = lavaPos;
this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
}
@Override
public boolean canStart(){
return mob != null && mob.isAlive() && !mob.isInLava() && lavaPos != null;
}
@Override
public boolean shouldContinue(){
return canStart();
}
@Override
public void start(){
EntityAttributeInstance attribute = this.mob.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT);
if(attribute != null){
this.originalStepHeight = attribute.getBaseValue();
attribute.setBaseValue(1.25); 
}
this.oldLavaPenalty = this.mob.getPathfindingPenalty(PathNodeType.LAVA);
this.oldFirePenalty = this.mob.getPathfindingPenalty(PathNodeType.DAMAGE_FIRE);
this.oldDangerFirePenalty = this.mob.getPathfindingPenalty(PathNodeType.DANGER_FIRE);
this.mob.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
this.mob.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
this.mob.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
}
@Override
public void stop(){
EntityAttributeInstance attribute = this.mob.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT);
if(attribute != null){
attribute.setBaseValue(this.originalStepHeight);
}
this.mob.setPathfindingPenalty(PathNodeType.LAVA, this.oldLavaPenalty);
this.mob.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, this.oldFirePenalty);
this.mob.setPathfindingPenalty(PathNodeType.DANGER_FIRE, this.oldDangerFirePenalty);
this.mob.getNavigation().stop();
 }
@Override
public void tick(){
if(this.lavaPos == null)
return;
double targetX = this.lavaPos.getX() + 0.5;
double targetY = this.lavaPos.getY();
double targetZ = this.lavaPos.getZ() + 0.5;
this.mob.getLookControl().lookAt(targetX, targetY, targetZ);
if(--this.pathCheckTimer <= 0){
this.pathCheckTimer = 10;
this.mob.getNavigation().startMovingTo(targetX, targetY, targetZ, 1.35);
}
if(this.mob.getNavigation().isIdle() || this.mob.squaredDistanceTo(targetX, targetY, targetZ) < 4.0){
this.mob.getMoveControl().moveTo(targetX, targetY, targetZ, 1.35);
}
if(this.mob.horizontalCollision){
this.mob.getJumpControl().setActive();
  }
 }
}
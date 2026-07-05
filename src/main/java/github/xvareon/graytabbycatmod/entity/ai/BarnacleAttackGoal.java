package github.xvareon.graytabbycatmod.entity.ai;

import java.util.EnumSet;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import github.xvareon.graytabbycatmod.entity.Barnacle;

public class BarnacleAttackGoal extends Goal {
    protected final Barnacle mob;
    protected final double speedModifier;
    protected double extraReach;
    protected final boolean followingTargetEvenIfNotSeen;
    protected double pathedTargetX;
    protected double pathedTargetY;
    protected double pathedTargetZ;
    protected double minDistanceSqr;
    protected int ticksUntilNextPathRecalculation;
    protected int ticksUntilNextAttack;
    protected int attackInterval;
    protected long lastCanUseCheck;
    protected Consumer<LivingEntity> onDamage;

    public BarnacleAttackGoal(Barnacle mob, int attackInterval, double extraReach, double speedModifier, double minDistanceSqr, boolean followTargetEvenIfNotSeen, @Nullable Consumer<LivingEntity> onDamage) {
        this.mob = mob;
        this.attackInterval = attackInterval;
        this.extraReach = extraReach;
        this.speedModifier = speedModifier;
        this.minDistanceSqr = minDistanceSqr;
        this.followingTargetEvenIfNotSeen = followTargetEvenIfNotSeen;
        this.onDamage = onDamage;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        long gameTime = mob.level().getGameTime();
        if (gameTime - lastCanUseCheck < attackInterval) return false;
        else {
            this.lastCanUseCheck = gameTime;
            LivingEntity target = mob.getTarget();
            if (target == null) return false;
            else if (!target.isAlive()) return false;
            else
                return this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ()) <= (mob.getAttribute(Attributes.FOLLOW_RANGE).getValue() + mob.getAttackReachDistance());
        }
    }

    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return false;
        else if (!target.isAlive()) return false;
        else if (!this.mob.isWithinRestriction(target.blockPosition())) return false;
        else return !(target instanceof Player) || !target.isSpectator() && !((Player) target).isCreative();
    }

    public void start() {
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    public void stop() {
        LivingEntity target = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) this.mob.setTarget(null);
        this.mob.setAggressive(false);
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double distSqr = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(target)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= minDistanceSqr || this.mob.getRandom().nextFloat() < 0.05F)) {
                int noAction = this.mob.getNoActionTime();
                if (noAction > 100) this.mob.setMovementVector(0.0F, 0.0F, 0.0F);
                else if (this.mob.getRandom().nextInt(reducedTickDelay(20)) == 0 || !this.mob.isInWater() || !this.mob.hasMovementVector()) {
                    Vec3 v = mob.getTarget().position().subtract(mob.position()).normalize().multiply(0.2, 0.2, 0.2);
                    this.mob.setMovementVector((float) v.x, (float) v.y, (float) v.z);
                }
            }
            this.ticksUntilNextAttack = Math.max(getTicksUntilNextAttack() - 1, 0);
            this.checkAndPerformAttack(target, distSqr);
        }
    }

    protected void checkAndPerformAttack(LivingEntity entity, double distSqr) {
        double attackReachSqr = this.getAttackReachSqr(entity);
        if (distSqr <= attackReachSqr + extraReach && isTimeToAttack() && mob.hasLineOfSight(entity)) {
            this.resetAttackCooldown();
            if (onDamage != null) onDamage.accept(entity);
        }
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = getAttackInterval();
    }

    protected boolean isTimeToAttack() {
        return getTicksUntilNextAttack() <= 0;
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }

    protected int getAttackInterval() {
        return this.adjustedTickDelay(attackInterval);
    }

    protected double getAttackReachSqr(LivingEntity entity) {
        return mob.getAttackReachDistance();
    }
}
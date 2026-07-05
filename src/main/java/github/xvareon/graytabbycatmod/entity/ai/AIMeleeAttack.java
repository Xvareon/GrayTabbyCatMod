package github.xvareon.graytabbycatmod.entity.ai;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import github.xvareon.graytabbycatmod.entity.GrayTabbyCat;

public class AIMeleeAttack extends Goal {
    protected final Level world;
    protected final GrayTabbyCat attacker;
    protected int attackTick;
    protected double liftY = 0;

    public AIMeleeAttack(GrayTabbyCat graytabbycat) {
        this.attacker = graytabbycat;
        this.world = graytabbycat.getCommandSenderWorld();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity entitylivingbase = this.attacker.getTarget();

        if (entitylivingbase == null) {
            return false;
        } else if (!entitylivingbase.isAlive()) {
            return false;
        }

        return !(entitylivingbase instanceof Player) || !entitylivingbase.isInvulnerable();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        // no-op (kept intentionally empty as in original)
    }

    @Override
    public void stop() {
        LivingEntity entitylivingbase = this.attacker.getTarget();

        if (entitylivingbase instanceof Player && (entitylivingbase.isSpectator() || ((Player) entitylivingbase).isCreative())) {
            this.attacker.setTarget(null);
        }
    }

    @Override
    public void tick() {
        LivingEntity entitylivingbase = this.attacker.getTarget();
        if (entitylivingbase == null) {
            return;
        }

        double targetX = entitylivingbase.getX();
        double targetZ = entitylivingbase.getZ();

        if (entitylivingbase.distanceToSqr(this.attacker) < 4096.0D) {
            double d1 = targetX - this.attacker.getX();
            double d2 = targetZ - this.attacker.getZ();
            this.attacker.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
            this.attacker.yBodyRot = this.attacker.getYRot();
        }

        double distanceToTarget = this.attacker.distanceToSqr(entitylivingbase.getX(), entitylivingbase.getY(),
                entitylivingbase.getZ());

        // Reduce time till attack
        this.attackTick--;

        double reachToTarget = this.getAttackReachSqr(entitylivingbase);

        // If the entity can reach its target and it's time to attack, reset the timer and attack if the entity is not grabbed
        if (distanceToTarget <= reachToTarget && this.attackTick <= 0) {
            this.attackTick = 20;
        }

        // If the entity is not grabbing a target, set it to move to its target
        if (attacker.getPassengers().isEmpty()) {
            this.attacker.getNavigation().moveTo(this.attacker.getNavigation().createPath(entitylivingbase.blockPosition(), 0), 2.0D);
        } else { // If the entity is grabbing a target, set it to move upwards
            this.attacker.getNavigation().moveTo(this.attacker.getNavigation().createPath(new BlockPos((int) targetX, (int) this.liftY + 15, (int) targetZ), 0), 0.4D);
        }

        // If the entity can grab the target
        if (distanceToTarget <= reachToTarget && attacker.getPassengers().isEmpty() && entitylivingbase.getBbHeight() <= 3 && this.attackTick == 15) {
            // Move the entity upwards to avoid being stuck in the ground
            this.attacker.moveTo(this.attacker.getX(), this.attacker.getY() + entitylivingbase.getBbHeight() + 15,
                    this.attacker.getZ(), this.attacker.getYRot(), this.attacker.getXRot());
            // Grab the target
            entitylivingbase.startRiding(this.attacker, true);
            // Set liftY so entity can continue moving up from the spot
            this.liftY = entitylivingbase.getY();
            if (entitylivingbase instanceof Mob) {
                Mob target = (Mob) entitylivingbase;
                target.setTarget(null);
                target.setLastHurtByMob(null);
                target.getNavigation().stop();
                target.setNoAi(true);
            }
            // Move upwards
            this.attacker.getNavigation().moveTo(this.attacker.getNavigation().createPath(new BlockPos((int) targetX, (int) this.liftY + 15, (int) targetZ), 0), 0.4D);
        }

        // If the entity is grabbing a target and the block above is solid
        if (attacker.getPassengers().isEmpty() && this.attacker.getCommandSenderWorld().getBlockState(this.attacker.blockPosition().above()).isRedstoneConductor(world, this.attacker.blockPosition().above())) {
            // Release target
            entitylivingbase.stopRiding();
            if (entitylivingbase instanceof Mob) {
                Mob target = (Mob) entitylivingbase;
                target.setNoAi(false);
            }
            // Remove target
            this.attacker.setTarget(null);
            // Create a random target position
            BlockPos rPos = new BlockPos(this.attacker.getRandom().nextInt(50) - 25, this.attacker.getRandom().nextInt(40) - 20, this.attacker.getRandom().nextInt(50) - 25);
            BlockPos pos = this.attacker.blockPosition();
            rPos = rPos.offset(pos);
            // Move to random target position
            this.attacker.getNavigation().moveTo(this.attacker.getNavigation().createPath(rPos, 0), 2.0D);
        }

        // If we've about reached the target lifting point and have a target grabbed, or have completed movement, drop the entity
        if (!attacker.getPassengers().isEmpty()) {
            entitylivingbase.stopRiding();
            if (entitylivingbase instanceof Mob) {
                Mob target = (Mob) entitylivingbase;
                target.setNoAi(false);
            }
        }
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return 4D + attackTarget.getBbWidth();
    }
}
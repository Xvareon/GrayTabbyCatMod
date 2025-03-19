package github.xvareon.graytabbycatmod.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

public class Barnacle extends Squid {
    public final AnimationState impaleAnimationState = new AnimationState();
    public final AnimationState hurtAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public boolean animating = false;
    public int animateTicks = 0;
    public Entity lookTarget = null;
    protected static final ImmutableList<SensorType<? extends Sensor<? super Barnacle>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    private static final EntityDataAccessor<Integer> LOOK_TARGET = SynchedEntityData.defineId(Barnacle.class, EntityDataSerializers.INT);
    public static final float ATTACK_REACH_SQR = 36;
    public float sizeMultiplier;

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(Barnacle.class, EntityDataSerializers.INT);

    public Barnacle(EntityType<? extends Squid> entityType, Level level) {

        super(entityType, level);

        float[] possibleSizes = {0.75f, 1.0f, 3.0f, 6.0f, 10.0f, 12.0f};
        this.sizeMultiplier = possibleSizes[random.nextInt(possibleSizes.length)];
        this.refreshDimensions(); // Update hitbox

        // Scale attributes
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((30.0) * (sizeMultiplier * 0.5f));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((6.0) * (sizeMultiplier * 0.5f));

        // Ensure health is set to max after modifying
        this.setHealth(this.getMaxHealth());

        // Assign a random color variant
        if (!level.isClientSide) {
            setVariant(BarnacleVariant.getRandomVariant(random));
        }
    }

    @NotNull
    public static AttributeSupplier.Builder createAttributes() {
        return Squid.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    @NotNull
    @Override
    protected Brain.Provider<Barnacle> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Brain<Barnacle> getBrain() {
        return (Brain<Barnacle>) super.getBrain();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(LOOK_TARGET, -1);
        entityData.define(VARIANT, BarnacleVariant.DEFAULT.ordinal()); // Default to BLUE
    }

    public BarnacleVariant getVariant() {
        return BarnacleVariant.byId(entityData.get(VARIANT));
    }

    public void setVariant(BarnacleVariant variant) {
        entityData.set(VARIANT, variant.getId());
    }

    public int getLookTarget() {
        return entityData.get(LOOK_TARGET);
    }

    public void setLookTarget(int lookTarget) {
        entityData.set(LOOK_TARGET, lookTarget);
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case 64 -> impaleAnimationState.start(tickCount);
            case 65 -> hurtAnimationState.start(tickCount);
            case 66 -> swimAnimationState.start(tickCount);
            default -> super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float damage) {
        if (super.hurt(damageSource, damage)) {
            level().broadcastEntityEvent(this, (byte) 65);
            return true;
        }
        return false;
    }

    @Override
    protected void customServerAiStep() {
        level().getProfiler().push("barnacleBrain");
        getBrain().tick((ServerLevel) level(), this);
        level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {

        super.aiStep();

        if (getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE).isPresent() && Sensor.isEntityAttackable(this, getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE).get()))
            setTarget(getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE).get());
        else setTarget(null);

        lookTarget = this.level().getEntity(getLookTarget());

        if (lookTarget != null) {
            Vec3 pos = lookTarget.position();
            lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(pos.x, lookTarget.getBoundingBox().getCenter().y, pos.z));
        }

        if (this.level().isClientSide) return;

        if (getTarget() == null || (!animating && getTarget().distanceToSqr(this) >= ATTACK_REACH_SQR + 10) || getTarget().distanceToSqr(this) < 1)
            setLookTarget(-1);
        else if (animating && getTarget() != null && getTarget().distanceToSqr(this) >= 1)
            setLookTarget(getTarget().getId());
        if (!animating) return;
        if (getTarget() != null) {
            if (animateTicks == 16 && getTarget().isPassenger()) getTarget().stopRiding();
            if (animateTicks >= 16 && getTarget().distanceToSqr(this) <= ATTACK_REACH_SQR + 10) {
                Vec3 vec = position().subtract(getTarget().position()).normalize();
                getTarget().setDeltaMovement(vec.multiply(0.2, 0.2, 0.2));
                getTarget().hurtMarked = true;
            }
            if (animateTicks == 38 && getTarget().distanceToSqr(this) <= 9)
                doHurtTarget(getTarget());
        }
        if (++animateTicks >= 40) {
            animating = false;
            animateTicks = 0;
        }
    }

    @Override
    public void lookAt(EntityAnchorArgument.Anchor anchor, Vec3 target) {
        Vec3 vec3 = anchor.apply(this);
        double dx = target.x - vec3.x;
        double dy = target.y - vec3.y;
        double dz = target.z - vec3.z;
        double sqrt = Math.sqrt(dx * dx + dz * dz);
        setXRot(Mth.wrapDegrees((float) (Mth.atan2(dy, sqrt) * 57.2957763671875D - 90.0F)));
        setYRot(Mth.wrapDegrees((float) (Mth.atan2(dz, dx) * 57.2957763671875D) - 90.0F));
        setYHeadRot(getYRot());
        xBodyRot = getXRot();
        yBodyRot = yHeadRot;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new BarnacleAttackGoal(this, 60, ATTACK_REACH_SQR, 1, 2, true, false, entity -> {
            level().broadcastEntityEvent(this, (byte) 64);
            animating = true;
        }));
        goalSelector.addGoal(1, new OceanDepthsMonsterRandomMovementGoal(this));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1, true));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, WaterAnimal.class, true));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractFish.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TropicalFish.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Dolphin.class, true));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, true));
        targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(7, new NearestBoatTargetGoal(this));
    }

    public static class OceanDepthsMonsterRandomMovementGoal extends Goal {
        private final Squid squid;

        public OceanDepthsMonsterRandomMovementGoal(Squid $$0) {
            this.squid = $$0;
        }

        public boolean canUse() {
            return true;
        }

        public void tick() {
            if (squid.getTarget() != null) return;
            int $$0 = squid.getNoActionTime();
            if ($$0 > 100) {
                squid.setMovementVector(0.0F, 0.0F, 0.0F);
            } else if (squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !squid.isInWater() || !squid.hasMovementVector()) {
                float $$1 = squid.getRandom().nextFloat() * 6.2831855F;
                float $$2 = Mth.cos($$1) * 0.2F;
                float $$3 = -0.1F + squid.getRandom().nextFloat() * 0.2F;
                float $$4 = Mth.sin($$1) * 0.2F;
                squid.setMovementVector($$2, $$3, $$4);
            }
        }
    }

    public static boolean canSpawn(EntityType<Barnacle> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
        return Barnacle.checkSurfaceWaterAnimalSpawnRules(entityType, level, spawnType, position, random);
    }

    @NotNull
    @Override
    public EntityDimensions getDimensions(@NotNull Pose pose) {
        return super.getDimensions(pose).scale(sizeMultiplier);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.refreshDimensions(); // Ensure hitbox updates
    }

    public float getSizeMultiplier() {
        return sizeMultiplier;
    }

    public static class BarnacleAttackGoal extends Goal {
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
        protected boolean wallCheck;
        protected Consumer<LivingEntity> onDamage;

        public BarnacleAttackGoal(Barnacle mob, int attackInterval, double extraReach, double speedModifier, double minDistanceSqr, boolean wallCheck, boolean followTargetEvenIfNotSeen, @Nullable Consumer<LivingEntity> onDamage) {
            this.mob = mob;
            this.attackInterval = attackInterval;
            this.extraReach = extraReach;
            this.speedModifier = speedModifier;
            this.minDistanceSqr = minDistanceSqr;
            this.wallCheck = wallCheck;
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
                    return this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ()) <= mob.getAttribute(Attributes.FOLLOW_RANGE).getValue();
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
                    int $$0 = this.mob.getNoActionTime();
                    if ($$0 > 100) this.mob.setMovementVector(0.0F, 0.0F, 0.0F);
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
            if (distSqr <= attackReachSqr + extraReach && isTimeToAttack() && (!wallCheck || mob.hasLineOfSight(entity))) {
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
            return this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + entity.getBbWidth();
        }
    }
    public static class NearestBoatTargetGoal extends Goal {
        private final Mob mob;
        private Boat targetBoat;

        public NearestBoatTargetGoal(Mob mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            // Find the nearest boat within a certain range
            List<Boat> boats = mob.level().getEntitiesOfClass(Boat.class, mob.getBoundingBox().inflate(10.0D));

            if (!boats.isEmpty()) {
                targetBoat = boats.get(0); // Get the first found boat
                return true;
            }

            return false;
        }

        @Override
        public void start() {
            if (targetBoat != null) {
                mob.setTarget(null); // Ensure it does not switch to another target
            }
        }

        @Override
        public void tick() {
            if (targetBoat != null && mob.distanceTo(targetBoat) < 2.0D) {
                // Simulate an attack on the boat (you can modify this logic)
                targetBoat.hurt(mob.damageSources().mobAttack(mob), 4.0F);
            }
        }
    }

}

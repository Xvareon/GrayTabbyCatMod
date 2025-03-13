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
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Barnacle extends Squid {
    public final AnimationState impaleAnimationState = new AnimationState();
    public final AnimationState hurtAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public boolean animating = false;
    public boolean swim = false;
    public int animateTicks = 0;
    public Entity lookTarget = null;
    protected static final ImmutableList<SensorType<? extends Sensor<? super Barnacle>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    private static final EntityDataAccessor<Integer> LOOK_TARGET = SynchedEntityData.defineId(Barnacle.class, EntityDataSerializers.INT);
    public static final float ATTACK_REACH_SQR = 36;
    public float sizeMultiplier = 0.75f;

    public Barnacle(EntityType<? extends Squid> entityType, Level level) {

        super(entityType, level);
        this.sizeMultiplier = this.sizeMultiplier + random.nextFloat() * 9.25f; // Random sizing
        this.refreshDimensions(); // Update hitbox
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
        lookTarget = level().getEntity(getLookTarget());

        if (lookTarget != null) {
            Vec3 pos = lookTarget.position();
            lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(pos.x, lookTarget.getBoundingBox().getCenter().y, pos.z));
        }

        if (level().isClientSide) return;

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
//        goalSelector.addGoal(0, new BarnacleAttackGoal(this, 60, ATTACK_REACH_SQR, 1, 2, true, false, entity -> {
//            level().broadcastEntityEvent(this, (byte) 64);
//            animating = true;
//        }));
        goalSelector.addGoal(1, new OceanDepthsMonsterRandomMovementGoal(this));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
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

    @Override
    public EntityDimensions getDimensions(Pose pose) {
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
}

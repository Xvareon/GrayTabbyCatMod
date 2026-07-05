package github.xvareon.graytabbycatmod.entity;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import github.xvareon.graytabbycatmod.entity.ai.OceanDepthsMonsterRandomMovementGoal;
import github.xvareon.graytabbycatmod.entity.ai.BarnacleAttackGoal;

public class Barnacle extends Squid {
    public final AnimationState impaleAnimationState = new AnimationState();
    public final AnimationState hurtAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public boolean animating = false;
    public int animateTicks = 0;
    private static final EntityDataAccessor<Integer> LOOK_TARGET = SynchedEntityData.defineId(Barnacle.class, EntityDataSerializers.INT);
    public static final float ATTACK_REACH_SQR = 36;

    private static final EntityDataAccessor<Float> SIZE_MULTIPLIER = SynchedEntityData.defineId(Barnacle.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(Barnacle.class, EntityDataSerializers.INT);

    public Barnacle(EntityType<? extends Squid> entityType, Level level) {

        super(entityType, level);

        float[] possibleSizes = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 12.0f};

        // Assign a random color variant
        if (!level.isClientSide) {
            setSizeMultiplier(possibleSizes[random.nextInt(possibleSizes.length)]);
            setVariant(BarnacleVariant.getRandomVariant(random));
        }

        this.refreshDimensions(); // Update hitbox
    }

    @NotNull
    public static AttributeSupplier.Builder createAttributes() {
        return Squid.createAttributes()
                .add(Attributes.MAX_HEALTH, 90.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(LOOK_TARGET, -1);
        entityData.define(SIZE_MULTIPLIER, 1.0f);
        entityData.define(VARIANT, BarnacleVariant.DEFAULT.ordinal()); // Default to BLUE
    }

    public float getSizeMultiplier() {
        return entityData.get(SIZE_MULTIPLIER);
    }

    public void setSizeMultiplier(float size) {
        entityData.set(SIZE_MULTIPLIER, size);
        this.refreshDimensions();
    }

    public BarnacleVariant getVariant() {
        return BarnacleVariant.byId(entityData.get(VARIANT));
    }

    public void setVariant(BarnacleVariant variant) {
        entityData.set(VARIANT, variant.getId());
    }

    // For Dye saves
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("SizeMultiplier", this.getSizeMultiplier());
        compound.putInt("BarnacleVariant", this.getVariant().getId()); // Save variant ID
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SizeMultiplier")) {
            this.setSizeMultiplier(compound.getFloat("SizeMultiplier"));
        }
        if (compound.contains("BarnacleVariant")) {
            this.setVariant(BarnacleVariant.byId(compound.getInt("BarnacleVariant"))); // Load variant ID
        }
    }

    public int getLookTarget() {
        return entityData.get(LOOK_TARGET);
    }

    public void setLookTarget(int lookTarget) {
        entityData.set(LOOK_TARGET, lookTarget);
    }

    @NotNull
    @Override
    public EntityDimensions getDimensions(@NotNull Pose pose) {
        return super.getDimensions(pose).scale(getSizeMultiplier());
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.refreshDimensions(); // Ensure hitbox updates
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
    public void aiStep() {
        super.aiStep();

        if (getTarget() == null || getTarget().isRemoved() || getTarget().isDeadOrDying()) {
            setTarget(null);
        }

        if (getTarget() != null) {
            Vec3 pos = getTarget().position();
            lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(pos.x, getTarget().getBoundingBox().getCenter().y, pos.z));
        }

        if (this.level().isClientSide) return;

        if (getTarget() != null) {
            double distanceSqr = getTarget().distanceToSqr(this);

            if (!animating && distanceSqr >= ATTACK_REACH_SQR + 10) {
                setLookTarget(-1);
            } else if (animating && distanceSqr >= 1) {
                setLookTarget(getTarget().getId());
            }

            if (!animating) return;

            if (animateTicks == 16 && getTarget().isPassenger()) {
                getTarget().stopRiding();
            }

            if (animateTicks >= 16 && distanceSqr <= ATTACK_REACH_SQR + 10) {
                Vec3 vec = position().subtract(getTarget().position()).normalize();
                getTarget().setDeltaMovement(vec.multiply(0.2, 0.2, 0.2));
                getTarget().hurtMarked = true;
            }

            if (animateTicks == 38 && distanceSqr <= 9) {
                doHurtTarget(getTarget());
            }
        }

        if (++animateTicks >= 40) {
            animating = false;
            animateTicks = 0;
        }
    }

    public static boolean canSpawn(EntityType<Barnacle> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
        return Barnacle.checkSurfaceWaterAnimalSpawnRules(entityType, level, spawnType, position, random);
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
        goalSelector.addGoal(0, new BarnacleAttackGoal(this, 60, ATTACK_REACH_SQR, 1, 2, false, entity -> {
            level().broadcastEntityEvent(this, (byte) 64);
            animating = true;
        }));
        goalSelector.addGoal(1, new OceanDepthsMonsterRandomMovementGoal(this));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractFish.class, true));
    }

    public float getAttackReachDistance() {
        return 5.0f + (getSizeMultiplier()/2.0f);
    }
}

package github.xvareon.graytabbycatmod.entity;

import java.util.UUID;

import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeItem;

import java.util.EnumSet;

public class GrayTabbyCat extends Cat {

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(GrayTabbyCat.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> COLLAR_COLOR = SynchedEntityData.defineId(GrayTabbyCat.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> SIZE_MULTIPLIER = SynchedEntityData.defineId(GrayTabbyCat.class, EntityDataSerializers.FLOAT);

    public GrayTabbyCat(EntityType<GrayTabbyCat> type, Level level) {
        super(type, level);

        float[] possibleSizes = {0.5f, 0.6f, 0.75f, 0.85f, 0.9f, 1.0f, 1.1f, 1.2f, 1.25f};

        // Assign a random color variant
        if (!level.isClientSide) {
            setSizeMultiplier(possibleSizes[random.nextInt(possibleSizes.length)]);
            setGrayTabbyVariant(GrayTabbyCatVariant.getRandomVariant(random));
        }

        this.refreshDimensions(); // Update hitbox

        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new AIMeleeAttack(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this) {
            @Override
            public boolean canUse() {
                return !GrayTabbyCat.this.isInSittingPose() && !GrayTabbyCat.this.isOrderedToSit() && super.canUse();
            }
        });
    }

    public GrayTabbyCat(Level level, BlockPos position) {
        super(EntityInit.GRAY_TABBY_CAT.get(), level);
        this.setPos(position.getX(), position.getY(), position.getZ());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SIZE_MULTIPLIER, 1.0f);
        entityData.define(VARIANT, GrayTabbyCatVariant.DEFAULT.ordinal());
        entityData.define(COLLAR_COLOR, DyeColor.GRAY.getId());
    }

    @NotNull
    public GrayTabbyCatVariant getGrayTabbyVariant() {
        return GrayTabbyCatVariant.byId(entityData.get(VARIANT));
    }

    public void setGrayTabbyVariant(GrayTabbyCatVariant variant) {
        entityData.set(VARIANT, variant.getId());
    }

    public @NotNull DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor collarcolor) {
        this.entityData.set(COLLAR_COLOR, collarcolor.getId());
    }

    public float getSizeMultiplier() {
        return entityData.get(SIZE_MULTIPLIER);
    }

    public void setSizeMultiplier(float size) {
        entityData.set(SIZE_MULTIPLIER, size);
        this.refreshDimensions();
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

    // For Dye saves
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("SizeMultiplier", this.getSizeMultiplier());
        compound.putInt("GrayTabbyCatVariant", this.getGrayTabbyVariant().getId()); // Save variant ID
        compound.putByte("CollarColor", (byte)this.getCollarColor().getId());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SizeMultiplier")) {
            this.setSizeMultiplier(compound.getFloat("SizeMultiplier"));
        }
        if (compound.contains("GrayTabbyCatVariant")) {
            this.setGrayTabbyVariant(GrayTabbyCatVariant.byId(compound.getInt("GrayTabbyCatVariant"))); // Load variant ID
        }
        if (compound.contains("CollarColor")){
            this.setCollarColor(DyeColor.byId(compound.getByte("CollarColor")));
        }
    }

    // For Dye
    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        if (this.isTame()){
            if (item instanceof DyeItem) {
                DyeColor dyecolor = ((DyeItem) item).getDyeColor();
                if (dyecolor != this.getCollarColor()) {
                    this.setCollarColor(dyecolor);
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean causeFallDamage(float f, float g, @NotNull DamageSource damageSource) {
        return false;
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Cat.createAttributes();
    }

    public static boolean canSpawn(EntityType<GrayTabbyCat> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
        return Cat.checkAnimalSpawnRules(entityType, level, spawnType, position, random);
    }

    @Override
    public Cat getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {

        GrayTabbyCat babycat = new GrayTabbyCat(level, this.blockPosition());

        // Set the ownership of the baby
        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            babycat.setOwnerUUID(uuid);
            babycat.setTame(true);
        }

        return babycat;
    }

    static class AIMeleeAttack extends Goal {
        protected Level world;
        protected GrayTabbyCat attacker;
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

            if(entitylivingbase == null) {
                return false;
            } else if(!entitylivingbase.isAlive()) {
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

        }

        @Override
        public void stop() {
            LivingEntity entitylivingbase = this.attacker.getTarget();

            if(entitylivingbase instanceof Player && (entitylivingbase.isSpectator() || ((Player) entitylivingbase).isCreative())) {
                this.attacker.setTarget(null);
            }
        }

        @Override
        public void tick() {

            LivingEntity entitylivingbase = this.attacker.getTarget();
            if(entitylivingbase == null) {
                return;
            }

            double targetX = entitylivingbase.getX();
            double targetZ = entitylivingbase.getZ();

            if(entitylivingbase.distanceToSqr(this.attacker) < 4096.0D) // If the distance is less than 4096 square blocks rotate towards them
            {
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

            // If the entity can reach its target and it's time to attack, reset
            // the timer and attack if the entity is not grabbed
            if(distanceToTarget <= reachToTarget && this.attackTick <= 0) {
                this.attackTick = 20;
            }

            // If the entity is not grabbing a target, set it to move to its target
            if(attacker.getPassengers().isEmpty()) {
                this.attacker.navigation.moveTo(this.attacker.navigation.createPath(entitylivingbase.blockPosition(), 0), 2.0D);
            } else { // If the entity is grabbing a target, set it to move upwards
                this.attacker.navigation.moveTo(this.attacker.navigation.createPath(new BlockPos((int) targetX, (int) this.liftY + 15, (int) targetZ), 0), 0.4D);
            }

            // If the entity can grab the target
            if(distanceToTarget <= reachToTarget && attacker.getPassengers().isEmpty() && entitylivingbase.getBbHeight() <= 3 && this.attackTick == 15) {
                // Move the entity upwards to avoid being stuck in the ground
                 this.attacker.moveTo(this.attacker.getX(), this.attacker.getY() + entitylivingbase.getBbHeight() + 15,
                        this.attacker.getZ(), this.attacker.getYRot(), this.attacker.getXRot());
                // Grab the target
                entitylivingbase.startRiding(this.attacker, true);
                // Set liftY so entity can continue moving up from the spot
                this.liftY = entitylivingbase.getY();
                if(entitylivingbase instanceof Mob) {
                    Mob target = (Mob) entitylivingbase;
                    target.setTarget(null);
                    target.setLastHurtByMob(null);
                    target.getNavigation().stop();
                    target.setNoAi(true);
                }
                // Move upwards
                this.attacker.navigation.moveTo(this.attacker.navigation.createPath(new BlockPos((int) targetX, (int) this.liftY + 15, (int) targetZ), 0), 0.4D);
            }

            // If the entity is grabbing a target and the block above is solid
            if(attacker.getPassengers().isEmpty() && this.attacker.getCommandSenderWorld().getBlockState(this.attacker.blockPosition().above()).isRedstoneConductor(world, this.attacker.blockPosition().above())) {
                // Release target
                entitylivingbase.stopRiding();
                if(entitylivingbase instanceof Mob) {
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
                this.attacker.navigation.moveTo(this.attacker.navigation.createPath(rPos, 0), 2.0D);
            }

            // If we've about reached the target lifting point and have a target
            // grabbed, or have completed movement, drop the entity
            // if (Math.abs(this.attacker.getY() - (this.liftY + 15)) <= 3 && !attacker.getPassengers().isEmpty()) {
            if (!attacker.getPassengers().isEmpty()) {
                entitylivingbase.stopRiding();
                if(entitylivingbase instanceof Mob) {
                    Mob target = (Mob) entitylivingbase;
                    target.setNoAi(false);
                }
            }

        }

        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return 4D + attackTarget.getBbWidth();
        }
    }
}
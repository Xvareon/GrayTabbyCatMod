package github.xvareon.graytabbycatmod.entity;

import java.util.UUID;

import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.core.BlockPos;
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

import github.xvareon.graytabbycatmod.entity.ai.AIMeleeAttack;

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
}
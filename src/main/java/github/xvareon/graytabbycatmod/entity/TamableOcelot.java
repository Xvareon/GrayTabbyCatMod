package github.xvareon.graytabbycatmod.entity;

import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TamableOcelot extends Cat {

    private static final EntityDataAccessor<Integer> COLLAR_COLOR = SynchedEntityData.defineId(TamableOcelot.class, EntityDataSerializers.INT);

    public TamableOcelot(EntityType<TamableOcelot> type, Level level) {
        super(type, level);

        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this) {
            @Override
            public boolean canUse() {
                return !TamableOcelot.this.isInSittingPose() && !TamableOcelot.this.isOrderedToSit() && super.canUse();
            }
        });
    }

    public TamableOcelot(Level level, BlockPos position) {
        super(EntityInit.TAMABLE_OCELOT.get(), level);
        this.setPos(position.getX(), position.getY(), position.getZ());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(COLLAR_COLOR, DyeColor.ORANGE.getId());
    }

    public @NotNull DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor collarcolor) {
        this.entityData.set(COLLAR_COLOR, collarcolor.getId());
    }

    // For Dye saves
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("CollarColor", (byte)this.getCollarColor().getId());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
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
        return Cat.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)  // Custom health value
                .add(Attributes.MOVEMENT_SPEED, 0.3)  // Custom movement speed
                .add(Attributes.ATTACK_DAMAGE, 3.0);  // Custom attack damage
    }

    public static boolean canSpawn(EntityType<TamableOcelot> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
        // Check if the spawn is happening in a jungle biome
        if (!level.getBiome(position).is(BiomeTags.IS_JUNGLE)) {
            return false;
        }

        // Ensure the entity is spawning on valid ground
        if (!level.getBlockState(position.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON)) {
            return false;
        }

        // Require light level similar to ocelots
        return level.getRawBrightness(position, 0) > 8;
    }

    @Override
    public Cat getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {

        TamableOcelot babycat = new TamableOcelot(level, this.blockPosition());

        // Set the ownership of the baby
        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            babycat.setOwnerUUID(uuid);
            babycat.setTame(true);
        }

        return babycat;
    }
}
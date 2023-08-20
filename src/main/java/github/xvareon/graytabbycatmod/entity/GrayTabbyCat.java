package github.xvareon.graytabbycatmod.entity;

import java.util.UUID;

import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
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

public class GrayTabbyCat extends Cat {

    private static final EntityDataAccessor<Integer> COLLAR_COLOR = SynchedEntityData.defineId(GrayTabbyCat.class, EntityDataSerializers.INT);

    public GrayTabbyCat(EntityType<GrayTabbyCat> type, Level level) {
        super(type, level);
    }

    public GrayTabbyCat(Level level, BlockPos position) {
        super(EntityInit.GRAY_TABBY_CAT.get(), level);
        this.setPos(position.getX(), position.getY(), position.getZ());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(COLLAR_COLOR, DyeColor.YELLOW.getId());
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
        if (compound.contains("CollarColor"))
            this.setCollarColor(DyeColor.byId(compound.getByte("CollarColor")));
    }

    // For Dye
    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

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

        return super.mobInteract(player, hand);
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
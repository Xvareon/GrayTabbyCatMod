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

public class GrayTabbyCat extends Cat {

    private static final EntityDataAccessor<Integer> COLLAR_COLOR = SynchedEntityData.defineId(GrayTabbyCat.class, EntityDataSerializers.INT);

    public GrayTabbyCat(EntityType<GrayTabbyCat> type, Level level) {
        super(type, level);
    }

    public GrayTabbyCat(Level level, double x, double y, double z) {
        this(EntityInit.GRAY_TABBY_CAT.get(), level);
        setPos(x, y, z);
    }

    public GrayTabbyCat(Level level, BlockPos position) {
        this(level, position.getX(), position.getY(), position.getZ());
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

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Cat.createAttributes();
    }

    public static boolean canSpawn(EntityType<GrayTabbyCat> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
        return Cat.checkAnimalSpawnRules(entityType, level, spawnType, position, random);
    }
}
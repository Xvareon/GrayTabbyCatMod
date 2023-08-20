package github.xvareon.graytabbycatmod.entity;

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
import org.jetbrains.annotations.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;

public class GrayTabbyCat extends Cat {
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

    @Nullable
    @Override
    public Cat getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return new GrayTabbyCat(level, this.blockPosition());
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Cat.createAttributes();
    }

    public static boolean canSpawn(EntityType<GrayTabbyCat> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
        return Cat.checkAnimalSpawnRules(entityType, level, spawnType, position, random);
    }
}
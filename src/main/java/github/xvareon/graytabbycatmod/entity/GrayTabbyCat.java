package github.xvareon.graytabbycatmod.entity;

import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

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
}
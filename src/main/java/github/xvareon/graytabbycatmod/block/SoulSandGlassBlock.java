package github.xvareon.graytabbycatmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class SoulSandGlassBlock extends ScaffoldingBlock {

    public SoulSandGlassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType pathComputationType) {
        return false;
    }

    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity livingEntity && !EnchantmentHelper.hasSoulSpeed(livingEntity)) {
            double soulSandSlowFactor = 0.2;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(soulSandSlowFactor, 1.0, soulSandSlowFactor));
        }
        if (!(entity instanceof Player)) {
            entity.setPos(entity.xOld, entity.yOld, entity.zOld); // Push non-player entities back
        }
        if (level.isClientSide) {
            boolean bl = entity.xOld != entity.getX() || entity.zOld != entity.getZ();
            RandomSource random = level.getRandom();
            if (bl && random.nextBoolean()) {
                level.addParticle(ParticleTypes.SOUL, entity.getX(), pos.getY() + 1, entity.getZ(), Mth.randomBetween(random, -1.0f, 1.0f) * 0.083333336f, 0.05f, Mth.randomBetween(random, -1.0f, 1.0f) * 0.083333336f);
            }
        }
    }

    // Slow Entities when they step on the block
    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (!entity.isSteppingCarefully() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity)) {
            double soulSandSlowFactor = 0.2;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(soulSandSlowFactor, 1.0, soulSandSlowFactor));
            if (level.isClientSide) {
                boolean bl = entity.xOld != entity.getX() || entity.zOld != entity.getZ();
                RandomSource random = level.getRandom();
                if (bl && random.nextBoolean()) {
                    level.addParticle(ParticleTypes.SOUL, entity.getX(), blockPos.getY() + 1, entity.getZ(), Mth.randomBetween(random, -1.0f, 1.0f) * 0.083333336f, 0.05f, Mth.randomBetween(random, -1.0f, 1.0f) * 0.083333336f);
                }
            }
        }
        super.stepOn(level, blockPos, blockState, entity);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }
}

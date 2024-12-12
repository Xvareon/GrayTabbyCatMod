package github.xvareon.graytabbycatmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class GlowingObsidianBlock extends Block {

    public GlowingObsidianBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter getter, BlockPos blockPos, PathComputationType computationType) {
        return false;
    }

    @Override
    public boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (!entity.isSteppingCarefully() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity)) {
            entity.hurt(level.damageSources().hotFloor(), 1.0F);
            entity.setSecondsOnFire(3);
        }
        super.stepOn(level, blockPos, blockState, entity);
    }

    @Override
    public BlockPathTypes getAdjacentBlockPathType(BlockState state, BlockGetter level, BlockPos pos, Mob mob, BlockPathTypes originalType) {
        return BlockPathTypes.DANGER_FIRE;
    }

    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        super.animateTick(stateIn, worldIn, pos, rand);

        if(rand.nextInt(5) == 0 && worldIn.getBlockState(pos.above()).isAir()){
            worldIn.addParticle(ParticleTypes.FLAME,
                    pos.getX() + 0.4 + rand.nextDouble() * 0.2,
                    pos.getY() + 0.5 + rand.nextDouble() * 0.1,
                    pos.getZ() + 0.4 + rand.nextDouble() * 0.2,
                    (Math.random() - 0.5) * 0.08,
                    (1 + Math.random()) * 0.04,
                    (Math.random() - 0.5) * 0.08);
        }
    }
}

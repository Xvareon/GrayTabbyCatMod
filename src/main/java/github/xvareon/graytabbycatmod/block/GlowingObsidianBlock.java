package github.xvareon.graytabbycatmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class GlowingObsidianBlock extends ConcretePowderBlock {

    public GlowingObsidianBlock(BlockBehaviour.Properties properties) {
        super(Blocks.WATER, properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (touchesLiquid(level, pos)) {
            turnIntoObsidian(level, pos);
        }
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

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        level.scheduleTick(currentPos, this, this.getDelayAfterPlace());
        return state;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (touchesLiquid(level, pos)) {
            turnIntoObsidian(level, pos);
        }
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        if (id == 1) {
            if (shouldTurnToObsidian(level, pos)) {
                turnIntoObsidian(level, pos);
            } else level.removeBlock(pos, false);
            return true;
        }
        return super.triggerEvent(state, level, pos, id, param);
    }

    private static void turnIntoObsidian(Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
    }


    private boolean shouldTurnToObsidian(Level level, BlockPos pos) {
        BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
        int count = 0;
        for (Direction direction : Direction.values()) {
            if (direction != Direction.DOWN) {
                mutableBlockPos.setWithOffset(pos, direction);
                var s = level.getBlockState(mutableBlockPos);
                if (isWater(s) && (direction == Direction.UP || s.getFluidState().isSource())) {
                    count++;
                }
                if (count >= 2) return true;
            }
        }
        return false;
    }

    private boolean touchesLiquid(BlockGetter level, BlockPos pos) {
        boolean bl = false;
        BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
        BlockState blockState = level.getBlockState(mutableBlockPos);
        if (isWater(blockState)) return true;
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) continue;
            mutableBlockPos.setWithOffset(pos, direction);
            blockState = level.getBlockState(mutableBlockPos);
            if (isWater(blockState) && !blockState.isFaceSturdy(level, pos, direction.getOpposite())) {
                bl = true;
                break;
            }
        }
        return bl;
    }

    private boolean isWater(BlockState state) {
        return state.getFluidState().is(FluidTags.WATER);
    }

    public void spawnDissolveParticles(Level level, BlockPos pos) {
        int d = 0, e = 0, f = 0;

        int amount = 4;
        for (int ax = 0; ax < amount; ++ax) {
            for (int ay = 0; ay < amount; ++ay) {
                for (int az = 0; az < amount; ++az) {
                    double s = (ax + 0.2) / amount;
                    double t = (ay + 0.2) / amount;
                    double u = (az + 0.2) / amount;
                    double px = s + d;
                    double py = t + e;
                    double pz = u + f;
                    level.addParticle(ParticleTypes.FLAME,
                            pos.getX() + px, pos.getY() + py, pos.getZ() + pz,
                            s - 0.5, 0, u - 0.5);
                }
            }
        }

    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            spawnDissolveParticles(level, pos);
        }
        SoundType soundtype = state.getSoundType();
        level.playSound(null, pos, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
    }

    private void fillBucket(Player player, int startIndex) {
        Inventory inv = player.getInventory();
        for(int i = startIndex; i < inv.getContainerSize(); i++) {
            ItemStack stackInSlot = inv.getItem(i);
            if(stackInSlot.getItem() == Items.BUCKET) {
                stackInSlot.shrink(1);
                ItemStack give = new ItemStack(Items.LAVA_BUCKET);
                if(!player.addItem(give)){
                    player.drop(give, false);
                }
                return;
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult raytrace) {

        ItemStack stack = player.getItemInHand(hand);

        if(stack.getItem() == Items.BUCKET) {
            fillBucket(player, player.getInventory().selected);
            world.removeBlock(pos, false);
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        if(stack.getItem() instanceof BlockItem bitem) {
            Block block = bitem.getBlock();

            UseOnContext context = new UseOnContext(player, hand, new BlockHitResult(new Vec3(0.5F, 1F, 0.5F), raytrace.getDirection(), pos, false));
            BlockPlaceContext bcontext = new BlockPlaceContext(context);

            BlockState stateToPlace = block.getStateForPlacement(bcontext);
            if(stateToPlace != null && stateToPlace.canSurvive(world, pos)) {
                world.setBlockAndUpdate(pos, stateToPlace);
                world.playSound(player, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.PLAYERS, 1.0F, 1.0F);

                if(!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    fillBucket(player, 0);
                }

                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }
}

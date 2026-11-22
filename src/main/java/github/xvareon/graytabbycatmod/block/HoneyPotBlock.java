package github.xvareon.graytabbycatmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class HoneyPotBlock extends Block implements WorldlyContainerHolder {

    public static final IntegerProperty HONEY_LEVEL = IntegerProperty.create("honey_level", 0, 4);

    public HoneyPotBlock() {
        super(BlockBehaviour.Properties.of()
            .sound(SoundType.STONE)
            .strength(1.0F, 10.0F)
            .noOcclusion()
            .isRedstoneConductor((state, level, pos) -> false
        ));

        registerDefaultState(stateDefinition.any().setValue(HONEY_LEVEL, 0));
    }

    private static ItemStack getCompatItem(String id) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        return item != null ? new ItemStack(item) : ItemStack.EMPTY;
    }

    private static boolean isCompatItem(ItemStack held, String id) {
        ItemStack compat = getCompatItem(id);
        return !compat.isEmpty() && held.is(compat.getItem());
    }

    private static ItemStack getPreferredHoneyBucket() {
        // Just change the order here to change the priority of the buckets. The first one that is found will be used.
        // For compat, this is where we can add other mod's honey buckets to be used, do not forget to add to the inverse interaction check in onUse() as well.
        ItemStack bumblezone = getCompatItem("the_bumblezone:honey_bucket");

        if (!bumblezone.isEmpty()) {
            return bumblezone;
        }

        ItemStack create = getCompatItem("create:honey_bucket");

        if (!create.isEmpty()) {
            return create;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {

        ItemStack held = player.getItemInHand(hand);
        int honeyLevel = state.getValue(HONEY_LEVEL);

        //
        // HONEY BOTTLE -> ADD HONEY
        //
        if (held.is(Items.HONEY_BOTTLE)) {

            if (honeyLevel >= 4)
                return InteractionResult.sidedSuccess(level.isClientSide);

            if (!level.isClientSide) {
                setHoneyLevel(level, pos, state, honeyLevel + 1);
                consumeItem(player, held);
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.GLASS_BOTTLE));
            }

            playFillSound(level, pos);
            player.swing(hand);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //
        // GLASS BOTTLE -> TAKE HONEY
        //
        if (held.is(Items.GLASS_BOTTLE)) {

            if (honeyLevel <= 0)
                return InteractionResult.sidedSuccess(level.isClientSide);

            if (!level.isClientSide) {
                setHoneyLevel(level, pos, state, honeyLevel - 1);
                consumeItem(player, held);
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.HONEY_BOTTLE));
            }

            playTakeSound(level, pos);
            player.swing(hand);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //
        // HONEY BLOCK -> ADD HONEY
        //
        if (held.is(Items.HONEY_BLOCK)) {

            if (honeyLevel != 0)
                return InteractionResult.sidedSuccess(level.isClientSide);

            if (!level.isClientSide) {
                setHoneyLevel(level, pos, state, 4);
                consumeItem(player, held);
            }

            playFillSound(level, pos);
            player.swing(hand);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //
        // EMPTY HAND -> TAKE HONEY BLOCK
        //
        if (held.isEmpty()) {

            if (honeyLevel != 4)
                return InteractionResult.PASS;

            if (!level.isClientSide) {
                setHoneyLevel(level, pos, state, 0);
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.HONEY_BLOCK));
            }

            playTakeSound(level, pos);
            player.swing(hand);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //
        // COMPAT HONEY BUCKET -> FULL POT
        //
        if (isCompatItem(held, "the_bumblezone:honey_bucket") || isCompatItem(held, "create:honey_bucket")) {

            if (honeyLevel != 0)
                return InteractionResult.sidedSuccess(level.isClientSide);

            if (!level.isClientSide) {
                setHoneyLevel(level, pos, state, 4);
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                }
            }

            playFillSound(level, pos);
            player.swing(hand);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //
        // EMPTY BUCKET -> HONEY BUCKET
        //
        if (held.is(Items.BUCKET)) {

            if (honeyLevel != 4)
                return InteractionResult.sidedSuccess(level.isClientSide);

            ItemStack honeyBucket = getPreferredHoneyBucket();

            // Neither Bumblezone nor Create is installed
            if (honeyBucket.isEmpty())
                return InteractionResult.PASS;

            if (!level.isClientSide) {
                setHoneyLevel(level, pos, state, 0);
                player.setItemInHand(hand, honeyBucket.copy());
            }

            playTakeSound(level, pos);
            player.swing(hand);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //
        // HONEYCOMB -> 33% chance of +1 honey
        //
        if (held.is(Items.HONEYCOMB)) {

            if (honeyLevel >= 4)
                return InteractionResult.sidedSuccess(level.isClientSide);

            if (!level.isClientSide) {
                if (level.random.nextInt(3) == 0) {
                    setHoneyLevel(level, pos, state, honeyLevel + 1);
                    consumeItem(player, held);
                    playFillSound(level, pos);
                }
            }

            player.swing(hand);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //
        // APPLE -> HONEY APPLE
        //
        if (held.is(Items.APPLE)) {

            if (honeyLevel <= 0)
                return InteractionResult.sidedSuccess(level.isClientSide);

            ItemStack result = getCompatItem("buzzier_bees:honey_apple");

            if (result.isEmpty())
                return InteractionResult.PASS;

            if (!level.isClientSide) {
                setHoneyLevel(level, pos, state, honeyLevel - 1);
                consumeItem(player, held);
                ItemHandlerHelper.giveItemToPlayer(player, result);
            }

            playTakeSound(level, pos);
            player.swing(hand);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //
        // BREAD -> HONEY BREAD
        //
        if (held.is(Items.BREAD)) {

            if (honeyLevel <= 0)
                return InteractionResult.sidedSuccess(level.isClientSide);

            ItemStack result = getCompatItem("buzzier_bees:honey_bread");

            if (result.isEmpty())
                return InteractionResult.PASS;

            if (!level.isClientSide) {
                setHoneyLevel(level, pos, state, honeyLevel - 1);
                consumeItem(player, held);
                ItemHandlerHelper.giveItemToPlayer(player, result);
            }

            playTakeSound(level, pos);
            player.swing(hand);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //
        // COOKED PORKCHOP -> GLAZED PORKCHOP
        //
        if (held.is(Items.COOKED_PORKCHOP)) {

            if (honeyLevel <= 0)
                return InteractionResult.sidedSuccess(level.isClientSide);

            ItemStack result = getCompatItem("buzzier_bees:glazed_porkchop");

            if (result.isEmpty())
                return InteractionResult.PASS;

            if (!level.isClientSide) {
                setHoneyLevel(level, pos, state, honeyLevel - 1);
                consumeItem(player, held);
                ItemHandlerHelper.giveItemToPlayer(player, result);
            }

            playTakeSound(level, pos);
            player.swing(hand);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return state.getValue(HONEY_LEVEL);
    }

    private static void setHoneyLevel(Level level, BlockPos pos, BlockState state, int value) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(HONEY_LEVEL, value), 3);
        }
    }

    private static void consumeItem(Player player, ItemStack stack) {

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private static void playFillSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE,
                SoundSource.BLOCKS, 1.0F, 1.0F
        );
    }

    private static void playTakeSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.HONEY_BLOCK_BREAK,
                SoundSource.BLOCKS, 1.0F, 1.0F
        );
    }

    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 0;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {

        int honeyLevel = state.getValue(HONEY_LEVEL);

        return switch (honeyLevel) {
            case 1 -> Shapes.or(
                    box(0, 0, 0, 16, 2, 16),
                    box(0, 2, 0, 3, 16, 16),
                    box(13, 2, 0, 16, 16, 16),
                    box(3, 2, 0, 13, 16, 3),
                    box(3, 2, 13, 13, 16, 16),
                    box(3, 2, 3, 13, 6, 13),
                    box(4, 3, 4, 12, 5, 12)
            );
            case 2 -> Shapes.or(
                    box(0, 0, 0, 16, 2, 16),
                    box(0, 2, 0, 3, 16, 16),
                    box(13, 2, 0, 16, 16, 16),
                    box(3, 2, 0, 13, 16, 3),
                    box(3, 2, 13, 13, 16, 16),
                    box(3, 2, 3, 13, 9, 13),
                    box(4, 3, 4, 12, 8, 12)
            );
            case 3 -> Shapes.or(
                    box(0, 0, 0, 16, 2, 16),
                    box(0, 2, 0, 3, 16, 16),
                    box(13, 2, 0, 16, 16, 16),
                    box(3, 2, 0, 13, 16, 3),
                    box(3, 2, 13, 13, 16, 16),
                    box(3, 2, 3, 13, 12, 13),
                    box(4, 3, 4, 12, 11, 12)
            );
            case 4 -> Shapes.or(
                    box(0, 0, 0, 16, 2, 16),
                    box(0, 2, 0, 3, 16, 16),
                    box(13, 2, 0, 16, 16, 16),
                    box(3, 2, 0, 13, 16, 3),
                    box(3, 2, 13, 13, 16, 16),
                    box(3, 2, 3, 13, 15, 13),
                    box(4, 3, 4, 12, 14, 12)
            );
            default -> Shapes.or(
                    box(0, 0, 0, 16, 2, 16),
                    box(0, 2, 0, 3, 16, 16),
                    box(13, 2, 0, 16, 16, 16),
                    box(3, 2, 0, 13, 16, 3),
                    box(3, 2, 13, 13, 16, 16)
            );
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HONEY_LEVEL);
    }

    @Override
    public @NotNull WorldlyContainer getContainer(BlockState state, @NotNull LevelAccessor level, @NotNull BlockPos pos) {

        int honeyLevel = state.getValue(HONEY_LEVEL);

        if (honeyLevel == 4) {
            return new FullInventory(state, level, pos, new ItemStack(Items.HONEY_BLOCK));
        }

        return new PartialInventory(state, level, pos);
    }

    static class PartialInventory extends SimpleContainer implements WorldlyContainer {

        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean inserted;

        public PartialInventory(BlockState state, LevelAccessor level, BlockPos pos) {
            super(1);
            this.state = state;
            this.level = level;
            this.pos = pos;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
            return side == Direction.UP ? new int[]{0} : new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
            Item item = stack.getItem();
            return !inserted && side == Direction.UP && (item == Items.HONEYCOMB || item == Items.HONEY_BLOCK);
        }

        @Override
        public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction side) {
            return false;
        }

        @Override
        public void setChanged() {
            ItemStack stack = getItem(0);
            if (!stack.isEmpty()) {
                inserted = true;
                attemptInteract(stack);
                removeItemNoUpdate(0);
            }
        }

        private void attemptInteract(ItemStack stack) {
            int honeyLevel = state.getValue(HONEY_LEVEL);
            if (stack.is(Items.HONEYCOMB) && honeyLevel < 4) {
                if (level.getRandom().nextInt(3) == 0) {
                    level.setBlock(pos, state.setValue(HONEY_LEVEL, honeyLevel + 1), 3);
                }
                return;
            }
            if (stack.is(Items.HONEY_BLOCK) && honeyLevel == 0) {
                level.setBlock(pos, state.setValue(HONEY_LEVEL, 4), 3);
            }
        }
    }

    static class FullInventory extends SimpleContainer implements WorldlyContainer {

        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean extracted;

        public FullInventory(BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
            super(stack);
            this.state = state;
            this.level = level;
            this.pos = pos;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
            return side == Direction.DOWN ? new int[]{0} : new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, Direction side) {
            return false;
        }

        @Override
        public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction side) {
            return !extracted && side == Direction.DOWN && stack.is(Items.HONEY_BLOCK);
        }

        @Override
        public void setChanged() {
            level.setBlock(pos, state.setValue(HONEY_LEVEL, 0), 3);
            extracted = true;
        }
    }
}
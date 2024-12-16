package github.xvareon.graytabbycatmod.block.dispenser;

import github.xvareon.graytabbycatmod.entity.DragonFireball;
import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class DragonFireballDispenseBehavior extends DefaultDispenseItemBehavior {

    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        Level level = source.getLevel();
        Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);

        // Position where the entity will spawn
        double x = source.x() + direction.getStepX() * 1.0D;
        double y = source.y() + direction.getStepY() * 1.5D;
        double z = source.z() + direction.getStepZ() * 1.0D;

        // Create and configure the LightningCharge entity
        DragonFireball dragonFireball = new DragonFireball(EntityInit.DRAGON_FIREBALL.get(), level);
        dragonFireball.setPos(x, y, z); // Set the position of the entity
        dragonFireball.setDeltaMovement(
                direction.getStepX() * 1.0,
                direction.getStepY() * 1.0,
                direction.getStepZ() * 1.0
        );

        // Add the entity to the world
        level.addFreshEntity(dragonFireball);

        // Reduce stack size in the dispenser
        stack.shrink(1);
        return stack;
    }
}
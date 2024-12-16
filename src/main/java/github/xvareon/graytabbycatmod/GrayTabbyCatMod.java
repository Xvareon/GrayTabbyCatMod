package github.xvareon.graytabbycatmod;

import github.xvareon.graytabbycatmod.block.dispenser.DragonFireballDispenseBehavior;
import github.xvareon.graytabbycatmod.block.dispenser.LightningChargeDispenseBehavior;
import github.xvareon.graytabbycatmod.init.BlockInit;
import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import github.xvareon.graytabbycatmod.init.ItemInit;
import github.xvareon.graytabbycatmod.init.CreativeTabInit;


@Mod(GrayTabbyCatMod.MODID)
public class GrayTabbyCatMod {
    public static final String MODID = "graytabbycatmod";

    public GrayTabbyCatMod(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.ITEMS.register(bus);
        EntityInit.ENTITIES.register(bus);
        BlockInit.BLOCKS.register(bus);
        CreativeTabInit.TABS.register(bus);

        bus.addListener(this::setupDispenserBehaviors);
    }

    private void setupDispenserBehaviors(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            // Lightning Charge
            DispenserBlock.registerBehavior(
                    ItemInit.LIGHTNING_CHARGE.get(), // Replace with your item
                    new LightningChargeDispenseBehavior()
            );

            // Dragon Fireball
            DispenserBlock.registerBehavior(
                    ItemInit.DRAGON_FIREBALL.get(), // Replace with your item
                    new DragonFireballDispenseBehavior()
            );

        });
    }
}

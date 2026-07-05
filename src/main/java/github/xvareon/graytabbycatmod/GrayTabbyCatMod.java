package github.xvareon.graytabbycatmod;

import github.xvareon.graytabbycatmod.block.dispenser.DragonFireballDispenseBehavior;
import github.xvareon.graytabbycatmod.block.dispenser.LightningChargeDispenseBehavior;
import github.xvareon.graytabbycatmod.config.ModConfigHandler;
import github.xvareon.graytabbycatmod.init.BlockInit;
import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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

        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigHandler.COMMON_SPEC);

        bus.addListener(this::setupDispenserBehaviors);
        bus.addListener(ModConfigHandler::onLoad);
        bus.addListener(ModConfigHandler::onReload);
    }

    private void setupDispenserBehaviors(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            // Lightning Charge
            DispenserBlock.registerBehavior(
                    ItemInit.LIGHTNING_CHARGE.get(),
                    new LightningChargeDispenseBehavior()
            );

            // Dragon Fireball
            DispenserBlock.registerBehavior(
                    ItemInit.DRAGON_FIREBALL.get(),
                    new DragonFireballDispenseBehavior()
            );

        });
    }
}

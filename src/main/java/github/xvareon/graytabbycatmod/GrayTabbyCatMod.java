package github.xvareon.graytabbycatmod;

import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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
        CreativeTabInit.TABS.register(bus);
    }
}

package github.xvareon.graytabbycatmod;

import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(GrayTabbyCatMod.MODID)
public class GrayTabbyCatMod {
    public static final String MODID = "graytabbycatmod";

    public GrayTabbyCatMod(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        EntityInit.ENTITIES.register(bus);
    }
}

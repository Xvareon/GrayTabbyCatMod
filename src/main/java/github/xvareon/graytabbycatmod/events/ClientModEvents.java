package github.xvareon.graytabbycatmod.events;

import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.client.renderer.GrayTabbyCatRenderer;
import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GrayTabbyCatMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)

public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntityInit.GRAY_TABBY_CAT.get(), GrayTabbyCatRenderer::new);
        event.registerEntityRenderer(EntityInit.DRAGON_FIREBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(EntityInit.LIGHTNING_CHARGE.get(), ThrownItemRenderer::new);
    }
}

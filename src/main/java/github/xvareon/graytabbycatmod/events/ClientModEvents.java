package github.xvareon.graytabbycatmod.events;

import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.client.renderer.*;
import github.xvareon.graytabbycatmod.init.BlockInit;
import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GrayTabbyCatMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)

public class ClientModEvents {

    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BarnacleModel.LAYER_LOCATION, BarnacleModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntityInit.GRAY_TABBY_CAT.get(), GrayTabbyCatRenderer::new);
        event.registerEntityRenderer(EntityInit.DRAGON_FIREBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(EntityInit.LIGHTNING_CHARGE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(EntityInit.BARNACLE.get(), BarnacleRenderer::new);
        event.registerEntityRenderer(EntityInit.TAMABLE_OCELOT.get(), TamableOcelotRenderer::new);
        event.registerEntityRenderer(EntityInit.DOGGO.get(), DoggoRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(
                    BlockInit.HONEY_POT.get(),
                    RenderType.translucent()
            );
        });
    }
}

package github.xvareon.graytabbycatmod.client.renderer;

import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.entity.GrayTabbyCat;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import org.jetbrains.annotations.NotNull;

public class GrayTabbyCatRenderer extends MobRenderer<GrayTabbyCat, CatModel<GrayTabbyCat>> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat.png");

    public GrayTabbyCatRenderer (EntityRendererProvider.Context ctx){
        super(ctx, new CatModel<>(ctx.bakeLayer(ModelLayers.CAT)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GrayTabbyCat entity){
        return TEXTURE;
    }
}
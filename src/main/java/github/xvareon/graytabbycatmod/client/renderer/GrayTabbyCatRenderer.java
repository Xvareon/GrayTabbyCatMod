package github.xvareon.graytabbycatmod.client.renderer;

import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.entity.GrayTabbyCat;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public class GrayTabbyCatRenderer extends MobRenderer<GrayTabbyCat, CatModel<GrayTabbyCat>> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat.png");
    private static final ResourceLocation COLLAR = new ResourceLocation(GrayTabbyCatMod.MODID, "textures/collar.png");

    public GrayTabbyCatRenderer (EntityRendererProvider.Context ctx){
        super(ctx, new CatModel<>(ctx.bakeLayer(ModelLayers.CAT)), 0.5F);

        addLayer(new GrayTabbyCatCollarLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GrayTabbyCat entity){
        return TEXTURE;
    }

    static class GrayTabbyCatCollarLayer extends RenderLayer<GrayTabbyCat, CatModel<GrayTabbyCat>> {

        public GrayTabbyCatCollarLayer(RenderLayerParent<GrayTabbyCat, CatModel<GrayTabbyCat>> renderer) {
            super(renderer);
        }

        @Override
        public void render(@NotNull PoseStack matrix, @NotNull MultiBufferSource buffer, int light, @NotNull GrayTabbyCat entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

            float[] afloat = entity.getCollarColor().getTextureDiffuseColors();

            renderColoredCutoutModel(getParentModel(), COLLAR, matrix, buffer, light, entity, afloat[0], afloat[1], afloat[2]);

        }

    }
}
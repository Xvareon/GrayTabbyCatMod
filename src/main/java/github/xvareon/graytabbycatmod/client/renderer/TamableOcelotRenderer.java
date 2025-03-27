package github.xvareon.graytabbycatmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.entity.TamableOcelot;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TamableOcelotRenderer extends MobRenderer<TamableOcelot, CatModel<TamableOcelot>> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/tamable_ocelot.png");

    private static final ResourceLocation COLLAR = new ResourceLocation(GrayTabbyCatMod.MODID, "textures/collar.png");

    public TamableOcelotRenderer(EntityRendererProvider.Context ctx){
        super(ctx, new CatModel<>(ctx.bakeLayer(ModelLayers.CAT)), 0.5F);

        addLayer(new GrayTabbyCatCollarLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TamableOcelot entity){
        return TEXTURE;
    }

    static class GrayTabbyCatCollarLayer extends RenderLayer<TamableOcelot, CatModel<TamableOcelot>> {

        public GrayTabbyCatCollarLayer(RenderLayerParent<TamableOcelot, CatModel<TamableOcelot>> renderer) {
            super(renderer);
        }

        @Override
        public void render(@NotNull PoseStack matrix, @NotNull MultiBufferSource buffer, int light, @NotNull TamableOcelot entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

            float[] afloat = entity.getCollarColor().getTextureDiffuseColors();

            renderColoredCutoutModel(getParentModel(), COLLAR, matrix, buffer, light, entity, afloat[0], afloat[1], afloat[2]);

        }

    }
}
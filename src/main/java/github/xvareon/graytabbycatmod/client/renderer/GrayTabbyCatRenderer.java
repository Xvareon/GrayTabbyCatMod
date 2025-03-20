package github.xvareon.graytabbycatmod.client.renderer;

import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.entity.GrayTabbyCat;
import github.xvareon.graytabbycatmod.entity.GrayTabbyCatVariant;
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

import java.util.HashMap;
import java.util.Map;

public class GrayTabbyCatRenderer extends MobRenderer<GrayTabbyCat, CatModel<GrayTabbyCat>> {

    // Map of textures for different gray tabby cat variants
    private static final Map<GrayTabbyCatVariant, ResourceLocation> TEXTURES =
        Map.copyOf(new HashMap<>(Map.ofEntries(
                Map.entry(GrayTabbyCatVariant.DEFAULT, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat.png")),
                Map.entry(GrayTabbyCatVariant.CALICO, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_calico.png")),
                Map.entry(GrayTabbyCatVariant.GREEN, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_green.png")),
                Map.entry(GrayTabbyCatVariant.YELLOW, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_yellow.png")),
                Map.entry(GrayTabbyCatVariant.ORANGE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_orange.png")),
                Map.entry(GrayTabbyCatVariant.HALF, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_half.png")),
                Map.entry(GrayTabbyCatVariant.CYAN, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_cyan.png")),
                Map.entry(GrayTabbyCatVariant.BROWN, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_brown.png")),
                Map.entry(GrayTabbyCatVariant.PINK, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_pink.png")),
                Map.entry(GrayTabbyCatVariant.BLACK, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_black.png")),
                Map.entry(GrayTabbyCatVariant.RED, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_red.png")),
                Map.entry(GrayTabbyCatVariant.WHITE_CALICO, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_white_calico.png")),
                Map.entry(GrayTabbyCatVariant.WHITE_GREEN, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_white_green.png")),
                Map.entry(GrayTabbyCatVariant.WHITE_BLUE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_white_blue.png")),
                Map.entry(GrayTabbyCatVariant.WHITE_YELLOW, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_white_yellow.png")),
                Map.entry(GrayTabbyCatVariant.WHITE_ORANGE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_white_orange.png")),
                Map.entry(GrayTabbyCatVariant.WHITE_CYAN, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_white_cyan.png")),
                Map.entry(GrayTabbyCatVariant.WHITE_PINK, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_white_pink.png")),
                Map.entry(GrayTabbyCatVariant.GRAY, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_gray.png")),
                Map.entry(GrayTabbyCatVariant.VIOLET, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/graytabbycat_violet.png"))
        )));

    private static final ResourceLocation COLLAR = new ResourceLocation(GrayTabbyCatMod.MODID, "textures/collar.png");

    public GrayTabbyCatRenderer (EntityRendererProvider.Context ctx){
        super(ctx, new CatModel<>(ctx.bakeLayer(ModelLayers.CAT)), 0.5F);

        addLayer(new GrayTabbyCatCollarLayer(this));
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull GrayTabbyCat entity) {
        return TEXTURES.getOrDefault(entity.getGrayTabbyVariant(), TEXTURES.get(GrayTabbyCatVariant.DEFAULT));
    }

    @Override
    protected void scale(@NotNull GrayTabbyCat entity, @NotNull PoseStack poseStack, float partialTickTime) {
        float scale = entity.getSizeMultiplier(); // Get the random scale
        poseStack.scale(scale, scale, scale); // Apply scaling to the model
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
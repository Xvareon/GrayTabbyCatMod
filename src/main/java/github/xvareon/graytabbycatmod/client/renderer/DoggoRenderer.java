package github.xvareon.graytabbycatmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.entity.Doggo;
import github.xvareon.graytabbycatmod.entity.DoggoVariant;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DoggoRenderer extends MobRenderer<Doggo, WolfModel<Doggo>> {

    // Wild Doggos
    private static final Map<DoggoVariant, ResourceLocation> WILD_TEXTURES =
        Map.copyOf(new HashMap<>(Map.ofEntries(
                Map.entry(DoggoVariant.OGGIE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/oggie.png")),
                Map.entry(DoggoVariant.PEANUT, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/peanut.png")),
                Map.entry(DoggoVariant.NUGGETS, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/nuggets.png")),
                Map.entry(DoggoVariant.BEAGLE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/beagle.png")),
                Map.entry(DoggoVariant.BLACK_MOUNTAIN_FEIST, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/black_mountain_feist.png")),
                Map.entry(DoggoVariant.BLOCCO, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/blocco.png")),
                Map.entry(DoggoVariant.BORDER_COLLIE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/border_collie.png")),
                Map.entry(DoggoVariant.BOSTON_TERRIER, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/boston_terrier.png")),
                Map.entry(DoggoVariant.GOLDEN_RETRIEVER, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/golden_retriever.png")),
                Map.entry(DoggoVariant.PUG, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/pug.png")),
                Map.entry(DoggoVariant.RED_SHIBA_INU, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/red_shiba_inu.png")),
                Map.entry(DoggoVariant.SIBERIAN_HUSKY, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/siberian_husky.png")),
                Map.entry(DoggoVariant.ST_BERNARD, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/st_bernard.png")),
                Map.entry(DoggoVariant.WHITE_TERRIER, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/white_terrier.png"))
        )));

    // Angry Doggos
    private static final Map<DoggoVariant, ResourceLocation> ANGRY_TEXTURES =
            Map.copyOf(new HashMap<>(Map.ofEntries(
                    Map.entry(DoggoVariant.OGGIE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/oggie_angry.png")),
                    Map.entry(DoggoVariant.PEANUT, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/peanut_angry.png")),
                    Map.entry(DoggoVariant.NUGGETS, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/nuggets_angry.png")),
                    Map.entry(DoggoVariant.BEAGLE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/beagle_angry.png")),
                    Map.entry(DoggoVariant.BLACK_MOUNTAIN_FEIST, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/black_mountain_feist_angry.png")),
                    Map.entry(DoggoVariant.BLOCCO, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/blocco_angry.png")),
                    Map.entry(DoggoVariant.BORDER_COLLIE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/border_collie_angry.png")),
                    Map.entry(DoggoVariant.BOSTON_TERRIER, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/boston_terrier_angry.png")),
                    Map.entry(DoggoVariant.GOLDEN_RETRIEVER, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/golden_retriever_angry.png")),
                    Map.entry(DoggoVariant.PUG, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/pug_angry.png")),
                    Map.entry(DoggoVariant.RED_SHIBA_INU, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/red_shiba_inu_angry.png")),
                    Map.entry(DoggoVariant.SIBERIAN_HUSKY, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/siberian_husky_angry.png")),
                    Map.entry(DoggoVariant.ST_BERNARD, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/st_bernard_angry.png")),
                    Map.entry(DoggoVariant.WHITE_TERRIER, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/white_terrier_angry.png"))
            )));

    // Tamed Doggos
    private static final Map<DoggoVariant, ResourceLocation> TAMED_TEXTURES =
            Map.copyOf(new HashMap<>(Map.ofEntries(
                    Map.entry(DoggoVariant.OGGIE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/oggie_tamed.png")),
                    Map.entry(DoggoVariant.PEANUT, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/peanut_tamed.png")),
                    Map.entry(DoggoVariant.NUGGETS, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/nuggets_tamed.png")),
                    Map.entry(DoggoVariant.BEAGLE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/beagle_tamed.png")),
                    Map.entry(DoggoVariant.BLACK_MOUNTAIN_FEIST, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/black_mountain_feist_tamed.png")),
                    Map.entry(DoggoVariant.BLOCCO, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/blocco_tamed.png")),
                    Map.entry(DoggoVariant.BORDER_COLLIE, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/border_collie_tamed.png")),
                    Map.entry(DoggoVariant.BOSTON_TERRIER, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/boston_terrier_tamed.png")),
                    Map.entry(DoggoVariant.GOLDEN_RETRIEVER, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/golden_retriever_tamed.png")),
                    Map.entry(DoggoVariant.PUG, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/pug_tamed.png")),
                    Map.entry(DoggoVariant.RED_SHIBA_INU, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/red_shiba_inu_tamed.png")),
                    Map.entry(DoggoVariant.SIBERIAN_HUSKY, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/siberian_husky_tamed.png")),
                    Map.entry(DoggoVariant.ST_BERNARD, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/st_bernard_tamed.png")),
                    Map.entry(DoggoVariant.WHITE_TERRIER, new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/doggo/white_terrier_tamed.png"))
            )));

    private static final ResourceLocation COLLAR = new ResourceLocation(GrayTabbyCatMod.MODID, "textures/collar.png");

    public DoggoRenderer(EntityRendererProvider.Context ctx){
        super(ctx, new WolfModel<>(ctx.bakeLayer(ModelLayers.WOLF)), 0.5F);

        addLayer(new DoggoCollarLayer(this));
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull Doggo entity) {
        DoggoVariant variant = entity.getDoggoVariant();

        if (entity.isAngry()) {
            return ANGRY_TEXTURES.getOrDefault(variant, WILD_TEXTURES.get(DoggoVariant.OGGIE));
        } else if (entity.isTame()) {
            return TAMED_TEXTURES.getOrDefault(variant, WILD_TEXTURES.get(DoggoVariant.OGGIE));
        } else {
            return WILD_TEXTURES.getOrDefault(variant, WILD_TEXTURES.get(DoggoVariant.OGGIE));
        }
    }

    @Override
    protected void scale(@NotNull Doggo entity, @NotNull PoseStack poseStack, float partialTickTime) {
        float scale = entity.getSizeMultiplier(); // Get the random scale
        poseStack.scale(scale, scale, scale); // Apply scaling to the model
    }

    static class DoggoCollarLayer extends RenderLayer<Doggo, WolfModel<Doggo>> {

        public DoggoCollarLayer(RenderLayerParent<Doggo, WolfModel<Doggo>> renderer) {
            super(renderer);
        }

        @Override
        public void render(@NotNull PoseStack matrix, @NotNull MultiBufferSource buffer, int light,
                           @NotNull Doggo entity, float limbSwing, float limbSwingAmount,
                           float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

            // Only render collar if the dog is tame and visible
            if (entity.isTame() && !entity.isInvisible()) {
                float[] afloat = entity.getCollarColor().getTextureDiffuseColors();
                renderColoredCutoutModel(getParentModel(), COLLAR, matrix, buffer, light,
                        entity, afloat[0], afloat[1], afloat[2]);
            }
        }
    }

}
package github.xvareon.graytabbycatmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.entity.Barnacle;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class BarnacleRenderer extends MobRenderer<Barnacle, BarnacleModel<Barnacle>> {
    private static final ResourceLocation BARNACLE_LOCATION = new ResourceLocation(GrayTabbyCatMod.MODID, "textures/entity/barnacle.png");

    public BarnacleRenderer(EntityRendererProvider.Context context) {
        super(context, new BarnacleModel<>(context.bakeLayer(BarnacleModel.LAYER_LOCATION)), 0.7F);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull Barnacle entity) {
        return BARNACLE_LOCATION;
    }

    @Override
    protected void scale(@NotNull Barnacle entity, @NotNull PoseStack poseStack, float partialTickTime) {
        float scale = entity.getSizeMultiplier(); // Get the random scale
        poseStack.scale(scale, scale, scale); // Apply scaling to the model
    }

    @Override
    protected void setupRotations(@NotNull Barnacle entity, @NotNull PoseStack poseStack, float p_116037_, float p_116038_, float p_116039_) {
        if (entity.deathTime > 0) {
            float $$5 = ((float) entity.deathTime - 1.0F) / 20.0F * 1.6F;
            $$5 = Mth.sqrt($$5);
            if ($$5 > 1.0F) $$5 = 1.0F;
            poseStack.mulPose(Axis.ZP.rotationDegrees($$5 * getFlipDegrees(entity)));
        } else if (isEntityUpsideDown(entity)) {
            poseStack.translate(0.0F, entity.getBbHeight() + 0.1F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
        float f = Mth.lerp(p_116039_, entity.xBodyRotO, entity.xBodyRot);
        float f1 = Mth.lerp(p_116039_, entity.zBodyRotO, entity.zBodyRot);
        float f2 = Mth.lerp(p_116039_, entity.yBodyRotO, entity.yBodyRot);
        poseStack.translate(0.0F, 0.5F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - f2));
        poseStack.mulPose(Axis.XP.rotationDegrees(f));
        poseStack.mulPose(Axis.YP.rotationDegrees(f1));
        poseStack.translate(0.0F, -1.2F, 0.0F);
    }
}
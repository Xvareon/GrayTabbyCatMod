package github.xvareon.graytabbycatmod.client.renderer;

import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.entity.Barnacle;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BarnacleModel<T extends Barnacle> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(GrayTabbyCatMod.MODID, "barnacle"), "main");
    private final ModelPart root;

    public BarnacleModel(ModelPart root) {
        this.root = root.getChild("monster");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition monster = partdefinition.addOrReplaceChild("monster", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 11.5F, 0.0F, -1.5708F, 0.0F, 1.5708F));
        PartDefinition body = monster.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 32).addBox(0.0F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(27, 27).addBox(5.0F, -2.5F, -2.5F, 6.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("piercer", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.1F, 0.0F, 0.0F));
        PartDefinition segmentCluster1 = body.addOrReplaceChild("segmentCluster1", CubeListBuilder.create(), PartPose.offset(0.1F, 0.0F, 0.0F));
        segmentCluster1.addOrReplaceChild("segment1", CubeListBuilder.create().texOffs(0, 24).addBox(-12.0F, -4.0F, -2.0F, 12.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));
        segmentCluster1.addOrReplaceChild("segment3", CubeListBuilder.create().texOffs(0, 8).addBox(-12.0F, 0.0F, -2.0F, 12.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));
        PartDefinition segmentCluster2 = body.addOrReplaceChild("segmentCluster2", CubeListBuilder.create(), PartPose.offset(0.1F, 0.0F, 0.0F));
        segmentCluster2.addOrReplaceChild("segment2", CubeListBuilder.create().texOffs(0, 16).addBox(-12.0F, -4.0F, -2.0F, 12.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));
        segmentCluster2.addOrReplaceChild("segment4", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, 0.0F, -2.0F, 12.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        root().getAllParts().forEach(ModelPart::resetPose);
        animate(entity.impaleAnimationState, BarnacleAnimation.IMPALE, ageInTicks, 1.0f);
        animate(entity.hurtAnimationState, BarnacleAnimation.HURT, ageInTicks, 1.0f);
        animate(entity.swimAnimationState, BarnacleAnimation.SWIM, ageInTicks, 1.0f);
    }

    @NotNull
    @Override
    public ModelPart root() {
        return root;
    }
}
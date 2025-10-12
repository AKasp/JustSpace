package net.vildulv.minecraft.justspace.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.vildulv.minecraft.justspace.client.render.model.VoidMantaModel;
import net.vildulv.minecraft.justspace.mob.entity.VoidMantaEntity;

public class VoidMantaRenderer extends MobRenderer<VoidMantaEntity, VoidMantaModel<VoidMantaEntity>> {


    public VoidMantaRenderer(EntityRendererProvider.Context context) {
        super(context, new VoidMantaModel(context.bakeLayer(ModelLayers.PHANTOM)), 0.75F);
    }

    @Override
    protected void scale(VoidMantaEntity livingEntity, PoseStack poseStack, float partialTickTime) {
        poseStack.translate(0.0F, 1.3125F, 0.1875F);
    }


    protected void setupRotations(VoidMantaEntity entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
    }


    public ResourceLocation getTextureLocation(VoidMantaEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("justspace", "textures/entity/void_manta.png");
    }
}

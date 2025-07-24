package net.vildulv.minecraft.justspace.client.render.effects;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.vildulv.minecraft.justspace.Config;
import org.joml.*;

import java.lang.Math;
import java.util.OptionalDouble;
import java.util.OptionalInt;


public class SpaceDimensionEffect extends DimensionSpecialEffects {

    private static final ResourceLocation SUN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");

    private final Minecraft minecraft = Minecraft.getInstance();

    private final GpuBuffer starBuffer;
    private final RenderSystem.AutoStorageIndexBuffer starIndices;
    private int starIndexCount;

    public SpaceDimensionEffect() {
        super(SkyType.OVERWORLD, false, true);

        this.starIndices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        this.starBuffer = this.buildStars();
    }

    public Vec3 getBrightnessDependentFogColor(Vec3 pFogColor, float pBrightness) {
        return pFogColor.multiply(pBrightness * 0.94F + 0.06F, pBrightness * 0.94F + 0.06F, pBrightness * 0.91F + 0.09F);
    }

    public boolean isFoggyAt(int pX, int pY) {
        return false;
    }

    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Runnable setupFog) {
        PoseStack posestack = new PoseStack();
        posestack.mulPose(modelViewMatrix);
        renderSunMoonAndStars(posestack, minecraft.renderBuffers().bufferSource(), 0.7f, -1, 1, 0.8f, camera);
        return true;
    }

    public void renderSunMoonAndStars(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float timeOfDay, int moonPhase, float rainLevel, float starBrightness, Camera camera) {
        poseStack.pushPose();
        //poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        //poseStack.mulPose(Axis.XP.rotationDegrees(timeOfDay * 360.0F));
        this.renderSun(rainLevel, bufferSource, poseStack);
        for (Config.PlanetRecord planet : Config.PARSED_PLANETS.values()) {
            this.renderPlanet(rainLevel, bufferSource, poseStack, camera, planet);
        }
        bufferSource.endBatch();
        if (starBrightness > 0.0F) {
            this.renderStars(starBrightness, poseStack);
        }

        poseStack.popPose();
    }

    private void renderSun(float alpha, MultiBufferSource bufferSource, PoseStack poseStack) {
        float f = 30.0F;
        float f1 = 100.0F;
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.celestial(SUN_LOCATION));
        int i = ARGB.white(alpha);
        Matrix4f matrix4f = poseStack.last().pose();
        vertexconsumer.addVertex(matrix4f, -30.0F, 100.0F, -30.0F).setUv(0.0F, 0.0F).setColor(i);
        vertexconsumer.addVertex(matrix4f, 30.0F, 100.0F, -30.0F).setUv(1.0F, 0.0F).setColor(i);
        vertexconsumer.addVertex(matrix4f, 30.0F, 100.0F, 30.0F).setUv(1.0F, 1.0F).setColor(i);
        vertexconsumer.addVertex(matrix4f, -30.0F, 100.0F, 30.0F).setUv(0.0F, 1.0F).setColor(i);
    }

    private void renderStars(float starBrightness, PoseStack poseStack) {
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        matrix4fstack.pushMatrix();
        matrix4fstack.mul(poseStack.last().pose());
        RenderPipeline renderpipeline = RenderPipelines.STARS;
        GpuTextureView gputextureview = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView gputextureview1 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        GpuBuffer gpubuffer = this.starIndices.getBuffer(this.starIndexCount);
        GpuBufferSlice gpubufferslice = RenderSystem.getDynamicUniforms().writeTransform(matrix4fstack, new Vector4f(starBrightness, starBrightness, starBrightness, starBrightness), new Vector3f(), new Matrix4f(), 0.0F);

        try (RenderPass renderpass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Stars", gputextureview, OptionalInt.empty(), gputextureview1, OptionalDouble.empty())) {
            renderpass.setPipeline(renderpipeline);
            RenderSystem.bindDefaultUniforms(renderpass);
            renderpass.setUniform("DynamicTransforms", gpubufferslice);
            renderpass.setVertexBuffer(0, this.starBuffer);
            renderpass.setIndexBuffer(gpubuffer, this.starIndices.type());
            renderpass.drawIndexed(0, 0, this.starIndexCount, 1);
        }

        matrix4fstack.popMatrix();
    }


    private GpuBuffer buildStars() {
        RandomSource randomsource = RandomSource.create(10842L);
        float f = 100.0F;

        GpuBuffer gpubuffer;
        try (ByteBufferBuilder bytebufferbuilder = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION.getVertexSize() * 1500 * 4)) {
            BufferBuilder bufferbuilder = new BufferBuilder(bytebufferbuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

            for (int i = 0; i < 1500; ++i) {
                float f1 = randomsource.nextFloat() * 2.0F - 1.0F;
                float f2 = randomsource.nextFloat() * 2.0F - 1.0F;
                float f3 = randomsource.nextFloat() * 2.0F - 1.0F;
                float f4 = 0.15F + randomsource.nextFloat() * 0.1F;
                float f5 = Mth.lengthSquared(f1, f2, f3);
                if (!(f5 <= 0.010000001F) && !(f5 >= 1.0F)) {
                    Vector3f vector3f = (new Vector3f(f1, f2, f3)).normalize(100.0F);
                    float f6 = (float) (randomsource.nextDouble() * (double) (float) Math.PI * (double) 2.0F);
                    Matrix3f matrix3f = (new Matrix3f()).rotateTowards((new Vector3f(vector3f)).negate(), new Vector3f(0.0F, 1.0F, 0.0F)).rotateZ(-f6);
                    bufferbuilder.addVertex((new Vector3f(f4, -f4, 0.0F)).mul(matrix3f).add(vector3f));
                    bufferbuilder.addVertex((new Vector3f(f4, f4, 0.0F)).mul(matrix3f).add(vector3f));
                    bufferbuilder.addVertex((new Vector3f(-f4, f4, 0.0F)).mul(matrix3f).add(vector3f));
                    bufferbuilder.addVertex((new Vector3f(-f4, -f4, 0.0F)).mul(matrix3f).add(vector3f));
                }
            }

            try (MeshData meshdata = bufferbuilder.buildOrThrow()) {
                this.starIndexCount = meshdata.drawState().indexCount();
                gpubuffer = RenderSystem.getDevice().createBuffer(() -> "Stars vertex buffer", 40, meshdata.vertexBuffer());
            }
        }

        return gpubuffer;
    }


    private void renderPlanet(float alpha, MultiBufferSource bufferSource, PoseStack poseStack, Camera camera, Config.PlanetRecord record) {
        float f = 200.0F;
        float planetYPlane = 100.0F;
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.celestial(record.texture()));
        int i = ARGB.white(1.0F);
        poseStack.pushPose();
        double px = record.x();
        double pz = record.z();
        double cx = camera.getPosition().x;
        double cy = camera.getPosition().y;
        double cz = camera.getPosition().z;
        double dx = cx - px;
        double dy = cy + planetYPlane; //500 is the height of the planet
        double dz = cz - pz;
        double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
        if (distance > 500) {
            return; //Don't draw further than 500 blocks away
        }
        System.out.println("Distance to planet: " + distance);
        f = planetYPlane - (float) distance * 0.2f;
        System.out.println("size: " + f);
        Matrix4f matrix4f = poseStack.last().pose().translation(-(float) dx, -(float) dy, -(float) dz);

        vertexconsumer.addVertex(matrix4f, -f, 0, f).setUv(0.0F, 0.0F).setColor(i);
        vertexconsumer.addVertex(matrix4f, f, 0, f).setUv(1.0F, 0.0F).setColor(i);
        vertexconsumer.addVertex(matrix4f, f, 0, -f).setUv(1.0F, 1.0F).setColor(i);
        vertexconsumer.addVertex(matrix4f, -f, 0, -f).setUv(0.0F, 1.0F).setColor(i);
        poseStack.popPose();

    }

}

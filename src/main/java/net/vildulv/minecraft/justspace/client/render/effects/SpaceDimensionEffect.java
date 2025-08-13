package net.vildulv.minecraft.justspace.client.render.effects;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.vildulv.minecraft.justspace.Config;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;


public class SpaceDimensionEffect extends DimensionSpecialEffects {

    private static final ResourceLocation SUN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");

    @Nullable
    private VertexBuffer skyBuffer;

    private VertexBuffer starBuffer;

    public SpaceDimensionEffect() {
        super(0, false, SkyType.NORMAL, false, true);
        createStars();
        createLightSky();
    }

    public Vec3 getBrightnessDependentFogColor(Vec3 pFogColor, float pBrightness) {
        return pFogColor.multiply(pBrightness * 0.94F + 0.06F, pBrightness * 0.94F + 0.06F, pBrightness * 0.91F + 0.09F);
    }

    public boolean isFoggyAt(int pX, int pY) {
        return false;
    }


    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        return true; // No clouds in space
    }

    private void createLightSky() {
        if (this.skyBuffer != null) {
            this.skyBuffer.close();
        }

        this.skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.skyBuffer.bind();
        this.skyBuffer.upload(buildSkyDisc(Tesselator.getInstance(), 16.0F));
        VertexBuffer.unbind();
    }

    private static MeshData buildSkyDisc(Tesselator tesselator, float y) {
        float f = Math.signum(y) * 512.0F;
        float f1 = 512.0F;
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        bufferbuilder.addVertex(0.0F, y, 0.0F);

        for (int i = -180; i <= 180; i += 45) {
            bufferbuilder.addVertex(f * Mth.cos((float) i * (float) (Math.PI / 180.0)), y, 512.0F * Mth.sin((float) i * (float) (Math.PI / 180.0)));
        }

        return bufferbuilder.buildOrThrow();
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable skyFogSetup) {


        skyFogSetup.run();
        PoseStack posestack = new PoseStack();
        posestack.mulPose(modelViewMatrix);

        Tesselator tesselator = Tesselator.getInstance();
        RenderSystem.setShaderColor(0F, 0F, 0F, 1.0F);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        renderSun(1F, posestack, tesselator);


        for (Config.PlanetRecord planet : Config.PARSED_PLANETS.values()) {
            renderPlanet(1F, null, posestack, tesselator, camera, planet);
        }
        renderStars(1F, posestack, projectionMatrix);


        return true;
    }



    private void renderSun(float alpha, PoseStack poseStack, Tesselator tesselator) {
        poseStack.pushPose();
        float f11 = 1.0F; // - level.getRainLevel(partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        //  posestack.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360.0F));
        Matrix4f matrix4f1 = poseStack.last().pose();
        float f12 = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_LOCATION);
        BufferBuilder bufferbuilder1 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder1.addVertex(matrix4f1, -f12, 100.0F, -f12).setUv(0.0F, 0.0F);
        bufferbuilder1.addVertex(matrix4f1, f12, 100.0F, -f12).setUv(1.0F, 0.0F);
        bufferbuilder1.addVertex(matrix4f1, f12, 100.0F, f12).setUv(1.0F, 1.0F);
        bufferbuilder1.addVertex(matrix4f1, -f12, 100.0F, f12).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferbuilder1.buildOrThrow());
        poseStack.popPose();
    }

    private void renderStars(float starBrightness, PoseStack poseStack, Matrix4f modelViewMatrix) {
        //  float f10 = this.level.getStarBrightness(partialTick) * f11;
        if (starBrightness > 0.0F) {
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            FogRenderer.setupNoFog();
            this.starBuffer.bind();
            this.starBuffer.drawWithShader(poseStack.last().pose(), modelViewMatrix, GameRenderer.getPositionShader());
            VertexBuffer.unbind();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.popPose();
    }

    private void createStars() {
        if (this.starBuffer != null) {
            this.starBuffer.close();
        }

        this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.starBuffer.bind();
        this.starBuffer.upload(this.drawStars(Tesselator.getInstance()));
        VertexBuffer.unbind();
    }

    private MeshData drawStars(Tesselator tesselator) {
        RandomSource randomsource = RandomSource.create(10842L);
        int i = 1500;
        float f = 100.0F;
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for (int j = 0; j < 1500; j++) {
            float f1 = randomsource.nextFloat() * 2.0F - 1.0F;
            float f2 = randomsource.nextFloat() * 2.0F - 1.0F;
            float f3 = randomsource.nextFloat() * 2.0F - 1.0F;
            float f4 = 0.15F + randomsource.nextFloat() * 0.1F;
            float f5 = Mth.lengthSquared(f1, f2, f3);
            if (!(f5 <= 0.010000001F) && !(f5 >= 1.0F)) {
                Vector3f vector3f = new Vector3f(f1, f2, f3).normalize(100.0F);
                float f6 = (float) (randomsource.nextDouble() * (float) Math.PI * 2.0);
                Quaternionf quaternionf = new Quaternionf().rotateTo(new Vector3f(0.0F, 0.0F, -1.0F), vector3f).rotateZ(f6);
                bufferbuilder.addVertex(vector3f.add(new Vector3f(f4, -f4, 0.0F).rotate(quaternionf)));
                bufferbuilder.addVertex(vector3f.add(new Vector3f(f4, f4, 0.0F).rotate(quaternionf)));
                bufferbuilder.addVertex(vector3f.add(new Vector3f(-f4, f4, 0.0F).rotate(quaternionf)));
                bufferbuilder.addVertex(vector3f.add(new Vector3f(-f4, -f4, 0.0F).rotate(quaternionf)));
            }
        }

        return bufferbuilder.buildOrThrow();
    }

    private void renderPlanet(float alpha, MultiBufferSource bufferSource, PoseStack poseStack, Tesselator tesselator, Camera camera, Config.PlanetRecord record) {

        float planetYPlane = 100.0F;
        float planetSize = 512F;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, record.texture());
        BufferBuilder bufferbuilder2 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);


        float f = 0F;

        double px = record.x();
        double pz = record.z();
        double cx = camera.getPosition().x;
        double cy = camera.getPosition().y;
        double cz = camera.getPosition().z;
        double dx = cx - px;
        double dy = cy + planetYPlane; //500 is the height of the planet
        double dz = cz - pz;
        double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
        if (distance > 512) {
            return; //Don't draw further than 500 blocks away
        }
        //     System.out.println("Distance to planet: " + distance);
        f = planetSize - (float) distance;
        //     System.out.println("size: " + f);

        poseStack.pushPose();
        Matrix4f matrix4f = poseStack.last().pose().translate(-(float) dx, -(float) dy, -(float) dz);


        bufferbuilder2.addVertex(matrix4f, -f, 0, f).setUv(0.0F, 0.0F);
        bufferbuilder2.addVertex(matrix4f, f, 0, f).setUv(1.0F, 0.0F);
        bufferbuilder2.addVertex(matrix4f, f, 0, -f).setUv(1.0F, 1.0F);
        bufferbuilder2.addVertex(matrix4f, -f, 0, -f).setUv(0.0F, 1.0F);

        BufferUploader.drawWithShader(bufferbuilder2.buildOrThrow());

        poseStack.popPose();


    }

}

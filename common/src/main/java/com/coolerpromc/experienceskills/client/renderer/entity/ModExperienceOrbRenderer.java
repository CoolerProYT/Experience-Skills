package com.coolerpromc.experienceskills.client.renderer.entity;

import com.coolerpromc.experienceskills.client.renderer.entity.state.ModExperienceOrbRenderState;
import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

public class ModExperienceOrbRenderer<T extends ModExperienceOrb> extends EntityRenderer<T, ModExperienceOrbRenderState> {
    private static final Identifier EXPERIENCE_ORB_LOCATION = Identifier.withDefaultNamespace("textures/entity/experience/experience_orb.png");
    private static final RenderType RENDER_TYPE = RenderTypes.entityTranslucentCullItemTarget(EXPERIENCE_ORB_LOCATION);

    public ModExperienceOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected int getBlockLightLevel(@NonNull T entity, @NonNull BlockPos blockPos) {
        return Mth.clamp(super.getBlockLightLevel(entity, blockPos) + 7, 0, 15);
    }

    public void submit(ModExperienceOrbRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        int icon = state.icon;

        int color = state.color;
        int baseR = (color >> 16) & 0xFF;
        int baseG = (color >> 8) & 0xFF;
        int baseB = color & 0xFF;

        float u0 = (float)(icon % 4 * 16) / 64.0F;
        float u1 = (float)(icon % 4 * 16 + 16) / 64.0F;
        float v0 = (float)(icon / 4 * 16) / 64.0F;
        float v1 = (float)(icon / 4 * 16 + 16) / 64.0F;

        float rr = state.ageInTicks / 2.0F;
        float pulse = (Mth.sin(rr) + 1.0F) * 0.5F;
        float brightness = 0.5F + 0.5F * pulse;

        int rc = (int)(baseR * brightness);
        int gc = (int)(baseG * brightness);
        int bc = (int)(baseB * brightness);

        poseStack.translate(0.0F, 0.1F, 0.0F);
        poseStack.mulPose(camera.orientation);
        poseStack.scale(0.3F, 0.3F, 0.3F);
        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, buffer) -> {
            vertex(buffer, pose, -0.5F, -0.25F, rc, gc, bc, u0, v1, state.lightCoords);
            vertex(buffer, pose, 0.5F, -0.25F, rc, gc, bc, u1, v1, state.lightCoords);
            vertex(buffer, pose, 0.5F, 0.75F, rc, gc, bc, u1, v0, state.lightCoords);
            vertex(buffer, pose, -0.5F, 0.75F, rc, gc, bc, u0, v0, state.lightCoords);
        });
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, int r, int g, int b, float u, float v, int lightCoords) {
        buffer.addVertex(pose, x, y, 0.0F).setColor(ARGB.color(128, r, g, b)).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightCoords).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public @NonNull ModExperienceOrbRenderState createRenderState() {
        return new ModExperienceOrbRenderState();
    }

    @Override
    public void extractRenderState(@NonNull T entity, @NonNull ModExperienceOrbRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.icon = entity.getIcon();
        state.color = entity.getExperienceType().color();
    }
}

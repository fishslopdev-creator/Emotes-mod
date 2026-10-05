package dev.freemotes.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.freemotes.client.cosmetic.Cosmetic;
import dev.freemotes.client.emote.PropCtx;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/** Draws equipped cosmetics and the current emote's props/VFX on players. */
public class FMLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {
    public FMLayer(RenderLayerParent<S, M> parent) {
        super(parent);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, S state, float yRot, float xRot) {
        EmoteSnapshot snap = ((EmoteRenderState) state).freemotes$getSnapshot();
        if (snap == null || snap.isEmpty()) return;
        M model = this.getParentModel();
        Draw draw = new Draw(poseStack, collector, packedLight);
        float lift = snap.pose() != null ? snap.pose().lift : 0f;

        for (Cosmetic cosmetic : snap.cosmetics()) {
            draw.resetMaterials();
            cosmetic.renderer().accept(new PropCtx(draw, model, snap.clockMs(), lift));
        }
        if (snap.emote() != null) {
            draw.resetMaterials();
            snap.emote().renderProps(new PropCtx(draw, model, snap.elapsedMs(), lift));
        }
    }
}

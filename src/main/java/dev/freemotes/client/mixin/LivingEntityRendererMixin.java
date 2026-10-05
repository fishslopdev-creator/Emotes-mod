package dev.freemotes.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.freemotes.client.CosmeticManager;
import dev.freemotes.client.EmoteManager;
import dev.freemotes.client.cosmetic.Cosmetic;
import dev.freemotes.client.emote.Emote;
import dev.freemotes.client.emote.Pose;
import dev.freemotes.client.render.EmoteRenderState;
import dev.freemotes.client.render.EmoteSnapshot;
import java.util.List;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    /** Snapshot the player's emote + cosmetics into the render state. */
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL"))
    private void freemotes$extract(LivingEntity entity, LivingEntityRenderState state, float partialTick, CallbackInfo ci) {
        EmoteSnapshot snapshot = null;
        if (entity instanceof Player player) {
            Emote emote = null;
            Pose pose = null;
            long elapsed = 0;
            EmoteManager.Active active = EmoteManager.get(player.getUUID());
            if (active != null && !player.isSleeping() && !player.isFallFlying() && !player.isSwimming()) {
                emote = active.emote;
                elapsed = Math.max(0, active.elapsed());
                pose = emote.poseAt(elapsed);
            }
            List<Cosmetic> cosmetics = player.isInvisible() ? List.of() : CosmeticManager.forPlayer(player.getUUID());
            snapshot = new EmoteSnapshot(emote, elapsed, pose, cosmetics, EmoteManager.now());
        }
        ((EmoteRenderState) state).freemotes$setSnapshot(snapshot);
    }

    /** Whole-body emote transforms: float, spin, flip. Snapped, never interpolated. */
    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V",
            at = @At("TAIL"))
    private void freemotes$bodyTransform(LivingEntityRenderState state, PoseStack poseStack, float bodyRot, float scale, CallbackInfo ci) {
        EmoteSnapshot snapshot = ((EmoteRenderState) state).freemotes$getSnapshot();
        if (snapshot == null || snapshot.pose() == null) return;
        Pose pose = snapshot.pose();
        if (pose.lift != 0) poseStack.translate(0, pose.lift, 0);
        if (pose.spin != 0) poseStack.mulPose(Axis.YP.rotationDegrees(-pose.spin));
        if (pose.pitch != 0 || pose.roll != 0) {
            poseStack.translate(0, 0.9, 0);
            if (pose.pitch != 0) poseStack.mulPose(Axis.XP.rotationDegrees(pose.pitch));
            if (pose.roll != 0) poseStack.mulPose(Axis.ZP.rotationDegrees(pose.roll));
            poseStack.translate(0, -0.9, 0);
        }
    }
}

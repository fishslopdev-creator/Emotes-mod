package dev.freemotes.client.mixin;

import dev.freemotes.client.render.EmotePoser;
import dev.freemotes.client.render.EmoteRenderState;
import dev.freemotes.client.render.EmoteSnapshot;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Overrides the vanilla limb animation with the emote's current keyframe (armor models follow too). */
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    private void freemotes$applyEmote(HumanoidRenderState state, CallbackInfo ci) {
        EmoteSnapshot snapshot = ((EmoteRenderState) state).freemotes$getSnapshot();
        if (snapshot != null && snapshot.pose() != null) {
            EmotePoser.apply((HumanoidModel<?>) (Object) this, snapshot.pose());
        }
    }
}

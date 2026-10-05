package dev.freemotes.client.mixin;

import dev.freemotes.client.render.EmoteRenderState;
import dev.freemotes.client.render.EmoteSnapshot;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements EmoteRenderState {
    @Unique
    private EmoteSnapshot freemotes$snapshot;

    @Override
    public EmoteSnapshot freemotes$getSnapshot() {
        return freemotes$snapshot;
    }

    @Override
    public void freemotes$setSnapshot(EmoteSnapshot snapshot) {
        this.freemotes$snapshot = snapshot;
    }
}

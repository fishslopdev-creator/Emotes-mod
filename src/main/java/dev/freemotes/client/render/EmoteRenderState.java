package dev.freemotes.client.render;

/** Duck interface mixed into LivingEntityRenderState. */
public interface EmoteRenderState {
    EmoteSnapshot freemotes$getSnapshot();

    void freemotes$setSnapshot(EmoteSnapshot snapshot);
}

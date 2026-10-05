package dev.freemotes.client.render;

import dev.freemotes.client.cosmetic.Cosmetic;
import dev.freemotes.client.emote.Emote;
import dev.freemotes.client.emote.Pose;
import java.util.List;

/** Everything the renderer needs about one player for one frame. {@code emote}/{@code pose} are null when not emoting. */
public record EmoteSnapshot(Emote emote, long elapsedMs, Pose pose, List<Cosmetic> cosmetics, long clockMs) {
    public boolean isEmpty() {
        return emote == null && cosmetics.isEmpty();
    }
}

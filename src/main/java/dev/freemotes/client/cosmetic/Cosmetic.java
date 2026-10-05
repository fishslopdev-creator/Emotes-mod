package dev.freemotes.client.cosmetic;

import dev.freemotes.client.emote.Emote.Rarity;
import dev.freemotes.client.emote.PropCtx;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;

/** A wearable cosmetic. One per slot can be equipped. Rendered as chunky voxels. */
public record Cosmetic(String id, String name, Slot slot, Rarity rarity, Item icon, String description, Consumer<PropCtx> renderer) {
    public enum Slot {
        HEAD("Hat"), FACE("Face"), BACK("Back");

        public final String label;

        Slot(String label) {
            this.label = label;
        }
    }
}

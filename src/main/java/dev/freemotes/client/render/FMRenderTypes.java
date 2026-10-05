package dev.freemotes.client.render;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/** The three render types all cosmetics and props are drawn with (on a plain white texture, colored per vertex). */
public final class FMRenderTypes {
    public static final Identifier WHITE = Identifier.fromNamespaceAndPath("freemotes", "textures/misc/white.png");

    private FMRenderTypes() {}

    /** Opaque, lit like the player. */
    public static RenderType solid() {
        return RenderTypes.entityCutoutNoCull(WHITE);
    }

    /** See-through, full bright. */
    public static RenderType ghost() {
        return RenderTypes.entityTranslucent(WHITE);
    }

    /** Additive glow (like spider eyes). */
    public static RenderType glow() {
        return RenderTypes.eyes(WHITE);
    }
}

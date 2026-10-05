package dev.freemotes.client.render;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/** The three render types all cosmetics and props are drawn with: the material atlas, tinted per vertex. */
public final class FMRenderTypes {
    /** 4x4 grid of 16px material tiles, see {@link Material} and tools/gen_textures.py. */
    public static final Identifier ATLAS = Identifier.fromNamespaceAndPath("freemotes", "textures/misc/materials.png");

    private FMRenderTypes() {}

    /** Opaque, lit like the player. */
    public static RenderType solid() {
        return RenderTypes.entityCutoutNoCull(ATLAS);
    }

    /** See-through, full bright. */
    public static RenderType ghost() {
        return RenderTypes.entityTranslucent(ATLAS);
    }

    /** Additive glow (like spider eyes). */
    public static RenderType glow() {
        return RenderTypes.eyes(ATLAS);
    }
}

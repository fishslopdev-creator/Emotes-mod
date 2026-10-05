package dev.freemotes.client.render;

/** A 16x16 tile in the material atlas. Tiles are grayscale; the box color tints them. */
public enum Material {
    PLAIN, WOOD, GOLD, METAL,
    CLOTH, FEATHER, SCALES, GEM,
    ENERGY, CLOUD, LEATHER, GRILLE,
    FUR, STRIPES, STARS, BLADE;

    static final float TILE = 0.25f;

    float u0() {
        return (ordinal() % 4) * TILE;
    }

    float v0() {
        return (ordinal() / 4) * TILE;
    }
}

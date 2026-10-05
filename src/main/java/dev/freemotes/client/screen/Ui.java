package dev.freemotes.client.screen;

import net.minecraft.client.gui.GuiGraphics;

/** Chunky pixel-art UI helpers. All colors are ARGB. */
final class Ui {
    private Ui() {}

    static void panel(GuiGraphics g, int x, int y, int w, int h, int fill, int border) {
        g.fill(x, y, x + w, y + h, border);
        g.fill(x + 2, y + 2, x + w - 2, y + h - 2, fill);
        // bevel
        g.fill(x + 2, y + 2, x + w - 2, y + 3, lighten(fill, 28));
        g.fill(x + 2, y + h - 3, x + w - 2, y + h - 2, darken(fill, 28));
    }

    static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }

    static int lighten(int argb, int amount) {
        return shift(argb, amount);
    }

    static int darken(int argb, int amount) {
        return shift(argb, -amount);
    }

    private static int shift(int argb, int amount) {
        int a = argb >>> 24;
        int r = clamp(((argb >> 16) & 0xFF) + amount);
        int gr = clamp(((argb >> 8) & 0xFF) + amount);
        int b = clamp((argb & 0xFF) + amount);
        return (a << 24) | (r << 16) | (gr << 8) | b;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    static String price(int amount) {
        return String.format("%,d", amount);
    }
}

package dev.freemotes.client.cosmetic;

import dev.freemotes.client.cosmetic.Cosmetic.Slot;
import dev.freemotes.client.emote.Emote.Rarity;
import dev.freemotes.client.emote.PropCtx;
import dev.freemotes.client.render.Draw;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * All cosmetics. Head cosmetics draw relative to the head pivot (the neck); the head cube spans
 * x/z -4..4 and y -8..0 in pixels, -z is the face. Back cosmetics draw relative to the body (z = +2 is the back).
 */
public final class Cosmetics {
    private static final Map<String, Cosmetic> BY_ID = new LinkedHashMap<>();
    public static final List<Cosmetic> ALL;

    private Cosmetics() {}

    public static Cosmetic get(String id) {
        return BY_ID.get(id);
    }

    private static void reg(String id, String name, Slot slot, Rarity rarity, Item icon, String desc, Consumer<PropCtx> draw) {
        Consumer<PropCtx> wrapped = ctx -> {
            if (slot == Slot.BACK) ctx.body();
            else ctx.head();
            draw.accept(ctx);
            ctx.end();
        };
        BY_ID.put(id, new Cosmetic(id, name, slot, rarity, icon, desc, wrapped));
    }

    static {
        // ------------------------------------------------------------------ HATS
        reg("top_hat", "Top Hat", Slot.HEAD, Rarity.COMMON, Items.BLACK_WOOL, "Very distinguished. Very tall.", c -> {
            Draw d = c.d;
            d.box(-6, -8.7f, -6, 6, -8f, 6, 0xFF151515);
            d.box(-4, -16, -4, 4, -8.7f, 4, 0xFF1B1B1B);
            d.box(-4.15f, -10.5f, -4.15f, 4.15f, -8.7f, 4.15f, 0xFFB01020);
        });

        reg("party_hat", "Party Hat", Slot.HEAD, Rarity.COMMON, Items.CAKE, "Every day is a party.", c -> {
            Draw d = c.d;
            int[] cols = {0xFFFF4FA0, 0xFF40D0FF, 0xFFFFE040, 0xFF60FF70, 0xFFFF4FA0};
            for (int i = 0; i < 5; i++) {
                float r = 3.6f - i * 0.75f;
                d.box(-r, -10 - i * 1.6f, -r, r, -8 - i * 1.6f, r, cols[i]);
            }
            int pom = c.step(400) % 2 == 0 ? 0xFFFFFFFF : 0xFFFFF0A0;
            d.box(-0.9f, -17.6f, -0.9f, 0.9f, -15.8f, 0.9f, pom);
        });

        reg("propeller", "Propeller Cap", Slot.HEAD, Rarity.RARE, Items.LIGHT_WEIGHTED_PRESSURE_PLATE, "Does not let you fly. Spins anyway.", c -> {
            Draw d = c.d;
            int[] cols = {0xFFE02020, 0xFFFFD020, 0xFF2060E0, 0xFF20B040};
            for (int i = 0; i < 4; i++) {
                d.push();
                d.rotY(i * 90);
                d.box(-4.3f, -8.8f, -4.3f, 0, -7.5f, 0, cols[i]);
                d.pop();
            }
            d.box(-4.3f, -8f, -7.5f, 4.3f, -7.5f, -4.3f, 0xFFE02020); // visor
            d.box(-0.4f, -11, -0.4f, 0.4f, -8.8f, 0.4f, 0xFF888888);
            d.push();
            d.move(0, -11.2f, 0);
            d.rotY(c.step(50) * 30f);
            d.box(-6, -0.4f, -0.9f, 6, 0.4f, 0.9f, 0xFFDDDDDD);
            d.box(-0.9f, -0.4f, -6, 0.9f, 0.4f, 6, 0xFFDDDDDD);
            d.pop();
        });

        reg("wizard_hat", "Wizard Hat", Slot.HEAD, Rarity.EPIC, Items.ENCHANTED_BOOK, "A wizard is never late.", c -> {
            Draw d = c.d;
            int blue = 0xFF2A2F9C;
            d.box(-6.5f, -8.8f, -6.5f, 6.5f, -8f, 6.5f, blue);
            float[] r = {4.2f, 3.4f, 2.6f, 1.9f, 1.3f};
            float x = 0;
            for (int i = 0; i < 5; i++) {
                x += i >= 3 ? 1.2f : 0;
                d.box(x - r[i], -11 - i * 2.2f, -r[i], x + r[i], -8.8f - i * 2.2f, r[i], blue);
            }
            d.box(x + 0.5f, -20.6f, -0.7f, x + 2.6f, -19.4f, 0.7f, blue);
            d.box(-4.3f, -10, -4.3f, 4.3f, -8.8f, 4.3f, 0xFFE8C030);
            boolean tw = c.step(300) % 2 == 0;
            d.glow(-2, tw ? -13 : -14, -3.5f, -1, tw ? -12 : -13, -3.3f, 0xFFFFF070);
            d.glow(1.5f, tw ? -15 : -16, -2.8f, 2.3f, tw ? -14.2f : -15.2f, -2.6f, 0xFFFFF070);
        });

        reg("viking", "Viking Helmet", Slot.HEAD, Rarity.RARE, Items.IRON_HELMET, "Raid the village. Politely.", c -> {
            Draw d = c.d;
            d.box(-4.6f, -9, -4.6f, 4.6f, -5, 4.6f, 0xFF8D8D95);
            d.box(-3.5f, -10, -3.5f, 3.5f, -9, 3.5f, 0xFF9D9DA5);
            d.box(-0.6f, -5, -4.9f, 0.6f, -2, -4.6f, 0xFF8D8D95); // nose guard
            d.box(-4.7f, -6, -4.7f, 4.7f, -5, 4.7f, 0xFF7A5A2A);
            for (int s = -1; s <= 1; s += 2) {
                d.box(s * 4.6f, -8, -1, s * 6.6f, -6.5f, 1, 0xFFF0E8D0);
                d.box(s * 6, -11, -0.9f, s * 7.4f, -8, 0.9f, 0xFFF0E8D0);
                d.box(s * 6.3f, -13, -0.7f, s * 7.3f, -11, 0.7f, 0xFFE0D8C0);
            }
        });

        reg("crown", "Royal Crown", Slot.HEAD, Rarity.LEGENDARY, Items.GOLDEN_HELMET, "Heavy is the head. Sparkly too.", c -> {
            Draw d = c.d;
            int gold = 0xFFFFC21A;
            d.box(-4.6f, -10, -4.6f, 4.6f, -8, 4.6f, gold);
            for (int i = -1; i <= 1; i++) {
                float o = i * 3.4f;
                float h = i == 0 ? -12.5f : -11.5f;
                d.box(o - 0.8f, h, -4.6f, o + 0.8f, -10, -3.9f, gold);
                d.box(o - 0.8f, h, 3.9f, o + 0.8f, -10, 4.6f, gold);
                d.box(-4.6f, h, o - 0.8f, -3.9f, -10, o + 0.8f, gold);
                d.box(3.9f, h, o - 0.8f, 4.6f, -10, o + 0.8f, gold);
            }
            d.glow(-0.9f, -9.6f, -4.95f, 0.9f, -8.4f, -4.6f, c.step(400) % 2 == 0 ? 0xFFFF2050 : 0xFFFF6080);
            d.glow(-4.95f, -9.6f, -0.9f, -4.6f, -8.4f, 0.9f, 0xFF30A0FF);
            d.glow(4.6f, -9.6f, -0.9f, 4.95f, -8.4f, 0.9f, 0xFF30FF80);
        });

        reg("halo", "Halo", Slot.HEAD, Rarity.EPIC, Items.GOLD_NUGGET, "Totally innocent. Definitely.", c -> {
            Draw d = c.d;
            float y = -12f + (c.step(500) % 2 == 0 ? 0 : -0.7f);
            for (int i = 0; i < 12; i++) {
                d.push();
                d.rotY(i * 30f);
                d.glow(-1.2f, y - 0.5f, 4.2f, 1.2f, y + 0.5f, 5.2f, 0xFFFFE070);
                d.pop();
            }
        });

        reg("cat_ears", "Cat Ears", Slot.HEAD, Rarity.COMMON, Items.STRING, "Nya.", c -> {
            Draw d = c.d;
            int fur = 0xFF303030, pink = 0xFFFF9EC0;
            boolean twitch = c.ms % 3000 < 150;
            for (int s = -1; s <= 1; s += 2) {
                float tw = (twitch && s > 0) ? -0.6f : 0f;
                d.box(s * 1.2f, -9 + tw, -1, s * 4f, -8, 0.6f, fur);
                d.box(s * 1.8f, -10 + tw, -1, s * 3.6f, -9 + tw, 0.6f, fur);
                d.box(s * 2.4f, -11 + tw, -1, s * 3.2f, -10 + tw, 0.6f, fur);
                d.box(s * 2.0f, -9.5f + tw, -1.1f, s * 3.4f, -8.2f, -0.9f, pink);
            }
        });

        reg("devil_horns", "Devil Horns", Slot.HEAD, Rarity.RARE, Items.NETHER_WART, "A little bit evil.", c -> {
            Draw d = c.d;
            for (int s = -1; s <= 1; s += 2) {
                d.box(s * 1.5f, -9, -2.5f, s * 3.5f, -8, -0.5f, 0xFFB01010);
                d.box(s * 2.2f, -10.5f, -2.2f, s * 3.8f, -9, -0.8f, 0xFFC82020);
                d.box(s * 3.0f, -11.8f, -1.9f, s * 4.2f, -10.5f, -1.0f, 0xFFE03030);
                d.box(s * 3.6f, -12.8f, -1.7f, s * 4.4f, -11.8f, -1.2f, 0xFFFF5050);
            }
        });

        reg("chef_hat", "Chef Hat", Slot.HEAD, Rarity.COMMON, Items.BREAD, "Let him cook.", c -> {
            Draw d = c.d;
            d.box(-4.3f, -10.5f, -4.3f, 4.3f, -7.8f, 4.3f, 0xFFF4F4F4);
            d.box(-5.2f, -14, -5.2f, 5.2f, -10.5f, 5.2f, 0xFFFFFFFF);
            d.box(-3.5f, -15, -3.5f, 3.5f, -14, 3.5f, 0xFFF8F8F8);
        });

        // ------------------------------------------------------------------ FACE
        reg("shades", "Deal-With-It Shades", Slot.FACE, Rarity.RARE, Items.TINTED_GLASS, "Pixelated. Obviously.", c -> {
            Draw d = c.d;
            int black = 0xFF080808;
            d.box(-4.4f, -5, -4.5f, 4.4f, -4.2f, -4.1f, black);
            d.box(-3.6f, -4.2f, -4.5f, -0.6f, -2.6f, -4.1f, black);
            d.box(0.6f, -4.2f, -4.5f, 3.6f, -2.6f, -4.1f, black);
            d.glow(-3.2f, -4.0f, -4.6f, -2.5f, -3.3f, -4.45f, 0xFF606060);
            d.glow(1.0f, -4.0f, -4.6f, 1.7f, -3.3f, -4.45f, 0xFF606060);
            d.box(-4.5f, -5, -4.1f, -4.1f, -4.2f, 0, black);
            d.box(4.1f, -5, -4.1f, 4.5f, -4.2f, 0, black);
        });

        reg("mustache", "Fancy Mustache", Slot.FACE, Rarity.COMMON, Items.BROWN_WOOL, "Sophistication intensifies.", c -> {
            Draw d = c.d;
            int brown = 0xFF4A2C14;
            d.box(-2.5f, -2.6f, -4.5f, 2.5f, -1.8f, -4.1f, brown);
            d.box(-3.6f, -2.2f, -4.5f, -2.5f, -1.4f, -4.1f, brown);
            d.box(2.5f, -2.2f, -4.5f, 3.6f, -1.4f, -4.1f, brown);
            d.box(-4.2f, -2.8f, -4.5f, -3.6f, -2.0f, -4.1f, brown);
            d.box(3.6f, -2.8f, -4.5f, 4.2f, -2.0f, -4.1f, brown);
        });

        reg("visor", "Cyber Visor", Slot.FACE, Rarity.EPIC, Items.CYAN_STAINED_GLASS, "Scanning... target is cringe.", c -> {
            Draw d = c.d;
            d.box(-4.5f, -5.2f, -4.6f, 4.5f, -2.8f, -4.1f, 0xFF1A1A22);
            long s = c.step(90) % 14;
            float x = s < 7 ? -3.5f + s : 3.5f - (s - 7);
            d.glow(-4.2f, -4.4f, -4.75f, 4.2f, -3.6f, -4.6f, 0xFF105060);
            d.glow(x - 0.8f, -4.5f, -4.8f, x + 0.8f, -3.5f, -4.6f, 0xFFFF2040);
        });

        // ------------------------------------------------------------------ BACK
        reg("angel_wings", "Angel Wings", Slot.BACK, Rarity.EPIC, Items.FEATHER, "Fluffy, flappy, holy.", c -> {
            wings(c, 0xFFFFFFFF, 0xFFE8EEF8, 0xFFD0D8E8, false);
        });

        reg("dragon_wings", "Dragon Wings", Slot.BACK, Rarity.LEGENDARY, Items.DRAGON_HEAD, "Ender dragon cosplay.", c -> {
            wings(c, 0xFF2A1238, 0xFF3D1A52, 0xFF8A3CC0, true);
        });

        reg("backpack", "Adventurer Pack", Slot.BACK, Rarity.COMMON, Items.BUNDLE, "Holds exactly zero extra items.", c -> {
            Draw d = c.d;
            d.box(-3.5f, 1, 2, 3.5f, 10, 5, 0xFF7A4E2A);
            d.box(-3.6f, 1, 1.9f, 3.6f, 3, 5.1f, 0xFF5E3B1E);
            d.box(-2.5f, 5, 5, 2.5f, 9, 6, 0xFF8E5E34);
            d.box(-0.5f, 2.5f, 5.1f, 0.5f, 3.5f, 5.3f, 0xFFE0C040);
            d.box(-4, 0, -2.2f, -3, 8, 2, 0xFF5E3B1E);
            d.box(3, 0, -2.2f, 4, 8, 2, 0xFF5E3B1E);
        });

        reg("jetpack", "Jetpack", Slot.BACK, Rarity.LEGENDARY, Items.FIRE_CHARGE, "Flames included. Flight not.", c -> {
            Draw d = c.d;
            for (int s = -1; s <= 1; s += 2) {
                d.box(s * 0.5f, 1, 2, s * 4, 10, 5.5f, 0xFFB8BCC4);
                d.box(s * 0.7f, 0, 2.3f, s * 3.8f, 1, 5.2f, 0xFFE03020);
                d.box(s * 1.2f, 10, 2.6f, s * 3.3f, 11.5f, 4.9f, 0xFF4A4A50);
                boolean big = (c.step(60) + (s > 0 ? 1 : 0)) % 2 == 0;
                float len = big ? 5.5f : 3.5f;
                float cx = s * 2.25f;
                d.glow(cx - 0.9f, 11.5f, 2.85f, cx + 0.9f, 11.5f + len, 4.65f, 0xFFFF7A10);
                d.glow(cx - 0.45f, 11.5f, 3.3f, cx + 0.45f, 11.5f + len * 0.6f, 4.2f, 0xFFFFF070);
            }
            d.box(-0.5f, 3, 2, 0.5f, 8, 4, 0xFF6A6E76);
        });
        ALL = Collections.unmodifiableList(new ArrayList<>(BY_ID.values()));
    }

    /** Feathered / membrane wings that flap in two snapped positions. */
    private static void wings(PropCtx c, int outer, int mid, int inner, boolean membrane) {
        Draw d = c.d;
        boolean up = c.step(400) % 2 == 0;
        for (int side = -1; side <= 1; side += 2) {
            d.push();
            d.move(side * 1.5f, 2, 2.5f);
            d.rotY(side > 0 ? -(up ? 20 : 35) : 180 + (up ? 20 : 35));
            d.rotZ(up ? -20 : -5);
            d.box(0, -1, -0.5f, 14, 1.5f, 0.5f, outer);          // arm bone / leading edge
            if (membrane) {
                for (int i = 0; i < 4; i++) {
                    float x0 = i * 3.5f;
                    d.box(x0, 1.5f, -0.25f, x0 + 3.5f, 1.5f + 9 - i * 2, 0.25f, i % 2 == 0 ? mid : inner);
                }
                d.box(13, -2.5f, -0.4f, 15, -1, 0.4f, 0xFFE0E0E0); // claw
            } else {
                for (int i = 0; i < 5; i++) {
                    float x0 = i * 2.8f;
                    d.box(x0, 1.5f, -0.4f, x0 + 2.8f, 1.5f + 11 - i * 1.8f, 0.4f, i % 2 == 0 ? mid : inner);
                }
            }
            d.pop();
        }
    }
}

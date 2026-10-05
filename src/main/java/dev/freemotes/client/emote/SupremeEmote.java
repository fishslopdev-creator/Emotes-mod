package dev.freemotes.client.emote;

import dev.freemotes.client.emote.Emote.Rarity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;

/**
 * SUPREME: ASCENSION. Charge, detonate, rise into a pillar of light with a crown, halo rings, orbiting
 * crystals, energy wings and a rune circle, unleash a shockwave, then slam back down. ~10.4 seconds of excess.
 */
final class SupremeEmote {
    private static final int GOLD = 0xFFFFC21A;
    private static final int VOID = 0xFF9B30FF;
    private static final int CYAN = 0xFF30F0FF;
    private static final int WHITE = 0xFFFFFFFF;

    static final long CHARGE_END = 900, RISE_END = 2100, SHOCKWAVE = 5000, FINALE = 8200, SLAM = 9200, END_SLAM = 9600;

    private SupremeEmote() {}

    static Emote create() {
        return Emote.create("supreme", "SUPREME: Ascension", Rarity.SUPREME, Items.NETHER_STAR,
                        "Over the top. Crown, halo, light pillar, wings, crystals, shockwave. You will be seen.")
                // --- CHARGE: deep crouch, shaking with power (two alternating frames)
                .frames(e -> {
                    for (int i = 0; i < 15; i++) {
                        float shake = i % 2 == 0 ? 0.6f : -0.6f;
                        e.frame(60, p -> p.squat(5).bodyPos(shake, 0, 0).headPos(shake, 5, 0)
                                .rArm(40, 0, 35).lArm(40, 0, -35).head(30, 0, 0).body(25, 0, 0));
                    }
                })
                // --- RISE: 6 snapping steps upward, arms spreading
                .frames(e -> {
                    float[] lift = {0.25f, 0.5f, 0.8f, 1.1f, 1.4f, 1.6f};
                    for (int i = 0; i < 6; i++) {
                        float l = lift[i];
                        float spread = 20 + i * 12;
                        e.frame(200, p -> p.lift(l).rArm(-10, 0, spread).lArm(-10, 0, -spread).head(-20, 0, 0)
                                .rLeg(10, 0, 4).lLeg(10, 0, -4));
                    }
                })
                // --- ASCENDED: hover with a stepped bob, slow 15-degree stepped spin, alternating power poses
                .frames(e -> {
                    int frames = hoverFor(e, 0, SHOCKWAVE - RISE_END);
                    // shockwave thrust
                    e.frame(150, p -> p.lift(1.6f).squat(2).rArm(-90, 0, 90).lArm(-90, 0, -90).head(-10, 0, 0));
                    e.frame(450, p -> p.lift(1.7f).rArm(0, 0, 100).lArm(0, 0, -100).head(-30, 0, 0).rLeg(0, 0, 15).lLeg(0, 0, -15));
                    hoverFor(e, frames, FINALE - SHOCKWAVE - 600);
                })
                // --- FINALE: everything goes off, arms to the sky
                .frame(150, p -> p.lift(1.8f).squat(3).rArm(30, 0, 30).lArm(30, 0, -30).head(20, 0, 0))
                .frame(850, p -> p.lift(2.0f).rArm(-180, 0, 8).lArm(-180, 0, -8).head(-40, 0, 0).rLeg(0, 0, 6).lLeg(0, 0, -6))
                // --- SLAM: drop down in two frames
                .frame(100, p -> p.lift(1.2f).pitch(-15).rArm(-200, 0, 10).lArm(-200, 0, -10).squat(2))
                .frame(100, p -> p.lift(0.5f).pitch(-25).rArm(-230, 0, 10).lArm(-230, 0, -10).squat(3))
                .frame(400, p -> p.squat(6).pitch(-20).rArm(-30, 0, 20).lArm(0, 0, -50).head(-10, 0, 0).rLeg(-90, 0, 0).lLeg(-10, 0, -20))
                // --- POSE: stand up, arms crossed, look smug
                .frame(1000, p -> p.rArm(-75, 40, 0).lArm(-75, -40, 0).head(-12, 15, 0))
                .effects(SupremeEmote::effects)
                .props(SupremeEmote::props);
    }

    /** Adds hover frames filling exactly {@code ms}; returns how many frames were added. */
    private static int hoverFor(Emote e, int startIndex, long ms) {
        int count = 0;
        while (ms > 0) {
            int len = (int) Math.min(125, ms);
            addHover(e, startIndex + count, len);
            ms -= len;
            count++;
        }
        return count;
    }

    private static void addHover(Emote e, int i, int ms) {
        float bob = (i / 2) % 2 == 0 ? 1.55f : 1.7f;
        float spin = i * 15f;
        boolean alt = (i / 6) % 2 == 0;
        e.frame(ms, p -> {
            p.lift(bob).spin(spin).rLeg(15, 0, 6).lLeg(5, 0, -6).head(-15, 0, 0);
            if (alt) p.rArm(-150, 0, 35).lArm(-150, 0, -35);
            else p.rArm(-90, -20, 70).lArm(-90, 20, -70);
            return p;
        });
    }

    // ------------------------------------------------------------------------------------------ particles + sound
    private static void effects(Fx fx) {
        long t = fx.now;
        double up = fx.lift();

        if (fx.at(0)) {
            fx.sound(SoundEvents.WARDEN_SONIC_CHARGE, 1.0f, 1.2f);
            fx.sound(SoundEvents.BEACON_POWER_SELECT, 1.0f, 0.6f);
        }
        if (t < CHARGE_END) {
            // energy sucked inward + a tightening dust spiral on the floor
            for (int i = 0; i < 4; i++) {
                double a = fx.random.nextDouble() * Math.PI * 2;
                double r = 2.5 + fx.random.nextDouble();
                fx.particle(ParticleTypes.REVERSE_PORTAL, Math.cos(a) * r, 0.2 + fx.random.nextDouble() * 2, Math.sin(a) * r,
                        -Math.cos(a) * r * 0.08, 0, -Math.sin(a) * r * 0.08);
            }
            double rad = 2.0 - t / 600.0;
            fx.spiral(new DustParticleOptions(0x9B30FF, 1.6f), 0.1, rad, t * 0.8, 0);
            fx.spiral(new DustParticleOptions(0xFFC21A, 1.6f), 0.1, rad, t * 0.8 + 180, 0);
        }
        if (fx.at(CHARGE_END)) {
            fx.sound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.2f, 0.8f);
            fx.sound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1.0f, 1.0f);
            fx.ring(ParticleTypes.EXPLOSION, 0.3, 1.8, 10, 0, 0);
            fx.ring(ParticleTypes.CLOUD, 0.1, 0.6, 30, 0.45, 0.02);
            fx.burst(ParticleTypes.ELECTRIC_SPARK, 1.0, 60, 0.6);
            for (int i = 0; i < 30; i++) fx.particle(ParticleTypes.END_ROD, 0, i * 0.5, 0, 0, 0.3, 0);
        }
        if (fx.at(RISE_END)) {
            fx.sound(SoundEvents.BEACON_ACTIVATE, 1.5f, 0.8f);
            fx.sound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5f, 0.5f);
        }
        if (t >= CHARGE_END && t < SLAM) {
            // the aura: twin dust helix, soul fire crown at the feet, end rods streaming up, sparks crackling
            double base = up + 0.1;
            for (int k = 0; k < 2; k++) {
                double a = t * 0.9 + k * 180;
                double h = (t % 1000) / 1000.0 * 2.2;
                fx.spiral(new DustParticleOptions(k == 0 ? 0x9B30FF : 0xFFC21A, 1.4f), base + h, 0.9, a, 0.02);
            }
            if (fx.every(150)) fx.ring(ParticleTypes.SOUL_FIRE_FLAME, base - 0.1, 0.7, 10, 0.01, 0.04);
            fx.particle(ParticleTypes.END_ROD, (fx.random.nextDouble() - 0.5) * 1.6, base, (fx.random.nextDouble() - 0.5) * 1.6, 0, 0.15, 0);
            if (fx.random.nextInt(2) == 0) {
                fx.particle(ParticleTypes.ELECTRIC_SPARK, (fx.random.nextDouble() - 0.5) * 1.8, base + fx.random.nextDouble() * 2.2,
                        (fx.random.nextDouble() - 0.5) * 1.8, 0, 0, 0);
            }
            if (t >= RISE_END && fx.every(100)) {
                // rain of light from the pillar + ground rune sparks
                fx.particle(ParticleTypes.END_ROD, (fx.random.nextDouble() - 0.5) * 0.6, 12, (fx.random.nextDouble() - 0.5) * 0.6, 0, -0.6, 0);
                fx.ring(ParticleTypes.WITCH, 0.05, 2.0, 8, 0, 0.05);
            }
            if (t >= RISE_END && fx.every(1000)) fx.sound(SoundEvents.BEACON_AMBIENT, 1.0f, 1.3f);
            if (t >= RISE_END && fx.random.nextInt(20) == 0) fx.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0f, 0.5f + fx.random.nextFloat());
        }
        if (fx.at(SHOCKWAVE)) {
            fx.sound(SoundEvents.WARDEN_SONIC_BOOM, 1.5f, 0.9f);
            fx.sound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0f, 1.4f);
            fx.ring(ParticleTypes.SONIC_BOOM, up + 1.0, 1.5, 8, 0, 0);
            fx.ring(ParticleTypes.EXPLOSION, 0.3, 3.0, 14, 0, 0);
            fx.ring(ParticleTypes.CLOUD, up + 1.0, 0.6, 50, 0.8, 0);
            fx.ring(ParticleTypes.END_ROD, up + 1.0, 0.6, 40, 0.6, 0);
            fx.burst(ParticleTypes.TOTEM_OF_UNDYING, up + 1.0, 80, 0.7);
        }
        if (fx.at(FINALE)) {
            fx.sound(SoundEvents.END_PORTAL_SPAWN, 1.0f, 1.2f);
            fx.sound(SoundEvents.TOTEM_USE, 1.2f, 0.8f);
            fx.sound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            fx.burst(ParticleTypes.TOTEM_OF_UNDYING, up + 1.2, 200, 1.0);
            fx.burst(ParticleTypes.FIREWORK, up + 1.2, 80, 0.6);
            fx.burst(ParticleTypes.END_ROD, up + 1.2, 60, 0.5);
            for (int i = 0; i < 40; i++) fx.particle(ParticleTypes.FIREWORK, 0, up + 2 + i * 0.4, 0, 0, 0.4, 0);
        }
        if (t >= FINALE && t < SLAM && fx.every(100)) {
            for (int i = 0; i < 6; i++) {
                double a = fx.random.nextDouble() * Math.PI * 2;
                fx.particle(ParticleTypes.TOTEM_OF_UNDYING, Math.cos(a) * 3, 6 + fx.random.nextDouble() * 3, Math.sin(a) * 3, 0, -0.2, 0);
            }
        }
        if (fx.at(SLAM + 200)) {
            fx.sound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1.2f, 0.7f);
            fx.sound(SoundEvents.ANVIL_LAND, 0.6f, 0.5f);
            fx.ring(ParticleTypes.EXPLOSION, 0.2, 1.2, 8, 0, 0);
            fx.ring(ParticleTypes.CLOUD, 0.1, 0.5, 40, 0.6, 0.03);
            fx.ring(ParticleTypes.SOUL_FIRE_FLAME, 0.1, 0.5, 40, 0.35, 0.05);
            fx.burst(ParticleTypes.ELECTRIC_SPARK, 0.3, 50, 0.6);
        }
        if (t >= END_SLAM && fx.every(200)) {
            fx.particle(ParticleTypes.SMOKE, (fx.random.nextDouble() - 0.5), 0.1, (fx.random.nextDouble() - 0.5), 0, 0.03, 0);
        }
    }

    // ------------------------------------------------------------------------------------------ objects
    private static void props(PropCtx c) {
        long t = c.ms;
        boolean powered = t >= CHARGE_END && t < SLAM;

        // crown hovers above the head the whole time
        c.head();
        float crownBob = c.step(250) % 2 == 0 ? 0 : -0.6f;
        c.d.move(0, -10.5f + crownBob, 0);
        crown(c);
        c.end();

        c.root();
        float ground = c.groundY();

        // rune circle on the ground (grows during the charge, spins in 15 degree snaps)
        if (t < END_SLAM) {
            float grow = t < CHARGE_END ? Math.min(1f, (c.step(150) + 1) / 6f) : 1f;
            c.d.push();
            c.d.move(0, ground - 0.3f, 0);
            c.d.rotY(c.step(100) * 15f);
            segmentRing(c, 30 * grow, 24, 2.2f, 0.4f, VOID);
            c.d.rotY(-c.step(100) * 30f);
            segmentRing(c, 20 * grow, 12, 1.6f, 0.4f, GOLD);
            for (int i = 0; i < 4; i++) {
                c.d.push();
                c.d.rotY(i * 90f + 45f);
                c.d.glow(-0.8f, -0.2f, 6 * grow, 0.8f, 0.2f, 28 * grow, 0xFF6020B0); // spokes
                c.d.pop();
            }
            c.d.pop();
        }

        // pillar of light (from the ground into the sky) while ascended
        // (split above the crown and below the feet so the player stays visible inside it)
        if (t >= RISE_END && t < SLAM) {
            float w = c.step(100) % 2 == 0 ? 4f : 5.5f;
            pillar(c, -16f, -400f, w, ground);
        }
        // detonation flash column
        if (t >= CHARGE_END && t < CHARGE_END + 200) {
            pillar(c, -16f, -200f, 3f, ground);
        }

        if (powered) {
            float center = 12f;

            // three halo rings tilted on different axes, each snapping round at its own rhythm
            c.d.push();
            c.d.move(0, center, 0);
            c.d.rotY(c.step(90) * 15f);
            c.d.rotX(70);
            segmentRing(c, 18, 16, 1.4f, 1.4f, GOLD);
            c.d.pop();
            c.d.push();
            c.d.move(0, center, 0);
            c.d.rotZ(c.step(120) * -22.5f);
            c.d.rotY(60);
            c.d.rotX(20);
            segmentRing(c, 21, 16, 1.1f, 1.1f, CYAN);
            c.d.pop();
            c.d.push();
            c.d.move(0, center, 0);
            c.d.rotX(c.step(150) * 30f);
            c.d.rotZ(50);
            segmentRing(c, 24, 20, 1.0f, 1.0f, VOID);
            c.d.pop();

            // orbiting crystals once risen
            if (t >= RISE_END) {
                long s = c.step(80);
                for (int i = 0; i < 8; i++) {
                    float a = (float) Math.toRadians(s * 11.25 + i * 45);
                    float bob = ((s + i) / 3) % 2 == 0 ? -2f : 2f;
                    c.d.push();
                    c.d.move((float) Math.cos(a) * 30, center + bob, (float) Math.sin(a) * 30);
                    c.d.rotY(s * 30f);
                    c.d.rotZ(45);
                    c.d.rotX(45);
                    int col = i % 2 == 0 ? CYAN : 0xFFFF5AD7;
                    c.d.glowCube(0, 0, 0, 2.4f, col);
                    c.d.pop();
                }
            }

            // flickering aura flames: thin glow columns around the body whose heights jump every 80ms
            long fs = c.step(80);
            for (int i = 0; i < 10; i++) {
                float a = (float) Math.toRadians(i * 36);
                int hash = (int) ((fs * 31 + i * 17) * 2654435761L >>> 26) & 15;
                float h = 10 + hash;
                float x = (float) Math.cos(a) * 9.5f, z = (float) Math.sin(a) * 9.5f;
                int col = i % 2 == 0 ? 0xFF7A2BFF : 0xFFFFB020;
                c.d.glow(x - 0.7f, 24 - h, z - 0.7f, x + 0.7f, 24, z + 0.7f, col);
            }
        }

        // shockwave: expanding ring in 6 jumps
        if (t >= SHOCKWAVE && t < SHOCKWAVE + 600) {
            int stage = (int) ((t - SHOCKWAVE) / 100);
            c.d.push();
            c.d.move(0, 12, 0);
            segmentRing(c, 20 + stage * 18, 32, 3f, 1.2f, stage % 2 == 0 ? WHITE : CYAN);
            c.d.pop();
        }
        // slam crater ring
        if (t >= SLAM + 200 && t < SLAM + 600) {
            int stage = (int) ((t - SLAM - 200) / 100);
            c.d.push();
            c.d.move(0, ground - 0.5f, 0);
            segmentRing(c, 10 + stage * 12, 24, 3f, 0.6f, GOLD);
            c.d.pop();
        }
        c.end();

        // energy wings unfurl in 4 snaps, then flap between two angles
        if (t >= CHARGE_END + 300 && t < SLAM) {
            float open = Math.min(1f, (c.step(100) - (CHARGE_END + 300) / 100 + 1) / 4f);
            float flap = c.step(250) % 2 == 0 ? 25f : 40f;
            c.body();
            c.d.move(0, 3, 2.5f);
            wing(c, open, flap, 1);
            wing(c, open, flap, -1);
            c.end();
        }
    }

    /** Beam of light from {@code top} up to {@code sky}, plus a stub under the feet down to the ground. */
    private static void pillar(PropCtx c, float top, float sky, float w, float ground) {
        c.d.glow(-w * 0.45f, sky, -w * 0.45f, w * 0.45f, top, w * 0.45f, 0xFFFFF2B0);
        c.d.glow(-w, sky, -w, w, top, w, 0xFF4A1A80);
        if (ground > 24.5f) {
            c.d.glow(-w * 0.45f, 24.5f, -w * 0.45f, w * 0.45f, ground, w * 0.45f, 0xFFFFF2B0);
            c.d.glow(-w, 24.5f, -w, w, ground, w, 0xFF4A1A80);
        }
    }

    private static void crown(PropCtx c) {
        c.d.box(-4.6f, -1.5f, -4.6f, 4.6f, 0.5f, 4.6f, GOLD);
        c.d.box(-4.2f, -1.4f, -4.2f, 4.2f, 0.6f, 4.2f, 0xFF7A4A00); // inner (hides the hollow)
        for (int i = -1; i <= 1; i++) {
            float o = i * 3.5f;
            c.d.box(o - 0.8f, -4f, -4.6f, o + 0.8f, -1.5f, -3.8f, GOLD);
            c.d.box(o - 0.8f, -4f, 3.8f, o + 0.8f, -1.5f, 4.6f, GOLD);
            c.d.box(-4.6f, -4f, o - 0.8f, -3.8f, -1.5f, o + 0.8f, GOLD);
            c.d.box(3.8f, -4f, o - 0.8f, 4.6f, -1.5f, o + 0.8f, GOLD);
        }
        int gem = c.step(200) % 2 == 0 ? 0xFFFF2050 : 0xFF40E0FF;
        c.d.glow(-0.8f, -1.2f, -5.0f, 0.8f, 0.2f, -4.5f, gem);
        c.d.glow(-0.6f, -5.0f, -4.4f, 0.6f, -3.8f, -4.0f, gem);
    }

    /** One wing; side 1 = right, -1 = left (mirrored by a rotation so face culling stays correct). */
    private static void wing(PropCtx c, float open, float flap, int side) {
        c.d.push();
        c.d.move(side, 0, 0);
        c.d.rotY(side > 0 ? -flap : 180 + flap);
        c.d.rotZ(-15);
        c.d.scale(Math.max(0.05f, open));
        for (int i = 0; i < 6; i++) {
            float len = 26 - i * 3.2f;
            float y = i * 2.4f;
            int col = i % 2 == 0 ? 0xFFFFD45A : 0xFFB070FF;
            c.d.glow(0, y - 1, -0.4f, len, y + 1, 0.4f, col);
        }
        c.d.glow(0, -1.6f, -0.6f, 28, -0.6f, 0.6f, WHITE); // leading edge
        c.d.pop();
    }

    /** A flat ring (in the XZ plane) made of glowing blocks. */
    private static void segmentRing(PropCtx c, float radius, int segments, float size, float thickness, int color) {
        for (int i = 0; i < segments; i++) {
            float a = (float) (Math.PI * 2 * i / segments);
            float x = (float) Math.cos(a) * radius, z = (float) Math.sin(a) * radius;
            c.d.glow(x - size / 2, -thickness / 2, z - size / 2, x + size / 2, thickness / 2, z + size / 2, color);
        }
    }
}

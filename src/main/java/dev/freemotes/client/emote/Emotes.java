package dev.freemotes.client.emote;

import dev.freemotes.client.emote.Emote.Rarity;
import dev.freemotes.client.render.Material;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;

/**
 * Every emote in the game. Timings are in milliseconds; frames snap instantly (no tweening).
 * Polish comes from posing, not blending: wind-up frames before big moves, short "impact" holds,
 * follow-through/recovery frames, and secondary motion (head, hips, the idle arm) on every beat.
 */
public final class Emotes {
    private static final Map<String, Emote> BY_ID = new LinkedHashMap<>();
    public static final List<Emote> ALL;

    private Emotes() {}

    private static Emote reg(Emote e) {
        BY_ID.put(e.id, e);
        return e;
    }

    public static Emote get(String id) {
        return BY_ID.get(id);
    }

    static {
        // ================================================================ COMMON
        reg(Emote.create("wave", "Wave", Rarity.COMMON, Items.OAK_SIGN, "Hey! Over here!")
                .frame(70, p -> p.rArm(-50, 0, 10).body(0, 0, -2).head(0, 0, 3))                         // wind-up
                .frame(70, p -> p.rArm(-140, 0, 15).lArm(5, 0, -4).body(0, 0, 3).head(-4, 0, -4))       // swing up
                .frames(e -> {
                    for (int i = 0; i < 6; i++) {
                        boolean out = i % 2 == 0;
                        e.frame(i == 0 ? 90 : 110, p -> p.rArm(-168, 0, out ? 38 : -8).lArm(out ? 6 : 2, 0, -4)
                                .body(0, out ? 3 : -2, out ? 3 : 1).head(-6, out ? -6 : 4, out ? -6 : -2));
                    }
                })
                .frame(160, p -> p.rArm(-170, 0, 15).body(0, 0, 2).head(-6, 0, -4))                      // hold
                .frame(80, p -> p.rArm(-90, 0, 12).head(-2, 0, -1))                                      // follow-through
                .frame(80, p -> p.rArm(-20, 0, 5)));                                                     // settle

        reg(Emote.create("clap", "Clap", Rarity.COMMON, Items.SLIME_BALL, "Slow clap... then fast clap.")
                .loop(4000)
                .frame(110, p -> p.rArm(-62, 42, 0).lArm(-62, -42, 0).head(-3, 0, 0))                    // open wide
                .frame(60, p -> p.rArm(-68, 18, 0).lArm(-68, -18, 0))                                   // closing
                .frame(90, p -> p.rArm(-72, -8, 0).lArm(-72, 8, 0).squat(0.6f).head(6, 0, 0).body(4, 0, 0)) // CLAP (impact)
                .frame(80, p -> p.rArm(-66, 24, 0).lArm(-66, -24, 0).head(2, 0, 0))                     // rebound
                .effects(fx -> {
                    if (fx.at(170)) {
                        fx.particle(ParticleTypes.CRIT, 0, 1.15, 0.45, 0, 0.1, 0);
                        fx.sound(SoundEvents.WOOL_HIT, 0.4f, 1.5f + fx.random.nextFloat() * 0.3f);
                    }
                }));

        reg(Emote.create("facepalm", "Facepalm", Rarity.COMMON, Items.PAPER, "Why are you like this.")
                .frame(110, p -> p.rArm(-45, 0, 8).head(-5, 0, 0))                                       // wind-up
                .frame(70, p -> p.rArm(-138, 28, 0).head(34, 0, 0).body(6, 0, 0))                        // SLAP
                .frame(250, p -> p.rArm(-128, 26, 0).head(30, 0, 0).body(5, 0, 0).squat(0.5f))           // impact hold
                .then(250, p -> p.head(30, -7, 0))                                                       // slow head shake
                .then(250, p -> p.head(30, 7, 0))
                .then(250, p -> p.head(30, -7, 0))
                .frame(220, p -> p.rArm(-112, 24, 0).head(22, 0, 0).body(3, 0, 0))                       // drag hand down
                .frame(160, p -> p.rArm(-60, 10, 0).head(12, 0, 0).lArm(0, 0, -6))                       // sigh
                .frame(120, p -> p.rArm(-15, 0, 4).head(4, 0, 0)));

        reg(Emote.create("yes", "Nod", Rarity.COMMON, Items.LIME_DYE, "Big agreeing energy.")
                .frame(110, p -> p.head(-12, 0, 0).body(-2, 0, 0))                                       // wind-up
                .frames(e -> {
                    for (int i = 0; i < 3; i++) {
                        e.frame(120, p -> p.head(24, 0, 0).body(4, 0, 0).squat(0.4f));
                        e.frame(120, p -> p.head(-10, 0, 0).body(-1, 0, 0));
                    }
                })
                .frame(220, p -> p.head(6, 0, 0).rArm(-30, 0, 10).lArm(-10, 0, -5)));                    // satisfied

        reg(Emote.create("no", "Nope", Rarity.COMMON, Items.RED_DYE, "Absolutely not.")
                .frame(90, p -> p.rArm(-25, 0, 0).head(0, 10, 0))
                .frames(e -> {
                    for (int i = 0; i < 4; i++) {
                        boolean l = i % 2 == 0;
                        e.frame(110, p -> p.head(0, l ? 34 : -34, 0).rArm(-82, l ? 20 : -20, 0).body(0, l ? 6 : -6, 0)
                                .lArm(5, 0, l ? -10 : -4));
                    }
                })
                .frame(150, p -> p.rArm(-50, 40, 30).lArm(-50, -40, -30).head(-6, 0, 0))                 // arms crossed-ish
                .frame(120, p -> p.rArm(-15, 0, 0).head(0, 0, 0)));

        // ================================================================ RARE
        reg(Emote.create("dab", "Dab", Rarity.RARE, Items.GLOWSTONE_DUST, "It's 2016 forever.")
                .frame(90, p -> p.squat(1.5f).rArm(-30, 0, 25).lArm(-30, 0, -25).head(8, 0, 0))           // gather
                .frame(60, p -> p.rArm(-90, 0, -30).lArm(-80, 0, -40).head(25, -15, 0).body(5, 0, 0))     // smear frame
                .frame(110, p -> p.rArm(-122, 0, -58).lArm(-112, 0, -64).head(44, -32, 0).body(12, -6, 0).squat(0.6f)) // DAB (impact)
                .then(800, p -> p.squat(0).body(10, -6, 0))                                             // hold
                .then(150, p -> p.head(36, -26, 0))                                                      // tiny relax
                .frame(110, p -> p.rArm(-50, 0, -10).lArm(-30, 0, -20).head(10, 0, 0))                    // follow-through
                .frame(100, p -> p.rArm(-15, 0, 0).lArm(-5, 0, 0))
                .effects(fx -> {
                    if (fx.at(150)) {
                        fx.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.6f, 1.6f);
                        fx.burst(ParticleTypes.GLOW, 1.4, 12, 0.15);
                    }
                }));

        reg(Emote.create("floss", "Floss", Rarity.RARE, Items.STRING, "Hips one way, arms the other.")
                .loop(0)
                .frames(e -> {
                    // 6-frame floss: arms swing across front/back while hips counter-swing
                    float[][] f = {
                            // rArmX, rArmZ, lArmX, lArmZ, hip
                            {-18, -12, 18, -38, 1.4f},
                            {-10, 14, 10, -14, 0.5f},
                            {18, 38, -18, 12, -1.4f},
                            {-18, 12, 18, -12, -1.4f},
                            {-10, -14, 10, 14, -0.5f},
                            {18, -38, -18, 12, 1.4f},
                    };
                    for (float[] k : f) {
                        float hip = k[4];
                        e.frame(90, p -> p.rArm(k[0], 0, k[1]).lArm(k[2], 0, k[3])
                                .body(0, 0, hip * 6).bodyPos(hip, 0, 0).headPos(hip, 0, 0)
                                .rArmPos(hip, 0, 0).lArmPos(hip, 0, 0)
                                .rLeg(0, 0, -hip * 4).lLeg(0, 0, -hip * 4)
                                .head(0, 0, -hip * 5));
                    }
                }));

        reg(Emote.create("flex", "Flex", Rarity.RARE, Items.IRON_INGOT, "Do you even mine, bro?")
                .frame(140, p -> p.squat(1.5f).rArm(10, 0, 35).lArm(10, 0, -35).head(10, 0, 0))           // wind-up
                .frames(e -> {
                    // double bicep, trembling with effort
                    for (int i = 0; i < 8; i++) {
                        float s = i % 2 == 0 ? 0.35f : -0.35f;
                        e.frame(i == 0 ? 120 : 70, p -> p.rArm(-90, -90, 90).lArm(-90, 90, -90).head(-12, 0, 0)
                                .squat(1).bodyPos(s, 0, 0).headPos(s, 1, 0).rArmPos(s, 1, 0).lArmPos(s, 1, 0));
                    }
                })
                .frame(80, p -> p.rArm(-40, 0, 40).lArm(-40, 0, -40))                                   // transition
                .frames(e -> {
                    // most muscular: arms down and in, hunched
                    for (int i = 0; i < 8; i++) {
                        float s = i % 2 == 0 ? 0.3f : -0.3f;
                        e.frame(i == 0 ? 120 : 70, p -> p.rArm(-25, 0, -25).lArm(-25, 0, 25).body(14, 0, 0)
                                .head(-18, 0, 0).squat(2).bodyPos(s, 0, 0).headPos(s, 2, -1));
                    }
                })
                .frame(120, p -> p.rArm(-150, 0, 70).lArm(-150, 0, -70).head(-20, 0, 0))                  // victory
                .frame(500, p -> p.rArm(-150, 0, 80).lArm(-150, 0, -80).head(-15, 0, 0))
                .frame(100, p -> p.rArm(-30, 0, 15).lArm(-30, 0, -15))
                .effects(fx -> {
                    if (fx.at(140) || fx.at(1000) || fx.at(1860)) {
                        fx.sound(SoundEvents.ANVIL_LAND, 0.25f, 1.9f);
                        fx.burst(ParticleTypes.CRIT, 1.6, 15, 0.3);
                    }
                }));

        reg(Emote.create("heart", "Heart Hands", Rarity.RARE, Items.POPPY, "Spreading the love.")
                .frame(90, p -> p.rArm(-60, 10, 10).lArm(-60, -10, -10))                                // raise
                .frame(80, p -> p.rArm(-105, 18, 22).lArm(-105, -18, -22).head(-3, 0, 0))
                .frames(e -> {
                    for (int i = 0; i < 8; i++) {
                        float sway = (i / 2) % 2 == 0 ? 4 : -4;
                        boolean squeeze = i % 2 == 0;
                        e.frame(300, p -> p.rArm(-120, squeeze ? 20 : 24, 25).lArm(-120, squeeze ? -20 : -24, -25)
                                .head(-5, 0, sway * 1.5f).body(0, 0, sway * 0.5f));
                    }
                })
                .frame(120, p -> p.rArm(-40, 0, 10).lArm(-40, 0, -10))
                .effects(fx -> {
                    if (fx.every(300)) fx.particle(ParticleTypes.HEART, (fx.random.nextDouble() - 0.5) * 0.6, 2.0, 0.7, 0, 0.05, 0);
                    if (fx.at(170)) fx.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1f, 1.2f);
                })
                .props(ctx -> {
                    if (ctx.ms < 170 || ctx.ms > 2570) return;
                    // big blocky pixel heart floating in front of the hands, beating in two sizes
                    ctx.root();
                    ctx.d.move(0, -14 - (ctx.step(600) % 2) * 0.6f, -9);
                    ctx.d.scale(ctx.step(300) % 2 == 0 ? 1f : 1.25f);
                    String[] art = {".XX.XX.", "XXXXXXX", "XXXXXXX", ".XXXXX.", "..XXX..", "...X..."};
                    for (int row = 0; row < art.length; row++) {
                        for (int col = 0; col < 7; col++) {
                            if (art[row].charAt(col) == 'X') {
                                float x = (col - 3.5f) * 1.2f, y = row * 1.2f;
                                boolean shine = row == 1 && col == 1;
                                ctx.d.glow(x, y, -0.6f, x + 1.2f, y + 1.2f, 0.6f, shine ? 0xFFFFC0D0 : 0xFFFF2050);
                            }
                        }
                    }
                    ctx.end();
                }));

        reg(Emote.create("headbang", "Rock Out", Rarity.RARE, Items.NOTE_BLOCK, "Shred the blocky guitar.")
                .loop(0)
                .frames(e -> {
                    // wide stance, 4-beat bar: bang - up - bang - strum flourish
                    e.frame(120, p -> p.head(38, 0, 0).body(12, 0, 0).rArm(-45, 0, 22).lArm(-72, 40, 0).squat(1.5f)
                            .rLeg(-8, 0, 12).lLeg(-8, 0, -12));
                    e.frame(110, p -> p.head(-18, 0, 0).body(-2, 0, 0).rArm(-15, 0, 36).lArm(-70, 40, 0).squat(0.5f)
                            .rLeg(0, 0, 10).lLeg(0, 0, -10));
                    e.frame(120, p -> p.head(38, 0, 5).body(12, 0, 0).rArm(-45, 0, 18).lArm(-75, 42, 0).squat(1.5f)
                            .rLeg(-8, 0, 12).lLeg(-8, 0, -12));
                    e.frame(150, p -> p.head(-25, 0, -5).body(-4, 0, 0).rArm(-140, 0, 40).lArm(-68, 38, 0)
                            .rLeg(0, 0, 10).lLeg(0, 0, -10));
                })
                .effects(fx -> {
                    if (fx.at(0) || fx.at(230)) {
                        fx.particleLocal(ParticleTypes.NOTE, -0.5, 1.6, 0.4, fx.random.nextDouble(), 0, 0);
                        fx.sound(SoundEvents.NOTE_BLOCK_GUITAR.value(), 0.7f, 0.5f + fx.random.nextFloat());
                    }
                    if (fx.at(350)) fx.particleLocal(ParticleTypes.CRIT, -0.6, 1.4, 0.4, 0, 0.2, 0);
                })
                .props(ctx -> {
                    ctx.body();
                    ctx.d.move(0, 8, -3.5f);
                    ctx.d.rotZ(-35);
                    ctx.d.mat(Material.WOOD);
                    ctx.d.box(-4, -3, -1, 4, 3, 1, 0xFFE02A40);                       // body
                    ctx.d.box(-2, -4.5f, -1, 2, 4.5f, 1, 0xFFE02A40);
                    ctx.d.mat(Material.PLAIN);
                    ctx.d.box(-1, -1, -1.3f, 1, 1, -1, 0xFF181818);                   // sound hole
                    ctx.d.mat(Material.WOOD);
                    ctx.d.box(4, -0.6f, -0.6f, 15, 0.6f, 0.6f, 0xFF9A6A3A);           // neck
                    ctx.d.box(15, -1.2f, -0.8f, 18, 1.2f, 0.8f, 0xFF6A4424);          // headstock
                    ctx.d.mat(Material.METAL);
                    ctx.d.box(-3, -0.4f, -1.2f, 15, 0.4f, -1.05f, 0xFFE0E0E0);        // strings
                    ctx.d.box(15.5f, -1.6f, -0.3f, 16.2f, 1.6f, 0.3f, 0xFFD0D0D0);    // tuning pegs
                    ctx.end();
                }));

        reg(Emote.create("salute", "Sword Salute", Rarity.RARE, Items.DIAMOND_SWORD, "For the realm!")
                .frame(140, p -> p.rArm(-20, 0, -35).body(0, 15, 0).head(0, 10, 0))                       // reach for the hilt
                .frame(90, p -> p.rArm(-90, 0, 15).body(0, -5, 0))                                       // draw
                .frame(70, p -> p.rArm(-150, -15, 5).head(-10, 0, 0))                                    // smear up
                .frame(1100, p -> p.rArm(-165, -20, 0).head(-15, 0, 0).lArm(0, 0, -4).rLeg(0, 0, 2).lLeg(0, 0, -2)) // SALUTE
                .frame(90, p -> p.rArm(-130, 0, 0).head(-5, 0, 0))
                .frame(350, p -> p.rArm(-95, 0, 0).head(0, 0, 0).body(4, 0, 0).lLeg(-25, 0, 0).rLeg(10, 0, 0)) // point it forward
                .frame(120, p -> p.rArm(-40, 0, -30).body(0, 15, 0))                                     // sheathe
                .frame(100, p -> p.rArm(-15, 0, 0))
                .effects(fx -> {
                    if (fx.at(300)) {
                        fx.sound(SoundEvents.PLAYER_ATTACK_STRONG, 0.8f, 0.9f);
                        for (int i = 0; i < 8; i++) fx.particle(ParticleTypes.ELECTRIC_SPARK, -0.35, 2.6 + i * 0.12, 0, 0, 0.02, 0);
                    }
                    if (fx.at(1490)) fx.particleLocal(ParticleTypes.SWEEP_ATTACK, 0, 1.3, 1.0, 0, 0, 0);
                })
                .props(ctx -> {
                    if (ctx.ms < 140 || ctx.ms > 1840) return; // only out of the sheath between draw and sheathe
                    ctx.rightHand();
                    ctx.d.rotX(180); // blade points away from the shoulder
                    ctx.d.mat(Material.LEATHER);
                    ctx.d.box(-0.5f, -3, -0.5f, 0.5f, 1, 0.5f, 0xFF8A5A30);             // grip
                    ctx.d.mat(Material.GOLD);
                    ctx.d.box(-0.7f, 1, -0.7f, 0.7f, 2.2f, 0.7f, 0xFFFFD040);           // pommel
                    ctx.d.mat(Material.GEM);
                    ctx.d.box(-2.5f, -3.8f, -0.6f, 2.5f, -3, 0.6f, 0xFF40E0C8);         // guard
                    ctx.d.mat(Material.BLADE);
                    ctx.d.box(-0.9f, -16, -0.4f, 0.9f, -3.8f, 0.4f, 0xFFA0FFF4);        // blade
                    ctx.d.box(-0.5f, -17, -0.3f, 0.5f, -16, 0.3f, 0xFFA0FFF4);          // tip
                    if (ctx.ms >= 300 && ctx.ms < 1400 && ctx.step(150) % 3 == 0) {
                        ctx.d.glow(-0.3f, -16.5f, -0.45f, 0.3f, -4f, 0.45f, 0xFF60C0B0); // glint
                    }
                    ctx.end();
                }));

        // ================================================================ EPIC
        reg(Emote.create("boombox", "Boombox Groove", Rarity.EPIC, Items.JUKEBOX, "Drop the beat. Literally.")
                .loop(0)
                .frames(e -> {
                    // 8 beats: shoulder-carried boombox, bouncing knees, head bob, free arm doing the groove
                    for (int i = 0; i < 8; i++) {
                        boolean l = (i / 2) % 2 == 0;
                        boolean down = i % 2 == 0;
                        e.frame(150, p -> {
                            p.rArm(down ? -165 : -170, 0, down ? 14 : 10).head(down ? 16 : -8, l ? 18 : -18, 0)
                                    .body(0, l ? 12 : -12, 0)
                                    .lArm(down ? -25 : -65, 0, l ? -45 : -15);
                            if (down) p.squat(2);
                            p.rLeg(down ? -20 : 0, l ? 10 : -10, 6).lLeg(down ? -20 : 0, l ? 10 : -10, -6);
                            return p;
                        });
                    }
                })
                .effects(fx -> {
                    if (fx.every(300)) {
                        fx.particleLocal(ParticleTypes.NOTE, (fx.random.nextDouble() - 0.5) * 1.4, 2.4, 0, fx.random.nextDouble(), 0, 0);
                        fx.sound(SoundEvents.NOTE_BLOCK_BASEDRUM.value(), 0.8f, 1f);
                    }
                    if (fx.every(600)) fx.sound(SoundEvents.NOTE_BLOCK_HAT.value(), 0.6f, 1.2f);
                })
                .props(ctx -> {
                    ctx.rightHand();
                    float pump = ctx.step(150) % 2 == 0 ? 1f : 1.15f;
                    ctx.d.mat(Material.METAL);
                    ctx.d.box(-3, -1.5f, -0.5f, 3, -0.5f, 0.5f, 0xFF707070);          // handle
                    ctx.d.box(-3, -0.5f, -0.5f, -2, 0.5f, 0.5f, 0xFF707070);
                    ctx.d.box(2, -0.5f, -0.5f, 3, 0.5f, 0.5f, 0xFF707070);
                    ctx.d.box(-8, 0.5f, -2.5f, 8, 8.5f, 2.5f, 0xFF5A5A62);            // case
                    for (int side = -1; side <= 1; side += 2) {
                        ctx.d.push();
                        ctx.d.move(side * 4.5f, 5, -2.6f);
                        ctx.d.scale(pump);
                        ctx.d.mat(Material.GRILLE);
                        ctx.d.box(-2.5f, -2.5f, -0.4f, 2.5f, 2.5f, 0.2f, 0xFF808080);   // speaker
                        ctx.d.glow(-1, -1, -0.6f, 1, 1, 0f, side < 0 ? 0xFF20D0FF : 0xFFFF30D0);
                        ctx.d.pop();
                    }
                    ctx.d.glow(-1.5f, 1.5f, -2.7f, 1.5f, 2.5f, -2.4f, ctx.step(300) % 2 == 0 ? 0xFF40FF60 : 0xFF105020); // display
                    ctx.end();
                }));

        reg(Emote.create("breakdance", "Windmill", Rarity.EPIC, Items.MUSIC_DISC_CAT, "Spin to win, 45 degrees at a time.")
                .frame(120, p -> p.squat(2).rArm(-40, 0, 30).lArm(-40, 0, -30).head(10, 0, 0))            // drop in
                .frame(110, p -> p.squat(5).rArm(-70, 0, 50).lArm(-20, 0, -60).body(20, 0, 0).head(15, 0, 0))
                .frame(80, p -> p.lift(0.2f).roll(45).squat(4).rArm(-170, 0, 0).lArm(0, 0, -50))           // kick off
                .frames(e -> {
                    for (int i = 0; i < 24; i++) {
                        float spin = i * 45f;
                        boolean odd = i % 2 == 1;
                        e.frame(75, p -> p.spin(spin).roll(odd ? 70 : 90).lift(0.35f)
                                .rArm(-180, 0, 0).lArm(0, 0, odd ? -20 : -40).head(0, 0, odd ? 10 : -10)
                                .rLeg(odd ? -60 : 20, 0, 40).lLeg(odd ? 20 : -60, 0, -40));
                    }
                })
                .frame(90, p -> p.spin(1080).roll(40).lift(0.2f).squat(4).rArm(-90, 0, 40).lArm(-90, 0, -40))  // land
                .frame(200, p -> p.squat(5).rArm(-150, 0, 60).lArm(-150, 0, -60).head(-15, 0, 0))
                .frame(500, p -> p.rArm(-90, 40, 0).lArm(-90, -40, 0).head(-10, 20, 0))                   // freeze pose
                .frame(100, p -> p.rArm(-20, 0, 5).lArm(-20, 0, -5))
                .effects(fx -> {
                    if (fx.between(310, 2110) && fx.every(150)) fx.ring(ParticleTypes.CLOUD, 0.15, 0.6, 6, 0.08, 0);
                    if (fx.at(2110)) {
                        fx.sound(SoundEvents.PLAYER_LEVELUP, 0.6f, 1.5f);
                        fx.burst(ParticleTypes.FIREWORK, 1.2, 25, 0.25);
                    }
                }));

        reg(Emote.create("backflip", "Backflip", Rarity.EPIC, Items.FEATHER, "Parkour! In 8 frames.")
                .frame(160, p -> p.squat(3).rArm(20, 0, 10).lArm(20, 0, -10).head(10, 0, 0))              // crouch
                .frame(110, p -> p.squat(6).rArm(45, 0, 15).lArm(45, 0, -15).head(15, 0, 0).body(15, 0, 0)) // load
                .frame(60, p -> p.lift(0.2f).rArm(-170, 0, 20).lArm(-170, 0, -20).head(-20, 0, 0))         // launch (stretch)
                .frames(e -> {
                    float[] lifts = {0.5f, 0.95f, 1.25f, 1.4f, 1.4f, 1.25f, 0.95f, 0.5f};
                    for (int i = 0; i < 8; i++) {
                        float pitch = -(i + 1) * 45f;
                        float lift = lifts[i];
                        boolean tuck = i >= 2 && i <= 5;
                        e.frame(70, p -> {
                            p.pitch(pitch).lift(lift);
                            if (tuck) p.squat(6).rArm(-60, 0, 10).lArm(-60, 0, -10).head(20, 0, 0);  // tucked
                            else p.squat(2).rArm(-170, 0, 25).lArm(-170, 0, -25);
                            return p;
                        });
                    }
                })
                .frame(90, p -> p.squat(6).rArm(-80, 0, 50).lArm(-80, 0, -50).head(15, 0, 0).body(15, 0, 0)) // LAND (squash)
                .frame(110, p -> p.squat(3).rArm(-110, 0, 40).lArm(-110, 0, -40))
                .frame(650, p -> p.rArm(-170, 0, 30).lArm(-170, 0, -30).head(-15, 0, 0))                  // ta-da
                .frame(100, p -> p.rArm(-30, 0, 10).lArm(-30, 0, -10))
                .effects(fx -> {
                    if (fx.at(330)) {
                        fx.ring(ParticleTypes.CLOUD, 0.1, 0.4, 10, 0.12, 0.02);
                        fx.sound(SoundEvents.GOAT_LONG_JUMP, 1f, 1.2f);
                    }
                    if (fx.between(330, 890)) fx.particle(ParticleTypes.CRIT, 0, 0.8 + fx.lift(), 0, 0, 0, 0);
                    if (fx.at(890)) {
                        fx.ring(ParticleTypes.CLOUD, 0.1, 0.4, 14, 0.18, 0.02);
                        fx.sound(SoundEvents.PLAYER_SMALL_FALL, 1f, 1f);
                    }
                }));

        reg(Emote.create("raincloud", "Rain Cloud", Rarity.EPIC, Items.WATER_BUCKET, "When it's just not your day.")
                .frame(150, p -> p.head(10, 0, 0))
                .frame(150, p -> p.head(20, 0, 0).body(4, 0, 0).rArm(0, 0, 3).lArm(0, 0, -3))
                .frames(e -> {
                    // slumped, breathing slowly; a big sigh in the middle
                    for (int i = 0; i < 10; i++) {
                        boolean in = i % 2 == 0;
                        boolean sigh = i == 4;
                        e.frame(sigh ? 450 : 330, p -> p.head(sigh ? -5 : 30, 0, in ? 0 : 3)
                                .body(sigh ? 0 : 9, 0, 0).rArm(sigh ? -10 : 6, 0, 5).lArm(sigh ? -10 : 6, 0, -5)
                                .bodyPos(0, in ? 0 : 0.4f, 0).headPos(0, in ? 0 : 0.4f, 0)
                                .rArmPos(0, in ? 0 : 0.4f, 0).lArmPos(0, in ? 0 : 0.4f, 0));
                    }
                })
                .frame(150, p -> p.head(12, 0, 0))
                .effects(fx -> {
                    if (fx.every(100)) {
                        for (int i = 0; i < 2; i++) {
                            fx.particle(ParticleTypes.FALLING_WATER, (fx.random.nextDouble() - 0.5) * 0.9, 2.65,
                                    (fx.random.nextDouble() - 0.5) * 0.9, 0, 0, 0);
                        }
                    }
                    if (fx.at(1300) || fx.at(3100)) fx.sound(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.25f, 1.8f);
                })
                .props(ctx -> {
                    ctx.root();
                    float bob = ctx.step(500) % 2 == 0 ? 0 : -0.5f;
                    float drift = ctx.step(700) % 2 == 0 ? 0 : 0.6f;
                    ctx.d.move(drift, -21 + bob, 0);
                    ctx.d.mat(Material.CLOUD);
                    ctx.d.box(-7, -1, -5, 7, 2, 5, 0xFFA0A6B0);
                    ctx.d.box(-5, -3, -3, 3, -1, 4, 0xFFB0B6C0);
                    ctx.d.box(-1, -4, -4, 5, -1, 2, 0xFF949AA4);
                    ctx.d.box(-6, 2, -4, 6, 3, 4, 0xFF70757E);
                    if (ctx.ms % 1800 < 120) ctx.d.glow(-1, 3, -0.5f, 1, 9, 0.5f, 0xFFFFFF80); // tiny lightning flash
                    ctx.end();
                }));

        reg(Emote.create("zen", "Zen Mode", Rarity.EPIC, Items.ENCHANTING_TABLE, "Inner peace. Outer levitation.")
                .frame(150, p -> p.squat(3).rArm(-20, 0, 10).lArm(-20, 0, -10).head(5, 0, 0))              // settle down
                .frame(150, p -> p.lift(0.2f).rLeg(-90, 35, 0).lLeg(-90, -35, 0).rArm(-30, 0, 20).lArm(-30, 0, -20))
                .loop(0)
                .loopFromHere()
                .frames(e -> {
                    // 4-step breathing float
                    float[] lift = {0.45f, 0.5f, 0.55f, 0.5f};
                    for (int i = 0; i < 4; i++) {
                        float l = lift[i];
                        boolean inhale = i < 2;
                        e.frame(350, p -> p.lift(l).rLeg(-90, 35, 0).lLeg(-90, -35, 0)
                                .rArm(inhale ? -38 : -32, 0, inhale ? 28 : 22).lArm(inhale ? -38 : -32, 0, inhale ? -28 : -22)
                                .head(inhale ? -8 : -3, 0, 0));
                    }
                })
                .effects(fx -> {
                    if (fx.every(100)) {
                        double a = fx.now * 0.3;
                        fx.spiral(ParticleTypes.ENCHANT, 1.0 + fx.lift(), 1.0, a, 0.05);
                        fx.spiral(ParticleTypes.ENCHANT, 1.0 + fx.lift(), 1.0, a + 180, 0.05);
                    }
                })
                .props(ctx -> {
                    ctx.root();
                    long s = ctx.step(125);
                    ctx.d.glowMat(Material.GEM);
                    for (int i = 0; i < 4; i++) {
                        float a = (float) Math.toRadians(s * 22.5 + i * 90);
                        float x = (float) Math.cos(a) * 14, z = (float) Math.sin(a) * 14;
                        float y = 4 + (((s + i) / 2) % 2 == 0 ? 0 : -1);
                        int col = i % 2 == 0 ? 0xFF55FFE0 : 0xFFB070FF;
                        ctx.d.glowCube(x, y, z, 1.2f, col);
                    }
                    ctx.end();
                }));

        // ================================================================ LEGENDARY
        reg(Emote.create("trophy", "Champion", Rarity.LEGENDARY, Items.GOLD_BLOCK, "GG EZ. Hoist the golden cup.")
                .frame(160, p -> p.squat(2).rArm(-30, 0, 0).lArm(-30, 0, 0).head(10, 0, 0))               // pick it up
                .frame(90, p -> p.squat(3).rArm(-60, 0, 0).lArm(-60, 0, 0))
                .frame(70, p -> p.lift(0.15f).rArm(-150, 0, 0).lArm(-90, 0, -30).head(-15, 0, 0))          // hoist!
                .frames(e -> {
                    // victory hops with arm pumps
                    for (int i = 0; i < 6; i++) {
                        boolean up = i % 2 == 0;
                        e.frame(up ? 160 : 190, p -> p.lift(up ? 0.25f : 0f).rArm(up ? -178 : -168, 0, up ? 0 : 6)
                                .lArm(up ? -150 : -40, 0, up ? -40 : -55).head(up ? -28 : -20, up ? 8 : -8, 0)
                                .rLeg(up ? -15 : 0, 0, 4).lLeg(up ? 10 : 0, 0, -4));
                    }
                })
                .frame(700, p -> p.rArm(-175, 0, 0).lArm(-30, 0, -50).head(-25, 0, 0))                    // hold it high
                .frame(120, p -> p.rArm(-90, 0, 0).lArm(-20, 0, -20))
                .frame(100, p -> p.rArm(-30, 0, 0))
                .effects(fx -> {
                    if (fx.at(320)) {
                        fx.sound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.7f, 1f);
                        fx.burst(ParticleTypes.TOTEM_OF_UNDYING, 2.6, 40, 0.35);
                    }
                    if (fx.between(320, 2400) && fx.every(100)) {
                        fx.particle(ParticleTypes.WAX_OFF, (fx.random.nextDouble() - 0.5), 3.0, (fx.random.nextDouble() - 0.5), 0, 0.05, 0);
                    }
                })
                .props(ctx -> {
                    ctx.rightHand();
                    ctx.d.rotX(180); // cup opens away from the shoulder (upward while raised)
                    int gold = 0xFFFFD040, dark = 0xFFE0A020;
                    ctx.d.mat(Material.WOOD);
                    ctx.d.box(-3, 0, -3, 3, 1.5f, 3, 0xFF7A4A24);                     // plinth
                    ctx.d.mat(Material.GOLD);
                    ctx.d.box(-2, -1, -2, 2, 0, 2, dark);                              // foot
                    ctx.d.box(-1, -3, -1, 1, -1, 1, gold);                             // stem
                    ctx.d.box(-3, -4, -3, 3, -3, 3, dark);                             // cup base
                    ctx.d.box(-4, -9, -4, 4, -4, 4, gold);                             // cup
                    ctx.d.box(-4.4f, -9.6f, -4.4f, 4.4f, -9, 4.4f, dark);              // rim
                    ctx.d.mat(Material.PLAIN);
                    ctx.d.box(-3, -9.7f, -3, 3, -8, 3, 0xFF6A4400);                   // inside
                    ctx.d.mat(Material.GOLD);
                    ctx.d.box(-6.5f, -8, -0.6f, -4, -7, 0.6f, gold);                   // handles
                    ctx.d.box(-6.5f, -7, -0.6f, -5.5f, -4.5f, 0.6f, gold);
                    ctx.d.box(4, -8, -0.6f, 6.5f, -7, 0.6f, gold);
                    ctx.d.box(5.5f, -7, -0.6f, 6.5f, -4.5f, 0.6f, gold);
                    ctx.d.mat(Material.GEM);
                    ctx.d.box(-1, -7, -4.25f, 1, -5, -4f, 0xFFFF3050);                // ruby
                    if (ctx.step(200) % 2 == 0) ctx.d.glow(-2.5f, -8.5f, -4.4f, -1.5f, -5, -4.1f, 0xFFFFFFFF); // shine
                    ctx.end();
                }));

        reg(Emote.create("pyro", "Pyromancer", Rarity.LEGENDARY, Items.BLAZE_POWDER, "Juggle fire. Summon a fire ring. Be cool about it.")
                .frame(200, p -> p.rArm(-80, 0, 0).lArm(-80, 0, 0).squat(1).head(10, 0, 0))
                .frames(e -> {
                    // juggling: alternate hands toss, head follows the fireballs
                    for (int i = 0; i < 6; i++) {
                        boolean r = i % 2 == 0;
                        e.frame(200, p -> p.rArm(r ? -115 : -78, 0, r ? 22 : 10).lArm(r ? -78 : -115, 0, r ? -10 : -22)
                                .head(-12, r ? -10 : 10, 0).body(0, r ? -4 : 4, 0));
                    }
                })
                .frame(110, p -> p.squat(3).rArm(-30, 0, 40).lArm(-30, 0, -40).head(10, 0, 0))            // gather
                .frame(80, p -> p.squat(5).rArm(10, 0, 50).lArm(10, 0, -50).head(15, 0, 0).body(15, 0, 0)) // SLAM
                .frame(1500, p -> p.rArm(-175, 0, 30).lArm(-175, 0, -30).head(-25, 0, 0))                 // summon
                .frame(120, p -> p.rArm(-90, 0, 50).lArm(-90, 0, -50).head(-10, 0, 0))
                .frame(100, p -> p.rArm(-20, 0, 8).lArm(-20, 0, -8))
                .effects(fx -> {
                    if (fx.between(0, 1400) && fx.every(50)) {
                        fx.particleLocal(ParticleTypes.FLAME, 0.35, 1.5, 0.5, 0, 0.02, 0);
                        fx.particleLocal(ParticleTypes.FLAME, -0.35, 1.5, 0.5, 0, 0.02, 0);
                    }
                    if (fx.at(1590)) {
                        fx.sound(SoundEvents.BLAZE_SHOOT, 1f, 0.7f);
                        fx.ring(ParticleTypes.FLAME, 0.1, 0.5, 40, 0.25, 0.0);
                        fx.ring(ParticleTypes.LAVA, 0.1, 1.0, 10, 0.0, 0.0);
                    }
                    if (fx.between(1700, 3290) && fx.every(100)) {
                        fx.ring(ParticleTypes.FLAME, 0.1, 2.2, 24, 0, 0.12);
                        fx.spiral(ParticleTypes.SMALL_FLAME, 2.5, 0.3, fx.now * 0.6, 0.1);
                    }
                    if (fx.at(1700)) fx.sound(SoundEvents.FIRECHARGE_USE, 1f, 0.6f);
                })
                .props(ctx -> {
                    if (ctx.ms >= 1400) return;
                    long s = ctx.step(125);
                    ctx.root();
                    for (int i = 0; i < 3; i++) {
                        float a = (float) Math.toRadians(s * 60 + i * 120);
                        float x = (float) Math.cos(a) * 4, y = (float) Math.sin(a) * 4;
                        ctx.d.glowCube(x, 2 + y - 8, -10, 1.3f, 0xFFFF7A10);
                        ctx.d.glowCube(x, 2 + y - 8, -10, 0.7f, 0xFFFFF060);
                    }
                    ctx.end();
                }));

        reg(Emote.create("celebrate", "Fireworks Show", Rarity.LEGENDARY, Items.FIREWORK_ROCKET, "Light it up. Everyone look.")
                .frame(160, p -> p.squat(2).rArm(-40, 0, 10).lArm(-40, 0, -10).head(10, 0, 0))
                .frame(140, p -> p.squat(4).rArm(-10, 0, 20).lArm(-10, 0, -20))
                .frames(e -> {
                    // jumping jacks to the launches
                    for (int i = 0; i < 6; i++) {
                        boolean up = i % 2 == 0;
                        e.frame(up ? 220 : 180, p -> p.lift(up ? 0.35f : 0f)
                                .rArm(up ? -175 : -95, 0, up ? 25 : 70).lArm(up ? -175 : -95, 0, up ? -25 : -70)
                                .rLeg(0, 0, up ? 18 : 3).lLeg(0, 0, up ? -18 : -3).head(up ? -30 : -15, 0, 0));
                    }
                })
                .frame(150, p -> p.squat(2).rArm(-120, 0, 50).lArm(-120, 0, -50).head(-20, 0, 0))
                .frame(600, p -> p.rArm(-150, 0, 60).lArm(-150, 0, -60).head(-30, 0, 0))
                .frame(100, p -> p.rArm(-30, 0, 10).lArm(-30, 0, -10))
                .effects(fx -> {
                    for (int i = 0; i < 4; i++) {
                        long t = 300 + i * 400L;
                        if (fx.at(t)) {
                            fx.sound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 1f, 1f);
                            for (int k = 0; k < 12; k++) fx.particle(ParticleTypes.FIREWORK, 0, 0.5 + k * 0.4, 0, 0, 0.1, 0);
                        }
                        if (fx.at(t + 300)) {
                            fx.sound(SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
                            double r = (fx.random.nextDouble() - 0.5) * 3;
                            for (int k = 0; k < 50; k++) {
                                double a = fx.random.nextDouble() * Math.PI * 2, b = fx.random.nextDouble() * Math.PI;
                                fx.particle(ParticleTypes.FIREWORK, r, 6, 0, Math.cos(a) * Math.sin(b) * 0.3, Math.cos(b) * 0.3, Math.sin(a) * Math.sin(b) * 0.3);
                                if (k % 3 == 0) {
                                    int[] colors = {0xFF3050, 0x30FF70, 0x40A0FF, 0xFFE030, 0xFF50FF};
                                    fx.particle(new DustParticleOptions(colors[i % colors.length], 2.5f), r + Math.cos(a) * 1.5, 6 + Math.cos(b) * 1.5, Math.sin(a) * 1.5, 0, 0, 0);
                                }
                            }
                        }
                    }
                }));

        // ================================================================ SUPREME
        reg(SupremeEmote.create());

        ALL = Collections.unmodifiableList(new ArrayList<>(BY_ID.values()));
    }
}

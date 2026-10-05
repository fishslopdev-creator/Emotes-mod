package dev.freemotes.client.emote;

import dev.freemotes.client.emote.Emote.Rarity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;

/** Every emote in the game. Timings are in milliseconds; frames snap instantly (no tweening). */
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
        // ---------------------------------------------------------------- COMMON
        reg(Emote.create("wave", "Wave", Rarity.COMMON, Items.OAK_SIGN, "Hey! Over here!")
                .frame(110, p -> p.rArm(-170, 0, 25).head(0, 0, -6))
                .frame(110, p -> p.rArm(-170, 0, -15).head(0, 0, -6))
                .frame(110, p -> p.rArm(-170, 0, 25).head(0, 0, -6))
                .frame(110, p -> p.rArm(-170, 0, -15).head(0, 0, -6))
                .frame(110, p -> p.rArm(-170, 0, 25).head(0, 0, -6))
                .frame(110, p -> p.rArm(-170, 0, -15).head(0, 0, -6))
                .frame(250, p -> p.rArm(-170, 0, 5).head(0, 0, -6)));

        reg(Emote.create("clap", "Clap", Rarity.COMMON, Items.SLIME_BALL, "Slow clap... then fast clap.")
                .loop(4000)
                .frame(120, p -> p.rArm(-70, 35, 0).lArm(-70, -35, 0))
                .frame(120, p -> p.rArm(-70, -20, 0).lArm(-70, 20, 0))
                .effects(fx -> {
                    if (fx.at(120)) {
                        fx.particle(ParticleTypes.CRIT, 0, 1.15, 0.45, 0, 0.1, 0);
                        fx.sound(SoundEvents.WOOL_HIT, 0.4f, 1.6f);
                    }
                }));

        reg(Emote.create("facepalm", "Facepalm", Rarity.COMMON, Items.PAPER, "Why are you like this.")
                .frame(150, p -> p.rArm(-60, 0, 0).head(10, 0, 0))
                .frame(1400, p -> p.rArm(-125, 25, 0).head(25, 0, 0))
                .frame(400, p -> p.rArm(-125, 25, 0).head(35, -10, 0)));

        reg(Emote.create("yes", "Nod", Rarity.COMMON, Items.LIME_DYE, "Big agreeing energy.")
                .frame(140, p -> p.head(20, 0, 0)).frame(140, p -> p.head(-10, 0, 0))
                .frame(140, p -> p.head(20, 0, 0)).frame(140, p -> p.head(-10, 0, 0))
                .frame(140, p -> p.head(20, 0, 0)).frame(200, p -> p.head(0, 0, 0)));

        reg(Emote.create("no", "Nope", Rarity.COMMON, Items.RED_DYE, "Absolutely not.")
                .frame(120, p -> p.head(0, 35, 0).rArm(-40, 0, 0)).frame(120, p -> p.head(0, -35, 0).rArm(-40, 0, 0))
                .frame(120, p -> p.head(0, 35, 0).rArm(-40, 0, 0)).frame(120, p -> p.head(0, -35, 0).rArm(-40, 0, 0))
                .frame(250, p -> p.head(0, 0, 0)));

        // ---------------------------------------------------------------- RARE
        reg(Emote.create("dab", "Dab", Rarity.RARE, Items.GLOWSTONE_DUST, "It's 2016 forever.")
                .frame(90, p -> p.squat(1).rArm(-45, 0, 30).lArm(-45, 0, -30))
                .frame(1100, p -> p.rArm(-120, 0, -55).lArm(-110, 0, -60).head(40, -30, 0).body(10, 0, 0))
                .frame(150, p -> p.rArm(-20, 0, 0))
                .effects(fx -> {
                    if (fx.at(90)) {
                        fx.sound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.6f, 1.6f);
                        fx.burst(ParticleTypes.GLOW, 1.4, 12, 0.15);
                    }
                }));

        reg(Emote.create("floss", "Floss", Rarity.RARE, Items.STRING, "Hips one way, arms the other.")
                .loop(0)
                .frame(110, p -> p.rArm(-15, 0, -10).lArm(15, 0, -35).body(0, 0, 8).rLeg(0, 0, -5).lLeg(0, 0, -5).bodyPos(1, 0, 0))
                .frame(110, p -> p.rArm(15, 0, 35).lArm(-15, 0, 10).body(0, 0, -8).rLeg(0, 0, 5).lLeg(0, 0, 5).bodyPos(-1, 0, 0))
                .frame(110, p -> p.rArm(-15, 0, -35).lArm(15, 0, -10).body(0, 0, 8).rLeg(0, 0, -5).lLeg(0, 0, -5).bodyPos(1, 0, 0))
                .frame(110, p -> p.rArm(15, 0, 10).lArm(-15, 0, 35).body(0, 0, -8).rLeg(0, 0, 5).lLeg(0, 0, 5).bodyPos(-1, 0, 0)));

        reg(Emote.create("flex", "Flex", Rarity.RARE, Items.IRON_INGOT, "Do you even mine, bro?")
                .frame(150, p -> p.rArm(0, 0, 60).lArm(0, 0, -60))
                .frame(700, p -> p.rArm(-90, -90, 90).lArm(-90, 90, -90).head(-10, 0, 0).squat(1))
                .frame(700, p -> p.rArm(-150, 0, 80).lArm(-150, 0, -80).head(-15, 0, 0))
                .frame(700, p -> p.rArm(-90, -90, 90).lArm(-90, 90, -90).head(-10, 0, 0).squat(1))
                .effects(fx -> {
                    if (fx.at(150) || fx.at(850) || fx.at(1550)) {
                        fx.sound(SoundEvents.ANVIL_LAND, 0.25f, 1.9f);
                        fx.burst(ParticleTypes.CRIT, 1.6, 15, 0.3);
                    }
                }));

        reg(Emote.create("heart", "Heart Hands", Rarity.RARE, Items.POPPY, "Spreading the love.")
                .frame(2600, p -> p.rArm(-120, 20, 25).lArm(-120, -20, -25).head(-5, 0, 0))
                .effects(fx -> {
                    if (fx.every(250)) fx.particle(ParticleTypes.HEART, 0, 2.0, 0.7, 0, 0.05, 0);
                    if (fx.at(0)) fx.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1f, 1.2f);
                })
                .props(ctx -> {
                    // big blocky pixel heart floating in front of the hands, pulsing in two sizes
                    ctx.root();
                    ctx.d.move(0, -14, -9);
                    ctx.d.scale(ctx.step(300) % 2 == 0 ? 1f : 1.25f);
                    String[] art = {".XX.XX.", "XXXXXXX", "XXXXXXX", ".XXXXX.", "..XXX..", "...X..."};
                    for (int row = 0; row < art.length; row++) {
                        for (int col = 0; col < 7; col++) {
                            if (art[row].charAt(col) == 'X') {
                                float x = (col - 3.5f) * 1.2f, y = row * 1.2f;
                                ctx.d.glow(x, y, -0.6f, x + 1.2f, y + 1.2f, 0.6f, 0xFFFF2050);
                            }
                        }
                    }
                    ctx.end();
                }));

        reg(Emote.create("headbang", "Rock Out", Rarity.RARE, Items.NOTE_BLOCK, "Shred the blocky guitar.")
                .loop(0)
                .frame(130, p -> p.head(35, 0, 0).body(10, 0, 0).rArm(-40, 0, 20).lArm(-70, 40, 0).squat(1))
                .frame(130, p -> p.head(-15, 0, 0).body(0, 0, 0).rArm(-20, 0, 35).lArm(-70, 40, 0))
                .effects(fx -> {
                    if (fx.at(0)) {
                        fx.particleLocal(ParticleTypes.NOTE, -0.5, 1.6, 0.4, fx.random.nextDouble(), 0, 0);
                        fx.sound(SoundEvents.NOTE_BLOCK_GUITAR.value(), 0.7f, 0.5f + fx.random.nextFloat());
                    }
                })
                .props(ctx -> {
                    ctx.body();
                    ctx.d.move(0, 8, -3.5f);
                    ctx.d.rotZ(-35);
                    ctx.d.box(-4, -3, -1, 4, 3, 1, 0xFFC0182A);      // body
                    ctx.d.box(-2, -4.5f, -1, 2, 4.5f, 1, 0xFFC0182A);
                    ctx.d.box(-1, -1, -1.3f, 1, 1, -1, 0xFF111111);   // sound hole
                    ctx.d.box(4, -0.6f, -0.6f, 15, 0.6f, 0.6f, 0xFF6B4423); // neck
                    ctx.d.box(15, -1.2f, -0.8f, 18, 1.2f, 0.8f, 0xFF3A2414); // headstock
                    ctx.end();
                }));

        reg(Emote.create("salute", "Sword Salute", Rarity.RARE, Items.DIAMOND_SWORD, "For the realm!")
                .frame(200, p -> p.rArm(-40, 0, 0))
                .frame(1200, p -> p.rArm(-160, -20, 0).head(-15, 0, 0))
                .frame(450, p -> p.rArm(-95, 0, 0).head(0, 0, 0))
                .frame(250, p -> p.rArm(-30, 0, 0))
                .effects(fx -> {
                    if (fx.at(200)) {
                        fx.sound(SoundEvents.PLAYER_ATTACK_STRONG, 0.8f, 0.9f);
                        for (int i = 0; i < 8; i++) fx.particle(ParticleTypes.ELECTRIC_SPARK, -0.35, 2.6 + i * 0.12, 0, 0, 0.02, 0);
                    }
                    if (fx.at(1400)) fx.particleLocal(ParticleTypes.SWEEP_ATTACK, 0, 1.3, 1.0, 0, 0, 0);
                })
                .props(ctx -> {
                    ctx.rightHand();
                    ctx.d.rotX(180); // blade points away from the shoulder
                    ctx.d.box(-0.5f, -3, -0.5f, 0.5f, 1, 0.5f, 0xFF4A2F1A); // grip
                    ctx.d.box(-2.5f, -3.8f, -0.6f, 2.5f, -3, 0.6f, 0xFF2BC7B0); // guard
                    ctx.d.box(-0.9f, -16, -0.4f, 0.9f, -3.8f, 0.4f, 0xFF7FFFEF); // blade
                    ctx.d.glow(-0.3f, -16.5f, -0.45f, 0.3f, -4f, 0.45f, 0xFF207060);
                    ctx.end();
                }));

        // ---------------------------------------------------------------- EPIC
        reg(Emote.create("boombox", "Boombox Groove", Rarity.EPIC, Items.JUKEBOX, "Drop the beat. Literally.")
                .loop(0)
                .frames(e -> {
                    for (int i = 0; i < 4; i++) {
                        boolean l = i % 2 == 0;
                        e.frame(150, p -> p.squat(2).rArm(-160, 0, 15).lArm(-20, 0, l ? -40 : -10).head(15, l ? 20 : -20, 0).body(0, l ? 15 : -15, 0));
                        e.frame(150, p -> p.rArm(-170, 0, 10).lArm(-60, 0, l ? -60 : -20).head(-10, l ? 20 : -20, 0).body(0, l ? 10 : -10, 0));
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
                    ctx.d.move(0, 0, 0);
                    float pump = ctx.step(150) % 2 == 0 ? 1f : 1.12f;
                    ctx.d.box(-1, -1, -0.5f, 1, 0, 0.5f, 0xFF333333);                // handle
                    ctx.d.box(-8, 0, -2.5f, 8, 8, 2.5f, 0xFF2B2B2E);                // case
                    ctx.d.push();
                    ctx.d.move(-4.5f, 4.5f, -2.6f);
                    ctx.d.scale(pump);
                    ctx.d.box(-2.5f, -2.5f, -0.4f, 2.5f, 2.5f, 0.2f, 0xFF0E0E0E);     // left speaker
                    ctx.d.glow(-1, -1, -0.6f, 1, 1, 0f, 0xFF20D0FF);
                    ctx.d.pop();
                    ctx.d.push();
                    ctx.d.move(4.5f, 4.5f, -2.6f);
                    ctx.d.scale(pump);
                    ctx.d.box(-2.5f, -2.5f, -0.4f, 2.5f, 2.5f, 0.2f, 0xFF0E0E0E);     // right speaker
                    ctx.d.glow(-1, -1, -0.6f, 1, 1, 0f, 0xFFFF30D0);
                    ctx.d.pop();
                    ctx.d.glow(-1.5f, 1, -2.7f, 1.5f, 2, -2.4f, ctx.step(300) % 2 == 0 ? 0xFF40FF60 : 0xFF105020); // display
                    ctx.end();
                }));

        reg(Emote.create("breakdance", "Windmill", Rarity.EPIC, Items.MUSIC_DISC_CAT, "Spin to win, 45 degrees at a time.")
                .frame(150, p -> p.squat(4).rArm(-60, 0, 30).lArm(-60, 0, -30))
                .frames(e -> {
                    for (int i = 0; i < 24; i++) {
                        float spin = i * 45f;
                        boolean odd = i % 2 == 1;
                        e.frame(75, p -> p.spin(spin).roll(odd ? 70 : 90).lift(0.35f)
                                .rArm(-180, 0, 0).lArm(0, 0, odd ? -20 : -40)
                                .rLeg(odd ? -60 : 20, 0, 40).lLeg(odd ? 20 : -60, 0, -40));
                    }
                })
                .frame(600, p -> p.squat(3).rArm(-150, 0, 50).lArm(-150, 0, -50).head(-15, 0, 0))
                .effects(fx -> {
                    if (fx.between(150, 1950) && fx.every(150)) fx.ring(ParticleTypes.CLOUD, 0.15, 0.6, 6, 0.08, 0);
                    if (fx.at(1950)) {
                        fx.sound(SoundEvents.PLAYER_LEVELUP, 0.6f, 1.5f);
                        fx.burst(ParticleTypes.FIREWORK, 1.2, 25, 0.25);
                    }
                }));

        reg(Emote.create("backflip", "Backflip", Rarity.EPIC, Items.FEATHER, "Parkour! In 8 frames.")
                .frame(250, p -> p.squat(5).rArm(30, 0, 10).lArm(30, 0, -10))
                .frames(e -> {
                    float[] lifts = {0.4f, 0.9f, 1.2f, 1.35f, 1.35f, 1.2f, 0.9f, 0.4f};
                    for (int i = 0; i < 8; i++) {
                        float pitch = -(i + 1) * 45f;
                        float lift = lifts[i];
                        e.frame(70, p -> p.pitch(pitch).lift(lift).squat(3).rArm(-170, 0, 20).lArm(-170, 0, -20));
                    }
                })
                .frame(220, p -> p.squat(5).rArm(-90, 0, 40).lArm(-90, 0, -40))
                .frame(700, p -> p.rArm(-170, 0, 30).lArm(-170, 0, -30).head(-15, 0, 0))
                .effects(fx -> {
                    if (fx.at(250)) {
                        fx.ring(ParticleTypes.CLOUD, 0.1, 0.4, 10, 0.12, 0.02);
                        fx.sound(SoundEvents.GOAT_LONG_JUMP, 1f, 1.2f);
                    }
                    if (fx.between(250, 810)) fx.particle(ParticleTypes.CRIT, 0, 0.8 + fx.lift(), 0, 0, 0, 0);
                    if (fx.at(810)) {
                        fx.ring(ParticleTypes.CLOUD, 0.1, 0.4, 14, 0.18, 0.02);
                        fx.sound(SoundEvents.PLAYER_SMALL_FALL, 1f, 1f);
                    }
                }));

        reg(Emote.create("raincloud", "Rain Cloud", Rarity.EPIC, Items.WATER_BUCKET, "When it's just not your day.")
                .frame(4000, p -> p.head(30, 0, 0).rArm(5, 0, 5).lArm(5, 0, -5).body(8, 0, 0))
                .effects(fx -> {
                    if (fx.every(100)) {
                        for (int i = 0; i < 2; i++) {
                            fx.particle(ParticleTypes.FALLING_WATER, (fx.random.nextDouble() - 0.5) * 0.9, 2.65,
                                    (fx.random.nextDouble() - 0.5) * 0.9, 0, 0, 0);
                        }
                    }
                    if (fx.at(1200) || fx.at(3000)) fx.sound(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.25f, 1.8f);
                })
                .props(ctx -> {
                    ctx.root();
                    float bob = ctx.step(500) % 2 == 0 ? 0 : -0.5f;
                    ctx.d.move(0, -21 + bob, 0);
                    ctx.d.box(-7, -1, -5, 7, 2, 5, 0xFF8A8F99);
                    ctx.d.box(-5, -3, -3, 3, -1, 4, 0xFF9AA0AA);
                    ctx.d.box(-1, -4, -4, 5, -1, 2, 0xFF7C818A);
                    ctx.d.box(-6, 2, -4, 6, 3, 4, 0xFF5E626B);
                    if (ctx.ms % 1800 < 120) ctx.d.glow(-1, 3, -0.5f, 1, 9, 0.5f, 0xFFFFFF80); // tiny lightning flash
                    ctx.end();
                }));

        reg(Emote.create("zen", "Zen Mode", Rarity.EPIC, Items.ENCHANTING_TABLE, "Inner peace. Outer levitation.")
                .loop(0)
                .frame(500, p -> p.lift(0.45f).rLeg(-90, 35, 0).lLeg(-90, -35, 0).rArm(-35, 0, 25).lArm(-35, 0, -25)
                        .head(-5, 0, 0).rLegPos(0, 0, 0))
                .frame(500, p -> p.lift(0.55f).rLeg(-90, 35, 0).lLeg(-90, -35, 0).rArm(-35, 0, 25).lArm(-35, 0, -25)
                        .head(-5, 0, 0))
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
                    for (int i = 0; i < 4; i++) {
                        float a = (float) Math.toRadians(s * 22.5 + i * 90);
                        float x = (float) Math.cos(a) * 14, z = (float) Math.sin(a) * 14;
                        int col = i % 2 == 0 ? 0xFF55FFE0 : 0xFFB070FF;
                        ctx.d.glowCube(x, 4, z, 1.2f, col);
                    }
                    ctx.end();
                }));

        // ---------------------------------------------------------------- LEGENDARY
        reg(Emote.create("trophy", "Champion", Rarity.LEGENDARY, Items.GOLD_BLOCK, "GG EZ. Hoist the golden cup.")
                .frame(250, p -> p.squat(2).rArm(-40, 0, 0).lArm(-40, 0, 0))
                .frame(1800, p -> p.rArm(-175, 0, 0).lArm(-30, 0, -50).head(-25, 0, 0))
                .frame(150, p -> p.rArm(-175, 0, 0).lArm(-30, 0, -50).head(-25, 0, 0).lift(0.15f))
                .frame(150, p -> p.rArm(-175, 0, 0).lArm(-30, 0, -50).head(-25, 0, 0))
                .frame(150, p -> p.rArm(-175, 0, 0).lArm(-30, 0, -50).head(-25, 0, 0).lift(0.15f))
                .frame(500, p -> p.rArm(-175, 0, 0).lArm(-30, 0, -50).head(-25, 0, 0))
                .effects(fx -> {
                    if (fx.at(250)) {
                        fx.sound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.7f, 1f);
                        fx.burst(ParticleTypes.TOTEM_OF_UNDYING, 2.6, 40, 0.35);
                    }
                    if (fx.between(250, 3000) && fx.every(100)) {
                        fx.particle(ParticleTypes.WAX_OFF, (fx.random.nextDouble() - 0.5), 3.0, (fx.random.nextDouble() - 0.5), 0, 0.05, 0);
                    }
                })
                .props(ctx -> {
                    ctx.rightHand();
                    ctx.d.rotX(180); // cup opens away from the shoulder (upward while raised)
                    int gold = 0xFFFFC62E, dark = 0xFFCC8A10;
                    ctx.d.box(-3, 0, -3, 3, 1.5f, 3, dark);           // base
                    ctx.d.box(-1, -3, -1, 1, 0, 1, gold);              // stem
                    ctx.d.box(-4, -9, -4, 4, -3, 4, gold);             // cup
                    ctx.d.box(-3, -9.2f, -3, 3, -8, 3, 0xFF5A3A00);    // inside
                    ctx.d.box(-6.5f, -8, -0.6f, -4, -7, 0.6f, gold);   // handles
                    ctx.d.box(-6.5f, -7, -0.6f, -5.5f, -4.5f, 0.6f, gold);
                    ctx.d.box(4, -8, -0.6f, 6.5f, -7, 0.6f, gold);
                    ctx.d.box(5.5f, -7, -0.6f, 6.5f, -4.5f, 0.6f, gold);
                    if (ctx.step(200) % 2 == 0) ctx.d.glow(-1, -7, -4.3f, 1, -5, -4f, 0xFFFFFFFF); // shine
                    ctx.end();
                }));

        reg(Emote.create("pyro", "Pyromancer", Rarity.LEGENDARY, Items.BLAZE_POWDER, "Juggle fire. Summon a fire ring. Be cool about it.")
                .frame(300, p -> p.rArm(-90, 0, 0).lArm(-90, 0, 0).squat(1))
                .frame(250, p -> p.rArm(-110, 0, 20).lArm(-110, 0, -20))
                .frame(250, p -> p.rArm(-80, 0, 10).lArm(-80, 0, -10))
                .frame(250, p -> p.rArm(-110, 0, 20).lArm(-110, 0, -20))
                .frame(250, p -> p.rArm(-80, 0, 10).lArm(-80, 0, -10))
                .frame(200, p -> p.squat(4).rArm(-20, 0, 40).lArm(-20, 0, -40))
                .frame(1600, p -> p.rArm(-175, 0, 30).lArm(-175, 0, -30).head(-25, 0, 0))
                .effects(fx -> {
                    if (fx.between(0, 1500) && fx.every(50)) {
                        fx.particleLocal(ParticleTypes.FLAME, 0.35, 1.5, 0.5, 0, 0.02, 0);
                        fx.particleLocal(ParticleTypes.FLAME, -0.35, 1.5, 0.5, 0, 0.02, 0);
                    }
                    if (fx.at(1500)) {
                        fx.sound(SoundEvents.BLAZE_SHOOT, 1f, 0.7f);
                        fx.ring(ParticleTypes.FLAME, 0.1, 0.5, 40, 0.25, 0.0);
                        fx.ring(ParticleTypes.LAVA, 0.1, 1.0, 10, 0.0, 0.0);
                    }
                    if (fx.between(1700, 3300) && fx.every(100)) {
                        fx.ring(ParticleTypes.FLAME, 0.1, 2.2, 24, 0, 0.12);
                        fx.spiral(ParticleTypes.SMALL_FLAME, 2.5, 0.3, fx.now * 0.6, 0.1);
                    }
                    if (fx.at(1700)) fx.sound(SoundEvents.FIRECHARGE_USE, 1f, 0.6f);
                })
                .props(ctx -> {
                    if (ctx.ms >= 1500) return;
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
                .frame(300, p -> p.squat(3).rArm(-60, 0, 0).lArm(-60, 0, 0))
                .frames(e -> {
                    for (int i = 0; i < 6; i++) {
                        boolean l = i % 2 == 0;
                        e.frame(250, p -> p.lift(l ? 0.3f : 0f).rArm(-170, 0, l ? 30 : 10).lArm(-170, 0, l ? -10 : -30).head(-30, 0, 0));
                    }
                })
                .frame(600, p -> p.rArm(-150, 0, 60).lArm(-150, 0, -60).head(-30, 0, 0))
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

        // ---------------------------------------------------------------- SUPREME
        reg(SupremeEmote.create());

        ALL = Collections.unmodifiableList(new ArrayList<>(BY_ID.values()));
    }
}

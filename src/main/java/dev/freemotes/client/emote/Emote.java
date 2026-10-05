package dev.freemotes.client.emote;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.world.item.Item;

/**
 * A stepped-keyframe emote. The timeline is sampled with millisecond precision, but poses are never
 * interpolated: every keyframe snaps in instantly and holds until the next one. That gives the chonky,
 * vanilla-Minecraft look instead of smooth tweening.
 */
public final class Emote {
    public enum Rarity {
        COMMON("Common", 0xFFB0B0B0, 400),
        RARE("Rare", 0xFF4FA3FF, 900),
        EPIC("Epic", 0xFFB55CFF, 1800),
        LEGENDARY("Legendary", 0xFFFFB12E, 3200),
        SUPREME("SUPREME", 0xFFFF3B6B, 99999);

        public final String label;
        public final int color;
        /** The "price" it would have had. It is crossed out in the shop because everything is free. */
        public final int fakePrice;

        Rarity(String label, int color, int fakePrice) {
            this.label = label;
            this.color = color;
            this.fakePrice = fakePrice;
        }
    }

    @FunctionalInterface
    public interface Effects {
        void tick(Fx fx);
    }

    @FunctionalInterface
    public interface Props {
        void render(PropCtx ctx);
    }

    public final String id;
    public final String name;
    public final String description;
    public final Rarity rarity;
    public final Item icon;
    final List<Pose> poses = new ArrayList<>();
    final List<Integer> durations = new ArrayList<>();
    boolean loop;
    int totalMs;
    /** Looping emotes restart here (ms), so intro frames before it play only once. */
    int loopStart;
    /** For looping emotes: how long they play in total before ending on their own (0 = until you move). */
    int playFor;
    Effects effects = fx -> {};
    Props props = ctx -> {};

    private Pose cursor = new Pose();

    private Emote(String id, String name, String description, Rarity rarity, Item icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.icon = icon;
    }

    public static Emote create(String id, String name, Rarity rarity, Item icon, String description) {
        return new Emote(id, name, description, rarity, icon);
    }

    /** Adds a keyframe built from scratch. */
    public Emote frame(int ms, UnaryOperator<Pose> pose) {
        cursor = pose.apply(new Pose());
        return add(ms, cursor);
    }

    /** Adds a keyframe that starts as a copy of the previous one. */
    public Emote then(int ms, UnaryOperator<Pose> pose) {
        cursor = pose.apply(cursor.copy());
        return add(ms, cursor);
    }

    /** Lets code generate many frames (procedural dances). */
    public Emote frames(Consumer<Emote> generator) {
        generator.accept(this);
        return this;
    }

    private Emote add(int ms, Pose pose) {
        poses.add(pose);
        durations.add(ms);
        totalMs += ms;
        return this;
    }

    /** Frames added after this call form the loop; earlier frames are a one-time intro. */
    public Emote loopFromHere() {
        this.loopStart = totalMs;
        return this;
    }

    public Emote loop(int playForMs) {
        this.loop = true;
        this.playFor = playForMs;
        return this;
    }

    public Emote effects(Effects effects) {
        this.effects = effects;
        return this;
    }

    public Emote props(Props props) {
        this.props = props;
        return this;
    }

    public boolean loops() {
        return loop;
    }

    /** Total play time in ms, or -1 if it loops until the player moves. */
    public long lengthMs() {
        if (loop) return playFor > 0 ? playFor : -1;
        return totalMs;
    }

    /** Maps elapsed time onto the timeline (wrapping loops back to {@link #loopStart}). */
    public long timeInCycle(long elapsedMs) {
        if (!loop) return Math.min(elapsedMs, totalMs - 1);
        if (elapsedMs < totalMs) return Math.max(0, elapsedMs);
        return loopStart + Math.floorMod(elapsedMs - loopStart, (long) (totalMs - loopStart));
    }

    /** Length of the repeating part of a looping emote. */
    long cycleMs() {
        return totalMs - loopStart;
    }

    public Pose poseAt(long elapsedMs) {
        if (poses.isEmpty()) return new Pose();
        long t = timeInCycle(elapsedMs);
        long acc = 0;
        for (int i = 0; i < poses.size(); i++) {
            acc += durations.get(i);
            if (t < acc) return poses.get(i);
        }
        return poses.get(poses.size() - 1);
    }

    public void tickEffects(Fx fx) {
        effects.tick(fx);
    }

    public void renderProps(PropCtx ctx) {
        props.render(ctx);
    }
}

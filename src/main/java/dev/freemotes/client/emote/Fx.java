package dev.freemotes.client.emote;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

/** Particle/sound context, run every client tick for every player that is emoting. */
public final class Fx {
    public final ClientLevel level;
    public final Player player;
    public final Emote emote;
    /** Elapsed ms at the previous tick (-1 on the first) and now. */
    public final long prev, now;
    public final RandomSource random;
    private final double sin, cos;

    public Fx(ClientLevel level, Player player, Emote emote, long prev, long now) {
        this.level = level;
        this.player = player;
        this.emote = emote;
        this.prev = prev;
        this.now = now;
        this.random = level.getRandom();
        double yaw = Math.toRadians(player.yBodyRot);
        this.sin = Math.sin(yaw);
        this.cos = Math.cos(yaw);
    }

    /** True exactly once, on the tick that passes {@code ms}. Inside loops it fires on every pass. */
    public boolean at(long ms) {
        if (prev < ms && ms <= now) return true;
        if (!emote.loops() || ms < emote.loopStart || ms >= emote.totalMs) return false;
        long cycle = emote.cycleMs();
        // later repetitions of a looped timestamp happen at ms + k*cycle
        long k = Math.max(1, (prev - ms) / cycle);
        for (long t = ms + k * cycle; t <= now; t += cycle) {
            if (t > prev) return true;
        }
        return false;
    }

    public boolean every(long periodMs) {
        return prev < 0 || prev / periodMs != now / periodMs;
    }

    public boolean between(long from, long to) {
        long t = emote.loops() ? emote.timeInCycle(now) : now;
        return t >= from && t < to;
    }

    /** Current whole-body lift (blocks) so effects can follow a floating player. */
    public float lift() {
        return emote.poseAt(now).lift;
    }

    public double x(double right, double fwd) {
        return player.getX() - cos * right - sin * fwd;
    }

    public double z(double right, double fwd) {
        return player.getZ() - sin * right + cos * fwd;
    }

    public double y(double up) {
        return player.getY() + up;
    }

    /** Spawns a particle at a position relative to the player (right, up, forward) with a world velocity. */
    public void particle(ParticleOptions p, double right, double up, double fwd, double vx, double vy, double vz) {
        level.addParticle(p, x(right, fwd), y(up), z(right, fwd), vx, vy, vz);
    }

    /** Particle with velocity relative to the player's facing too. */
    public void particleLocal(ParticleOptions p, double right, double up, double fwd, double vRight, double vy, double vFwd) {
        double vx = -cos * vRight - sin * vFwd;
        double vz = -sin * vRight + cos * vFwd;
        level.addParticle(p, x(right, fwd), y(up), z(right, fwd), vx, vy, vz);
    }

    /** Random explosion of particles from a point. */
    public void burst(ParticleOptions p, double up, int count, double speed) {
        for (int i = 0; i < count; i++) {
            double vx = (random.nextDouble() - 0.5) * 2 * speed;
            double vy = (random.nextDouble() - 0.5) * 2 * speed;
            double vz = (random.nextDouble() - 0.5) * 2 * speed;
            level.addParticle(p, player.getX(), y(up), player.getZ(), vx, vy, vz);
        }
    }

    /** Flat ring of particles around the player, flying outward. */
    public void ring(ParticleOptions p, double up, double radius, int count, double outward, double vy) {
        for (int i = 0; i < count; i++) {
            double a = Math.PI * 2 * i / count;
            double dx = Math.cos(a), dz = Math.sin(a);
            level.addParticle(p, player.getX() + dx * radius, y(up), player.getZ() + dz * radius, dx * outward, vy, dz * outward);
        }
    }

    /** Point on a helix around the player. */
    public void spiral(ParticleOptions p, double up, double radius, double angleDeg, double vy) {
        double a = Math.toRadians(angleDeg);
        level.addParticle(p, player.getX() + Math.cos(a) * radius, y(up), player.getZ() + Math.sin(a) * radius, 0, vy, 0);
    }

    public void sound(SoundEvent sound, float volume, float pitch) {
        level.playLocalSound(player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, volume, pitch, false);
    }
}

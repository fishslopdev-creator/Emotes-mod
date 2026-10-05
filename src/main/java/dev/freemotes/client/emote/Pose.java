package dev.freemotes.client.emote;

/**
 * One keyframe pose. Rotations are degrees, offsets are model pixels (1/16 block).
 * Parts that are not set stay neutral, except the head which then keeps following the camera.
 * Whole-body transforms (lift, spin, pitch, roll) are applied to the entire player.
 */
public final class Pose {
    public static final int HEAD = 0, BODY = 1, RIGHT_ARM = 2, LEFT_ARM = 3, RIGHT_LEG = 4, LEFT_LEG = 5;
    public static final int PARTS = 6;

    /** [part][xRot, yRot, zRot, dx, dy, dz] */
    public final float[][] parts = new float[PARTS][6];
    public final boolean[] set = new boolean[PARTS];
    /** Blocks the whole body floats up. */
    public float lift;
    /** Whole-body rotations in degrees (yaw around vertical axis, pitch = forward flip, roll = cartwheel). */
    public float spin, pitch, roll;

    public Pose copy() {
        Pose p = new Pose();
        for (int i = 0; i < PARTS; i++) {
            System.arraycopy(parts[i], 0, p.parts[i], 0, 6);
            p.set[i] = set[i];
        }
        p.lift = lift;
        p.spin = spin;
        p.pitch = pitch;
        p.roll = roll;
        return p;
    }

    private Pose rot(int part, float x, float y, float z) {
        parts[part][0] = x;
        parts[part][1] = y;
        parts[part][2] = z;
        set[part] = true;
        return this;
    }

    private Pose off(int part, float x, float y, float z) {
        parts[part][3] = x;
        parts[part][4] = y;
        parts[part][5] = z;
        set[part] = true;
        return this;
    }

    public Pose head(float x, float y, float z) { return rot(HEAD, x, y, z); }
    public Pose body(float x, float y, float z) { return rot(BODY, x, y, z); }
    public Pose rArm(float x, float y, float z) { return rot(RIGHT_ARM, x, y, z); }
    public Pose lArm(float x, float y, float z) { return rot(LEFT_ARM, x, y, z); }
    public Pose rLeg(float x, float y, float z) { return rot(RIGHT_LEG, x, y, z); }
    public Pose lLeg(float x, float y, float z) { return rot(LEFT_LEG, x, y, z); }

    public Pose headPos(float x, float y, float z) { return off(HEAD, x, y, z); }
    public Pose bodyPos(float x, float y, float z) { return off(BODY, x, y, z); }
    public Pose rArmPos(float x, float y, float z) { return off(RIGHT_ARM, x, y, z); }
    public Pose lArmPos(float x, float y, float z) { return off(LEFT_ARM, x, y, z); }
    public Pose rLegPos(float x, float y, float z) { return off(RIGHT_LEG, x, y, z); }
    public Pose lLegPos(float x, float y, float z) { return off(LEFT_LEG, x, y, z); }

    /** Crouch-ish squat: legs bend, upper body drops by {@code px} pixels. */
    public Pose squat(float px) {
        float legAngle = (float) Math.toDegrees(Math.acos(Math.max(-1f, 1f - px / 12f)));
        rLeg(-legAngle, 0, 8).lLeg(-legAngle, 0, -8);
        rLegPos(0, px, 0).lLegPos(0, px, 0);
        headPos(0, px, 0).bodyPos(0, px, 0).rArmPos(0, px, 0).lArmPos(0, px, 0);
        return this;
    }

    public Pose lift(float blocks) { this.lift = blocks; return this; }
    public Pose spin(float deg) { this.spin = deg; return this; }
    public Pose pitch(float deg) { this.pitch = deg; return this; }
    public Pose roll(float deg) { this.roll = deg; return this; }
}

package dev.freemotes.client.emote;

import dev.freemotes.client.render.Draw;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

/**
 * Context handed to emote props and cosmetics. All attach methods leave the pose in model pixel units
 * (y points down, -z is the direction the player faces) and must be closed with {@link #end()}.
 */
public final class PropCtx {
    public final Draw d;
    public final HumanoidModel<?> model;
    /** Milliseconds since the emote (or, for cosmetics, the game) started. */
    public final long ms;
    /** How many blocks the body is currently lifted by the emote. */
    public final float lift;

    public PropCtx(Draw d, HumanoidModel<?> model, long ms, float lift) {
        this.d = d;
        this.model = model;
        this.ms = ms;
        this.lift = lift;
    }

    private PropCtx attach(ModelPart part) {
        d.push();
        part.translateAndRotate(d.ps);
        d.scale(1f / 16f);
        return this;
    }

    /** Origin at the neck pivot, following the body's whole-body transforms but not the head. */
    public PropCtx root() {
        d.push();
        d.scale(1f / 16f);
        return this;
    }

    public PropCtx head() {
        return attach(model.head);
    }

    public PropCtx body() {
        return attach(model.body);
    }

    /** Origin in the palm of the right hand. */
    public PropCtx rightHand() {
        attach(model.rightArm);
        d.move(-1f, 10f, 0f);
        return this;
    }

    /** Origin in the palm of the left hand. */
    public PropCtx leftHand() {
        attach(model.leftArm);
        d.move(1f, 10f, 0f);
        return this;
    }

    public void end() {
        d.pop();
    }

    /** Stepped counter: increments once every {@code periodMs}. Use it to animate in chunky jumps. */
    public long step(long periodMs) {
        return ms / periodMs;
    }

    /** Model-pixel y of the ground relative to {@link #root()}. */
    public float groundY() {
        return 24f + lift * 16f;
    }
}

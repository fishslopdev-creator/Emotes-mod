package dev.freemotes.client.render;

import dev.freemotes.client.emote.Pose;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

/** Writes a stepped pose straight onto the model parts - no blending with the previous frame. */
public final class EmotePoser {
    private static final float RAD = (float) (Math.PI / 180.0);

    private EmotePoser() {}

    public static void apply(HumanoidModel<?> model, Pose pose) {
        ModelPart[] parts = {model.head, model.body, model.rightArm, model.leftArm, model.rightLeg, model.leftLeg};
        for (int i = 0; i < Pose.PARTS; i++) {
            if (i == Pose.HEAD && !pose.set[i]) continue; // head keeps looking around
            ModelPart part = parts[i];
            PartPose initial = part.getInitialPose();
            float[] v = pose.parts[i];
            part.xRot = v[0] * RAD;
            part.yRot = v[1] * RAD;
            part.zRot = v[2] * RAD;
            part.x = initial.x() + v[3];
            part.y = initial.y() + v[4];
            part.z = initial.z() + v[5];
        }
    }
}

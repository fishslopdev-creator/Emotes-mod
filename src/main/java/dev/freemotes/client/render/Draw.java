package dev.freemotes.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Tiny voxel drawing helper. Coordinates are in whatever units the current pose uses (usually model pixels). */
public final class Draw {
    public static final int FULL_BRIGHT = 0xF000F0;

    // normal axis, normal sign, u axis, v axis   (u x v == normal so quads wind counter-clockwise)
    private static final int[][] FACES = {
            {0, 1, 1, 2}, {0, -1, 2, 1},
            {1, 1, 2, 0}, {1, -1, 0, 2},
            {2, 1, 0, 1}, {2, -1, 1, 0}
    };
    private static final int[][] CORNERS = {{-1, -1}, {1, -1}, {1, 1}, {-1, 1}};

    public final PoseStack ps;
    private final SubmitNodeCollector collector;
    private final int light;

    public Draw(PoseStack ps, SubmitNodeCollector collector, int light) {
        this.ps = ps;
        this.collector = collector;
        this.light = light;
    }

    /** Opaque lit box. */
    public void box(float x1, float y1, float z1, float x2, float y2, float z2, int argb) {
        submit(FMRenderTypes.solid(), x1, y1, z1, x2, y2, z2, argb, light);
    }

    /** Additive glowing box (alpha is ignored by the blend, so darker colors = fainter glow). */
    public void glow(float x1, float y1, float z1, float x2, float y2, float z2, int argb) {
        submit(FMRenderTypes.glow(), x1, y1, z1, x2, y2, z2, argb, FULL_BRIGHT);
    }

    /** Translucent full-bright box. */
    public void ghost(float x1, float y1, float z1, float x2, float y2, float z2, int argb) {
        submit(FMRenderTypes.ghost(), x1, y1, z1, x2, y2, z2, argb, FULL_BRIGHT);
    }

    /** Cube centered on a point. */
    public void cube(float cx, float cy, float cz, float half, int argb) {
        box(cx - half, cy - half, cz - half, cx + half, cy + half, cz + half, argb);
    }

    public void glowCube(float cx, float cy, float cz, float half, int argb) {
        glow(cx - half, cy - half, cz - half, cx + half, cy + half, cz + half, argb);
    }

    public void push() {
        ps.pushPose();
    }

    public void pop() {
        ps.popPose();
    }

    public void move(float x, float y, float z) {
        ps.translate(x, y, z);
    }

    public void rotX(float deg) {
        ps.mulPose(Axis.XP.rotationDegrees(deg));
    }

    public void rotY(float deg) {
        ps.mulPose(Axis.YP.rotationDegrees(deg));
    }

    public void rotZ(float deg) {
        ps.mulPose(Axis.ZP.rotationDegrees(deg));
    }

    public void scale(float s) {
        ps.scale(s, s, s);
    }

    private void submit(RenderType type, float x1, float y1, float z1, float x2, float y2, float z2, int argb, int packedLight) {
        final float[] c = {(x1 + x2) / 2f, (y1 + y2) / 2f, (z1 + z2) / 2f};
        final float[] h = {Math.abs(x2 - x1) / 2f, Math.abs(y2 - y1) / 2f, Math.abs(z2 - z1) / 2f};
        collector.submitCustomGeometry(ps, type, (pose, vc) -> emitBox(pose, vc, c, h, argb, packedLight));
    }

    private static void emitBox(PoseStack.Pose pose, VertexConsumer vc, float[] c, float[] h, int argb, int packedLight) {
        float[] p = new float[3];
        float[] n = new float[3];
        for (int[] face : FACES) {
            int a = face[0], s = face[1], u = face[2], v = face[3];
            n[0] = n[1] = n[2] = 0f;
            n[a] = s;
            for (int[] corner : CORNERS) {
                p[0] = c[0];
                p[1] = c[1];
                p[2] = c[2];
                p[a] += s * h[a];
                p[u] += corner[0] * h[u];
                p[v] += corner[1] * h[v];
                vc.addVertex(pose, p[0], p[1], p[2])
                        .setColor(argb)
                        .setUv((corner[0] + 1) * 0.5f, (corner[1] + 1) * 0.5f)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(pose, n[0], n[1], n[2]);
            }
        }
    }
}

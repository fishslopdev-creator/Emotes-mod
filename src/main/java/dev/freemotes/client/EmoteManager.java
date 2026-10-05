package dev.freemotes.client;

import dev.freemotes.client.emote.Emote;
import dev.freemotes.client.emote.Emotes;
import dev.freemotes.client.emote.Fx;
import dev.freemotes.net.FMPayloads;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Tracks which players are emoting and since when. Timing uses the millisecond clock, not ticks. */
public final class EmoteManager {
    public static final class Active {
        public final Emote emote;
        public final long startMs;
        long lastFxMs = -1;

        Active(Emote emote, long startMs) {
            this.emote = emote;
            this.startMs = startMs;
        }

        public long elapsed() {
            return now() - startMs;
        }
    }

    private static final Map<UUID, Active> ACTIVE = new HashMap<>();
    private static CameraType cameraBefore;
    private static Vec3 startPos;

    private EmoteManager() {}

    /** Millisecond clock the whole animation system runs on. */
    public static long now() {
        return System.nanoTime() / 1_000_000L;
    }

    public static Active get(UUID player) {
        return ACTIVE.get(player);
    }

    /** Plays an emote on the local player and tells the server so other players see it. */
    public static void playLocal(Emote emote) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        ACTIVE.put(player.getUUID(), new Active(emote, now()));
        startPos = player.position();
        if (FMConfig.get().thirdPersonWhileEmoting && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            cameraBefore = mc.options.getCameraType();
            mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
        }
        send(emote.id);
    }

    public static void stopLocal() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (ACTIVE.remove(mc.player.getUUID()) != null) {
            send("");
        }
        restoreCamera();
    }

    private static void restoreCamera() {
        if (cameraBefore != null) {
            Minecraft.getInstance().options.setCameraType(cameraBefore);
            cameraBefore = null;
        }
    }

    private static void send(String id) {
        if (ClientPlayNetworking.canSend(FMPayloads.PlayEmote.TYPE)) {
            ClientPlayNetworking.send(new FMPayloads.PlayEmote(id));
        }
    }

    /** Called when the server tells us another player started/stopped an emote. */
    public static void onRemote(UUID player, String id) {
        Emote emote = id.isEmpty() ? null : Emotes.get(id);
        if (emote == null) ACTIVE.remove(player);
        else ACTIVE.put(player, new Active(emote, now()));
    }

    public static void clear() {
        ACTIVE.clear();
        restoreCamera();
    }

    public static void tick(Minecraft mc) {
        if (mc.level == null || mc.player == null) {
            if (!ACTIVE.isEmpty()) clear();
            return;
        }
        LocalPlayer self = mc.player;

        // moving, jumping, sneaking or attacking cancels your own emote, like Essential
        Active own = ACTIVE.get(self.getUUID());
        if (own != null && startPos != null && mc.screen == null) {
            boolean input = mc.options.keyUp.isDown() || mc.options.keyDown.isDown() || mc.options.keyLeft.isDown()
                    || mc.options.keyRight.isDown() || mc.options.keyJump.isDown() || mc.options.keyShift.isDown()
                    || mc.options.keyAttack.isDown();
            if (input || self.position().distanceToSqr(startPos) > 0.25 || self.hurtTime > 0) {
                stopLocal();
            }
        }

        Iterator<Map.Entry<UUID, Active>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Active> entry = it.next();
            Active active = entry.getValue();
            long now = active.elapsed();
            long length = active.emote.lengthMs();
            if (length >= 0 && now >= length) {
                it.remove();
                if (entry.getKey().equals(self.getUUID())) {
                    restoreCamera();
                }
                continue;
            }
            Player player = mc.level.getPlayerByUUID(entry.getKey());
            if (player == null) continue;
            active.emote.tickEffects(new Fx(mc.level, player, active.emote, active.lastFxMs, now));
            active.lastFxMs = now;
        }
    }
}

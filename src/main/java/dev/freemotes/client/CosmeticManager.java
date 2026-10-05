package dev.freemotes.client;

import dev.freemotes.client.cosmetic.Cosmetic;
import dev.freemotes.client.cosmetic.Cosmetics;
import dev.freemotes.net.FMPayloads;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

/** Who is wearing what. Your own cosmetics come from the config; everyone else's from the server. */
public final class CosmeticManager {
    private static final Map<UUID, List<Cosmetic>> REMOTE = new HashMap<>();

    private CosmeticManager() {}

    public static List<Cosmetic> forPlayer(UUID uuid) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getUUID().equals(uuid)) return own();
        return REMOTE.getOrDefault(uuid, List.of());
    }

    public static List<Cosmetic> own() {
        List<Cosmetic> list = new ArrayList<>();
        for (String id : FMConfig.get().equipped.values()) {
            Cosmetic c = Cosmetics.get(id);
            if (c != null) list.add(c);
        }
        return list;
    }

    public static void onRemote(UUID uuid, String csv) {
        List<Cosmetic> list = new ArrayList<>();
        for (String id : csv.split(",")) {
            Cosmetic c = Cosmetics.get(id.trim());
            if (c != null) list.add(c);
        }
        REMOTE.put(uuid, list);
    }

    public static void clear() {
        REMOTE.clear();
    }

    /** Sends our equipped cosmetics to the server (if it has the mod). */
    public static void sync() {
        if (ClientPlayNetworking.canSend(FMPayloads.SetCosmetics.TYPE)) {
            ClientPlayNetworking.send(new FMPayloads.SetCosmetics(String.join(",", FMConfig.get().equipped.values())));
        }
    }
}

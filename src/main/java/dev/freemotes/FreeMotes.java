package dev.freemotes;

import dev.freemotes.net.FMPayloads;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entrypoint. The server only relays emote and cosmetic state between players that have the mod,
 * so the mod works on dedicated servers (install it there too) and in singleplayer/LAN.
 */
public class FreeMotes implements ModInitializer {
    public static final String MOD_ID = "freemotes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Map<UUID, String> COSMETICS = new ConcurrentHashMap<>();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(FMPayloads.PlayEmote.TYPE, FMPayloads.PlayEmote.CODEC);
        PayloadTypeRegistry.playC2S().register(FMPayloads.SetCosmetics.TYPE, FMPayloads.SetCosmetics.CODEC);
        PayloadTypeRegistry.playS2C().register(FMPayloads.EmoteState.TYPE, FMPayloads.EmoteState.CODEC);
        PayloadTypeRegistry.playS2C().register(FMPayloads.CosmeticState.TYPE, FMPayloads.CosmeticState.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(FMPayloads.PlayEmote.TYPE, (payload, context) -> {
            String id = payload.emoteId();
            if (id.length() > 64) return;
            ServerPlayer sender = context.player();
            broadcast(context.server(), sender, new FMPayloads.EmoteState(sender.getUUID(), id));
        });

        ServerPlayNetworking.registerGlobalReceiver(FMPayloads.SetCosmetics.TYPE, (payload, context) -> {
            String csv = payload.cosmetics();
            if (csv.length() > 512) return;
            ServerPlayer sender = context.player();
            COSMETICS.put(sender.getUUID(), csv);
            broadcast(context.server(), sender, new FMPayloads.CosmeticState(sender.getUUID(), csv));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer joined = handler.getPlayer();
            if (!ServerPlayNetworking.canSend(joined, FMPayloads.CosmeticState.TYPE)) return;
            COSMETICS.forEach((uuid, csv) -> {
                if (!uuid.equals(joined.getUUID())) {
                    ServerPlayNetworking.send(joined, new FMPayloads.CosmeticState(uuid, csv));
                }
            });
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> COSMETICS.remove(handler.getPlayer().getUUID()));

        LOGGER.info("FreeMotes loaded - everything in the shop is free!");
    }

    private static void broadcast(MinecraftServer server, ServerPlayer except, net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
        for (ServerPlayer player : PlayerLookup.all(server)) {
            if (player != except && ServerPlayNetworking.canSend(player, payload.type())) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }
}

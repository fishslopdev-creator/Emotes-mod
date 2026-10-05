package dev.freemotes.net;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** All network payloads. Emote id "" means "stop emoting"; cosmetics are a comma separated id list. */
public final class FMPayloads {
    private FMPayloads() {}

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> type(String path) {
        return new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("freemotes", path));
    }

    /** Client -> server: I started (or stopped) an emote. */
    public record PlayEmote(String emoteId) implements CustomPacketPayload {
        public static final Type<PlayEmote> TYPE = type("play_emote");
        public static final StreamCodec<RegistryFriendlyByteBuf, PlayEmote> CODEC =
                StreamCodec.composite(ByteBufCodecs.STRING_UTF8, PlayEmote::emoteId, PlayEmote::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Client -> server: my equipped cosmetics. */
    public record SetCosmetics(String cosmetics) implements CustomPacketPayload {
        public static final Type<SetCosmetics> TYPE = type("set_cosmetics");
        public static final StreamCodec<RegistryFriendlyByteBuf, SetCosmetics> CODEC =
                StreamCodec.composite(ByteBufCodecs.STRING_UTF8, SetCosmetics::cosmetics, SetCosmetics::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Server -> client: a player started (or stopped) an emote. */
    public record EmoteState(UUID player, String emoteId) implements CustomPacketPayload {
        public static final Type<EmoteState> TYPE = type("emote_state");
        public static final StreamCodec<RegistryFriendlyByteBuf, EmoteState> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, EmoteState::player, ByteBufCodecs.STRING_UTF8, EmoteState::emoteId, EmoteState::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Server -> client: a player's equipped cosmetics. */
    public record CosmeticState(UUID player, String cosmetics) implements CustomPacketPayload {
        public static final Type<CosmeticState> TYPE = type("cosmetic_state");
        public static final StreamCodec<RegistryFriendlyByteBuf, CosmeticState> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, CosmeticState::player, ByteBufCodecs.STRING_UTF8, CosmeticState::cosmetics, CosmeticState::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}

package dev.freemotes.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.freemotes.client.render.FMLayer;
import dev.freemotes.client.screen.EmoteWheelScreen;
import dev.freemotes.client.screen.ShopScreen;
import dev.freemotes.net.FMPayloads;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EntityType;
import org.lwjgl.glfw.GLFW;

public class FreeMotesClient implements ClientModInitializer {
    /** Hold to open the emote wheel, release over an emote to play it. */
    public static KeyMapping wheelKey;
    /** Opens the shop where everything is free. */
    public static KeyMapping shopKey;

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void onInitializeClient() {
        wheelKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.freemotes.wheel", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, KeyMapping.Category.MISC));
        shopKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.freemotes.shop", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (wheelKey.consumeClick()) {
                if (mc.screen == null && mc.player != null) mc.setScreen(new EmoteWheelScreen());
            }
            while (shopKey.consumeClick()) {
                if (mc.screen == null && mc.player != null) mc.setScreen(new ShopScreen());
            }
            EmoteManager.tick(mc);
        });

        ClientPlayNetworking.registerGlobalReceiver(FMPayloads.EmoteState.TYPE,
                (payload, context) -> EmoteManager.onRemote(payload.player(), payload.emoteId()));
        ClientPlayNetworking.registerGlobalReceiver(FMPayloads.CosmeticState.TYPE,
                (payload, context) -> CosmeticManager.onRemote(payload.player(), payload.cosmetics()));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> CosmeticManager.sync());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            EmoteManager.clear();
            CosmeticManager.clear();
        });

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, renderer, helper, context) -> {
            if (entityType == EntityType.PLAYER) {
                helper.register(new FMLayer((RenderLayerParent) renderer));
            }
        });
    }
}

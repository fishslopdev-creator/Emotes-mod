package dev.freemotes.test;

import dev.freemotes.client.EmoteManager;
import dev.freemotes.client.FMConfig;
import dev.freemotes.client.emote.Emote;
import dev.freemotes.client.emote.Emotes;
import dev.freemotes.client.screen.EmoteWheelScreen;
import dev.freemotes.client.screen.ShopScreen;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/** Boots the real client, plays emotes with cosmetics on, opens the screens and takes screenshots. */
public class FreeMotesClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        context.runOnClient(mc -> {
            FMConfig cfg = FMConfig.get();
            cfg.equipped.put("HEAD", "propeller");
            cfg.equipped.put("FACE", "shades");
            cfg.equipped.put("BACK", "dragon_wings");
        });

        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getClientWorld().waitForChunksToRender();

            for (Emote emote : Emotes.ALL) {
                context.runOnClient(mc -> EmoteManager.playLocal(emote));
                // snapshot at ~1/3 and ~2/3 through (or mid-loop) so props/VFX are on screen
                long len = emote.lengthMs() > 0 ? emote.lengthMs() : 2000;
                context.waitTicks((int) Math.max(2, len / 50 / 3));
                context.takeScreenshot("emote_" + emote.id + "_a");
                if (emote.id.equals("supreme")) {
                    context.waitTicks(40);
                    context.takeScreenshot("emote_supreme_b");
                    context.waitTicks(60);
                    context.takeScreenshot("emote_supreme_c");
                }
                context.runOnClient(mc -> EmoteManager.stopLocal());
                context.waitTick();
            }

            context.setScreen(ShopScreen::new);
            context.waitTicks(5);
            context.takeScreenshot("shop");
            context.setScreen(EmoteWheelScreen::new);
            context.waitTicks(5);
            context.takeScreenshot("wheel");
            context.setScreen(() -> null);
        }
    }
}

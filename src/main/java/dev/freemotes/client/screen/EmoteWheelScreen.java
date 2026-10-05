package dev.freemotes.client.screen;

import dev.freemotes.client.EmoteManager;
import dev.freemotes.client.FMConfig;
import dev.freemotes.client.FreeMotesClient;
import dev.freemotes.client.emote.Emote;
import dev.freemotes.client.emote.Emotes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Hold the wheel key, point at an emote, let go. Tapping the key keeps the wheel open so you can click instead.
 */
public class EmoteWheelScreen extends Screen {
    private static final int RADIUS = 78;
    private static final int SLOT = 34;
    private static final long TAP_MS = 220;

    private final long openedAt = EmoteManager.now();
    private int hovered = -1;

    public EmoteWheelScreen() {
        super(Component.literal("Emote Wheel"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int slotX(int i) {
        double a = Math.toRadians(-90 + i * 45);
        return width / 2 + (int) Math.round(Math.cos(a) * RADIUS) - SLOT / 2;
    }

    private int slotY(int i) {
        double a = Math.toRadians(-90 + i * 45);
        return height / 2 + (int) Math.round(Math.sin(a) * RADIUS) - SLOT / 2;
    }

    private int pick(double mx, double my) {
        double dx = mx - width / 2.0, dy = my - height / 2.0;
        if (dx * dx + dy * dy < 22 * 22) return -1;
        double deg = Math.toDegrees(Math.atan2(dy, dx)) + 90 + 22.5;
        return (int) Math.floorMod((long) Math.floor(deg / 45.0), 8L);
    }

    private Emote emoteIn(int slot) {
        if (slot < 0) return null;
        return Emotes.get(FMConfig.get().wheel.get(slot));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        hovered = pick(mouseX, mouseY);
        int cx = width / 2, cy = height / 2;

        // chunky ring backdrop made of blocks
        for (int i = 0; i < 48; i++) {
            double a = Math.toRadians(i * 7.5);
            int x = cx + (int) Math.round(Math.cos(a) * RADIUS);
            int y = cy + (int) Math.round(Math.sin(a) * RADIUS);
            g.fill(x - 4, y - 4, x + 4, y + 4, 0xAA1A1030);
        }

        for (int i = 0; i < FMConfig.WHEEL_SLOTS; i++) {
            Emote e = emoteIn(i);
            int x = slotX(i), y = slotY(i);
            boolean hot = i == hovered;
            int border = e != null ? e.rarity.color : 0xFF505050;
            int grow = hot ? 3 : 0;
            Ui.panel(g, x - grow, y - grow, SLOT + grow * 2, SLOT + grow * 2, hot ? 0xF0402A70 : 0xE0201830, hot ? 0xFFFFFFFF : border);
            if (e != null) {
                g.pose().pushMatrix();
                g.pose().translate(x + SLOT / 2f - 12, y + SLOT / 2f - 12);
                g.pose().scale(1.5f, 1.5f);
                g.renderItem(new ItemStack(e.icon), 0, 0);
                g.pose().popMatrix();
            } else {
                g.drawCenteredString(font, "+", x + SLOT / 2, y + SLOT / 2 - 4, 0xFF707070);
            }
            g.drawString(font, String.valueOf(i + 1), x + 3, y + 3, 0xFF9090A0);
        }

        // center hub
        Ui.panel(g, cx - 40, cy - 14, 80, 28, 0xF0140C24, 0xFF6040A0);
        Emote e = emoteIn(hovered);
        if (e != null) {
            g.drawCenteredString(font, e.name, cx, cy - 9, 0xFFFFFFFF);
            g.drawCenteredString(font, e.rarity.label, cx, cy + 2, e.rarity.color);
        } else if (hovered >= 0) {
            g.drawCenteredString(font, "Empty", cx, cy - 4, 0xFF909090);
        } else {
            g.drawCenteredString(font, "Emotes", cx, cy - 4, 0xFFFFD040);
        }
        g.drawCenteredString(font, "Release / click to play  -  [" + FreeMotesClient.shopKey.getTranslatedKeyMessage().getString()
                + "] Shop  -  right-click: stop", cx, cy + RADIUS + 30, 0xFFC0C0C0);
    }

    private void play(int slot) {
        Emote e = emoteIn(slot);
        onClose();
        if (e != null) EmoteManager.playLocal(e);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 1) {
            onClose();
            EmoteManager.stopLocal();
            return true;
        }
        if (event.button() == 0) {
            play(pick(event.x(), event.y()));
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (FreeMotesClient.wheelKey.matches(event)) {
            if (EmoteManager.now() - openedAt < TAP_MS && hovered < 0) return true; // tapped: stay open
            play(hovered);
            return true;
        }
        return super.keyReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (FreeMotesClient.shopKey.matches(event)) {
            minecraft.setScreen(new ShopScreen());
            return true;
        }
        return super.keyPressed(event);
    }
}

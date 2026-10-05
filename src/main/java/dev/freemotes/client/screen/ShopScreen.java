package dev.freemotes.client.screen;

import dev.freemotes.client.CosmeticManager;
import dev.freemotes.client.EmoteManager;
import dev.freemotes.client.FMConfig;
import dev.freemotes.client.cosmetic.Cosmetic;
import dev.freemotes.client.cosmetic.Cosmetics;
import dev.freemotes.client.emote.Emote;
import dev.freemotes.client.emote.Emote.Rarity;
import dev.freemotes.client.emote.Emotes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** The store. Prices are displayed... and then crossed out, because everything is free. */
public class ShopScreen extends Screen {
    private enum Tab { EMOTES, COSMETICS }

    private static final int CARD_W = 118, CARD_H = 58, GAP = 6;
    private static final int TOP = 44;

    private static Tab tab = Tab.EMOTES;
    private static int selectedSlot = 0;
    private int scroll;
    private int gridX, gridW, gridBottom, cols;
    private String toast = "";
    private long toastUntil;

    public ShopScreen() {
        super(Component.literal("FreeMotes Shop"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        gridX = 130;
        gridW = width - gridX - 10;
        cols = Math.max(1, (gridW + GAP) / (CARD_W + GAP));
        gridBottom = height - (tab == Tab.EMOTES ? 62 : 30);

        addRenderableWidget(Button.builder(Component.literal("Emotes"), b -> switchTab(Tab.EMOTES))
                .bounds(gridX, 18, 70, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cosmetics"), b -> switchTab(Tab.COSMETICS))
                .bounds(gridX + 74, 18, 70, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Claim ALL (free)"), b -> claimAll())
                .bounds(width - 110, 18, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Stop emote"), b -> EmoteManager.stopLocal())
                .bounds(10, height - 26, 110, 20).build());
    }

    private void switchTab(Tab t) {
        tab = t;
        scroll = 0;
        rebuildWidgets();
    }

    private int count() {
        return tab == Tab.EMOTES ? Emotes.ALL.size() : Cosmetics.ALL.size();
    }

    private int maxScroll() {
        int rows = (count() + cols - 1) / cols;
        int visible = gridBottom - TOP;
        return Math.max(0, rows * (CARD_H + GAP) - visible);
    }

    private int cardX(int i) {
        return gridX + (i % cols) * (CARD_W + GAP);
    }

    private int cardY(int i) {
        return TOP + (i / cols) * (CARD_H + GAP) - scroll;
    }

    // ------------------------------------------------------------------------------------------------ render
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        FMConfig cfg = FMConfig.get();

        g.drawString(font, Component.literal("FREEMOTES SHOP").withStyle(ChatFormatting.BOLD), 10, 5, 0xFFFFD040);
        g.drawString(font, "Coins: ∞", 10, 16, 0xFF7CFF7C);
        g.drawString(font, "Everything is FREE!", 10, 26, 0xFF7CFF7C);

        // player preview (plays your current emote, shows your cosmetics)
        Ui.panel(g, 8, 36, 116, height - 70, 0xE0180F28, 0xFF4A3A70);
        if (minecraft.player != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(g, 12, 40, 120, height - 38, 48, 0.0625f, mouseX, mouseY, minecraft.player);
        }

        // card grid
        g.enableScissor(gridX, TOP, gridX + gridW, gridBottom);
        for (int i = 0; i < count(); i++) {
            int x = cardX(i), y = cardY(i);
            if (y + CARD_H < TOP || y > gridBottom) continue;
            boolean hot = Ui.inside(mouseX, mouseY, x, y, CARD_W, CARD_H) && mouseY >= TOP && mouseY < gridBottom;
            if (tab == Tab.EMOTES) {
                Emote e = Emotes.ALL.get(i);
                boolean owned = cfg.ownedEmotes.contains(e.id);
                boolean onWheel = cfg.wheel.contains(e.id);
                String status = !owned ? "Click to claim" : onWheel ? "On wheel" : "Click: add to slot " + (selectedSlot + 1);
                card(g, x, y, hot, e.icon, e.name, e.rarity, owned, status, onWheel);
            } else {
                Cosmetic c = Cosmetics.ALL.get(i);
                boolean owned = cfg.ownedCosmetics.contains(c.id());
                boolean equipped = c.id().equals(cfg.equipped.get(c.slot().name()));
                String status = !owned ? "Click to claim" : equipped ? "Equipped (" + c.slot().label + ")" : "Click to wear (" + c.slot().label + ")";
                card(g, x, y, hot, c.icon(), c.name(), c.rarity(), owned, status, equipped);
            }
        }
        g.disableScissor();

        // tooltip-ish description for the hovered card
        int hoveredCard = cardAt(mouseX, mouseY);
        String desc = null;
        if (hoveredCard >= 0) {
            desc = tab == Tab.EMOTES ? Emotes.ALL.get(hoveredCard).description + "  (right-click: preview)"
                    : Cosmetics.ALL.get(hoveredCard).description();
        }

        // wheel slot editor
        if (tab == Tab.EMOTES) {
            int y = height - 56;
            g.drawString(font, "Emote wheel - pick a slot, then click an owned emote:", gridX, y - 2, 0xFFC8C8D8);
            for (int s = 0; s < FMConfig.WHEEL_SLOTS; s++) {
                int x = gridX + s * 30;
                boolean sel = s == selectedSlot;
                Emote e = Emotes.get(cfg.wheel.get(s));
                Ui.panel(g, x, y + 10, 26, 26, sel ? 0xF0503090 : 0xE0201830, sel ? 0xFFFFFFFF : e != null ? e.rarity.color : 0xFF505050);
                if (e != null) g.renderItem(new ItemStack(e.icon), x + 5, y + 15);
                else g.drawString(font, String.valueOf(s + 1), x + 10, y + 19, 0xFF707070);
            }
        }

        if (desc != null) {
            g.drawString(font, desc, gridX, height - 12 - (tab == Tab.EMOTES ? 0 : 6), 0xFFE0E0E0);
        } else if (tab == Tab.EMOTES) {
            g.drawString(font, "Right-click a wheel slot to clear it.", gridX, height - 12, 0xFF808090);
        }

        if (EmoteManager.now() < toastUntil) {
            int w = font.width(toast) + 16;
            Ui.panel(g, width / 2 - w / 2, 2, w, 16, 0xF0103018, 0xFF40FF70);
            g.drawCenteredString(font, toast, width / 2, 6, 0xFF9CFFAE);
        }
    }

    private void card(GuiGraphics g, int x, int y, boolean hot, Item icon, String name, Rarity rarity, boolean owned, String status, boolean active) {
        int fill = hot ? 0xF0342456 : 0xE01C1430;
        int border = active ? 0xFF7CFF7C : rarity.color;
        Ui.panel(g, x, y, CARD_W, CARD_H, fill, border);
        if (rarity == Rarity.SUPREME && (EmoteManager.now() / 150) % 2 == 0) {
            Ui.panel(g, x, y, CARD_W, CARD_H, 0xF0401030, 0xFFFFD040);
        }
        g.renderItem(new ItemStack(icon), x + 6, y + 6);
        g.drawString(font, name, x + 26, y + 6, 0xFFFFFFFF);
        g.drawString(font, rarity.label, x + 26, y + 16, rarity.color);
        if (owned) {
            g.drawString(font, "OWNED", x + 6, y + 30, 0xFF7CFF7C);
        } else {
            Component was = Component.literal(Ui.price(rarity.fakePrice)).withStyle(ChatFormatting.STRIKETHROUGH);
            g.drawString(font, was, x + 6, y + 30, 0xFF9A7070);
            g.drawString(font, "FREE", x + 10 + font.width(was), y + 30, 0xFF7CFF7C);
        }
        g.drawString(font, status, x + 6, y + 43, 0xFFB8B8C8);
    }

    // ------------------------------------------------------------------------------------------------ input
    private int cardAt(double mx, double my) {
        if (my < TOP || my >= gridBottom) return -1;
        for (int i = 0; i < count(); i++) {
            if (Ui.inside(mx, my, cardX(i), cardY(i), CARD_W, CARD_H)) return i;
        }
        return -1;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x(), my = event.y();
        int button = event.button();
        FMConfig cfg = FMConfig.get();

        if (tab == Tab.EMOTES) {
            int y = height - 56 + 10;
            for (int s = 0; s < FMConfig.WHEEL_SLOTS; s++) {
                if (Ui.inside(mx, my, gridX + s * 30, y, 26, 26)) {
                    if (button == 1) {
                        cfg.wheel.set(s, "");
                        cfg.save();
                    }
                    selectedSlot = s;
                    click();
                    return true;
                }
            }
        }

        int i = cardAt(mx, my);
        if (i >= 0) {
            if (tab == Tab.EMOTES) clickEmote(Emotes.ALL.get(i), button);
            else clickCosmetic(Cosmetics.ALL.get(i));
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    private void clickEmote(Emote e, int button) {
        FMConfig cfg = FMConfig.get();
        if (button == 1) {
            EmoteManager.playLocal(e);
            return;
        }
        if (!cfg.ownedEmotes.contains(e.id)) {
            cfg.ownedEmotes.add(e.id);
            int empty = cfg.wheel.indexOf("");
            if (empty >= 0) cfg.wheel.set(empty, e.id);
            cfg.save();
            purchased(e.name, e.rarity);
            return;
        }
        int existing = cfg.wheel.indexOf(e.id);
        if (existing >= 0) cfg.wheel.set(existing, "");
        cfg.wheel.set(selectedSlot, e.id);
        selectedSlot = (selectedSlot + 1) % FMConfig.WHEEL_SLOTS;
        cfg.save();
        click();
    }

    private void clickCosmetic(Cosmetic c) {
        FMConfig cfg = FMConfig.get();
        String slot = c.slot().name();
        if (!cfg.ownedCosmetics.contains(c.id())) {
            cfg.ownedCosmetics.add(c.id());
            cfg.equipped.put(slot, c.id());
            purchased(c.name(), c.rarity());
        } else if (c.id().equals(cfg.equipped.get(slot))) {
            cfg.equipped.remove(slot);
            click();
        } else {
            cfg.equipped.put(slot, c.id());
            click();
        }
        cfg.save();
        CosmeticManager.sync();
    }

    private void claimAll() {
        FMConfig cfg = FMConfig.get();
        Emotes.ALL.forEach(e -> cfg.ownedEmotes.add(e.id));
        Cosmetics.ALL.forEach(c -> cfg.ownedCosmetics.add(c.id()));
        // make sure the SUPREME emote is on the wheel
        if (!cfg.wheel.contains("supreme")) {
            int empty = cfg.wheel.indexOf("");
            cfg.wheel.set(empty >= 0 ? empty : FMConfig.WHEEL_SLOTS - 1, "supreme");
        }
        cfg.save();
        int total = 0;
        for (Emote e : Emotes.ALL) total += e.rarity.fakePrice;
        for (Cosmetic c : Cosmetics.ALL) total += c.rarity().fakePrice;
        showToast("Claimed everything! You saved " + Ui.price(total) + " coins. Total: 0");
        sound(1.0f);
    }

    private void purchased(String name, Rarity rarity) {
        showToast("Claimed " + name + " for 0 coins!");
        sound(rarity == Rarity.SUPREME ? 0.5f : 1.0f);
    }

    private void showToast(String text) {
        toast = text;
        toastUntil = EmoteManager.now() + 2500;
    }

    private void sound(float pitch) {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, pitch));
    }

    private void click() {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll = (int) Math.max(0, Math.min(maxScroll(), scroll - scrollY * 20));
        return true;
    }
}

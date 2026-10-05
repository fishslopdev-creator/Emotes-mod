package dev.freemotes.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.freemotes.FreeMotes;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;

/** Saved to config/freemotes.json: what you "bought" (for free), what's equipped, and the wheel layout. */
public final class FMConfig {
    public static final int WHEEL_SLOTS = 8;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("freemotes.json");

    public Set<String> ownedEmotes = new LinkedHashSet<>(List.of("wave", "clap", "dab", "floss"));
    public Set<String> ownedCosmetics = new LinkedHashSet<>();
    /** slot name -> cosmetic id */
    public Map<String, String> equipped = new LinkedHashMap<>();
    public List<String> wheel = new ArrayList<>(List.of("wave", "clap", "dab", "floss", "", "", "", ""));
    public boolean thirdPersonWhileEmoting = true;

    private static FMConfig instance;

    public static FMConfig get() {
        if (instance == null) instance = load();
        return instance;
    }

    private static FMConfig load() {
        if (Files.exists(FILE)) {
            try (Reader r = Files.newBufferedReader(FILE)) {
                FMConfig cfg = GSON.fromJson(r, FMConfig.class);
                if (cfg != null) {
                    cfg.sanitize();
                    return cfg;
                }
            } catch (Exception e) {
                FreeMotes.LOGGER.warn("Could not read {}, using defaults", FILE, e);
            }
        }
        FMConfig cfg = new FMConfig();
        cfg.sanitize();
        return cfg;
    }

    private void sanitize() {
        if (ownedEmotes == null) ownedEmotes = new LinkedHashSet<>();
        if (ownedCosmetics == null) ownedCosmetics = new LinkedHashSet<>();
        if (equipped == null) equipped = new LinkedHashMap<>();
        if (wheel == null) wheel = new ArrayList<>();
        while (wheel.size() < WHEEL_SLOTS) wheel.add("");
        while (wheel.size() > WHEEL_SLOTS) wheel.remove(wheel.size() - 1);
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = Files.newBufferedWriter(FILE)) {
                GSON.toJson(this, w);
            }
        } catch (Exception e) {
            FreeMotes.LOGGER.warn("Could not save {}", FILE, e);
        }
    }
}

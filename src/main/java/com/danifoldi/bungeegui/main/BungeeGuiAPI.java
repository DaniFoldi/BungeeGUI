package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.gui.GuiGrid;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Singleton
@SuppressWarnings("unused")
public class BungeeGuiAPI {

    private static BungeeGuiAPI apiInstance;
    public static BungeeGuiAPI getInstance() {
        return apiInstance;
    }
    static void setInstance(BungeeGuiAPI apiInstance) {
        BungeeGuiAPI.apiInstance = apiInstance;
    }

    private final GuiHandler guiHandler;
    private final BungeeGuiLoader loader;

    @Inject
    BungeeGuiAPI(final @NotNull GuiHandler guiHandler,
                 final @NotNull BungeeGuiLoader loader) {
        this.guiHandler = guiHandler;
        this.loader = loader;
    }


    /**
     * Opens a GUI for a player
     *
     * @param player - the player that the GUI will open for
     * @param guiName - the name of the GUI to open
     * @param target - target value for the GUI, pass any {@link String} for untargeted
     */
    public void openGui(ProxiedPlayer player, String guiName, String target) {
        guiHandler.open(guiName, player, target);
    }

    /**
     * Closes the open GUI for a player
     * @param player - the player to force close the GUI for
     */
    public void closeGui(ProxiedPlayer player) {
        guiHandler.close(player);
    }

    /**
     * Get whether the player has an open GUI
     * @param player - the player to check
     * @return true if the player has an open GUI
     */
    public boolean hasOpenGui(ProxiedPlayer player) {
        return guiHandler.getOpenGui(player.getUniqueId()) != null;
    }

    /**
     * Get the open GUI name for a player
     * @param player - the player to check
     * @return the GUI name, or null if has no open GUI
     */
    public @Nullable String getOpenGui(ProxiedPlayer player) {
        return guiHandler.getGuiName(guiHandler.getOpenGui(player.getUniqueId()));
    }

    /**
     * Get the GUI with a name
     * @param name - the GUI name to retrieve
     * @return the {@link GuiGrid} with the name
     */
    public GuiGrid getGui(String name) {
        return guiHandler.getGui(name);
    }

    /**
     * Get all loaded GUI names
     * @return {@link List<String>} with the GUI names
     */
    public List<String> getAvailableGuis() {
        return guiHandler.getGuis();
    }

    /**
     * Reload the plugin and all GUIs
     * @return the time the reload took in ms
     */
    public long reloadGuis() {
        final Instant loadStart = Instant.now();

        loader.unload();
        loader.load();

        final Instant loadEnd = Instant.now();
        return Duration.between(loadStart, loadEnd).toMillis();
    }
}

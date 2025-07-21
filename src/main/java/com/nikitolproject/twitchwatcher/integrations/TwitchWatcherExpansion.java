package com.nikitolproject.twitchwatcher.integrations;

import com.nikitolproject.twitchwatcher.TwitchWatcherPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class TwitchWatcherExpansion extends PlaceholderExpansion {

    private final TwitchWatcherPlugin plugin;

    public TwitchWatcherExpansion(TwitchWatcherPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "twitchwatcher";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getPluginMeta().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // We want the expansion to persist
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("live_count")) {
            return String.valueOf(plugin.getAnnouncedStreamers().size());
        }
        return null;
    }
}

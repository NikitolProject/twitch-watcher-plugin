package com.nikitolproject.twitchwatcher.config;

import com.nikitolproject.twitchwatcher.TwitchWatcherPlugin;
import com.nikitolproject.twitchwatcher.twitch.model.Streamer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {

    private final TwitchWatcherPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(TwitchWatcherPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getTwitchClientId() {
        return config.getString("twitch-api.client-id", "");
    }

    public String getTwitchClientSecret() {
        return config.getString("twitch-api.client-secret", "");
    }

    public int getCheckIntervalSeconds() {
        return config.getInt("check-interval-seconds", 60);
    }

    public List<Streamer> getStreamers() {
        return config.getMapList("streamers").stream()
                .map(streamerMap -> {
                    String twitchNickname = (String) streamerMap.get("twitch-nickname");
                    String minecraftNickname = (String) streamerMap.get("minecraft-nickname");
                    return new Streamer(twitchNickname, minecraftNickname);
                })
                .collect(Collectors.toList());
    }

    public List<String> getStreamStartMessage() {
        return config.getStringList("messages.stream-start");
    }
}

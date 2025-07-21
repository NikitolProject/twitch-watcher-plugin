package com.nikitolproject.twitchwatcher;

import com.nikitolproject.twitchwatcher.commands.TwitchWatcherCommand;
import com.nikitolproject.twitchwatcher.config.ConfigManager;
import com.nikitolproject.twitchwatcher.integrations.TwitchWatcherExpansion;
import com.nikitolproject.twitchwatcher.listeners.PlayerJoinListener;
import com.nikitolproject.twitchwatcher.tasks.TwitchCheckTask;
import com.nikitolproject.twitchwatcher.twitch.TwitchAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TwitchWatcherPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private TwitchAPI twitchAPI;
    private TwitchCheckTask twitchCheckTask;
    private final Set<String> liveStreamersOnTwitch = ConcurrentHashMap.newKeySet();
    private final Set<String> announcedStreamers = ConcurrentHashMap.newKeySet();

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        twitchAPI = new TwitchAPI(
                configManager.getTwitchClientId(),
                configManager.getTwitchClientSecret()
        );

        twitchCheckTask = new TwitchCheckTask(this);
        long interval = configManager.getCheckIntervalSeconds() * 20L;
        twitchCheckTask.runTaskTimerAsynchronously(this, 0, interval);

        this.getServer().getCommandMap().register("twitchwatcher", new TwitchWatcherCommand(this));
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TwitchWatcherExpansion(this).register();
            getLogger().info("Successfully hooked into PlaceholderAPI.");
        }

        getLogger().info("TwitchWatcherPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if (twitchCheckTask != null) {
            twitchCheckTask.cancel();
        }
        getLogger().info("TwitchWatcherPlugin has been disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public TwitchAPI getTwitchAPI() {
        return twitchAPI;
    }

    public Set<String> getLiveStreamersOnTwitch() {
        return liveStreamersOnTwitch;
    }

    public Set<String> getAnnouncedStreamers() {
        return announcedStreamers;
    }

    public void reloadPlugin() {
        if (twitchCheckTask != null) {
            twitchCheckTask.cancel();
        }
        liveStreamersOnTwitch.clear();
        announcedStreamers.clear();
        configManager.loadConfig();
        twitchAPI = new TwitchAPI(
                configManager.getTwitchClientId(),
                configManager.getTwitchClientSecret()
        );
        twitchCheckTask = new TwitchCheckTask(this);
        long interval = configManager.getCheckIntervalSeconds() * 20L;
        twitchCheckTask.runTaskTimerAsynchronously(this, 0, interval);
    }
}


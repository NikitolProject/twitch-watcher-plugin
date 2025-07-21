package com.nikitolproject.twitchwatcher.listeners;

import com.nikitolproject.twitchwatcher.TwitchWatcherPlugin;
import com.nikitolproject.twitchwatcher.twitch.model.Streamer;
import com.nikitolproject.twitchwatcher.util.AnnouncementUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final TwitchWatcherPlugin plugin;

    public PlayerJoinListener(TwitchWatcherPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getConfigManager().getStreamers().stream()
                .filter(s -> s.getMinecraftNickname().equalsIgnoreCase(player.getName()))
                .findFirst()
                .ifPresent(streamer -> {
                    if (plugin.getLiveStreamersOnTwitch().contains(streamer.getTwitchNickname().toLowerCase())) {
                        if (!plugin.getAnnouncedStreamers().contains(streamer.getTwitchNickname().toLowerCase())) {
                            AnnouncementUtil.announceStream(plugin, streamer);
                            plugin.getAnnouncedStreamers().add(streamer.getTwitchNickname().toLowerCase());
                        }
                    }
                });
    }
}

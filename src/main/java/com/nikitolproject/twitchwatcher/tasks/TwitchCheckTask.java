package com.nikitolproject.twitchwatcher.tasks;

import com.nikitolproject.twitchwatcher.TwitchWatcherPlugin;
import com.nikitolproject.twitchwatcher.twitch.model.Streamer;
import com.nikitolproject.twitchwatcher.util.AnnouncementUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class TwitchCheckTask extends BukkitRunnable {

    private final TwitchWatcherPlugin plugin;

    public TwitchCheckTask(TwitchWatcherPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        List<Streamer> streamers = plugin.getConfigManager().getStreamers();
        if (streamers.isEmpty()) {
            return;
        }

        try {
            Set<String> currentLiveOnTwitch = plugin.getTwitchAPI().getLiveStreamers(streamers);
            Set<String> previouslyLiveOnTwitch = plugin.getLiveStreamersOnTwitch();
            Set<String> announcedStreamers = plugin.getAnnouncedStreamers();

            previouslyLiveOnTwitch.clear();
            previouslyLiveOnTwitch.addAll(currentLiveOnTwitch);

            for (Streamer streamer : streamers) {
                String twitchNickname = streamer.getTwitchNickname().toLowerCase();
                boolean isLive = currentLiveOnTwitch.contains(twitchNickname);

                if (isLive) {
                    if (!announcedStreamers.contains(twitchNickname)) {
                        boolean shouldAnnounce = !plugin.getConfigManager().isCheckPlayerOnline() ||
                                Bukkit.getPlayer(streamer.getMinecraftNickname()) != null;

                        if (shouldAnnounce) {
                            AnnouncementUtil.announceStream(plugin, streamer);
                            announcedStreamers.add(twitchNickname);
                        }
                    }
                } else {
                    announcedStreamers.remove(twitchNickname);
                }
            }

        } catch (IOException e) {
            plugin.getLogger().warning("Error checking Twitch streams: " + e.getMessage());
        }
    }
}
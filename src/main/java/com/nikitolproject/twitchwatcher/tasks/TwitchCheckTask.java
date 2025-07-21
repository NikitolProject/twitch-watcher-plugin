package com.nikitolproject.twitchwatcher.tasks;

import com.nikitolproject.twitchwatcher.TwitchWatcherPlugin;
import com.nikitolproject.twitchwatcher.twitch.model.Streamer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
            Set<String> currentLiveStreamers = plugin.getTwitchAPI().getLiveStreamers(streamers);
            Set<String> previouslyLiveStreamers = plugin.getLiveStreamers();

            for (Streamer streamer : streamers) {
                String twitchNickname = streamer.getTwitchNickname().toLowerCase();
                boolean isCurrentlyLive = currentLiveStreamers.contains(twitchNickname);
                boolean wasPreviouslyLive = previouslyLiveStreamers.contains(twitchNickname);

                if (isCurrentlyLive && !wasPreviouslyLive) {
                    announceStream(streamer);
                    previouslyLiveStreamers.add(twitchNickname);
                } else if (!isCurrentlyLive && wasPreviouslyLive) {
                    previouslyLiveStreamers.remove(twitchNickname);
                }
            }

        } catch (IOException e) {
            plugin.getLogger().warning("Error checking Twitch streams: " + e.getMessage());
        }
    }

    private void announceStream(Streamer streamer) {
        List<String> messageLines = plugin.getConfigManager().getStreamStartMessage();
        String twitchUrl = "https://twitch.tv/" + streamer.getTwitchNickname();

        for (String line : messageLines) {
            String processedLine = line
                    .replace("%streamer_name%", streamer.getTwitchNickname())
                    .replace("%minecraft_name%", streamer.getMinecraftNickname());

            Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(processedLine);

            if (line.contains("https://twitch.tv/%streamer_name%")) {
                component = component.clickEvent(ClickEvent.openUrl(twitchUrl));
            }

            Bukkit.broadcast(component);
        }
    }
}

package com.nikitolproject.twitchwatcher.util;

import com.nikitolproject.twitchwatcher.TwitchWatcherPlugin;
import com.nikitolproject.twitchwatcher.twitch.model.Streamer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

import java.util.List;

public class AnnouncementUtil {

    public static void announceStream(TwitchWatcherPlugin plugin, Streamer streamer) {
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

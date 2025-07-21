package com.nikitolproject.twitchwatcher.commands;

import com.nikitolproject.twitchwatcher.TwitchWatcherPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TwitchWatcherCommand extends Command {

    private final TwitchWatcherPlugin plugin;
    private static final List<String> SUBCOMMANDS = List.of("reload", "list");

    public TwitchWatcherCommand(TwitchWatcherPlugin plugin) {
        super("twitchwatcher");
        this.plugin = plugin;
        this.setAliases(List.of("tw"));
        this.setDescription("Main command for the TwitchWatcher plugin.");
        this.setUsage("/twitchwatcher <reload|list>");
        this.setPermission("twitchwatcher.admin"); // Default permission for base command
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: " + this.getUsage(), NamedTextColor.YELLOW));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("reload")) {
            if (!sender.hasPermission("twitchwatcher.admin")) {
                sender.sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
                return true;
            }
            plugin.reloadPlugin();
            sender.sendMessage(Component.text("TwitchWatcher configuration reloaded.", NamedTextColor.GREEN));
            return true;
        }

        if (subCommand.equals("list")) {
            // No specific permission needed for list, or you can add one like "twitchwatcher.list"
            handleListCommand(sender);
            return true;
        }

        sender.sendMessage(Component.text("Unknown subcommand. Usage: " + this.getUsage(), NamedTextColor.RED));
        return true;
    }

    private void handleListCommand(CommandSender sender) {
        Set<String> liveStreamers = plugin.getLiveStreamers();

        if (liveStreamers.isEmpty()) {
            sender.sendMessage(Component.text("No streamers are currently live.", NamedTextColor.YELLOW));
            return;
        }

        TextComponent.Builder message = Component.text()
                .append(Component.text("--- Live Streamers (", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text(liveStreamers.size(), NamedTextColor.AQUA))
                .append(Component.text(") ---", NamedTextColor.GOLD, TextDecoration.BOLD));

        for (String streamerName : liveStreamers) {
            String url = "https://twitch.tv/" + streamerName;
            message.append(Component.newline())
                    .append(Component.text("▶ ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(streamerName, NamedTextColor.AQUA)
                            .clickEvent(ClickEvent.openUrl(url))
                            .hoverEvent(Component.text("Click to open stream!", NamedTextColor.GRAY)));
        }

        sender.sendMessage(message.build());
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], SUBCOMMANDS, new ArrayList<>());
        }
        return List.of();
    }
}

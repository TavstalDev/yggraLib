package io.github.tavstaldev.yggra.core.commands;

import io.github.tavstaldev.yggra.core.YggraPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for plugin commands. Automatically registers as executor and tab completer.
 * Supports subcommands with paginated help menus and permission checks.
 */
public abstract class CommandBase implements CommandExecutor, TabCompleter {
    /** The plugin instance. */
    private final YggraPlugin plugin;
    /** The command name (must be defined in plugin.yml). */
    private final String command;
    /** Required permission node; null or blank allows all. */
    private final String permission;
    /** Registered subcommands. */
    private final List<SubCommand> subCommands;

    /**
     * Creates and registers a command with subcommands.
     *
     * @param plugin      The plugin instance.
     * @param command     The command name (must exist in plugin.yml).
     * @param permission  Required permission, or null/blank for none.
     * @param subCommands List of subcommands.
     * @throws IllegalAccessException If the command is not defined in plugin.yml.
     */
    public CommandBase(YggraPlugin plugin, String command, String permission, List<SubCommand> subCommands) throws IllegalAccessException {
        this.plugin = plugin;
        this.command = command;
        this.permission = permission;
        this.subCommands = subCommands;

        var commandObj = plugin.getCommand(command);
        if (commandObj == null)
            throw new IllegalAccessException("Command '" + command + "' is not defined in plugin.yml");

        commandObj.setExecutor(this);
        commandObj.setTabCompleter(this);
    }

    /**
     * Creates and registers a command without subcommands.
     *
     * @param plugin      The plugin instance.
     * @param command     The command name (must exist in plugin.yml).
     * @param permission  Required permission, or null/blank for none.
     * @throws IllegalAccessException If the command is not defined in plugin.yml.
     */
    public CommandBase(YggraPlugin plugin, String command, String permission) throws IllegalAccessException {
        this(plugin, command, permission, new ArrayList<>());
    }

    /**
     * Checks if the sender has the required permission.
     *
     * @param commandSender The sender to check.
     * @return True if no permission is set or the sender has it.
     */
    public boolean hasPermission(CommandSender commandSender) {
        if (this.permission == null || permission.isBlank())
            return true;
        return commandSender.hasPermission(this.permission);
    }

    /**
     * Sends a paginated help menu to the sender, listing visible subcommands
     * with clickable previous/next navigation buttons.
     *
     * @param sender The recipient.
     * @param page   The page number (1-indexed, clamped to valid range).
     */
    protected void sendHelp(CommandSender sender, int page) {
        var chat = plugin.chat();
        var translator = plugin.translator();
        int maxPage = 1 + (subCommands.size() / 15);
        if (page > maxPage)
            page = maxPage;
        if (page < 1)
            page = 1;
        int finalPage = page;

        chat.sendCommandReply(sender, "commands.help.title", Map.of(
                        "current_page", finalPage,
                        "max_page", maxPage
                )
        );
        chat.sendCommandReply(sender, "commands.help.info");

        boolean reachedEnd = false;
        int itemIndex = 0;
        // Display up to 15 subcommands per page
        for (int i = 0; i < 15; i++) {
            int index = itemIndex + (page - 1) * 15;
            if (index >= subCommands.size()) {
                reachedEnd = true;
                break;
            }
            itemIndex++;

            SubCommand subCommand = subCommands.get(index);
            if (!subCommand.hasPermission(sender)) {
                i--;
                continue;
            }

            subCommand.sendHelp(plugin, sender, command);
        }

        // Display navigation buttons for the help menu
        String previousBtn, nextBtn, bottomMsg;
        if (sender instanceof Player player)
        {
            previousBtn = translator.localize(player, "commands.help.previous-button");
            nextBtn = translator.localize(player, "commands.help.next-button");
            bottomMsg = translator.localize(player, "commands.help.bottom", Map.of(
                    "current_page", page,
                    "max_page", maxPage
            ));
        }
        else {
            previousBtn = translator.localize("commands.help.previous-button");
            nextBtn = translator.localize("commands.help.next-button");
            bottomMsg = translator.localize("commands.help.bottom", Map.of(
                    "current_page", page,
                    "max_page", maxPage
            ));
        }

        Map<String, Component> bottomParams = new HashMap<>();
        if (page > 1)
            bottomParams.put("previous_btn", chat.translateColors(previousBtn, true)
                    .clickEvent(ClickEvent.runCommand(String.format("/%s help %s", command, page - 1))));
        else
            bottomParams.put("previous_btn", chat.translateColors(previousBtn, true));

        if (!reachedEnd && maxPage >= page + 1)
            bottomParams.put("next_btn", chat.translateColors(nextBtn, true)
                    .clickEvent(ClickEvent.runCommand(String.format("/%s help %s", command, page + 1))));
        else
            bottomParams.put("next_btn", chat.translateColors(nextBtn, true));

        Component bottomComp = chat.buildWithButtons(bottomMsg, bottomParams);
        sender.sendMessage(bottomComp);
    }
}

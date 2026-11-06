package io.github.tavstaldev.yggra.bukkit.commands;

import io.github.tavstaldev.yggra.api.IYggraPlugin;
import io.github.tavstaldev.yggra.api.localization.ITranslationService;
import io.github.tavstaldev.yggra.api.utils.MapUtil;
import io.github.tavstaldev.yggra.bukkit.services.IChatService;
import io.github.tavstaldev.yggra.bukkit.utils.AdventureUtils;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Base class for plugin commands. Automatically registers as executor and tab completer.
 * Supports subcommands with paginated help menus and permission checks.
 */
public abstract class CommandBase implements CommandExecutor, TabCompleter {
    /** The plugin instance. */
    private final IYggraPlugin plugin;
    /** The command name (must be defined in plugin.yml). */
    private final String command;
    public final String getCommand() {
        return command;
    }
    /** Required permission node; null or blank allows all. */
    private final String permission;
    public final String getPermission() {
        return permission;
    }
    /** Registered subcommands. */
    private final List<SubCommand> subCommands;
    public final List<SubCommand> getSubCommands() {
        return Collections.unmodifiableList(subCommands);
    }

    /**
     * Creates and registers a command with subcommands.
     *
     * @param plugin      The plugin instance.
     * @param command     The command name (must exist in plugin.yml).
     * @param permission  Required permission, or null/blank for none.
     * @param subCommands List of subcommands.
     * @throws IllegalAccessException If the command is not defined in plugin.yml.
     */
    public CommandBase(IYggraPlugin plugin, String command, String permission, List<SubCommand> subCommands) throws IllegalAccessException {
        this.plugin = plugin;
        this.command = command;
        this.permission = permission;
        this.subCommands = subCommands;

        Object commandObj = plugin.command(command);
        if (commandObj == null)
            throw new IllegalAccessException("Command '" + command + "' is not defined in plugin.yml");

        PluginCommand cmd = (PluginCommand)commandObj;
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    /**
     * Creates and registers a command without subcommands.
     *
     * @param plugin      The plugin instance.
     * @param command     The command name (must exist in plugin.yml).
     * @param permission  Required permission, or null/blank for none.
     * @throws IllegalAccessException If the command is not defined in plugin.yml.
     */
    public CommandBase(IYggraPlugin plugin, String command, String permission) throws IllegalAccessException {
        this(plugin, command, permission, new ArrayList<>());
    }

    /**
     * Checks if the sender has the required permission.
     *
     * @param commandSender The sender to check.
     * @return True if no permission is set or the sender has it.
     */
    public boolean hasPermission(CommandSender commandSender) {
        if (permission == null || permission.isEmpty())
            return true;
        return commandSender.hasPermission(permission);
    }

    /**
     * Sends a paginated help menu to the sender, listing visible subcommands
     * with clickable previous/next navigation buttons.
     *
     * @param sender The recipient.
     * @param page   The page number (1-indexed, clamped to valid range).
     */
    @SneakyThrows
    protected void sendHelp(CommandSender sender, int page) {
        ITranslationService translator = plugin.getRequiredService(ITranslationService.class);
        IChatService chatService = plugin.getRequiredService(IChatService.class);
        int maxPage = 1 + (subCommands.size() / 15);
        if (page > maxPage)
            page = maxPage;
        if (page < 1)
            page = 1;
        int finalPage = page;

        chatService.sendCommandReply(sender, "commands.help.title", MapUtil.of(
                        "current_page", finalPage,
                        "max_page", maxPage
                )
        );
        chatService.sendCommandReply(sender, "commands.help.info");

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
        if (sender instanceof Player)
        {
            Player player = (Player)sender;
            previousBtn = translator.localize(player.getUniqueId(), "commands.help.previous-button");
            nextBtn = translator.localize(player.getUniqueId(), "commands.help.next-button");
            bottomMsg = translator.localize(player.getUniqueId(), "commands.help.bottom", MapUtil.of(
                    "current_page", page,
                    "max_page", maxPage
            ));
        }
        else {
            previousBtn = translator.localize("commands.help.previous-button");
            nextBtn = translator.localize("commands.help.next-button");
            bottomMsg = translator.localize("commands.help.bottom", MapUtil.of(
                    "current_page", page,
                    "max_page", maxPage
            ));
        }

        Map<String, Component> bottomParams = new HashMap<>();
        if (page > 1)
            bottomParams.put("previous_btn", AdventureUtils.parse(previousBtn, true)
                    .clickEvent(ClickEvent.runCommand(String.format("/%s help %s", command, page - 1))));
        else
            bottomParams.put("previous_btn", AdventureUtils.parse(previousBtn, true));

        if (!reachedEnd && maxPage >= page + 1)
            bottomParams.put("next_btn", AdventureUtils.parse(nextBtn, true)
                    .clickEvent(ClickEvent.runCommand(String.format("/%s help %s", command, page + 1))));
        else
            bottomParams.put("next_btn", AdventureUtils.parse(nextBtn, true));

        Component bottomComp = chatService.buildWithButtons(bottomMsg, bottomParams);
        sender.sendMessage(AdventureUtils.PLAIN_TEXT.serialize(bottomComp));
    }
}

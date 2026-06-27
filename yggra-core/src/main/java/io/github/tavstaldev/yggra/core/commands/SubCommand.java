package io.github.tavstaldev.yggra.core.commands;

import io.github.tavstaldev.yggra.core.YggraPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for a subcommand within a parent {@link CommandBase}.
 * Subclasses override {@link #onCommand} and {@link #onTabComplete} to implement behavior.
 */
public abstract class SubCommand implements CommandExecutor, TabCompleter {
    /** The subcommand name (used after the base command). */
    public String command;
    /** Optional permission node; null or blank allows all. */
    public String permission;
    /** Extra arguments passed to the help translation. Keys are placeholder names, values are localized strings. */
    public Map<String, Object> arguments;

    /**
     * @param command    The subcommand name.
     * @param permission Required permission, or null/blank for none.
     * @param arguments  Help template arguments (can be null).
     */
    public SubCommand(String command, String permission, Map<String, Object> arguments) {
        this.command = command;
        this.permission = permission;
        this.arguments = arguments;
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
     * Sends a help line describing this subcommand to the sender.
     * Localizes argument values based on the sender's locale.
     *
     * @param plugin         The plugin instance.
     * @param commandSender  The recipient.
     * @param baseCommand    The parent command name.
     */
    public void sendHelp(YggraPlugin<?> plugin, CommandSender commandSender, String baseCommand) {
        if (arguments == null)
            return;

        Map<String, Object> args = new HashMap<>(Map.of("command", baseCommand, "subcommand", command));
        var keys = arguments.keySet();

        Player player = null;
        if (commandSender instanceof Player p)
            player = p;

        for (String key : keys) {
            Object param = arguments.get(key);
            if (param == null) {
                args.put(key, "");
                continue;
            }
            if (param instanceof String stringParam && stringParam.isEmpty()) {
                args.put(key, param);
                continue;
            }
            if (player != null)
                args.put(key, plugin.translator().localize(player, param.toString()));
            else
                args.put(key, plugin.translator().localize(param.toString()));
        }

        plugin.chat().sendCommandReply(commandSender, "commands.help.line", args);
    }
}

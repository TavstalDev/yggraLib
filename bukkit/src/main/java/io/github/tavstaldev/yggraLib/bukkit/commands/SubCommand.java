package io.github.tavstaldev.yggraLib.bukkit.commands;

import io.github.tavstaldev.yggraLib.api.IYggraPlugin;
import io.github.tavstaldev.yggraLib.api.localization.ITranslationService;
import io.github.tavstaldev.yggraLib.api.utils.MapUtil;
import io.github.tavstaldev.yggraLib.bukkit.services.IChatService;
import lombok.SneakyThrows;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Base class for a subcommand within a parent {@link CommandBase}.
 */
public class SubCommand {
    /** The subcommand name (used after the base command). */
    private final String command;
    public final String getCommand() {
        return command;
    }
    /** Optional permission node; null or blank allows all. */
    private final String permission;
    public final String getPermission() {
        return permission;
    }
    /** Extra arguments passed to the help translation. Keys are placeholder names, values are localized strings. */
    private final Map<String, Object> arguments;
    public final Map<String, Object> getArguments() {
        return Collections.unmodifiableMap(arguments);
    }

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
        if (permission == null || permission.isEmpty())
            return true;
        return commandSender.hasPermission(permission);
    }

    /**
     * Sends a help line describing this subcommand to the sender.
     * Localizes argument values based on the sender's locale.
     *
     * @param plugin         The plugin instance.
     * @param commandSender  The recipient.
     * @param baseCommand    The parent command name.
     */
    @SneakyThrows
    public void sendHelp(IYggraPlugin plugin, CommandSender commandSender, String baseCommand) {
        if (arguments == null)
            return;

        Map<String, Object> args = MapUtil.of("command", baseCommand, "subcommand", command);
        Set<String> keys = arguments.keySet();

        Player player = null;
        if (commandSender instanceof Player)
            player = (Player)commandSender;

        ITranslationService translator = plugin.getRequiredService(ITranslationService.class);
        IChatService chatService = plugin.getRequiredService(IChatService.class);
        for (String key : keys) {
            Object param = arguments.get(key);
            if (param == null) {
                args.put(key, "");
                continue;
            }


            if (param instanceof String) {
                String stringParam = (String)param;
                if (stringParam.isEmpty()) {
                    args.put(key, param);
                    continue;
                }
            }


            if (player != null)
                args.put(key, translator.localize(player.getUniqueId(), param.toString()));
            else
                args.put(key, translator.localize(param.toString()));
        }

        chatService.sendCommandReply(commandSender, "commands.help.line", args);
    }
}

package io.github.tavstaldev.yggra.core.commands;

import io.github.tavstaldev.yggra.core.YggraPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SubCommandBase {
    public String command;
    public String permission;
    public Map<String, Object> arguments;

    public SubCommandBase(String command, String permission, Map<String, Object> arguments) {
        this.command = command;
        this.permission = permission;
        this.arguments = arguments;
    }

    public boolean hasPermission(CommandSender commandSender) {
        if (this.permission == null || permission.isBlank())
            return true;
        return commandSender.hasPermission(this.permission);
    }

    public void send(YggraPlugin plugin, CommandSender commandSender, String baseCommand) {
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

        //plugin.sendCommandReply(commandSender, "commands.help.line", args);
    }
}

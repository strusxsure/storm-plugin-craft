package com.thunderpvp.thunderpvppractice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import java.util.Map;

public class CommandManager implements CommandExecutor {

    private final Map<String, CommandExecutor> commands = new HashMap<>();

    public void registerCommand(String commandName, CommandExecutor executor) {
        commands.put(commandName.toLowerCase(), executor);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();
            CommandExecutor commandExecutor = commands.get(subCommand);
            if (commandExecutor != null) {
                return commandExecutor.onCommand(sender, command, label, args);
            }
        }
        // If no subcommand is matched, you can send a help message or default response.
        sender.sendMessage("Unknown command. Use /thunderpvp help for a list of commands.");
        return true;
    }
}

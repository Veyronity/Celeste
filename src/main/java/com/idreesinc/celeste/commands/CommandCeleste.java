package com.idreesinc.celeste.commands;

import com.idreesinc.celeste.Celeste;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandCeleste implements CommandExecutor {

    Celeste celeste;

    public CommandCeleste(Celeste celeste) {
        this.celeste = celeste;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("celeste.reload")) {
                celeste.reload();
                sender.sendRichMessage("<green>Celeste has been reloaded");
            } else {
                sender.sendRichMessage("<red>You do not have permission to use this command");
            }
            return true;
        }
        return false;
    }
}


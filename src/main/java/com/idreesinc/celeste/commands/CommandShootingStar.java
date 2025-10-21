package com.idreesinc.celeste.commands;

import com.idreesinc.celeste.Celeste;
import com.idreesinc.celeste.CelestialSphere;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandShootingStar implements CommandExecutor {

    Celeste celeste;

    public CommandShootingStar(Celeste celeste) {
        this.celeste = celeste;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendRichMessage("<red>Error: Player not found.");
                return true;
            }
            CelestialSphere.createShootingStar(celeste, player, false);
        } else {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                CelestialSphere.createShootingStar(celeste, player, false);
            } else {
                return false;
            }
        }
        String message = this.celeste.getConfig().getString("shooting-stars-summon-text");
        if (message != null) {
            sender.sendRichMessage(message);
        }
        return true;
    }

}

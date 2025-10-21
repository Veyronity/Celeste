package com.idreesinc.celeste;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import com.idreesinc.celeste.commands.CommandCeleste;
import com.idreesinc.celeste.commands.CommandFallingStar;
import com.idreesinc.celeste.commands.CommandShootingStar;
import com.idreesinc.celeste.config.CelesteConfigManager;

import java.util.concurrent.TimeUnit;

public class Celeste extends JavaPlugin {

    public CelesteConfigManager configManager = new CelesteConfigManager(this);

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        registerCommand("celeste", new CommandCeleste(this));
        registerCommand("shootingstar", new CommandShootingStar(this));
        registerCommand("fallingstar", new CommandFallingStar(this));
        configManager.processConfigs();

        this.getServer().getGlobalRegionScheduler().runAtFixedRate(this, new Astronomer(this), 1, 10);
    }

    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = this.getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            this.getLogger().warning("Unable to register " + name);
        }
    }

    public void reload() {
        reloadConfig();
        configManager.processConfigs();
    }
}

package com.idreesinc.celeste;

import com.idreesinc.celeste.config.CelesteConfig;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Astronomer implements Consumer<ScheduledTask> {

    private final Celeste celeste;

    public Astronomer(Celeste celeste) {
        this.celeste = celeste;
    }

    @Override
    public void accept(ScheduledTask task) {
        if (celeste.getServer().getOnlinePlayers().isEmpty()) {
            return;
        }
        List<World> worlds = celeste.getServer().getWorlds();
        for (World world : worlds) {
            CelesteConfig config = celeste.configManager.getConfigForWorld(world.getName());
            if (!celeste.configManager.doesWorldHaveOverrides(world.getName())
                    && !world.getEnvironment().equals(World.Environment.NORMAL)) {
                // Ensure that Celeste only runs on normal worlds unless override is specified in config
                continue;
            }
            if (world.getPlayers().isEmpty()) {
                continue;
            }
            if (!(world.getTime() >= config.beginSpawningStarsTime && world.getTime() <= config.endSpawningStarsTime)) {
                continue;
            }
            if (world.hasStorm()) {
                continue;
            }

            double shootingStarChance;
            double fallingStarChance;
            if (config.newMoonMeteorShower && (world.getFullTime() / 24000) % 8 == 4) {
                shootingStarChance = formatChance(world, config.shootingStarsPerMinuteMeteorShower / 120d);
                fallingStarChance = formatChance(world, config.fallingStarsPerMinuteMeteorShower / 120d);
            } else {
                shootingStarChance = formatChance(world, config.shootingStarsPerMinute / 120d);
                fallingStarChance = formatChance(world, config.fallingStarsPerMinute / 120d);
            }

            if (config.shootingStarsEnabled && ThreadLocalRandom.current().nextDouble() <= shootingStarChance) {
                CelestialSphere.createShootingStar(celeste,
                        world.getPlayers().get(ThreadLocalRandom.current().nextInt(world.getPlayers().size())));
            }
            if (config.fallingStarsEnabled && ThreadLocalRandom.current().nextDouble() <=  fallingStarChance) {
                CelestialSphere.createFallingStar(celeste,
                        world.getPlayers().get(ThreadLocalRandom.current().nextInt(world.getPlayers().size())));
            }
        }
    }

    private double formatChance(World world, double chance) {
        return chance * world.getPlayerCount() / 100;
    }
}
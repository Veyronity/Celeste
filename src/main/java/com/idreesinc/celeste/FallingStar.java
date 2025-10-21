package com.idreesinc.celeste;

import com.idreesinc.celeste.config.CelesteConfig;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class FallingStar implements Consumer<ScheduledTask> {
    private final Celeste celeste;
    private final Location location;
    private Location dropLoc;
    private final CelesteConfig config;
    private final World world;
    private double y = 256;
    private boolean soundPlayed = false;
    private boolean lootDropped = false;
    private int sparkTimer;

    public FallingStar(Celeste celeste, Location location) {
        this.celeste = celeste;
        this.location = location;
        this.world = (location.getWorld() != null ? location.getWorld() : Bukkit.getWorlds().getFirst());
        this.config = celeste.configManager.getConfigForWorld(world.getName());
        this.sparkTimer = config.fallingStarsSparkTime;

        celeste.getServer().getRegionScheduler().run(celeste, location, task -> {
            Block highest = world.getHighestBlockAt(location);
            this.dropLoc = new Location(world, location.getX(), highest.getY() + 1, location.getZ());

            celeste.getServer().getRegionScheduler().runAtFixedRate(celeste, location, this, 1L, 10L);
        });
    }


    @Override
    public void accept(ScheduledTask task) {
        double step = 1;
        world.spawnParticle(Particle.FIREWORK, location.getX(), y, location.getZ(),
                0,  0,  ThreadLocalRandom.current().nextDouble(), 0,
                0.2, null, true);
        world.spawnParticle(Particle.FIREWORK, location.getX(),
                y + ThreadLocalRandom.current().nextDouble() * step,
                location.getZ(),
                0,  0, -1, 0,
                1, null, true);
        if (y % (step * 2) == 0) {
            world.spawnParticle(Particle.LAVA, location.getX(), y + ThreadLocalRandom.current().nextDouble(),
                    location.getZ(),
                    0,  0, ThreadLocalRandom.current().nextDouble(), 0,
                    0.2, null, true);
        }
        if (config.fallingStarsSoundEnabled && !soundPlayed && y <= dropLoc.getY() + 75) {
            world.playSound(dropLoc, Sound.BLOCK_BELL_RESONATE, (float) config.fallingStarsVolume, 0.5f);
            soundPlayed = true;
        }
        if (y <= dropLoc.getY()) {
            if (!lootDropped) {
                // Note that both simple loot and loot tables will drop if both are configured because why not
                if (config.fallingStarSimpleLoot != null && !config.fallingStarSimpleLoot.entries.isEmpty()) {
                    ItemStack drop = new ItemStack(Material.valueOf(config.fallingStarSimpleLoot.getRandom()), 1);
                    world.dropItem(dropLoc, drop);
                    if (celeste.getConfig().getBoolean("debug")) {
                        celeste.getLogger().info("Spawned simple falling star loot");
                    }
                }
                if (config.fallingStarLootTable != null) {
                    // Armor stands are used as markers are not compatible with 1.14
                    Entity marker = world.spawnEntity(location, EntityType.ARMOR_STAND);
                    String command = String.format("execute at %s run loot spawn %s %s %s loot %s",
                            marker.getUniqueId(),
                            dropLoc.getX(),
                            dropLoc.getY(),
                            dropLoc.getZ(),
                            config.fallingStarLootTable);
                    celeste.getServer().dispatchCommand(celeste.getServer().getConsoleSender(), command);
                    marker.remove();
                    if (celeste.getConfig().getBoolean("debug")) {
                        celeste.getLogger().info("Spawned falling star loot from loot table '" + config.fallingStarLootTable + "'");
                    }
                }
                if (config.fallingStarsExperience > 0) {
                    ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(dropLoc, EntityType.EXPERIENCE_ORB);
                    orb.setExperience(config.fallingStarsExperience);
                    if (celeste.getConfig().getBoolean("debug")) {
                        celeste.getLogger().info("Dropping experience orbs with value " + config.fallingStarsExperience);
                    }
                }
                lootDropped = true;
            }
            if (y % (step * 5) == 0) {
                world.spawnParticle(Particle.LAVA, dropLoc,
                        0, 0, ThreadLocalRandom.current().nextDouble(), 0,
                        1, null, true);
            }
            sparkTimer--;
            if (sparkTimer <= 0) {
                task.cancel();
            }
        }
        y -= step;
    }

}
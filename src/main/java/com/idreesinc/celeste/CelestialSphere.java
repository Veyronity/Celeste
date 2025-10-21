package com.idreesinc.celeste;

import com.idreesinc.celeste.config.CelesteConfig;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class CelestialSphere {

    public static void createShootingStar(Celeste celeste, Player player) {
        createShootingStar(celeste, player, true);
    }

    public static void createShootingStar(Celeste celeste, Player player, boolean approximate) {
        createShootingStar(celeste, player.getLocation(), approximate);
    }

    public static void createShootingStar(Celeste celeste, Location location, boolean approximate) {
        Location starLocation;
        CelesteConfig config = celeste.configManager.getConfigForWorld(location.getWorld().getName());
        double w = 100 * Math.sqrt(ThreadLocalRandom.current().nextDouble());
        double t = 2d * Math.PI * ThreadLocalRandom.current().nextDouble();
        double x = w * Math.cos(t);
        double range = Math.max(0, config.shootingStarsMaxHeight - config.shootingStarsMinHeight);
        double y = Math.max(ThreadLocalRandom.current().nextDouble() * range + config.shootingStarsMinHeight, location.getY() + 50);
        double z = w * Math.sin(t);
        if (approximate) {
            starLocation = new Location(location.getWorld(), location.getX() + x, y, location.getZ() + z);
        } else {
            starLocation = new Location(location.getWorld(), location.getX(), y, location.getZ());
        }
        Vector direction = new Vector(ThreadLocalRandom.current().nextDouble() * 2 - 1, ThreadLocalRandom.current().nextDouble() * -0.75d, ThreadLocalRandom.current().nextDouble() * 2 - 1);
        direction.normalize();
        double speed = ThreadLocalRandom.current().nextDouble() * 2 + 0.75;

        celeste.getServer().getRegionScheduler().run(celeste, location, task -> {
            location.getWorld().spawnParticle(Particle.FIREWORK, starLocation, 0, direction.getX(),
                    direction.getY(), direction.getZ(), speed, null, true);
            if (ThreadLocalRandom.current().nextDouble() >= 0.5) {
                location.getWorld().spawnParticle(Particle.EXPLOSION, starLocation, 0, direction.getX(),
                        direction.getY(), direction.getZ(), speed, null, true);
            }
            if (celeste.getConfig().getBoolean("debug")) {
                celeste.getLogger().info("Shooting star at " + stringifyLocation(starLocation) + " in world " + starLocation.getWorld().getName());
            }
        });

    }

    public static void createFallingStar(Celeste celeste, Player player) {
        createFallingStar(celeste, player, true);
    }

    public static void createFallingStar(Celeste celeste, Player player, boolean approximate) {
        createFallingStar(celeste, player.getLocation(), approximate);
    }

    public static void createFallingStar(Celeste celeste, final Location location, boolean approximate) {
        Location target = location;
        CelesteConfig config = celeste.configManager.getConfigForWorld(location.getWorld().getName());
        if (approximate) {
            double fallingStarRadius = config.fallingStarsRadius;
            double w = fallingStarRadius * Math.sqrt(ThreadLocalRandom.current().nextDouble());
            double t = 2d * Math.PI * ThreadLocalRandom.current().nextDouble();
            double x = w * Math.cos(t);
            double z = w * Math.sin(t);
            target = new Location(location.getWorld(), location.getX() + x, location.getY(), location.getZ() + z);
        }
        new FallingStar(celeste, target);
        if (celeste.getConfig().getBoolean("debug")) {
            celeste.getLogger().info("Falling star at " + stringifyLocation(target) + " in world " + target.getWorld().getName());
        }
    }

    private static String stringifyLocation(Location location) {
        DecimalFormat format = new DecimalFormat("##.00");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return "x: " + format.format(location.getX()) + " y: " + format.format(location.getY()) + " z: " + format.format(location.getZ());
    }
}

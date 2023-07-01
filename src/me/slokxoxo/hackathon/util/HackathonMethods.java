package me.slokxoxo.hackathon.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class HackathonMethods {

    public static Vector calculateDirection(Location start, Location finish) {
        double startX = start.getX();
        double startY = start.getY();
        double startZ = start.getZ();
        double finishX = finish.getX();
        double finishY = finish.getY();
        double finishZ = finish.getZ();

        double directionX = finishX - startX;
        double directionY = finishY - startY;
        double directionZ = finishZ - startZ;

        return new Vector(directionX, directionY, directionZ);
    }

    public static double calculateSpeedFromForce(double force) {
        double minForce = 0.2;
        double maxForce = 1.0;
        double minSpeed = 0.4;
        double maxSpeed = 0.9;
        double normalizedForce = (force - minForce) / (maxForce - minForce);

        return minSpeed + normalizedForce * (maxSpeed - minSpeed);
    }


    public static List<Location> getLinePoints(Location startLoc, Location endLoc, int steps) {
        List<Location> locations = new ArrayList<>(steps);
        Vector direction = endLoc.toVector().subtract(startLoc.toVector()).normalize();
        double distance = startLoc.distance(endLoc);
        double interval = distance / steps;
        for (int i = 0; i < steps; i++) {
            double length = i * interval;
            Location loc = startLoc.clone().add(direction.clone().multiply(length));
            locations.add(loc);
        }
        return locations;
    }

    public static List<Location> getLinePoints(Player player, Location startLoc, Location endLoc, int steps) {
        List<Location> locations = new ArrayList<>(steps);
        Vector direction = endLoc.toVector().subtract(startLoc.toVector()).normalize();
        double distance = startLoc.distance(endLoc);
        double interval = distance / steps;
        for (int i = 0; i < steps; i++) {
            double length = i * interval;
            Location loc = startLoc.clone().add(direction.clone().multiply(length));
            loc.setDirection(loc.clone().subtract(player.getEyeLocation()).toVector().normalize());
            locations.add(loc);
        }
        return locations;
    }

    public static List<Block> getBlocksAlongLine(Location loc1, Location loc2) {
        World world = loc1.getWorld();
        List<Block> blocks = new ArrayList<>();

        Vector vector = loc2.toVector().subtract(loc1.toVector());
        Vector unit = vector.clone().normalize();
        double length = vector.length();

        for (double distance = 0; distance <= length; distance++) {
            Location currentLoc = loc1.clone().add(unit.clone().multiply(distance));
            assert world != null;
            Block block = world.getBlockAt(currentLoc);
            blocks.add(block);
        }

        return blocks;
    }
}

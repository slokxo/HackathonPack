package me.slokxoxo.hackathon.ability.metal;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.MetalAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import me.slokxoxo.hackathon.HackathonPack;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MetalRun extends MetalAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.SPEED)
    private double speed, dismountSpeed;

    private Vector direction;
    private boolean hasShifted;

    public MetalRun(Player player) {
        super(player);

        if (bPlayer.isOnCooldown(this) || hasAbility(player, MetalRun.class) || player.getGameMode().equals(GameMode.SPECTATOR))
            return;

        direction = player.getLocation().getDirection();
        hasShifted = false;

        FileConfiguration c = HackathonPack.hp.getConfig();

        cooldown = c.getLong("Abilities.Metal.MetalRun.Cooldown");
        duration = c.getLong("Abilities.Metal.MetalRun.Duration");
        speed = c.getDouble("Abilities.Metal.MetalRun.Speed");
        dismountSpeed = c.getDouble("Abilities.Metal.MetalRun.DismountSpeed");

        if (isAdjacentToMetal()) {
            start();
        }
    }


    private boolean isAdjacentToMetal() {
        Location location = player.getLocation();
        if (location.getBlock().getRelative(BlockFace.NORTH).getType().isSolid() && isMetalbendable(location.getBlock().getRelative(BlockFace.NORTH).getType())) {
            return true;
        } else if (location.getBlock().getRelative(BlockFace.SOUTH).getType().isSolid() && isMetalbendable(location.getBlock().getRelative(BlockFace.SOUTH).getType())) {
            return true;
        } else if (location.getBlock().getRelative(BlockFace.WEST).getType().isSolid() && isMetalbendable(location.getBlock().getRelative(BlockFace.WEST).getType())) {
            return true;
        } else return location.getBlock().getRelative(BlockFace.EAST).getType().isSolid() && isMetalbendable(location.getBlock().getRelative(BlockFace.EAST).getType());
    }


    @SuppressWarnings("deprecation")
    @Override
    public void progress() {
        if (player.isDead() || player.isOnGround() || player.isSneaking()) {
            this.remove();
            return;
        }
        if (System.currentTimeMillis() > this.getStartTime() + this.duration) {
            this.remove();
            return;
        }
        if (!isAdjacentToMetal()) {
            this.remove();
            return;
        }

        direction.multiply(speed);
        GeneralMethods.setVelocity(this, player, direction);

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 0.5f, 1f);

        player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 1, new Particle.DustOptions(Color.GRAY, 1.0f));
    }

    public void onShift() {
        if (!hasShifted && isAdjacentToMetal()) {
            player.sendMessage("hi");
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 0.5f, 1f);
            Vector direction = player.getLocation().getDirection().multiply(-1);
            direction.multiply(dismountSpeed);
            GeneralMethods.setVelocity(this, player, direction);
        }

        hasShifted = true;
    }

    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this);
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return true;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public String getName() {
        return "MetalRun";
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public void load() {}

    @Override
    public void stop() {}

    @Override
    public String getAuthor() {
        return HackathonPack.hp.dev();
    }

    @Override
    public String getDescription() {
        return "Advanced metalbenders are able to run along walls with their metalbending.";
    }

    @Override
    public String getInstructions() {
        return "To use, you must be adjacent to a block, then jump and sprint and shift! You can also tap shift to send yourself in the opposite direction, face directly at the block for best response.";
    }

    @Override
    public String getVersion() {
        return HackathonPack.hp.version();
    }

    @Override
    public boolean isEnabled() {
        return HackathonPack.hp.getConfig().getBoolean("Abilities.Metal.MetalRun.Enabled");
    }
}

package me.slokxoxo.hackathon.ability.ice;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.*;
import lombok.Getter;
import me.slokxoxo.hackathon.HackathonPack;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class IceDiscs extends IceAbility implements AddonAbility {

    public enum DiscType {
        SOURCED
    }

    @Attribute(Attribute.COOLDOWN)
    private long cooldown, iceDiscIntervals;
    @Attribute(Attribute.DURATION)
    @Getter private long duration;
    @Attribute(Attribute.RANGE)
    @Getter private double range;
    @Attribute(Attribute.SELECT_RANGE)
    @Getter private double selectRange;
    @Attribute(Attribute.SPEED)
    @Getter private double speed;
    @Attribute(Attribute.DAMAGE)
    @Getter private double damage;
    @Attribute("MaxUses")
    @Getter private double maxUses;
    @Attribute(Attribute.RADIUS)
    @Getter private double hitRadius;

    private final DiscType type;
    private List<TempBlock> tempBlocks;
    private final Block sourceBlock;
    private Location discLocation;
    Vector originalDirection;
    Vector direction;
    public boolean hasSourced;
    TempBlock tb;

    public IceDiscs(Player player, DiscType mode) {
        super(player);
        this.type = mode;

        sourceBlock = BlockSource.getWaterSourceBlock(player, selectRange, ClickType.SHIFT_DOWN, false, true, false);
        if (sourceBlock == null) {
            return;
        }

        if (!bPlayer.canBend(this)) {
            this.remove();
            return;
        }

        IceDiscs active = getAbility(player, getClass());
        if (active != null) {
            active.remove();
        }

        tempBlocks = new ArrayList<>();
        direction = player.getLocation().getDirection();
        Location spawnLoc = sourceBlock.getLocation().clone();
        discLocation = spawnLoc.clone().add(0.5, 1.3, 0.5);
        hasSourced = false;
        originalDirection = player.getLocation().getDirection();

        String path = "Abilities.Ice.IceDiscs.";

        FileConfiguration c = HackathonPack.hp.getConfig();

        cooldown = c.getLong(path + "Cooldown");
        duration = c.getLong(path + "Duration");
        range = c.getDouble(path + "Range");
        speed = c.getDouble(path + "Speed");
        selectRange = c.getDouble(path + "SelectRange");
        damage = c.getDouble(path + "Damage");
        maxUses = c.getDouble(path + "MaxUses");
        hitRadius = c.getDouble(path + "HitRadius");
        iceDiscIntervals = c.getLong(path + "IceDiscIntervals");

        start();
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this) || !player.isSneaking()) {
            this.remove();
            return;
        }
        if (System.currentTimeMillis() > this.getStartTime() + this.duration) {
            this.remove();
        }
        if (type == DiscType.SOURCED) {
            progressSourcing();
        }

        if (maxUses == 0) {
            this.remove();
        }
    }

    private void progressSourcing() {
        if (!hasSourced) {
            playIcebendingSound(sourceBlock.getLocation());
            if (sourceBlock.getType() == Material.SNOW) {
                tb = new TempBlock(sourceBlock, Material.ICE.createBlockData());
            } else {
                tb = new TempBlock(sourceBlock.getRelative(BlockFace.UP), Material.ICE.createBlockData());
            }
            GeneralMethods.displayColoredParticle("#ADD8E6", tb.getLocation().add(0, 1,0), 2, 0.03, 0, 0.03);
            tempBlocks.add(tb);
            hasSourced = true;
        }
    }

    public void onClick() {
        if (type == DiscType.SOURCED && maxUses > 0 && !bPlayer.isOnCooldown("IceDiscsIntervals")) {
            new IceDisc(player, sourceBlock.getLocation(), damage, range, speed);
            maxUses--;
            playIcebendingSound(player.getLocation());
            bPlayer.addCooldown("IceDiscsIntervals", iceDiscIntervals);
        }
    }

    private class IceDisc extends IceAbility {

        private final Player player;
        private final double range;
        private double distanceTravelled;
        private final double speed;
        private final double damage;
        private Location origin;

        public IceDisc(Player player, Location origin, double damage, double range, double speed) {
            super(player);
            this.player = player;
            this.range = range;
            this.speed = speed;
            this.damage = damage;
            this.origin = origin;

            start();
        }

        @Override
        public void progress() {
            if (!player.isSneaking()) {
                this.remove();
                return;
            }
            distanceTravelled += speed;

            if (distanceTravelled > range) {
                this.remove();
            }

            Vector playerDirection = player.getLocation().getDirection();
            Vector horizontalDirection = new Vector(playerDirection.getX(), 0, playerDirection.getZ()).normalize();
            Vector verticalDirection = new Vector(0, playerDirection.getY(), 0).normalize();

            double horizontalSpeed = speed * horizontalDirection.length();
            double verticalSpeed = speed * verticalDirection.length();


            if (player.isSneaking()) {
                horizontalDirection = new Vector(player.getLocation().getDirection().getX(), 0, player.getLocation().getDirection().getZ()).normalize();
            }

            discLocation.add(horizontalDirection.multiply(horizontalSpeed));
            discLocation.add(verticalDirection.multiply(verticalSpeed));

            origin = (origin == null) ? sourceBlock.getLocation() : origin;

            for (int angle = 0; angle < 360; angle += 5) {
                double radian = Math.toRadians(angle);
                double x = Math.cos(radian);
                double z = Math.sin(radian);
                Location eclipse = origin.clone().add(x * 0.5, 1.5, z * 0.5);
                GeneralMethods.displayColoredParticle("#ADD8E6", eclipse, 1, 0.11, 0.11, 0.11);
            }

            direction = player.getEyeLocation().getDirection();
            origin.add(direction);

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(origin, hitRadius, e -> e != player && e instanceof LivingEntity)) {
                DamageHandler.damageEntity(entity, player, damage, this);
            }
        }

        @Override
        public boolean isSneakAbility() {
            return true;
        }

        @Override
        public boolean isHiddenAbility() {
            return true;
        }

        @Override
        public boolean isHarmlessAbility() {
            return false;
        }

        @Override
        public long getCooldown() {
            return 1000;
        }

        @Override
        public String getName() {
            return "IceDisc";
        }

        @Override
        public Location getLocation() {
            return null;
        }
    }

    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this, cooldown);

        for (TempBlock tb : tempBlocks) {
            tb.revertBlock();
        }
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public String getName() {
        return "IceDisc";
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
    public String getVersion() {
        return HackathonPack.hp.version();
    }

    @Override
    public String getDescription() {
        return "As demonstrated by Master Katara in the Northern Water Tribe fighting Master Pakku, waterbenders, particularly icebenders can summon stumps of ice in which they can" +
                " break off pieces of the stumps and reshape them into discs.";
    }

    @Override
    public String getInstructions() {
        return "Shift to bring the stump up. Left click to move it where you look.";
    }

    @Override
    public boolean isEnabled() {
        return HackathonPack.hp.getConfig().getBoolean("Abilities.Ice.IceDiscs.Enabled");
    }
}

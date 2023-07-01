package me.slokxoxo.hackathon.ability.sand;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.util.*;
import lombok.Getter;
import me.slokxoxo.hackathon.HackathonPack;
import me.slokxoxo.hackathon.util.HackathonMethods;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class SandSlash extends SandAbility implements AddonAbility, ComboAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    @Getter
    private long duration;
    @Attribute(Attribute.SPEED)
    @Getter
    private double speed;
    @Attribute(Attribute.RADIUS)
    @Getter
    private double hitRadius;
    @Attribute(Attribute.DAMAGE)
    @Getter
    private double damage;
    @Attribute(Attribute.RANGE)
    @Getter
    private double range;

    private Location startLoc, endLoc;
    private int id = 0;
    private HashMap<Integer, Location> locations;
    private HashMap<Integer, Vector> directions;
    private List<Location> locList;
    private boolean setup, progressing;
    private int counter, startDistance, arcSteps;
    private float maxAngle;

    public SandSlash(Player player) {
        this(player,
                HackathonPack.hp.getConfig().getDouble("Abilities.Sand.SandSlash.Range"),
                HackathonPack.hp.getConfig().getDouble("Abilities.Sand.SandSlash.Damage"),
                HackathonPack.hp.getConfig().getDouble("Abilities.Sand.SandSlash.Speed"),
                3,
                (int) (HackathonPack.hp.getConfig().getDouble("Abilities.Sand.SandSlash.Range") * 3));
    }

    public SandSlash(Player player, double range, double damage, double speed, int startDistance, int arcSteps) {
        super(player);

        if (bPlayer != null && this.bPlayer.isOnCooldown(this)) {
            return;
        }

        if (!this.isSandbendable(player.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
            return;
        }

        if (hasAbility(player, EarthBlast.class))
            getAbility(player, EarthBlast.class).remove();

        if (!bPlayer.canBendIgnoreBinds(this) || hasAbility(player, SandSlash.class)) return;

        FileConfiguration c = HackathonPack.hp.getConfig();
        this.cooldown = c.getLong("Abilities.Sand.SandSlash.Cooldown");
        this.speed = speed;
        this.hitRadius = c.getDouble("Abilities.Sand.SandSlash.HitRadius");
        this.damage = damage;
        this.range = range;
        this.maxAngle = c.getInt("Abilities.Sand.SandSlash.MaxAngle");
        this.startDistance = startDistance;
        this.arcSteps = arcSteps;

        startLoc = GeneralMethods.getTargetedLocation(player, startDistance, Material.LIGHT);
        locations = new HashMap<>();
        directions = new HashMap<>();
        locList = new ArrayList<>();
        duration = (long) (200 + range * (50 / speed));


        start();
        if (!isRemoved())
            bPlayer.addCooldown(this);
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }

        if (System.currentTimeMillis() > getStartTime() + duration) {
            remove();
            return;
        }

        if (!setup) {
            if (System.currentTimeMillis() < getStartTime() + 200) {

                endLoc = GeneralMethods.getTargetedLocation(player, startDistance);

                if (Math.abs(endLoc.getYaw() - startLoc.getYaw()) >= maxAngle) {
                    setup = true;
                }
                if (Math.abs(endLoc.getPitch() - startLoc.getPitch()) >= maxAngle) {
                    setup = true;
                }

                return;
            } else setup = true;
        }

        if (!progressing) {
            List<Location> linePoints;
            linePoints = HackathonMethods.getLinePoints(player, startLoc, endLoc, arcSteps);
            for (Location loc : linePoints) {
                locations.put(id, loc);
                directions.put(id, loc.getDirection());
                id++;
            }
            progressing = true;
        } else {
            if (locations.isEmpty()) {
                remove();
                return;
            }

            locList.clear();

            for (Integer i : locations.keySet()) {
                updateLocations(locations.get(i));

                Block b = locations.get(i).getBlock();
                if (GeneralMethods.isSolid(b) || b.isLiquid()) {
                    continue;
                }

                if (GeneralMethods.checkDiagonalWall(locations.get(i), directions.get(i))) {
                    continue;
                }

                if (locations.get(i).distanceSquared(startLoc) > range * range) {
                    remove();
                    return;
                }

                if (progressing) {
                    for (int index = 0; index < 2; index++) {
                        locations.get(i).add(directions.get(i).clone().multiply(speed / 2));
                        ParticleEffect.FALLING_DUST.display(locations.get(i), 1, 0.2, 0.2, 0.2, Material.SAND.createBlockData());
                    }
                } else {
                    locations.get(i).add(directions.get(i).clone().multiply(speed));
                    ParticleEffect.FALLING_DUST.display(locations.get(i), 1, 0.2, 0.2, 0.2, Material.SAND.createBlockData());
                }

                if (counter % 6 == 0) {
                    playSandbendingSound(locations.get(i));
                }
                counter++;

                for (Entity e : GeneralMethods.getEntitiesAroundPoint(locations.get(i), hitRadius)) {
                    if (e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId()) {
                        DamageHandler.damageEntity(e, damage, this);
                        ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 2, true, false));
                        remove();
                        return;
                    }
                }
            }
        }
    }

    public void remove() {
        super.remove();
        bPlayer.addCooldown(this);
    }

    private void updateLocations(Location loc) {
        locList.add(loc);
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return startLoc;
    }

    @Override
    public List<Location> getLocations() {
        return locList;
    }

    @Override
    public String getName() {
        return "SandSlash";
    }

    @Override
    public String getDescription() {
        return "This combo allows a sand-bender to send forth a quick-moving blade of sand that damages players and mobs. Note: You must be standing on a sandbendable block and have sufficient " +
                "sand blocks around you.";
    }

    @Override
    public String getInstructions() {
        return "EarthBlast (Tap sneak) > EarthBlast (Tap sneak) > EarthBlast (Left click)";
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new SandSlash(player);
    }

    @Override
    public ArrayList<ComboManager.AbilityInformation> getCombination() {
        ArrayList<ComboManager.AbilityInformation> combo = new ArrayList<>();
        combo.add(new ComboManager.AbilityInformation("EarthBlast", ClickType.SHIFT_DOWN));
        combo.add(new ComboManager.AbilityInformation("EarthBlast", ClickType.SHIFT_UP));
        combo.add(new ComboManager.AbilityInformation("EarthBlast", ClickType.SHIFT_DOWN));
        combo.add(new ComboManager.AbilityInformation("EarthBlast", ClickType.SHIFT_UP));
        combo.add(new ComboManager.AbilityInformation("EarthBlast", ClickType.LEFT_CLICK));
        return combo;
    }

    @Override
    public String getAuthor() {
        return HackathonPack.hp.dev();
    }

    @Override
    public String getVersion() {
        return HackathonPack.hp.version();
    }

    @Override
    public void load() {}

    @Override
    public void stop() {}

    @Override
    public boolean isEnabled() {
        return HackathonPack.hp.getConfig().getBoolean("Abilities.Sand.SandSlash.Enabled");
    }
}
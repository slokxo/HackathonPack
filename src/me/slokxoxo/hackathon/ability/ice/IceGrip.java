package me.slokxoxo.hackathon.ability.ice;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.chiblocking.Paralyze;
import com.projectkorra.projectkorra.firebending.HeatControl;
import com.projectkorra.projectkorra.util.*;
import lombok.Getter;
import me.slokxoxo.hackathon.HackathonPack;
import me.slokxoxo.hackathon.util.HackathonMethods;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class IceGrip extends IceAbility implements AddonAbility {

    public enum GripType {
        SOURCING, MOVEMENT, GRIPPING, RETRACTING,
    }

    private final List<MovementHandler> movementHandlers = new ArrayList<>();
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.SELECT_RANGE)
    @Getter
    private double sourceRange;
    @Attribute(Attribute.SPEED)
    @Getter
    private double speed;
    @Attribute(Attribute.DURATION)
    @Getter
    private long gripDuration, duration;
    @Attribute(Attribute.RANGE)
    @Getter
    private double range;
    @Getter
    private double hitbox;
    @Attribute(Attribute.RADIUS)
    @Getter private double disperseRadius;
    private boolean doesMovementHandler;
    private boolean controllable;

    private Location loc;

    private GripType type;
    HeatControl.HeatControlType heatControlType;
    private List<TempBlock> tempBlocks;
    private List<ArmorStand> armorStands;
    private final Block sourceBlock;
    private LivingEntity le;
    private double distanceTravelled;
    private Vector dir;
    public boolean clicked;
    private boolean hasAffectedTarget;
    private boolean hasDispersed;
    private boolean armorStandSpawned = false;


    public IceGrip(Player player, GripType type) {
        super(player);

        this.sourceBlock = BlockSource.getWaterSourceBlock(player, this.sourceRange, ClickType.LEFT_CLICK, true, true, this.bPlayer.canPlantbend());
        if (sourceBlock == null) {
            return;
        }

        loc = sourceBlock.getLocation().add(.5, .5, .5);

        if (!bPlayer.canBend(this)) {
            this.remove();
            return;
        }

        IceGrip old = getAbility(player, getClass());
        if (old != null) {
            old.remove();
        }

        tempBlocks = new LinkedList<>();
        armorStands = new LinkedList<>();
        this.type = type;
        this.clicked = false;

        FileConfiguration c = HackathonPack.hp.getConfig();

        cooldown = c.getLong("Abilities.Ice.IceGrip.Cooldown");
        sourceRange = c.getDouble("Abilities.Ice.IceGrip.SourceRange");
        speed = c.getDouble("Abilities.Ice.IceGrip.Speed");
        gripDuration = c.getInt("Abilities.Ice.IceGrip.GripDuration");
        range = c.getDouble("Abilities.Ice.IceGrip.Range");
        hitbox = c.getDouble("Abilities.Ice.IceGrip.Hitbox");
        doesMovementHandler = c.getBoolean("Abilities.Ice.IceGrip.MovementHandler");
        disperseRadius = c.getDouble("Abilities.Ice.IceGrip.DisperseRadius");
        duration = c.getLong("Abilities.Ice.IceGrip.Duration");
        controllable = c.getBoolean("Abilities.Ice.IceGrip.Controllable");

        start();
        playIcebendingSound(sourceBlock.getLocation());
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreCooldowns(this)) {
            this.remove();
            return;
        } else if (System.currentTimeMillis() > this.getStartTime() + 10000) {
            this.remove();
        }
        switch (type) {
            case SOURCING -> progressSourcing();
            case MOVEMENT -> progressTravelling();
            case GRIPPING -> progressGripping();
            case RETRACTING -> progressRetracting();
        }

        if (type == GripType.MOVEMENT && player.isSneaking()) {
            type = GripType.RETRACTING;
        }

        if (le instanceof Player leAsPlayer) {
            HeatControl heatControl = CoreAbility.getAbility(leAsPlayer, HeatControl.class);
            if (heatControl != null && leAsPlayer.isSneaking() && heatControlType == HeatControl.HeatControlType.MELT) {
                this.remove();
                leAsPlayer.getWorld().spawnParticle(Particle.CLOUD, leAsPlayer.getLocation().add(0, 0.2, 0), 1, Math.random(), Math.random(), Math.random());
            }
        }
    }

    private void progressDispersing() {
        double maxRadius = 5.0;
        if (!hasDispersed) {
            player.getWorld().spawnParticle(Particle.CLOUD, loc, 1);
            new BukkitRunnable() {
                double currentRadius = 1.0;

                public void run() {
                    for (Block block : GeneralMethods.getBlocksAroundPoint(loc, currentRadius)) {
                        if (GeneralMethods.isSolid(block)) {
                            Material iceMaterials = switch ((int) (Math.random() * 4)) {
                                case 0 -> Material.ICE;
                                case 1 -> Material.BLUE_ICE;
                                case 2 -> Material.PACKED_ICE;
                                default -> Material.FROSTED_ICE;
                            };
                            new TempBlock(block, iceMaterials).setRevertTime(4000);
                        }
                        currentRadius++;
                        if (currentRadius > maxRadius) {
                            cancel();
                        }
                    }
                }
            }.runTaskTimer(HackathonPack.hp, 0L, 2L);
        }
        hasDispersed = true;
        this.remove();
    }


    private void progressRetracting() {
        loc.subtract(dir);

        List<Block> line = HackathonMethods.getBlocksAlongLine(sourceBlock.getLocation().add(.5, .5, .5), loc);
        for (int i = tempBlocks.size() - 1; i >= line.size(); i--) {
            tempBlocks.remove(i).revertBlock();
        }

        if (tempBlocks.isEmpty() || tempBlocks.size() < 2) {
            this.remove();
        }
    }

    private void progressSourcing() {
        playFocusWaterEffect(sourceBlock);
        if (sourceBlock.getLocation().distanceSquared(player.getLocation()) > sourceRange * sourceRange || !isWaterbendable(player, sourceBlock)) {
            this.remove();
        }
    }

    private void progressTravelling() {
        if (player.isSneaking() && controllable) {
            dir = player.getLocation().getDirection();
        }
        loc.add(dir);
        distanceTravelled += speed;

        List<Block> line = HackathonMethods.getBlocksAlongLine(sourceBlock.getLocation().add(.5, .5, .5), loc);
        for (int i = tempBlocks.size(); i < line.size(); i++) {
            Block line2 = line.get(i);
            if (GeneralMethods.isSolid(line2) && line2 != sourceBlock) {
               progressDispersing();
                return;
            }
            if (distanceTravelled > range) {
                type = GripType.RETRACTING;
                return;
            }
            tempBlocks.add(new TempBlock(line2, Material.WATER));
            if (line2.getLocation().getBlock().getType() == Material.LAVA) {
                new TempBlock(line2, Material.OBSIDIAN);
            }
        }

        if (isWater(loc.getBlock())) {
            Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.WATER_BUBBLE, loc, 8, .5, .5, .5, 0);
        }
        Optional<LivingEntity> target = Optional.empty();
        List<Entity> entities = GeneralMethods.getEntitiesAroundPoint(loc, hitbox);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity && entity.getUniqueId() != player.getUniqueId()) {
                target = Optional.of((LivingEntity) entity);
                break;
            }
        }

        if (target.isPresent() || distanceTravelled > range) {
            le = target.orElse(null);
            type = GripType.GRIPPING;
            if (le != null) {
                for (TempBlock tempBlock : tempBlocks) {
                    tempBlock.revertBlock();
                }
                tempBlocks.clear();
            }
        }
    }

    private void progressGripping() {
        if (!armorStandSpawned && (le instanceof Player || le instanceof Zombie || le instanceof Skeleton || le instanceof Creeper || le instanceof Enderman)) {
            ArmorStand armorStand = (ArmorStand) Objects.requireNonNull(player.getLocation().getWorld()).spawnEntity(le.getLocation().subtract(0, 0.5, 0), EntityType.ARMOR_STAND);
            Objects.requireNonNull(armorStand.getEquipment()).setHelmet(new ItemStack(Material.PACKED_ICE));
            armorStand.setVisible(false);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStandSpawned = true;

            if (armorStand.getTicksLived() > 5 * 20) {
                armorStand.remove();
                return;
            }
            armorStands.add(armorStand);

            if (armorStandSpawned) {
                Bukkit.getScheduler().runTaskLater(HackathonPack.hp, () -> {
                    if (Bukkit.getServer().getPluginManager().isPluginEnabled("GSit")) {
                        Bukkit.dispatchCommand(le, "lay");
                        armorStand.teleport(player.getLocation().subtract(0, 1, 0));
                    } else {
                        HackathonPack.hp.getLogger().info("The plugin " + ChatColor.GREEN + " GSit " + "is not enabled. Enable " + ChatColor.GREEN + "GSit " + "if you want the full IceGrip experience.");
                    }
                }, 30);
            }
        }

        if (!hasAffectedTarget && doesMovementHandler) {
            MovementHandler mh = new MovementHandler(le, CoreAbility.getAbility(Paralyze.class));
            mh.stopWithDuration(gripDuration * 20, Element.ICE.getColor() + "Gripped!");
            movementHandlers.add(mh);
            DamageHandler.damageEntity(le, player, 3, this);
            le.setFreezeTicks((int) (gripDuration * 20));
            le.setFireTicks(0);
        }

        if (ThreadLocalRandom.current().nextInt(3) == 0) {
            if (!le.isDead()) {
                double xOffset = Math.random();
                double zOffset = Math.random();
                double yOffset = Math.random() * 0.5;

                ParticleEffect.SNOW_SHOVEL.display(le.getLocation(), 2, xOffset, yOffset, zOffset);
            }
        }
        hasAffectedTarget = true;
    }

    public void onClick() {
        if (clicked) {
            return;
        }
        clicked = true;
        if (type == GripType.SOURCING) {

            type = GripType.MOVEMENT;
            dir = HackathonMethods.calculateDirection(loc, GeneralMethods.getTargetedLocation(player, range)).normalize().multiply(speed);
        }
    }

    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this);
        for (MovementHandler mh : movementHandlers) {
            mh.reset();
        }
        for (TempBlock tb : tempBlocks) {
            tb.revertBlock();
        }
        for (ArmorStand armorStand : armorStands) {
            armorStand.remove();

        }
        if (le != null) {
            le.setFreezeTicks(0);
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
    public Location getLocation() {
        return loc;
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
    public String getName() {
        return "IceGrip";
    }

    @Override
    public String getDescription() {
        return "This is a rare technique, where a waterbender can throw a tendril of water to wrap around a foe's limbs, holding them in place with a very strong grip, resulting " +
                "in the enemy unable to move! If your whip surpasses the range limit, it will retract back to you. If it meets a solid block it will start engulfing it. If it meets lava it will " +
                "thaw it.";
    }

    @Override
    public String getInstructions() {
        return "Tap shift to source, then click to send out a spear of water and ice out of a source. If it comes in contact with anyone, it will freeze their body part. You can right click while on snow or ice to grip your feet into the ground" +
                " and take cover inside the snow. If you see your spear is going to hit nothing, you can tap shift to retract it.";
    }

    @Override
    public String getVersion() {
        return HackathonPack.hp.version();
    }

    @Override
    public boolean isEnabled() {
        return HackathonPack.hp.getConfig().getBoolean("Abilities.Ice.IceGrip.Enabled");
    }
}
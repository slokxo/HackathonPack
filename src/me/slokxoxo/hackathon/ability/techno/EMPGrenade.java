package me.slokxoxo.hackathon.ability.techno;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import lombok.Getter;
import me.slokxoxo.hackathon.HackathonPack;

import me.slokxoxo.hackathon.util.TechnoAbility;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EMPGrenade extends TechnoAbility implements AddonAbility {

    @Getter private static final Map<Integer, EMPGrenade> SBULLET = new ConcurrentHashMap<>();
    @Getter private static final Map<String, Long> EMPT = new ConcurrentHashMap<>();
    @Getter private static final Map<String, EMPGrenade> EMP_GRENADE_MAP = new ConcurrentHashMap<>();

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RADIUS)
    @Getter private double radius;

    public static boolean launched;

    public EMPGrenade(Player player) {
        super(player);
        if (!bPlayer.canBend(this) || hasAbility(player, EMPGrenade.class)) return;

        if (bPlayer != null && this.bPlayer.isOnCooldown(this)) {
            return;
        }
        this.cooldown = getConfig().getLong("Abilities.Techno.EMPGrenade.Cooldown");
        this.radius = getConfig().getDouble("Abilities.Techno.EMPGrenade.Radius");

        launched = false;
        start();
        giveEMP();
    }

    private void giveEMP() {
        ItemStack emp = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta empMeta = emp.getItemMeta();
        assert empMeta != null;
        empMeta.setDisplayName(ChatColor.GRAY + "" + ChatColor.ITALIC + "EMP Grenade");
        empMeta.addEnchant(Enchantment.SOUL_SPEED, 1, true);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Get the upper hand by shooting");
        lore.add(ChatColor.GRAY + "an EMP grenade, disabling");
        lore.add(ChatColor.GRAY + "all technology, and stunning foes.");
        empMeta.setLore(lore);
        emp.setItemMeta(empMeta);
        PlayerInventory inventory = player.getInventory();
        int heldItemSlot = inventory.getHeldItemSlot();
        ItemStack heldItem = inventory.getItem(heldItemSlot);

        if (isFull(player)) {
            return;
        }

        if (heldItem == null || heldItem.getType() == Material.AIR) {
            inventory.setItem(heldItemSlot, emp);
        } else {
            int emptySlot = inventory.firstEmpty();
            if (emptySlot != -1) {
                inventory.setItem(emptySlot, heldItem);
                inventory.setItem(inventory.getHeldItemSlot(), emp);
            }
        }
    }

    private boolean isFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    private void removeEMP() {
        PlayerInventory inventory = player.getInventory();
        String desiredName = "EMP Grenade";

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == Material.HEART_OF_THE_SEA && item.hasItemMeta()) {
                ItemMeta itemMeta = item.getItemMeta();
                assert itemMeta != null;
                String itemName = itemMeta.getDisplayName();
                String strippedItemName = ChatColor.stripColor(itemName);
                String strippedDesiredName = ChatColor.stripColor(desiredName);

                if (strippedItemName.equalsIgnoreCase(strippedDesiredName)) {
                    inventory.removeItem(item);
                    break;
                }
            }
        }
    }

    public void onShift() {
        launched = true;
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreCooldowns(this)) {
            this.remove();
            return;
        }

        if (!this.player.isOnline() || this.player.isDead()) {
            this.remove();
            return;
        }

        if (launched) {
            ShulkerBullet shulkerBullet = player.launchProjectile(ShulkerBullet.class);
            Vector direction = player.getLocation().getDirection();
            shulkerBullet.setVelocity(direction.multiply(1.5));
            SBULLET.put(shulkerBullet.getEntityId(), this);
            bPlayer.addCooldown(this);
            this.remove();
            PlayerInventory inventory = player.getInventory();
            int hotsSlot = inventory.first(Material.HEART_OF_THE_SEA);
            if (hotsSlot != -1) {
                inventory.setItem(hotsSlot, null);
            }
        }
    }


    public void burstOfParticles(Location centerLocation) {
        double x, y, z;
        final double r = 5;
        final double heightScale = 0.5;

        for (double theta = 0; theta <= 180; theta += 10) {
            final double k = 10 / Math.sin(Math.toRadians(theta));

            for (double phi = 0; phi < 360; phi += k) {
                final double p = Math.toRadians(phi);
                final double a = Math.toRadians(theta);

                x = r * Math.cos(p) * Math.sin(a);
                y = heightScale * r * Math.sin(p) * Math.sin(a);
                z = r * Math.cos(a);

                player.spawnParticle(Particle.REDSTONE, centerLocation, 1, x, y, z, new Particle.DustOptions(Color.BLUE, 0.7f));
                player.spawnParticle(Particle.REDSTONE, centerLocation, 1, x, y, z, new Particle.DustOptions(Color.TEAL, 0.7f));
                player.spawnParticle(Particle.REDSTONE, centerLocation, 1, x, y, z, new Particle.DustOptions(Color.AQUA, 0.7f));
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(centerLocation, 4)) {
                affectTargets(entity);
            }
        }
    }

    public void affectTargets(final Entity entity) {
        if (entity instanceof final Player p) {
            GrapplingHook grapplingHook = (GrapplingHook) CoreAbility.getAbility(GrapplingHook.class);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 5));
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 2));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
            p.getWorld().spawnParticle(Particle.FLASH, p.getLocation(), 1);
            if (hasAbility(p, GrapplingHook.class)) {
                if (grapplingHook != null) {
                    grapplingHook.remove();
                }
            }
            EMPT.put(p.getName(), System.currentTimeMillis());
            EMP_GRENADE_MAP.put(p.getName(), this);
        }
    }


    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this);
        removeEMP();

        int duration = 5000;
        long startTime = System.currentTimeMillis();

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - startTime;
                if (elapsedTime >= duration) {
                    cancel();
                    return;
                }

                for (Entity en : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), 40)) {
                    if (en instanceof LivingEntity le) {
                        le.removePotionEffect(PotionEffectType.LEVITATION);
                    }
                }
            }
        };

        task.runTaskTimer(HackathonPack.hp, 0, 1);
    }

    @Override
    public boolean isSneakAbility() {
        return false;
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
        return "EMPGrenade";
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
        return "Send out an EMP Grenade that temporarily degrades the targets fatigue and strength, while also disabling their technological abilities.";
    }

    @Override
    public String getInstructions() {
        return "Left click to summon the grenade, tap shift to shoot it..";
    }

    @Override
    public String getVersion() {
        return HackathonPack.hp.version();
    }
    @Override
    public boolean isEnabled() {
        return HackathonPack.hp.getConfig().getBoolean("Abilities.Techno.EMPGrenade.Enabled");
    }
}

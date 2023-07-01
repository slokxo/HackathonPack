package me.slokxoxo.hackathon.listener;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.slokxoxo.hackathon.HackathonPack;
import me.slokxoxo.hackathon.ability.techno.EMPGrenade;
import me.slokxoxo.hackathon.ability.techno.GrapplingHook;
import me.slokxoxo.hackathon.util.HackathonMethods;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class Listeners implements Listener {

    private final GrapplingHook grapplingHook = (GrapplingHook) CoreAbility.getAbility(GrapplingHook.class);
    private BukkitRunnable grapplingHookTask;

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item.getType() == Material.BOW) {
            ItemMeta itemMeta = item.getItemMeta();
            assert itemMeta != null;
            String itemName = itemMeta.getDisplayName();

            String strippedItemName = ChatColor.stripColor(itemName);
            String strippedDesiredName = ChatColor.stripColor("Grappling Hook");

            if (strippedItemName.equalsIgnoreCase(strippedDesiredName)) {
                event.setCancelled(true);
            }
        } else if (item.getType() == Material.HEART_OF_THE_SEA) {
            ItemMeta itemMeta = item.getItemMeta();
            assert itemMeta != null;
            String itemName = itemMeta.getDisplayName();

            String strippedItemName = ChatColor.stripColor(itemName);
            String strippedDesiredName = ChatColor.stripColor("EMP Grenade");

            if (strippedItemName.equalsIgnoreCase(strippedDesiredName)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            String bowName = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(event.getBow()).getItemMeta()).getDisplayName());
            if (Objects.equals(Objects.requireNonNull(event.getBow().getItemMeta()).getLore(), List.of(ChatColor.GRAY + "Enhance your agility by grappling to surfaces."))) {
                if (ChatColor.stripColor(bowName).equalsIgnoreCase("Grappling Hook")) {
                    if (grapplingHook != null) {
                        if (event.getEntity() instanceof Player player) {
                            Arrow arrow = (Arrow) event.getProjectile();
                            arrow.setMetadata("HackathonPack://Arrow", new FixedMetadataValue(HackathonPack.hp, true));
                            double force = event.getForce();
                            double speed = HackathonMethods.calculateSpeedFromForce(force);

                            arrow.setVelocity(arrow.getVelocity().multiply(2D));

                            if (grapplingHookTask != null || grapplingHook.isRemoved()) {
                                grapplingHookTask.cancel();
                                player.setVelocity(new Vector(0, 0, 0));
                            }

                            grapplingHookTask = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (arrow.getLocation().distance(player.getLocation()) > GrapplingHook.getRange()) {
                                        arrow.remove();
                                        this.cancel();
                                    }

                                    if (player.isSneaking() && arrow.isOnGround()) {
                                        player.setVelocity(player.getLocation().getDirection().multiply(GrapplingHook.getShiftSpeed()));
                                        arrow.remove();
                                        this.cancel();
                                    }

                                    if (arrow.isOnGround() && !arrow.isDead()) {
                                        Vector dir = arrow.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                                        player.setVelocity(dir.multiply(speed));

                                        Location arrowBlockLocation = arrow.getLocation().getBlock().getLocation();
                                        List<Location> linePoints = HackathonMethods.getLinePoints(player.getLocation(), arrowBlockLocation, 10);

                                        for (Location loc : linePoints) {
                                            player.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(Color.GRAY, 1.0f));
                                        }
                                        if (arrow.isOnGround() && player.getLocation().distance(arrow.getLocation()) < 0.8) {
                                            player.setVelocity(new Vector(0, 0, 0));
                                        }
                                    }

                                    if (grapplingHook.isRemoved()) {
                                        arrow.remove();
                                        this.cancel();
                                        grapplingHook.remove();
                                        player.setVelocity(new Vector(0, 0, 0));
                                    }
                                }
                            };

                            grapplingHookTask.runTaskTimer(HackathonPack.hp, 0, 0);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Integer id = event.getEntity().getEntityId();
        final EMPGrenade empGrenade = EMPGrenade.getSBULLET().get(id);
        if (empGrenade != null) {
            final Location loc = event.getEntity().getLocation();
            empGrenade.burstOfParticles(loc);
            for (final Entity en : GeneralMethods.getEntitiesAroundPoint(loc, empGrenade.getRadius())) {
                empGrenade.affectTargets(en);
            }

            EMPGrenade.getSBULLET().remove(id);
        }
    }
}

package me.slokxoxo.hackathon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.util.ActionBar;
import me.slokxoxo.hackathon.HackathonPack;
import me.slokxoxo.hackathon.ability.techno.GrapplingHook;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.Objects;

@CommandAlias("ab|actionbar|actionb")
public class ActionBarCommand extends BaseCommand {
    private static boolean showBlocksRunning = false;

    private static boolean healthDisplayOn = false;
    private static BukkitTask healthDisplayTask = null;

    @Default
    public void toggleHealth(Player player) {
        if (!healthDisplayOn) {
            healthDisplayOn = true;
            player.sendMessage(ChatColor.GREEN + "Health display toggled on.");

            healthDisplayTask = Bukkit.getScheduler().runTaskTimer(HackathonPack.hp, () -> {
                Entity target = GeneralMethods.getTargetedEntity(player, 15);
                if (target instanceof LivingEntity livingEntity) {
                    if (livingEntity.isInvisible() || (livingEntity instanceof Player && ((Player) livingEntity).getGameMode() == GameMode.SPECTATOR)) {
                        return;
                    }
                    double health = livingEntity.getHealth();
                    DecimalFormat format = new DecimalFormat("#.##");
                    double maxHealth = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
                    ActionBar.sendActionBar(ChatColor.BLUE + livingEntity.getName() + "'s Health: " + ChatColor.GREEN + format.format(health) + ChatColor.GRAY + "" + ChatColor.ITALIC + " / " +
                            ChatColor.GREEN + format.format(maxHealth), player);
                }
            }, 0L, 20L);

        } else {
            healthDisplayOn = false;
            if (healthDisplayTask != null) {
                healthDisplayTask.cancel();
                healthDisplayTask = null;
            }
            player.sendMessage(ChatColor.RED + "Health display toggled off.");
        }
    }

    public static void showBlocks(Player player, ChatColor firstColor, ChatColor secondColor) {
        Location loc = GeneralMethods.getTargetedLocation(player, 30);
        Block block = loc.getBlock();
        DecimalFormat format = new DecimalFormat("#.#");
        Location playerLocation = player.getLocation();
        Location blockLocation = block.getLocation();

        double distance = playerLocation.distance(blockLocation);

        if (distance >= 30) {
            showBlocksRunning = false;
            return;
        }


        if (showBlocksRunning) {
            String message = (distance < GrapplingHook.getRange()) ? ChatColor.GREEN + "" + ChatColor.BOLD + "<- Within range!" : ChatColor.RED + "" + ChatColor.BOLD + "<- Not within range.";
            ActionBar.sendActionBar(firstColor + "Distance to block: " + secondColor + format.format(distance) + " blocks " + message, player);

        }
        showBlocksRunning = true;
    }
}


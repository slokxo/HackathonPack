package me.slokxoxo.hackathon.ability.techno;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import lombok.Getter;
import me.slokxoxo.hackathon.HackathonPack;
import me.slokxoxo.hackathon.command.ActionBarCommand;
import me.slokxoxo.hackathon.util.TechnoAbility;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class GrapplingHook extends TechnoAbility implements AddonAbility {

    @Attribute(Attribute.RANGE)
    @Getter public static double range;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.SPEED)
    @Getter public static double shiftSpeed;
    private boolean showBlocksInRange;

    public GrapplingHook(Player player) {
        super(player);

        if (!bPlayer.canBend(this) || hasAbility(player, GrapplingHook.class)) return;

        if (bPlayer != null && this.bPlayer.isOnCooldown(this)) {
            return;
        }

        FileConfiguration c = HackathonPack.hp.getConfig();
        cooldown = c.getLong("Abilities.Techno.GrapplingHook.Cooldown");
        range = c.getDouble("Abilities.Techno.GrapplingHook.Range");
        duration = c.getLong("Abilities.Techno.GrapplingHook.Duration");;
        shiftSpeed = c.getDouble("Abilities.Techno.GrapplingHook.ShiftSpeed");
        showBlocksInRange = c.getBoolean("Abilities.Techno.GrapplingHook.ShowBlocksInRange");

        start();
        giveBow();
    }

    private void giveBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        assert bowMeta != null;
        bowMeta.setDisplayName(ChatColor.GRAY + "" + ChatColor.ITALIC + "Grappling Hook");
        bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        bowMeta.setLore(List.of(ChatColor.GRAY + "Enhance your agility by grappling to surfaces."));
        bow.setItemMeta(bowMeta);


        PlayerInventory inventory = player.getInventory();
        int heldItemSlot = inventory.getHeldItemSlot();
        ItemStack heldItem = inventory.getItem(heldItemSlot);

        if (!inventory.contains(Material.ARROW)) {
            if (inventory.getItem(9) == null) {
                inventory.setItem(9, new ItemStack(Material.ARROW));
            } else
            inventory.setItem(inventory.firstEmpty(), new ItemStack(Material.ARROW));
        }

        if (isFull(player)) {
            return;
        }

        if (heldItem == null || heldItem.getType() == Material.AIR) {
            inventory.setItem(heldItemSlot, bow);
        } else {
            int emptySlot = inventory.firstEmpty();
            if (emptySlot != -1) {
                inventory.setItem(emptySlot, heldItem);
                inventory.setItem(inventory.getHeldItemSlot(), bow);
            }
        }
    }

    private boolean isFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    private void removeBow() {
        PlayerInventory inventory = player.getInventory();
        String desiredName = "Grappling Hook";

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == Material.BOW && item.hasItemMeta()) {
                ItemMeta itemMeta = item.getItemMeta();
                assert itemMeta != null;
                String itemName = itemMeta.getDisplayName();
                String strippedItemName = ChatColor.stripColor(itemName);
                String strippedDesiredName = ChatColor.stripColor(desiredName);

                if (strippedItemName.equalsIgnoreCase(strippedDesiredName)) {
                    inventory.removeItem(item);

                    ItemStack arrow = new ItemStack(Material.ARROW);
                    arrow.setAmount(1);
                    inventory.removeItem(arrow);

                    break;
                }
            }
        }
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this)) {
            this.remove();
        } else if (System.currentTimeMillis() - getStartTime() >= duration) {
            this.remove();
        }
        if (showBlocksInRange) {
            ActionBarCommand.showBlocks(player, net.md_5.bungee.api.ChatColor.GRAY, net.md_5.bungee.api.ChatColor.BLUE);
        }
    }

    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this, cooldown);
        removeBow();
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
        return "GrappleHook";
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
    public String getInstructions() {
        return "Left click to get a bow and an arrow. Make sure you have at least 2 free slots available. Shoot the bow where you want to go. You will cling there until you press shift to shoot yourself a bit.   ";
    }

    @Override
    public String getDescription() {
        return "Ultimate technomancers can make GrapplingHooks which can be used to cling onto walls and boost their agility.";
    }

    @Override
    public String getVersion() {
        return HackathonPack.hp.version();
    }

    @Override
    public boolean isEnabled() {
        return HackathonPack.hp.getConfig().getBoolean("Abilities.Techno.GrapplingHook.Enabled");
    }
}

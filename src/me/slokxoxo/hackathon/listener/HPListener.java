package me.slokxoxo.hackathon.listener;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.slokxoxo.hackathon.ability.ice.IceDiscs;
import me.slokxoxo.hackathon.ability.metal.MetalRun;
import me.slokxoxo.hackathon.ability.techno.EMPGrenade;
import me.slokxoxo.hackathon.ability.techno.GrapplingHook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class HPListener implements Listener {

    private final EMPGrenade empGrenade = (EMPGrenade) CoreAbility.getAbility(EMPGrenade.class);
    private final IceDiscs iceDiscs = (IceDiscs) CoreAbility.getAbility(IceDiscs.class);
    private final MetalRun metalRun = (MetalRun) CoreAbility.getAbility(MetalRun.class);

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null) return;

        String bound = bPlayer.getBoundAbilityName();

        if (bound.equalsIgnoreCase("MetalRun") && metalRun != null) {
            metalRun.onShift();
        }

        if (bPlayer.canBend(CoreAbility.getAbility(IceDiscs.class))) {
            if (player.isSneaking()) {
                return;
            }
            new IceDiscs(player, IceDiscs.DiscType.SOURCED);
        }
        if (player.getInventory().getItemInMainHand().getType() == Material.HEART_OF_THE_SEA) {
            if (empGrenade != null) {
                if (player.getInventory().getItemInMainHand().getItemMeta() != null) {
                    String snowballName = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
                    String strippedName = ChatColor.stripColor(snowballName);
                    if (strippedName.equalsIgnoreCase("EMP Grenade")) {
                        empGrenade.onShift();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);
            String bound = bendingPlayer.getBoundAbilityName();
            if (bound.equalsIgnoreCase("GrappleHook") && bendingPlayer.canBend(CoreAbility.getAbility(GrapplingHook.class))) {
                new GrapplingHook(player);
            }
            if (bound.equalsIgnoreCase("EMPGrenade") && bendingPlayer.canBend(CoreAbility.getAbility(EMPGrenade.class))) {
                new EMPGrenade(player);
            }
            if (bound.equalsIgnoreCase("IceDisc") && iceDiscs != null) {
                CoreAbility.getAbility(player, IceDiscs.class).onClick();
            }
            if (bendingPlayer.canBend(CoreAbility.getAbility(MetalRun.class))) {
                new MetalRun(player);
            }
        }
    }
}



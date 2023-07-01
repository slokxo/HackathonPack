package me.slokxoxo.hackathon.listener;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.slokxoxo.hackathon.ability.ice.IceGrip;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class test implements Listener {

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking()) return;

        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null) return;

        if (bPlayer.canBend(CoreAbility.getAbility(IceGrip.class))) {
            new IceGrip(player, IceGrip.GripType.SOURCING);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_AIR) {
            return;
        }

        Player player = event.getPlayer();
        IceGrip iceGrip = CoreAbility.getAbility(player, IceGrip.class);
        if (iceGrip != null) {
            iceGrip.onClick();
        }
    }
}

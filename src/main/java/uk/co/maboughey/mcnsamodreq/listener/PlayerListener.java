package uk.co.maboughey.mcnsamodreq.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.co.maboughey.mcnsamodreq.utils.Messaging;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler
    public void playerLogin(PlayerJoinEvent event) {
        //Get details
        Player player = event.getPlayer();

        if (player.hasPermission("modreq.mod")) {
            Messaging.modNotification(player);
        }
        if (player.hasPermission("modreq.player")) {
            Messaging.playerNotification(player);
        }
    }
}

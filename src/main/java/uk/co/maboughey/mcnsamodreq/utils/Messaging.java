package uk.co.maboughey.mcnsamodreq.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.maboughey.mcnsamodreq.database.DBModReq;

import java.util.UUID;

public class Messaging {

    public static void sendMessage(CommandSender src, String s) {
        src.sendMessage(Colour.colour(s));
    }
    public static void sendToMods(String message) {
        for (Player player: Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("modreq.mod")) {
                player.sendMessage(Colour.colour(message));
            }
        }
    }
    public static void sendToPlayer(UUID uuid, String message) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(Colour.colour(message));
        }
    }

    public static void playerNotification(Player player) {
        //Get counts
        int openCount = DBModReq.getCount(0, player.getUniqueId());
        int claimedCount = DBModReq.getCount(1, player.getUniqueId());
        int closedUnseen = DBModReq.getCount(2, player.getUniqueId());

        //Do we need to notify the player?
        if (openCount > 0 || claimedCount > 0 || closedUnseen > 0 ) {
            StringBuilder output = new StringBuilder();
            output.append("&6Your current mod requests: ");

            //Build the rest of the message
            if (openCount > 0)
                output.append("&F" + openCount + " &6Open, ");
            if (claimedCount > 0)
                output.append("&F" + claimedCount + " &6Processing, ");
            if (closedUnseen > 0)
                output.append("&F" + closedUnseen + " &6Unread replies, ");

            //Send the message to the player
            sendMessage(player, output.toString());
        }
    }

    public static void modNotification(Player player) {
        //get amount of open mod requests
        int openCount = DBModReq.getModCount(0, player);
        int claimedCount = DBModReq.getModCount(1, player);

        if (openCount > 0) {
            sendMessage(player, "&6There are currently &F"+openCount+" &6open mod requests. ");
        }

        if (claimedCount > 0) {
            //Mod has more than one claimed modreq
            sendMessage(player, "&6You have &F"+claimedCount+"&6 Mod Requests assigned to you. ");
        }
    }
}

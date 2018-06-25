package uk.co.maboughey.mcnsamodreq.type;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.maboughey.mcnsamodreq.utils.Configuration;
import uk.co.maboughey.mcnsamodreq.utils.Log;
import uk.co.maboughey.mcnsamodreq.utils.Messaging;
import uk.co.maboughey.mcnsanotes.database.DBuuid;

import java.sql.Date;
import java.util.UUID;

public class ModRequest {

    public int id;
    public Date date = new java.sql.Date((new java.util.Date()).getTime());
    public String server = Configuration.ServerName;

    public int status = 0;
    public UUID user = null;
    public UUID responder = null;
    public String message = null;
    public String response = null;
    public Location location = null;
    public Boolean escalated = false;

    public void teleport(Player player) {
        if (location == null) {
            Messaging.sendMessage(player, "&4Location is not set for this request");
            return;
        }
        if (server.equalsIgnoreCase(Configuration.ServerName)) {
            if (Bukkit.getWorld(location.getWorld().getUID()) == null) {
                Messaging.sendMessage(player, "&4Could not find world");
            }
            else {
                //Its from this server
                player.teleport(location);
                player.setGameMode(GameMode.SPECTATOR);
                Messaging.sendMessage(player, "&6Teleported to request: " + id);
            }
        }
        else {
            Messaging.sendMessage(player, "&4This request is from another server");
        }
    }

    public String getStatus() {
        String output = "";
        switch (this.status) {
            case 0: output = "&4Open"; break;
            case 1: output = "&4Claimed"; break;
            case 2: output = "&AUnread"; break;
            case 3: output = "&AClosed"; break;
        }
        return output;
    }

    public String getRequester() {
        try {
            return DBuuid.getUsername(user);
        }
        catch (IllegalArgumentException e) {
            return user.toString();
        }
    }
    public String getResponder() {
        try {
            return DBuuid.getUsername(responder);
        }
        catch (IllegalArgumentException e) {
            return responder.toString();
        }
    }

    public void setLocation(Double x, Double y, Double z, float pitch, float yaw, String world) {
        try {
            World bWorld = Bukkit.getWorld(UUID.fromString(world));
            Location newLocation = new Location(bWorld,x,y,z);
            newLocation.setYaw(yaw);
            newLocation.setPitch(pitch);

            location = newLocation;
        }
        catch (IllegalArgumentException e) {
            Log.warn("Error setting location of request id: "+id+". Malformed UUID from string");
        }

    }

    public void displayMod(CommandSender player) {
        String status = "";
        switch (this.status) {
            case(0): status = "Open"; break;
            case(1): status = "Claimed"; break;
            case(2): status = "Unread"; break;
            case(3): status = "Closed"; break;
        }

        Messaging.sendMessage(player, "&6ID: &F"+this.id+" &6Status: &F"+status);
        Messaging.sendMessage(player, "&6Date: &F"+this.date+" &6User: &F"+getRequester());
        Messaging.sendMessage(player, "&F"+this.message);
        if (this.status > 1)
            Messaging.sendMessage(player,"&6Responder: &F"+getResponder());
        if (this.status > 2)
            Messaging.sendMessage(player, "&6Response: &F"+this.response);
    }

    public void displayUser(CommandSender player) {
        String status = "";
        switch (this.status) {
            case(0): status = "&4Open"; break;
            case(1): status = "&4Claimed"; break;
            case(2): status = "&aUnread"; break;
            case(3): status = "&aClosed"; break;
        }

        Messaging.sendMessage(player, "&6---ID: &F"+this.id+" &6Status: &F"+status+" &6Date: &F"+this.date+"&6---");
        Messaging.sendMessage(player, "&F"+this.message);
        if (this.status > 0)
            Messaging.sendMessage(player,"&6Responder: &F"+getResponder());
        if (this.status > 1 && response != null)
            Messaging.sendMessage(player, "&6Response: &F"+this.response);
    }
}

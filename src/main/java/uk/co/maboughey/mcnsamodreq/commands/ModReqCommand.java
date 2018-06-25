package uk.co.maboughey.mcnsamodreq.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import uk.co.maboughey.mcnsamodreq.database.DBModReq;
import uk.co.maboughey.mcnsamodreq.type.ModRequest;
import uk.co.maboughey.mcnsamodreq.utils.Discord;
import uk.co.maboughey.mcnsamodreq.utils.Messaging;

import java.util.List;
import java.util.UUID;

public class ModReqCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //Filter out console and command blocks
        if (sender instanceof BlockCommandSender || sender instanceof ConsoleCommandSender) {
            Messaging.sendMessage(sender, "&4This command can only be ran as a player");
            return true;
        }

        if (args.length == 0) {
            return openRequests(sender);
        }
        else if (args.length == 1) {
            if (args[0].toLowerCase().contains("pending")) { return claimedRequests(sender); }
            if (args[0].toLowerCase().contains("unread")) { return closedRequests(sender); }
            if (args[0].toLowerCase().contains("closed")) { return oldRequests(sender); }
        }
        return newRequest(sender, args);
    }

    //List the player's currently opened requests
    private Boolean openRequests(CommandSender sender) {
        //Get the player's uuid
        UUID uuid = ((Player) sender).getUniqueId();

        //Get their requests
        List<ModRequest> requests = DBModReq.getUsersRequests(uuid, 0);

        if (requests.size() == 0) {
            Messaging.sendMessage(sender, "&4You have no open requests");
            return true;
        }

        for (ModRequest request: requests) {
            request.displayUser((Player) sender);
        }
        return true;
    }
    //View pending requests
    private Boolean claimedRequests(CommandSender sender) {
        //Get the player's uuid
        UUID uuid = ((Player) sender).getUniqueId();

        //Get their claimed requests
        List<ModRequest> requests = DBModReq.getUsersRequests(uuid, 1);

        if (requests.size() == 0) {
            Messaging.sendMessage(sender, "&4You have no pending requests");
            return true;
        }

        for (ModRequest request: requests) {
            request.displayUser((Player) sender);
        }


        return true;
    }
    //View Closed mod requests
    private Boolean closedRequests(CommandSender sender) {
        //Get the player's uuid
        UUID uuid = ((Player) sender).getUniqueId();

        //Get their requests
        List<ModRequest> requests = DBModReq.getUsersRequests(uuid, 2);

        if (requests.size() == 0) {
            Messaging.sendMessage(sender, "&4You have no recently closed requests");
            return true;
        }

        for (ModRequest request: requests) {
            request.displayUser((Player) sender);
            request.status = 3;
            DBModReq.updateRequestRead(request);
        }
        return true;
    }
    //View all old requests
    private Boolean oldRequests(CommandSender sender) {
        //Get the player's uuid
        UUID uuid = ((Player) sender).getUniqueId();

        //Get their requests
        List<ModRequest> requests = DBModReq.getUsersRequests(uuid, 3);

        if (requests.size() == 0) {
            Messaging.sendMessage(sender, "&4You have no unread and closed requests");
            return true;
        }

        for (ModRequest request: requests) {
            request.displayUser((Player) sender);
        }
        return true;
    }
    //New Mod request
    private Boolean newRequest(CommandSender sender, String[] args){

        UUID uuid = ((Player) sender).getUniqueId();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            if (i != args.length)
                sb.append(" ");
        }
        String message = sb.toString();

        //Setup the new request
        ModRequest request = new ModRequest();
        request.user = uuid;
        request.message = message;
        request.location = ((Player) sender).getLocation();

        //Save to database
        DBModReq.saveNewRequest(request);

        //Notify mods
        Messaging.sendToMods("&6A new Mod Request has been submitted");

        //Notify discord
        Discord.sendMod(request);

        //Notify player
        Messaging.sendMessage(sender, "&6Your request has been submitted");

        return true;
    }
}

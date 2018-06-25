package uk.co.maboughey.mcnsamodreq.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import uk.co.maboughey.mcnsamodreq.database.DBModReq;
import uk.co.maboughey.mcnsamodreq.type.ModRequest;
import uk.co.maboughey.mcnsamodreq.utils.Discord;
import uk.co.maboughey.mcnsamodreq.utils.Messaging;

import java.util.List;
import java.util.UUID;

public class ModReqModCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "list":
                    return list(sender, args);
                case "claim":
                    return claim(sender, args);
                case "claimed":
                    return claimed(sender, args);
                case "unclaim":
                    return unclaim(sender, args);
                case "close":
                    return close(sender, args);
                case "tp":
                    return tp(sender, args);
                case "escalate":
                    return escalate(sender, args);
                case "get":
                    return get(sender, args);
            }
        }
        else {
            return list(sender, args);
        }

        return true;
    }
    //List all open requests
    private Boolean list(CommandSender sender, String[] args) {
        if (!permissionCheck(sender)) { return true; }

        UUID uuid = null;

        if (sender instanceof Player)
            uuid = ((Player)sender).getUniqueId();

        //List the currently open mod requests
        List<ModRequest> requests = DBModReq.getRequests(0, uuid);

        if (requests.size() == 0) {
            Messaging.sendMessage(sender, "&aThere are no mod requests to view");
            return true;
        }

        for (ModRequest request: requests) {
            request.displayMod(sender);
        }
        
        return true;
    }
    //Claim a request
    private Boolean claim(CommandSender sender, String[] args) {
        if (!permissionCheck(sender)) { return true; }

        //Filter out console and command blocks
        if (sender instanceof BlockCommandSender || sender instanceof ConsoleCommandSender) {
            Messaging.sendMessage(sender, "&4This command can only be ran as a player");
            return true;
        }

        //Get the id
        if (args.length > 1 && !args[1].matches("\\d+")) {
            Messaging.sendMessage(sender, "&4Please specify an Mod Request ID: /modreqmod claim [id]");
            return true;
        }
        int id = Integer.parseInt(args[1]);

        //get uuid of player
        UUID uuid = ((Player) sender).getUniqueId();

        //Try and get the request
        ModRequest request = DBModReq.getRequest(id);

        //Do we have a request?
        if (request == null) {
            Messaging.sendMessage(sender, "&Could not find that request");
            return true;
        }

        //Has it been claimed? Check for admin status too (They can override the claims)
        if (request.status != 0 && !sender.hasPermission("modreq.admin")) {
            Messaging.sendMessage(sender, "&That request cannot be claimed");
            return true;
        }

        //Has it been escalated?
        if (request.escalated && !sender.hasPermission("modreq.admin")) {
            Messaging.sendMessage(sender, "&4That request has been escalated to the admins");
            return true;
        }

        //Now we can claim it
        request.responder = uuid;
        request.status = 1;

        //Save our changes
        DBModReq.updateRequestClaimed(request);

        //Tell the user
        Messaging.sendMessage(sender, "&6You have claimed request "+request.id);

        //Tell the player who sent the request
        Messaging.sendToPlayer(request.user, "&6Your request has been claimed by "+((Player) sender).getDisplayName());

        return true;
    }
    //view your claimed requests
    private Boolean claimed(CommandSender sender, String[] args) {
        if (!permissionCheck(sender)) { return true; }

        //Filter out console and command blocks
        if (sender instanceof BlockCommandSender || sender instanceof ConsoleCommandSender) {
            Messaging.sendMessage(sender, "&4This command can only be ran as a player");
            return true;
        }

        //get uuid of player
        UUID uuid = ((Player) sender).getUniqueId();

        //Get requests
        List<ModRequest> requests = DBModReq.getRequests(1, uuid);

        //Check if we have claimed requests
        if (requests.size() == 0) {
            Messaging.sendMessage(sender, "&6You have no claimed mod requests");
            return true;
        }

        //Display the requests
        for (ModRequest request: requests) {
            request.displayMod(sender);
        }

        return true;
    }
    //Unclaim a request
    private Boolean unclaim(CommandSender sender, String[] args) {
        if (!permissionCheck(sender)) { return true; }

        //Filter out console and command blocks
        if (sender instanceof BlockCommandSender || sender instanceof ConsoleCommandSender) {
            Messaging.sendMessage(sender, "&4This command can only be ran as a player");
            return true;
        }

        //Get the id
        if (args.length > 1 && !args[1].matches("\\d+")) {
            Messaging.sendMessage(sender, "&4Please specify an Mod Request ID: /modreqmod claim [id]");
            return true;
        }
        int id = Integer.parseInt(args[1]);

        //get uuid of player
        UUID uuid = ((Player) sender).getUniqueId();

        //Try and get the request
        ModRequest request = DBModReq.getRequest(id);

        //Do we have a request?
        if (request == null) {
            Messaging.sendMessage(sender, "&Could not find that request");
            return true;
        }

        //Has it been claimed?
        if (request.status != 1) {
            Messaging.sendMessage(sender, "&4That request has either not been claimed, or is closed");
            return true;
        }

        //Has the player claimed the request? Admin override check also
        if (request.responder == uuid && !sender.hasPermission("mcnsamod.admin")) {
            Messaging.sendMessage(sender, "&4You have not claimed this request");
            return true;
        }

        //Set request back to open
        request.status = 0;
        request.responder = null;

        //Save request
        DBModReq.updateRequestClaimed(request);

        //Tell the user
        Messaging.sendMessage(sender, "&6You have unclaimed request "+request.id);

        return true;
    }
    //Close a request
    private Boolean close(CommandSender sender, String[] args) {
        if (!permissionCheck(sender)) { return true; }

        //Filter out console and command blocks
        if (sender instanceof BlockCommandSender || sender instanceof ConsoleCommandSender) {
            Messaging.sendMessage(sender, "&4This command can only be ran as a player");
            return true;
        }

        //get uuid of player
        UUID uuid = ((Player) sender).getUniqueId();

        //Get the id of the request
        if (args.length < 2 || !args[1].matches("\\d+")) {
            Messaging.sendMessage(sender, "&4Please specify an Mod Request ID: /modreqmod close [id] [message]");
            return true;
        }
        int id = Integer.parseInt(args[1]);

        //Get the message string from arguments if present
        String response = null;
        if (args.length > 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                sb.append(args[i]);
                if (i != args.length)
                    sb.append(" ");
            }
            response = sb.toString();
        }

        //Try and get the request
        ModRequest request = DBModReq.getRequest(id);

        //Do we have a request?
        if (request == null) {
            Messaging.sendMessage(sender, "&Could not find that request");
            return true;
        }

        //Check if already closed
        if (request.status > 1) {
            Messaging.sendMessage(sender, "&4That request has already been closed");
            return true;
        }

        //Check if escalated
        if (request.escalated && !sender.hasPermission("modreq.admin")) {
            Messaging.sendMessage(sender, "&4This request has been escalated. You cannot close it");
            return true;
        }

        //Complete the request
        request.responder = uuid;
        request.response = response;
        request.status = 2;

        //Save request
        DBModReq.updateRequestDone(request);

        //Tell the sender
        Messaging.sendMessage(sender, "&6You have closed request: "+request.id);

        //Tell the user
        Messaging.sendToPlayer(request.user, "&6Your Mod Request has been closed. \"/modreq closed\" to view");

        //Notify discord
        Discord.closedRequest(request);

        return true;
    }
    //Teleport to a request location
    private Boolean tp(CommandSender sender, String[] args) {
        if (!permissionCheck(sender)) { return true; }

        //Filter out console and command blocks
        if (sender instanceof BlockCommandSender || sender instanceof ConsoleCommandSender) {
            Messaging.sendMessage(sender, "&4This command can only be ran as a player");
            return true;
        }

        //get uuid of player
        UUID uuid = ((Player) sender).getUniqueId();

        //Get the id of the request
        if (args.length < 2 || !args[1].matches("\\d+")) {
            Messaging.sendMessage(sender, "&4Please specify an Mod Request ID: /modreqmod tp [id]");
            return true;
        }
        int id = Integer.parseInt(args[1]);

        //Try and get the request
        ModRequest request = DBModReq.getRequest(id);

        //Do we have a request?
        if (request == null) {
            Messaging.sendMessage(sender, "&Could not find that request");
            return true;
        }

        //try teleporting the player
        request.teleport((Player) sender);

        return true;
    }
    //Escalate a request to the admins
    private Boolean escalate(CommandSender sender, String[] args) {
        if (!permissionCheck(sender)) { return true; }

        //Filter out console and command blocks
        if (sender instanceof BlockCommandSender || sender instanceof ConsoleCommandSender) {
            Messaging.sendMessage(sender, "&4This command can only be ran as a player");
            return true;
        }

        //Get the id
        if (args.length > 1 && !args[1].matches("\\d+")) {
            Messaging.sendMessage(sender, "&4Please specify an Mod Request ID: /modreqmod escalate [id]");
            return true;
        }
        int id = Integer.parseInt(args[1]);

        //get uuid of player
        UUID uuid = ((Player) sender).getUniqueId();

        //Try and get the request
        ModRequest request = DBModReq.getRequest(id);

        //Do we have a request?
        if (request == null) {
            Messaging.sendMessage(sender, "&Could not find that request");
            return true;
        }

        //Has it already been escalated?
        if (request.escalated) {
            Messaging.sendMessage(sender, "&That request has already been escalated");
            return true;
        }

        //Escalate the request
        request.escalated = true;

        //save the request
        DBModReq.updateRequestEscalation(request);

        //Notify discord
        Discord.sendAdmin(request, (Player) sender);

        //Notify the requester
        Messaging.sendToPlayer(request.user, "&6Your request has been escalated to the Admins");

        //Notify the player
        Messaging.sendMessage(sender, "&6Escalated the request");

        return true;
    }
    //View a single request
    private Boolean get(CommandSender sender, String[] args) {
        if (!permissionCheck(sender)) { return true; }

        //Filter out console and command blocks
        if (sender instanceof BlockCommandSender || sender instanceof ConsoleCommandSender) {
            Messaging.sendMessage(sender, "&4This command can only be ran as a player");
            return true;
        }

        //Get the id
        if (args.length > 1 && !args[1].matches("\\d+")) {
            Messaging.sendMessage(sender, "&4Please specify an Mod Request ID: /modreqmod get [id]");
            return true;
        }
        int id = Integer.parseInt(args[1]);

        //get uuid of player
        UUID uuid = ((Player) sender).getUniqueId();

        //Try and get the request
        ModRequest request = DBModReq.getRequest(id);

        //Do we have a request?
        if (request == null) {
            Messaging.sendMessage(sender, "&Could not find that request");
            return true;
        }

        //display to sender
        request.displayMod(sender);

        return true;
    }

    private Boolean permissionCheck(CommandSender sender) {
        if (!sender.hasPermission("modreq.mod")) {
            Messaging.sendMessage(sender, "&4You do not have the permissions for that");
            return false;
        }
        return true;
    }
}

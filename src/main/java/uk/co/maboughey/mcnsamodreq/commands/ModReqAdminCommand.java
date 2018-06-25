package uk.co.maboughey.mcnsamodreq.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.maboughey.mcnsamodreq.database.DBModReq;
import uk.co.maboughey.mcnsamodreq.type.ModRequest;
import uk.co.maboughey.mcnsamodreq.utils.Messaging;

import java.util.List;
import java.util.UUID;

public class ModReqAdminCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("modreq.mod")) {
            Messaging.sendMessage(sender, "&4You do not have the permissions for that");
            return true;
        }

        //Get the mod requests
        List<ModRequest> requests = DBModReq.getAdminRequests();

        //get uuid of player
        UUID uuid = ((Player) sender).getUniqueId();

        if (requests.size() == 0) {
            Messaging.sendMessage(sender, "&aThere are no escalated mod requests to view");
            return true;
        }

        for (ModRequest request: requests) {
            request.displayMod(sender);
        }
        return true;
    }
}

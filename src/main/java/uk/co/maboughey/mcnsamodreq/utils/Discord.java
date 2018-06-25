package uk.co.maboughey.mcnsamodreq.utils;

import fr.d0p1.hookscord.Hookscord;
import fr.d0p1.hookscord.utils.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.maboughey.mcnsamodreq.type.ModRequest;

import java.io.IOException;
import java.net.MalformedURLException;

public class Discord {

    public static void sendMod(ModRequest request) {
        if (Configuration.Discord) {
            try {
                Hookscord hk = new Hookscord(Configuration.DiscordModHook);
                Message msg = new Message("New Mod Request");
                msg.setText(
                        "**"+request.getRequester()+"** has submitted a new mod request on **"+Configuration.ServerName+"**\n" +
                                "Message: *"+request.message+"*");
                hk.sendMessage(msg);

            } catch (MalformedURLException e) {
                Log.error("Error sending discord message to Mods: "+e.getMessage());
            } catch (IOException e) {
                Log.error("Error sending discord message to Mods: "+e.getMessage());
            }
        }
    }

    public static void sendAdmin(ModRequest request, Player sender) {
        if (Configuration.Discord) {
            try {
                Hookscord hk = new Hookscord(Configuration.DiscordAdminHook);
                Message msg = new Message("Escalated Mod Request");
                msg.setText(
                        "**"+sender.getName()+"** wants an admin to look at mod request on **"+Configuration.ServerName+"**\n" +
                        "id: "+request.id+"\n" +
                        "Message: *"+request.message+"*\n");
                hk.sendMessage(msg);

            } catch (MalformedURLException e) {
                Log.error("Error sending discord message to Admins: "+e.getMessage());
            } catch (IOException e) {
                Log.error("Error sending discord message to Admins: "+e.getMessage());
            }
        }
    }

    public static void closedRequest(ModRequest request) {
        if (Configuration.Discord) {
            try {
                Hookscord hk = new Hookscord(Configuration.DiscordModHook);
                Message msg = new Message("Closed Mod Request");
                msg.setText("**"+request.getResponder()+"** has closed mod request on **"+Configuration.ServerName+"**\n" +
                        "Id: *"+request.id+"*\n"+
                        "Player: "+request.getRequester()+
                        "\nRequest text: *"+request.message+"*\n" +
                        "Comment: *"+request.response+"*");
                hk.sendMessage(msg);

            } catch (MalformedURLException e) {
                Log.error("Error sending discord message to Mods: "+e.getMessage());
            } catch (IOException e) {
                Log.error("Error sending discord message to Mods: "+e.getMessage());
            }
        }
    }
}

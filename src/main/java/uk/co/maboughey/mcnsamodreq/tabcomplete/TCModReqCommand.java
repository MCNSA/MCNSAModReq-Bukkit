package uk.co.maboughey.mcnsamodreq.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TCModReqCommand implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> output = new ArrayList<String>();
        List<String> subcommands = new ArrayList<String>();

        subcommands.add("pending");
        subcommands.add("unread");
        subcommands.add("closed");

        if (args.length == 0) {
            output = subcommands;
        }
        else if (args.length == 1) {
            for (String string: subcommands) {
                if (string.startsWith(args[0])) {
                    output.add(string);
                }
            }
        }
        return output;
    }
}

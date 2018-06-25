package uk.co.maboughey.mcnsamodreq;

import org.bukkit.plugin.java.JavaPlugin;
import uk.co.maboughey.mcnsamodreq.commands.ModReqAdminCommand;
import uk.co.maboughey.mcnsamodreq.commands.ModReqCommand;
import uk.co.maboughey.mcnsamodreq.commands.ModReqModCommand;
import uk.co.maboughey.mcnsamodreq.database.DatabaseManager;
import uk.co.maboughey.mcnsamodreq.listener.PlayerListener;
import uk.co.maboughey.mcnsamodreq.tabcomplete.TCModReqCommand;
import uk.co.maboughey.mcnsamodreq.tabcomplete.TCModReqModCommand;
import uk.co.maboughey.mcnsamodreq.utils.Configuration;
import uk.co.maboughey.mcnsamodreq.utils.Log;

public class ModReq extends JavaPlugin {

    private String name = "MCNSA ModReq";
    private String version = "v1.0";

    public static Configuration config;
    public DatabaseManager DBManager;

    @Override
    public void onEnable() {
        Log.info("Loading "+name+" "+version);

        saveDefaultConfig();
        config = new Configuration(getConfig(), this);
        config.load();

        Log.info("Loaded Configuration");

        DBManager = new DatabaseManager();

        this.getCommand("modreq").setExecutor(new ModReqCommand());
        this.getCommand("modreqmod").setExecutor(new ModReqModCommand());
        this.getCommand("modreqadmin").setExecutor(new ModReqAdminCommand());

        this.getCommand("modreq").setTabCompleter(new TCModReqCommand());
        this.getCommand("modreqmod").setTabCompleter(new TCModReqModCommand());

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {

    }
}

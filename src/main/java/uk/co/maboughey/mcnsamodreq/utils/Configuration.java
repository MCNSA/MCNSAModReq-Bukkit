package uk.co.maboughey.mcnsamodreq.utils;

import org.bukkit.configuration.file.FileConfiguration;
import uk.co.maboughey.mcnsamodreq.ModReq;

public class Configuration {
    public static String DBHost;
    public static String DBName;
    public static String DBUser;
    public static String DBPassword;
    public static String ServerName;

    public static Boolean Discord;
    public static String DiscordModHook;
    public static String DiscordAdminHook;


    private static ModReq plugin;
    FileConfiguration config = null;

    public Configuration(FileConfiguration config, ModReq modReq) {
        this.plugin = modReq;
        this.config = config;
    }

    public void load() {
        DBHost = config.getString("database-host");
        DBName = config.getString("database-name");
        DBUser = config.getString("database-user");
        DBPassword = config.getString("database-password");
        ServerName = config.getString("server-name");
        Discord = config.getBoolean("DiscordEnabled");

        if (Discord) {
            DiscordModHook = "https://discordapp.com/api/webhooks/"+config.getString("DiscordModHook");
            DiscordAdminHook = "https://discordapp.com/api/webhooks/"+config.getString("DiscordAdminHook");
        }
    }

    public static String getDatabaseString() {
        return "jdbc:mysql://"+DBHost+"/"+DBName;
    }
    public static void reload() {
        plugin.reloadConfig();
        ModReq.config.load();
    }
    public static void currentConfig() {
        Log.info("Current settings: DBH:"+DBHost+", DBN:"+DBName+", DBU:"+DBUser+", DBP:"+DBPassword);
    }
}

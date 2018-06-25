package uk.co.maboughey.mcnsamodreq.utils;

import org.bukkit.Bukkit;

public class Log {

    public Log() {
    }
    public static void info(String message){
        Bukkit.getConsoleSender().sendMessage(Colour.colour("&A[MCNSA ModReq] "+message));
    }
    public static void warn(String message){
        Bukkit.getConsoleSender().sendMessage(Colour.colour("&5[MCNSA ModReq] "+message));
    }
    public static void error(String message){
        Bukkit.getConsoleSender().sendMessage(Colour.colour("&C[MCNSA ModReq] "+message));
    }
}
package com.seb;


import com.seb.Login.LoginStatus;
import org.json.JSONObject;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;

public class Main {

    public static JSONObject sessionUserTimer = new JSONObject();
    public static HashMap<String, Process> serverPidMap = new HashMap<>();

    public static void main(String[] args) throws IOException, SQLException {
        /*TODO: Add console, delete server, delete world (for resets), world download
            Plugin upload + config settings (force port on value tho)
            Donation link, support for forge + maybe spigot?
            support tickets, maybe with discord bot?
            better error handling, ui, testing (alpha tester?)
         */
        new Webserver();
        Mysql.createMysql();
    }

    public static boolean isLoggedIn(String sessionId) {
        return sessionId != null && Main.sessionUserTimer.has(sessionId) && Main.sessionUserTimer.getJSONObject(sessionId).has("timestamp") && Main.sessionUserTimer.getJSONObject(sessionId).getLong("timestamp") >= System.currentTimeMillis() && Main.sessionUserTimer.getJSONObject(sessionId).get("loginstatus").equals(LoginStatus.SUCCESS);
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
}
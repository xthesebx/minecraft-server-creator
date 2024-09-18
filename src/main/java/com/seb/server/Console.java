package com.seb.server;

import com.hawolt.logger.Logger;
import com.seb.Main;
import com.seb.Mysql;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseStatus;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Console {

    private final HashMap<String, WsConnectContext> wsConnectContext = new HashMap<>();
    private final BufferedReader reader;
    private final String serverId, serverOwner, serverName;

    public Console(String serverId) throws IOException, SQLException {
        this.serverId = serverId;
        this.serverOwner = Mysql.getServerOwner(serverId);
        this.serverName = Mysql.getServerNameFromId(serverId);
        reader = new BufferedReader(new InputStreamReader(Main.serverObject.get(serverId).getInputStream()));
        new Thread (() -> {
            try {
                send();
            } catch (IOException e) {
                Logger.error(e);
            }
        }).start();
    }

    public void addContext(WsConnectContext connectContext) throws InterruptedException, SQLException, IOException {
        if (!Main.isLoggedIn(connectContext.cookie("JSESSIONID"))) {
            connectContext.closeSession(WsCloseStatus.POLICY_VIOLATION, "Not logged in");
            return;
        } else Main.sessionUserTimer.getJSONObject(connectContext.cookie("JSESSIONID")).put("timestamp", System.currentTimeMillis() + 300000);
        if (!serverOwner.equals(getUser(connectContext))) {
            connectContext.closeSession(WsCloseStatus.POLICY_VIOLATION, "Not the Owner!");
            return;
        }
        if (!Main.serverObject.containsKey(serverId)) {
            connectContext.send("Start server First");
            Thread.sleep(1000);
            connectContext.closeSession(WsCloseStatus.POLICY_VIOLATION, "Start the server First!");
            return;
        }
        connectContext.enableAutomaticPings(5, TimeUnit.SECONDS);
        wsConnectContext.put(connectContext.sessionId(), connectContext);
        sendLog(connectContext);
    }

    private void sendLog(WsConnectContext connectContext) throws IOException {
        File log = new File(serverOwner + "/" + serverName + "/logs/latest.log");
        if (log.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(log));
                String temp;
                while ((temp = reader.readLine()) != null) {
                    connectContext.send(temp);
                }
                reader.close();
            } catch (IOException e) {
                Logger.error(e);
            }
        }
    }

    protected String getUser(WsConnectContext connectContext) {
        return Main.sessionUserTimer.getJSONObject(connectContext.cookie("JSESSIONID")).getString("user");
    }

    private void send() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String finalLine = line;
            if (wsConnectContext.isEmpty()) continue;
            wsConnectContext.values().forEach(context -> context.send(finalLine));
        }
    }

    public void removeContext (WsCloseContext connectContext) {
        wsConnectContext.remove(connectContext.sessionId());
    }

    public void read(WsMessageContext ctx) {
        OutputStream os = Main.serverObject.get(serverId).getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
        try {
            writer.write(ctx.message());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}

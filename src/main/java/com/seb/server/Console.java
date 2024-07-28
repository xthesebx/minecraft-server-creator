package com.seb.server;

import com.hawolt.logger.Logger;
import com.seb.Main;
import com.seb.Mysql;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;
import io.javalin.websocket.WsCloseStatus;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import org.apache.commons.logging.Log;
import org.eclipse.jetty.websocket.api.CloseStatus;

import java.io.*;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Console {
    WsConnectContext ctx;
    BufferedReader reader;
    String serverId;
    public Console(WsConnectContext ctx) throws SQLException, IOException, InterruptedException {
        this.ctx = ctx;
        if (!Main.isLoggedIn(ctx.cookie("JSESSIONID"))) {
            Logger.error("close1");
            ctx.closeSession(WsCloseStatus.NO_STATUS_RECEIVED, "Not logged in");
            return;
        } else Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).put("timestamp", System.currentTimeMillis() + 300000);
        serverId = ctx.pathParam("id");
        if (!Mysql.getServerOwner(serverId).equals(getUser())) {
            Logger.error("close2");
            ctx.closeSession(WsCloseStatus.NO_STATUS_RECEIVED, "Not the Owner!");
            return;
        }
        if (!Main.serverObject.containsKey(serverId)) {
            Logger.error("close3");
            ctx.send("Start server First");
            Thread.sleep(1000);
            ctx.closeSession(WsCloseStatus.NO_STATUS_RECEIVED, "Start the server First!");
            return;
        }
        ctx.enableAutomaticPings(5, TimeUnit.SECONDS);

        reader = new BufferedReader(new InputStreamReader(Main.serverObject.get(serverId).getInputStream()));
        new Thread (() -> {
            try {
                send();
            } catch (IOException e) {
                Logger.error(e);
            }
        }).start();
        Logger.error("done?");
    }

    protected String getUser() {
        return Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).getString("user");
    }

    private void send() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            ctx.send(line);
        }
    }

    public void read(WsMessageContext ctx) throws IOException {
        OutputStream os = Main.serverObject.get(serverId).getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
        writer.write(ctx.message());
        writer.newLine();
        writer.flush();
    }
}

package com.seb;

import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;
import io.javalin.util.FileUtil;

import java.sql.SQLException;

public class ServerView extends JavalinAuthPage {
    public ServerView(Context ctx) throws SQLException {
        super(ctx);
        if (cancel) return;
        String html = FileUtil.readFile("html/serverview.html");
        html = html.replaceAll("SERVERID", ctx.pathParam("id"));
        html = html.replaceAll("STATUS", Main.serverPidMap.containsKey(ctx.pathParam("id")) ? "stop" : "start");
        ctx.html(html);
    }
}

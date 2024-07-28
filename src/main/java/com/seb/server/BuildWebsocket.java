package com.seb.server;

import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class BuildWebsocket extends JavalinAuthPage {
    public BuildWebsocket(Context ctx) throws SQLException, IOException {
        super(ctx);
        String html = Files.readString(Path.of("html/chat.html"));
        html = html.replace("SERVERID", ctx.pathParam("id"));
        ctx.html(html);
    }
}

package com.seb.startstop;

import com.seb.Main;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;

import java.io.IOException;
import java.sql.SQLException;

public class Stop extends JavalinAuthPage {
    //TODO: Test in Linux
    public Stop(Context ctx) throws IOException, SQLException {
        super(ctx);
        if (cancel) return;
        String id = ctx.pathParam("id");
        Process p = Main.serverPidMap.get(id);
        p.descendants().forEach(child -> {
            child.destroy();
        });
        Main.serverPidMap.remove(id);
        ctx.redirect("/serverview/" + id);
    }
}

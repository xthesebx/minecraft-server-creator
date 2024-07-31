package com.seb.abs;

import com.seb.Mysql;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;

import java.sql.SQLException;

public class JavalinAuthPage extends JavalinLoggedInPage {
    public JavalinAuthPage(Context ctx) throws SQLException {
        super(ctx);
        if (cancel) return;
        String serverId = ctx.pathParam("id");
        if (!Mysql.getServerOwner(serverId).equals(getUser())) {
            cancel = true;
            ctx.status(403);
        }
    }
}

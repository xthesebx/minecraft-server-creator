package com.seb.abs;

import io.javalin.http.Context;

public class JavalinAdminPage extends JavalinLoggedInPage {
    protected boolean cancel;
    public JavalinAdminPage(Context ctx) {
        super(ctx);
        if (cancel) return;
        if (!this.getUser().equals("stdbasti")) {
            ctx.redirect("/");
            cancel = true;
        }
    }
}

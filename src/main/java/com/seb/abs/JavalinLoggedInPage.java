package com.seb.abs;

import com.seb.Main;
import io.javalin.http.Context;

public class JavalinLoggedInPage extends JavalinPage {
    protected boolean cancel = false;
    public JavalinLoggedInPage(Context ctx) {
        super(ctx);
        if (!Main.isLoggedIn(ctx.cookie("JSESSIONID"))) {
            ctx.redirect("/login");
            cancel = true;
        } else Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).put("timestamp", System.currentTimeMillis() + 300000);
    }

    protected String getUser() {
        return Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).getString("user");
    }
}

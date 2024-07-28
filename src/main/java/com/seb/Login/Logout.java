package com.seb.Login;

import com.seb.Main;
import com.seb.abs.JavalinLoggedInPage;
import io.javalin.http.Context;

public class Logout extends JavalinLoggedInPage {
    public Logout(Context ctx) {
        super(ctx);
        if (cancel) return;
        Main.sessionUserTimer.remove(ctx.cookie("JSESSIONID"));
        ctx.redirect("/login");
    }
}

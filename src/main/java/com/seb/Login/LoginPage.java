package com.seb.Login;

import com.seb.abs.JavalinPage;
import com.seb.Main;
import io.javalin.http.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoginPage extends JavalinPage {

    public LoginPage(Context ctx) throws IOException {
        super(ctx);
        ctx.sessionAttributeMap();
        String page = Files.readString(Path.of("html/login.html"));
        LoginStatus ls = LoginStatus.NONE;
        if (ctx.cookie("JSESSIONID") != null) {
            if (Main.sessionUserTimer.has(ctx.cookie("JSESSIONID")))
                if (Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).has("loginstatus")) {
                    if (Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).has("timestamp"))
                        if (Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).getLong("timestamp") < System.currentTimeMillis())
                            Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONId")).put("loginstatus", LoginStatus.TIMED_OUT);
                    ls = (LoginStatus) Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).get("loginstatus");
                    Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).put("loginstatus", LoginStatus.NONE);
                }
        }
        switch (ls) {
            case WRONG_DATA -> page = page.replace("$ERROR", "Wrong Login Information, please try again");
            case SUCCESS -> ctx.redirect("/");
            case NONE -> page = page.replace("$ERROR", "");
            case TIMED_OUT -> page = page.replace("$ERROR", "Login timed out, please login again");
            case NO_CONNECTION -> page = page.replace("$ERROR", "Server Error, please try again later");
        }
        ctx.html(page);
    }
}

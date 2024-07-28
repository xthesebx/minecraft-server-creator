package com.seb;

import com.seb.Create.CreatePage;
import com.seb.Create.CreateServer;
import com.seb.Login.Login;
import com.seb.Login.LoginPage;
import com.seb.Login.Logout;
import com.seb.Registration.Register;
import com.seb.Registration.RegisterPage;
import com.seb.admin.AdminOverview;
import com.seb.admin.CreateCodes;
import com.seb.admin.Kill;
import com.seb.basicSite.Overview;
import com.seb.edit.EditServerPage;
import com.seb.edit.EditVersion;
import com.seb.startstop.Start;
import com.seb.startstop.Stop;
import io.javalin.Javalin;

import java.io.IOException;

public class Webserver {
    public Webserver() throws IOException {
        Javalin javalin = Javalin.create().start(8080);

        javalin.post("/createserver", ctx -> new CreateServer(ctx));
        javalin.post("/loginpost", ctx -> new Login(ctx));
        javalin.post("/editversion/<id>", ctx -> new EditVersion(ctx));
        javalin.post("/start/<id>", ctx -> new Start(ctx));
        javalin.post("/stop/<id>", ctx -> new Stop(ctx));
        javalin.post("/kill", ctx -> new Kill(ctx));
        javalin.post("/createcodes", ctx -> new CreateCodes(ctx));
        javalin.post("/registerpost", ctx -> new Register(ctx));
        javalin.get("/logout", ctx -> new Logout(ctx));

        javalin.get("/createpage", ctx -> new CreatePage(ctx));
        javalin.get("/login", ctx -> new LoginPage(ctx));
        javalin.get("/", ctx -> new Overview(ctx));
        javalin.get("/serverview/<id>", ctx -> new ServerView(ctx));
        javalin.get("/edit/<id>", ctx -> new EditServerPage(ctx));
        javalin.get("/admin", ctx -> new AdminOverview(ctx));
        javalin.get("/register", ctx -> new RegisterPage(ctx));
    }
}

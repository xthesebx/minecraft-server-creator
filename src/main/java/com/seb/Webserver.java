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


public class Webserver {
    public Webserver() {
        Javalin javalin = Javalin.create().start(8080);

        javalin.post("/createserver", CreateServer::new);
        javalin.post("/loginpost", Login::new);
        javalin.post("/editversion/<id>", EditVersion::new);
        javalin.post("/start/<id>", Start::new);
        javalin.post("/stop/<id>", Stop::new);
        javalin.post("/kill", Kill::new);
        javalin.post("/createcodes", CreateCodes::new);
        javalin.post("/registerpost", Register::new);
        javalin.get("/logout", Logout::new);

        javalin.get("/createpage", CreatePage::new);
        javalin.get("/login", LoginPage::new);
        javalin.get("/", Overview::new);
        javalin.get("/serverview/<id>", ServerView::new);
        javalin.get("/edit/<id>", EditServerPage::new);
        javalin.get("/admin", AdminOverview::new);
        javalin.get("/register", RegisterPage::new);
    }
}

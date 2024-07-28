package com.seb;

import com.hawolt.logger.Logger;
import com.seb.server.BuildWebsocket;
import com.seb.server.Console;
import com.seb.server.Create.CreatePage;
import com.seb.server.Create.CreateServer;
import com.seb.Login.Login;
import com.seb.Login.LoginPage;
import com.seb.Login.Logout;
import com.seb.Registration.Register;
import com.seb.Registration.RegisterPage;
import com.seb.admin.AdminOverview;
import com.seb.admin.CreateCodes;
import com.seb.admin.Kill;
import com.seb.basicSite.Overview;
import com.seb.server.ServerView;
import com.seb.server.edit.EditServerPage;
import com.seb.server.edit.EditVersion;
import com.seb.startstop.Start;
import com.seb.startstop.Stop;
import io.javalin.Javalin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;


public class Webserver {
    Console console;
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
        javalin.get("/console/<id>", BuildWebsocket::new);
        javalin.get("/websocket.js", ctx -> ctx.result(Files.readString(Path.of("html/websocket.js"))));

        javalin.ws("/ws/console/<id>", ws -> {
            ws.onConnect(ctx -> console = new Console(ctx));
            ws.onMessage(ctx -> console.read(ctx));
        });
    }
}

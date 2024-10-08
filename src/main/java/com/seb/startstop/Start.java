package com.seb.startstop;

import com.hawolt.logger.Logger;
import com.seb.Main;
import com.seb.Mysql;
import com.seb.Webserver;
import com.seb.abs.JavalinAuthPage;
import com.seb.server.Console;
import io.javalin.http.Context;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Start extends JavalinAuthPage {
    //TODO: Test in Linux
    public Start(Context ctx) throws SQLException, IOException, InterruptedException {
        super(ctx);
        AtomicBoolean wait = new AtomicBoolean(false);
        if (cancel) return;
        String id = ctx.pathParam("id");
        String name = Mysql.getServerNameFromId(id);
        File f = new File(getUser() + "/" + name);
        Thread t = new Thread(() -> {
            Process process = null;
            if (Main.isWindows()) {
                try {
                    process = new ProcessBuilder(f.getAbsolutePath() + "/start.bat").directory(f).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    process = new ProcessBuilder(f.getAbsolutePath() + "/start.sh").directory(f).start();
                } catch (IOException e) {
                    Logger.error(e);
                }
            }
            Main.serverObject.put(id, process);
            try {
                Webserver.consoles.put(id, new Console(id));
            } catch (IOException | SQLException e) {
                Logger.error(e);
            }
            wait.set(true);
        });
        t.start();
        do {
            Thread.sleep(10);
        } while (!wait.get());
        ctx.redirect("/serverview/" + id);
    }
}

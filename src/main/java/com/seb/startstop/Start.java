package com.seb.startstop;

import com.hawolt.logger.Logger;
import com.seb.Main;
import com.seb.Mysql;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
        Logger.error(name);
        File f = new File(getUser() + "/" + name);
        Thread t = new Thread(() -> {
            Process process;
            if (Main.isWindows()) {
                try {
                    process = new ProcessBuilder("c:\\Windows\\System32\\cmd.exe", "/c start " + f.getAbsolutePath() + "\\start.bat").directory(f).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    process = new ProcessBuilder("/bin/sh ", name + "/start.sh").directory(f).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Main.serverPidMap.put(id, process);
            wait.set(true);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    Logger.debug(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        do {
            Thread.sleep(10);
        } while (!wait.get());
        ctx.redirect("/serverview/" + id);
    }
}

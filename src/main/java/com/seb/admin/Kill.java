package com.seb.admin;

import com.seb.Main;
import com.seb.abs.JavalinAdminPage;
import io.javalin.http.Context;

//TODO: create admin page and make this admin page
public class Kill extends JavalinAdminPage {
    public Kill(Context ctx) {
        super(ctx);
        if (cancel) return;
        Main.serverPidMap.values().forEach(pid -> {
            pid.descendants().forEach(child -> {
                child.destroy();
            });
        });
        System.exit(0);
    }
}

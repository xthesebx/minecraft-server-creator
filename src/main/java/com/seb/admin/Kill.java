package com.seb.admin;

import com.seb.Main;
import com.seb.abs.JavalinAdminPage;
import io.javalin.http.Context;

public class Kill extends JavalinAdminPage {
    public Kill(Context ctx) {
        super(ctx);
        if (cancel) return;
        Main.serverPidMap.values().forEach(pid -> pid.descendants().forEach(ProcessHandle::destroy));
        System.exit(0);
    }
}

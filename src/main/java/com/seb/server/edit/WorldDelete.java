package com.seb.server.edit;

import com.seb.Mysql;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;

import java.io.File;
import java.sql.SQLException;

public class WorldDelete extends JavalinAuthPage {
    public WorldDelete(Context ctx) throws SQLException {
        super(ctx);
        if (cancel) return;
        String id = ctx.pathParam("id");
        File world = new File(getUser() + "/" + Mysql.getServerNameFromId(id) + "/world");
        deleteRecursive(world);
        ctx.redirect("/editserver/" + id);
    }

    private void deleteRecursive(File dir) {
        if (dir.isDirectory() && dir.listFiles() != null) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory() && file.listFiles() != null) {
                    deleteRecursive(file);
                } else file.delete();
            }
        } dir.delete();
    }
}
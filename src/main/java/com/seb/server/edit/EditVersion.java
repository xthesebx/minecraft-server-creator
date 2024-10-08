package com.seb.server.edit;

import com.hawolt.logger.Logger;
import com.seb.server.Create.Paper;
import com.seb.Main;
import com.seb.Mysql;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;

public class EditVersion extends JavalinAuthPage {
    public EditVersion(Context ctx) throws SQLException, IOException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        String version = ctx.formParam("version");
        String id = ctx.pathParam("id");
        String name = Mysql.getServerNameFromId(id);
        String user = Mysql.getServerOwner(id);
        String path = user + "/" + name;
        File dir = new File(path);
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            if (f.getName().startsWith("paper")) f.delete();
        }
        Paper.downloadPaperBuild(version, dir);
        Mysql.updateVersion(ctx.pathParam("id"), version);
        String jarname = "";
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            if (f.getName().startsWith("paper")) jarname = f.getName();
        }
        PrintWriter out;
        if (!Main.isWindows()) {
            out = new PrintWriter(path + "/start.sh");
            out.println("/home/jdk-22.0.1/bin/java -Xms1G -Xmx1G -jar " + jarname + " --nogui");
        }
        else {
            out = new PrintWriter(path + "/start.bat");
            out.println("D:\\Downloads\\jdk-21.0.3_windows-x64_bin\\jdk-21.0.3\\bin\\java.exe -jar " + jarname + " --nogui");
        }
        out.close();
        ctx.redirect("/serverview/" + ctx.pathParam("id"));

    }
}

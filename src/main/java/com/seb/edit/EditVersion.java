package com.seb.edit;

import com.seb.Create.Paper;
import com.seb.Main;
import com.seb.Mysql;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class EditVersion extends JavalinAuthPage {
    public EditVersion(Context ctx) throws SQLException, IOException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        String version = ctx.formParam("version");
        String name = Mysql.getServerNameFromId(ctx.pathParam("id"));
        File dir = new File(name);
        for (File f : dir.listFiles()) {
            if (f.getName().startsWith("paper")) f.delete();
        }
        Paper.downloadPaperBuild(version, dir);
        Mysql.updateVersion(ctx.pathParam("id"), version);
        String jarname = "";
        for (File f : dir.listFiles()) {
            if (f.getName().startsWith("paper")) jarname = f.getName();
        }
        PrintWriter out;
        if (!Main.isWindows()) {
            out = new PrintWriter(name + "/start.sh");
            out.println("screen -mS " + name + " /home/jdk-22.0.1/bin/java -Xms1G -Xmx 1G -jar " + jarname);
        }
        else {
            out = new PrintWriter(name + "/start.bat");
            out.println("D:\\Downloads\\jdk-21.0.3_windows-x64_bin\\jdk-21.0.3\\bin\\java.exe -jar " + jarname);
        }
        out.close();
        ctx.redirect("/serverview/" + ctx.pathParam("id"));

    }
}

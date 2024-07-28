package com.seb.Create;

import com.hawolt.logger.Logger;
import com.seb.abs.JavalinLoggedInPage;
import com.seb.Main;
import com.seb.Mysql;
import io.javalin.http.Context;
import io.javalin.util.FileUtil;

import java.io.*;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

public class CreateServer extends JavalinLoggedInPage {

    public CreateServer(Context ctx) throws IOException, SQLException {
        super(ctx);
        if (cancel) return;
        String name = ctx.formParam("name");
        String version = ctx.formParam("version");
        String user = getUser();
        File dir = new File(user + "/" + name);
        dir.mkdir();
        try {
            Paper.downloadPaperBuild(version, dir);
        } catch (Exception e) {
            Logger.error(e);
        }
        if (ctx.uploadedFiles("files").size() > 1 || !ctx.uploadedFiles("files").get(0).filename().isEmpty())  {
            ctx.uploadedFiles("files").forEach(uploadedFile -> {
                String filename = "world" + uploadedFile.filename().substring(uploadedFile.filename().indexOf("/"));
                Logger.error(filename);
                FileUtil.streamToFile(uploadedFile.content(), user + "/" + name + "/" + filename);
            });
        }
        PrintWriter out = new PrintWriter(user + "/" + name + "/eula.txt");
        out.println("eula=true");
        out.close();
        out = new PrintWriter(user + "/" + name + "/server.properties");
        int i = Mysql.freePort();
        out.println("server-port=" + i);
        out.close();
        byte[] bytesServerId = new byte[32];
        Random rand = new Random();
        String id;
        do {
            StringBuilder sb = new StringBuilder();
            rand.nextBytes(bytesServerId);
            for (byte b : bytesServerId) {
                sb.append(String.format("%02X", b));
            }
            id = sb.toString();
        } while (Mysql.serverIdExists(id));
        Mysql.addServer(id, name, i, Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).getString("user"), version);
        String jarname = "";
        for (File f : Objects.requireNonNull(dir.listFiles())) if (f.getName().startsWith("paper")) jarname = f.getName();
        if (!Main.isWindows()) {
            out = new PrintWriter(user + "/" + name + "/start.sh");
            out.println("screen -mS " + user + "_" + name + " /home/jdk-22.0.1/bin/java -Xms1G -Xmx 1G -jar " + jarname);
        }
        else {
            out = new PrintWriter(user + "/" + name + "/start.bat");
            out.println("D:\\Downloads\\jdk-21.0.3_windows-x64_bin\\jdk-21.0.3\\bin\\java.exe -jar paper-1.21-109.jar");
        }
        out.close();
        ctx.redirect("/");
    }
}

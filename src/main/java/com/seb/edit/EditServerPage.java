package com.seb.edit;

import com.seb.Create.Paper;
import com.seb.Mysql;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;
import io.javalin.util.FileUtil;
import org.json.JSONArray;

import java.io.IOException;
import java.sql.SQLException;

public class EditServerPage extends JavalinAuthPage {
    public EditServerPage(Context ctx) throws IOException, SQLException {
        super(ctx);
        if (cancel) return;
        String html = FileUtil.readFile("html/editserver.html");
        html = html.replaceAll("SERVERID", ctx.pathParam("id"));
        String baseversion = Mysql.getServerVersion(ctx.pathParam("id"));
        JSONArray paperVersions = Paper.getPaperVersions();
        String options = "";
        for (int i = 1; i <= paperVersions.length(); i++) {
            String version = paperVersions.getString(paperVersions.length() - i);
            options += "<option value=" + version + " " + (baseversion.equals(version) ? "selected" : "") + ">" + version + "</option>";
        }
        html = html.replace("VERSION", options);
        ctx.html(html);
    }
}

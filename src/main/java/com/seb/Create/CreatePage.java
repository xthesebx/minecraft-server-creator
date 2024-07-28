package com.seb.Create;

import com.seb.abs.JavalinLoggedInPage;
import io.javalin.http.Context;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreatePage extends JavalinLoggedInPage {
    //TODO: add ERRORS (do i even need errors here? idk)
    public CreatePage(Context ctx) throws IOException {
        super(ctx);
        if (cancel) return;
        String html = Files.readString(Path.of("html/createserver.html"));
        JSONArray paperVersions = Paper.getPaperVersions();
        StringBuilder options = new StringBuilder();
        for (int i = 1; i <= paperVersions.length(); i++) {
            options.append("<option value=").append(paperVersions.getString(paperVersions.length() - i)).append(">").append(paperVersions.getString(paperVersions.length() - i)).append("</option>");
        }
        html = html.replace("$OPTIONS", options.toString());

        ctx.html(html);
    }
}

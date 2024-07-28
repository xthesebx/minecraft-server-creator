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
        String options = "";
        for (int i = 1; i <= paperVersions.length(); i++) {
            options += "<option value=" + paperVersions.getString(paperVersions.length() - i) + ">" + paperVersions.getString(paperVersions.length() - i) + "</option>";
        }
        html = html.replace("$OPTIONS", options);

        ctx.html(html);
    }
}

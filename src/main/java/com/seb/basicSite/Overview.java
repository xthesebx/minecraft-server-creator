package com.seb.basicSite;

import com.hawolt.logger.Logger;
import com.seb.abs.JavalinLoggedInPage;
import com.seb.Main;
import com.seb.Mysql;
import io.javalin.http.Context;
import io.javalin.util.FileUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Overview extends JavalinLoggedInPage {

    public Overview(Context ctx) throws SQLException {
        super(ctx);
        if (cancel) return;
        String html = FileUtil.readFile("html/Overview.html");
        ResultSet rs = Mysql.getServers(getUser());
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            String id = rs.getString(1);
            sb.append("<tr onclick=\"window.location='/serverview/").append(id).append("'\"> <td>").append(rs.getString(3)).append("</td> <td> ").append(rs.getString(5)).append("</td> <td>").append(id).append("</td> <td>").append(rs.getInt(2)).append("</td> </tr>");
        }
        html = html.replace("$EINTRÄGE", sb.toString());
        if (Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).getString("user").equals("stdbasti"))
            html = html.replace("ADMINBUTTON","<br><button onclick=\"location='/admin'\">Admin</button>");
        else html = html.replace("ADMINbutTON","");
        ctx.html(html);
    }
}

package com.seb.Login;

import com.seb.abs.JavalinPage;
import com.seb.Main;
import com.seb.Mysql;
import io.javalin.http.Context;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Login extends JavalinPage {

    public Login(Context ctx) throws NoSuchAlgorithmException, SQLException {
        super(ctx);
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        assert password != null;
        byte[] data = password.getBytes();
        byte[] hashresult = MessageDigest.getInstance("SHA-256").digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : hashresult) {
            sb.append(String.format("%02X", b));
        }
        String pwhash = sb.toString().toLowerCase();
        LoginStatus ls = Mysql.login(username, pwhash);
        String sessionid = ctx.cookie("JSESSIONID");
        JSONObject sessionobject = new JSONObject().put("loginstatus", ls);
        if (ls.equals(LoginStatus.SUCCESS))
            sessionobject.put("user", username).put("timestamp", System.currentTimeMillis() + 300000);
        Main.sessionUserTimer.put(sessionid, sessionobject);
        ctx.redirect("/");
    }
}

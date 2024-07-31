package com.seb;

import com.hawolt.logger.Logger;
import com.seb.Login.LoginStatus;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Mysql {

    private static Connection con;
    private static JSONObject data;

    static {
        try {
            data = new JSONObject(Files.readString(Path.of("mysqldata.json")));
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    public static void createMysql() throws SQLException {
        String password = data.getString("password");
        String username = data.getString("username");
        con = DriverManager.getConnection("jdbc:mariadb://sebgameservers.de:3306/nitrado", username, password);
    }

    public static int freePort() throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT max(servers.port) FROM servers;").executeQuery();
        rs.next();
        return((rs.getInt(1) <= 25565) ? 25566 : rs.getInt(1) + 1);
    }

    public static LoginStatus login(String username, String password) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT count(*) FROM users WHERE username = '" + username + "' AND password = '" + password + "';").executeQuery();
        rs.next();
        int i = rs.getInt(1);
        if (i == 1) return LoginStatus.SUCCESS;
        else return LoginStatus.WRONG_DATA;
    }

    public static boolean serverIdExists(String serverId) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT serverId FROM servers WHERE serverId = '" + serverId + "';").executeQuery();
        return rs.next();
    }

    public static void addServer(String serverId, String serverName, int port, String user, String version) throws SQLException {
        checkCon();
        String statement = "INSERT INTO nitrado.servers (serverId, port, serverName, owner, version) VALUES ('" + serverId + "', '" + port + "', '" + serverName + "', '" + user + "', '" + version + "');";
        con.prepareStatement(statement).executeUpdate();
    }

    public static ResultSet getServers(String user) throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT * FROM servers WHERE owner = '" + user + "';").executeQuery();
    }

    public static String getServerNameFromId(String serverId) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT serverName FROM servers WHERE serverId = '" + serverId + "';").executeQuery();
        rs.next();
        return rs.getString(1);
    }

    public static String getServerVersion(String serverId) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT version FROM servers WHERE serverId = '" + serverId + "';").executeQuery();
        rs.next();
        return rs.getString(1);
    }

    public static void updateVersion(String serverId, String version) throws SQLException {
        checkCon();
        con.prepareStatement("UPDATE servers SET version = '" + version + "' WHERE serverId = '" + serverId + "';").executeUpdate();
    }

    public static void addCode(String code) throws SQLException {
        checkCon();
        con.prepareStatement("INSERT INTO regCodes (code) VALUES ('" + code + "');").executeUpdate();
    }

    public static boolean codeExists(String code) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT code FROM regCodes WHERE code = '" + code + "';").executeQuery();
        return rs.next();
    }

    public static ResultSet getCodes() throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT * FROM regCodes;").executeQuery();
    }

    public static void deleteCode(String code) throws SQLException {
        checkCon();
        con.prepareStatement("DELETE FROM regCodes WHERE code = '" + code + "';").executeUpdate();
    }

    public static void addUser(String username, String password) throws SQLException {
        checkCon();
        con.prepareStatement("INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "');").executeUpdate();
    }

    public static String getServerOwner(String serverId) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT owner FROM servers WHERE serverId = '" + serverId + "';").executeQuery();
        rs.next();
        return rs.getString(1);
    }

    private static void checkCon() throws SQLException {
        if (con.isClosed()) createMysql();
    }
}

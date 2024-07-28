package com.seb.Create;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Paper {

    public static JSONArray getPaperVersions() throws IOException {
        URL url = new URL("https://api.papermc.io/v2/projects/paper");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        byte[] buffer = new byte[1024];
        int read;
        String text = "";
        while ((read = in.read(buffer)) != -1) {
            text = text + new String(buffer, 0, read);
        }
        in.close();
        connection.disconnect();
        return new JSONArray(new JSONObject(text).getJSONArray("versions"));
    }

    public static void downloadPaperBuild(String version, File dir) throws IOException, NoSuchAlgorithmException {
        String hash = "", checksum = "";
        do {
            JSONObject object = new JSONObject(Paper.getLatestPaperBuild(version));
            JSONArray builds = object.getJSONArray("builds");
            int i = 1;
            JSONObject latest;
            do {
                latest = builds.getJSONObject(builds.length() - i);
                i++;
            } while (!latest.getString("channel").equals("default"));
            String build = Integer.toString(latest.getInt("build"));
            String fileName = latest.getJSONObject("downloads").getJSONObject("application").getString("name");
            hash = latest.getJSONObject("downloads").getJSONObject("application").getString("sha256");

            URL url = new URL("https://api.papermc.io/v2/projects/paper/versions/" + version + "/builds/" + build + "/downloads/" + fileName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
            FileOutputStream fos = new FileOutputStream(new File(dir, fileName));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            in.close();
            connection.disconnect();
            fos.close();
            Path filePath = Path.of(dir + "/" + fileName);
            byte[] data = Files.readAllBytes(Paths.get(filePath.toUri()));
            byte[] hashresult = MessageDigest.getInstance("SHA-256").digest(data);
            checksum = new BigInteger(1, hashresult).toString(16);
        } while (!hash.equals(checksum));
    }

    public static String getLatestPaperBuild(String version) throws IOException {
        URL url = new URL("https://api.papermc.io/v2/projects/paper/versions/" + version + "/builds");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        byte[] buffer = new byte[1024];
        int read;
        String text = "";
        while ((read = in.read(buffer)) != -1) {
            text = text + new String(buffer, 0, read);
        }
        in.close();
        connection.disconnect();
        return text;
    }
}

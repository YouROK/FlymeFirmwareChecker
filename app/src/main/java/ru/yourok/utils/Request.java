package ru.yourok.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yourok on 19.09.17.
 */

public class Request {

    private HttpURLConnection conn;

    public Request(String host) throws Exception {
        URL url = new URL(host + "/sysupgrade/check");
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
    }

    public void sendRequest(final String req) throws Exception {
        try {
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(req);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String recvResponce() throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

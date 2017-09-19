package ru.yourok.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yourok on 19.09.17.
 */

public class RequestManager {

    private HttpURLConnection conn;

    public RequestManager(String host) throws Exception {
        URL url = new URL(host + "/sysupgrade/check");
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
    }

    public void sendRequest(final String req) throws Exception {
        final Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(req);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
        th.join();
    }

    public String recvResponce() throws Exception {
        final String[] resp = new String[1];
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    resp[0] = sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
        th.join();
        return resp[0];
    }
}

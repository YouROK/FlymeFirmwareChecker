package ru.yourok.utils;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by yourok on 20.09.17.
 */

public class RequestManager {

    final public int ST_NONE = 0;
    final public int ST_REQUEST = 1;
    final public int ST_FOUND = 2;
    final public int ST_NOTFOUND = 3;
    final public int ST_ERROR = 4;

    JSONObject update, params;
    String host;
    String stat;
    int stati;
    long timestamp;

    Thread thread;
    boolean stop;

    public RequestManager(final String host, final JSONObject params) {
        stati = ST_NONE;
        this.host = host;
        this.params = params;
    }

    public String getStat() {
        return stat;
    }

    public int getStatI() {
        return stati;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public JSONObject getUpdate() {
        return update;
    }

    public void check(final Runnable onEndRequest) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    stati = ST_REQUEST;
                    Request conn = new Request(host);
                    String sparams = params.toString();
                    String sign = SystemParams.getSign(sparams);
                    String body = "unitType=0&sys=" + sparams + "&sign=" + sign;
                    conn.sendRequest(body);
                    String resp = conn.recvResponce();

                    JSONObject json = new JSONObject(resp);
                    Log.i("FlymeFirmwareChecker", json.toString(1));

                    if (json.has("code") && !json.getString("code").equals("200")) {
                        stat = json.getString("message");
                        stati = ST_ERROR;
                    }
                    update = json;
                    if (!Utils.has(update, "reply", "value", "new")) {
                        stat = "No firmware";
                        stati = ST_NOTFOUND;
                    } else {
                        stat = "";
                        stati = ST_FOUND;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    stati = ST_ERROR;
                    stat = e.getMessage();
                }
                onEndRequest.run();
            }
        });
        thread.start();
    }

    public void find(final Runnable onEndRequest, final Runnable onNext) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int count = 0;
                    stop = false;
                    while (!stop) {
                        stati = ST_REQUEST;
                        Request conn = new Request(host);

                        long ts = getTSFromMask();
                        if (ts == -1) {
                            stat = "Wrong mask id";
                            stati = ST_ERROR;
                            break;
                        }
                        timestamp = ts - 86400/*одни сутки*/;
                        setTSToMask(timestamp);

                        if (onNext != null) {
                            onNext.run();
                        }

                        String sparams = params.toString();
                        String sign = SystemParams.getSign(sparams);
                        String body = "unitType=0&sys=" + sparams + "&sign=" + sign;
                        conn.sendRequest(body);
                        String resp = conn.recvResponce();

                        JSONObject json = new JSONObject(resp);
                        Log.i("FlymeFirmwareChecker", json.toString(1));

                        if (json.has("code") && !json.getString("code").equals("200")) {
                            stat = json.getString("message");
                            stati = ST_ERROR;
                            break;
                        }
                        update = json;
                        if (Utils.has(update, "reply", "value", "new")) {
                            stat = "";
                            stati = ST_FOUND;
                            break;
                        }
                        count++;
                        if (count >= 100) {
                            stat = "FW not found";
                            stati = ST_NOTFOUND;
                            break;
                        }
                        Thread.currentThread().sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    stati = ST_ERROR;
                    stat = e.getMessage();
                }
                stop = true;
                if (onEndRequest != null)
                    onEndRequest.run();
            }
        });
        thread.start();
    }

    public boolean isFinding() {
        return !stop;
    }

    public void stop() {
        stop = true;
    }

    public void waitRequest() {
        try {
            if (thread != null)
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private long getTSFromMask() {
        try {
            String maskid = params.getString(SystemParams.SYSVER);
            String[] prop = maskid.split("-|_");
            if (prop.length == 3)
                return Long.parseLong(prop[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void setTSToMask(long ts) {
        try {
            String maskid = params.getString(SystemParams.SYSVER);
            String[] prop = maskid.split("-|_");
            if (prop.length == 3) {
                maskid = prop[0] + "-" + ts + "_" + prop[2];
                params.put(SystemParams.SYSVER, maskid);
                params.put(SystemParams.VERSION, maskid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package ru.yourok.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

/**
 * Created by yourok on 19.09.17.
 */

public class Params {

    private Context context;

    public Params(Context context) {
        this.context = context;
    }

    public JSONObject getParams() throws Exception {
        JSONObject basicParams = new JSONObject();
        basicParams.put(SystemParams.DEVICE_TYPE, get(SystemParams.DEVICE_TYPE));
        basicParams.put(SystemParams.FIRMWARE, get(SystemParams.FIRMWARE));
        basicParams.put(SystemParams.SYSVER, get(SystemParams.SYSVER));
        basicParams.put(SystemParams.VERSION, get(SystemParams.VERSION));
        basicParams.put(SystemParams.ROOT, get(SystemParams.ROOT));
        basicParams.put(SystemParams.DEVICE_ID, get(SystemParams.DEVICE_ID));
        basicParams.put(SystemParams.SN, get(SystemParams.SN));
        basicParams.put(SystemParams.IMEI, get(SystemParams.IMEI));
        return basicParams;
    }

    public void setParams(JSONObject params) throws Exception {
        set(SystemParams.DEVICE_TYPE, params.getString(SystemParams.DEVICE_TYPE));
        set(SystemParams.FIRMWARE, params.getString(SystemParams.FIRMWARE));
        set(SystemParams.SYSVER, params.getString(SystemParams.SYSVER));
        set(SystemParams.VERSION, params.getString(SystemParams.VERSION));
        set(SystemParams.ROOT, params.getString(SystemParams.ROOT));
        set(SystemParams.DEVICE_ID, params.getString(SystemParams.DEVICE_ID));
        set(SystemParams.SN, params.getString(SystemParams.SN));
        set(SystemParams.IMEI, params.getString(SystemParams.IMEI));
    }

    public String getUpdate() {
        return get("Update");
    }

    public void setUpdate(String update) {
        set("Update", update);
    }

    private String get(String name) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return prefs.getString(name, "");
        } catch (Exception e) {
            return "";
        }
    }

    private void set(String name, String val) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(name, val).apply();
    }
}

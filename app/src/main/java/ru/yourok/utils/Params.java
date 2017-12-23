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
        basicParams.put(SystemParams.Companion.getDEVICE_TYPE(), get(SystemParams.Companion.getDEVICE_TYPE()));
        basicParams.put(SystemParams.Companion.getFIRMWARE(), get(SystemParams.Companion.getFIRMWARE()));
        basicParams.put(SystemParams.Companion.getSYSVER(), get(SystemParams.Companion.getSYSVER()));
        basicParams.put(SystemParams.Companion.getVERSION(), get(SystemParams.Companion.getVERSION()));
        basicParams.put(SystemParams.Companion.getROOT(), get(SystemParams.Companion.getROOT()));
        basicParams.put(SystemParams.Companion.getDEVICE_ID(), get(SystemParams.Companion.getDEVICE_ID()));
        basicParams.put(SystemParams.Companion.getSN(), get(SystemParams.Companion.getSN()));
        basicParams.put(SystemParams.Companion.getIMEI(), get(SystemParams.Companion.getIMEI()));
        return basicParams;
    }

    public void setParams(JSONObject params) throws Exception {
        set(SystemParams.Companion.getDEVICE_TYPE(), params.getString(SystemParams.Companion.getDEVICE_TYPE()));
        set(SystemParams.Companion.getFIRMWARE(), params.getString(SystemParams.Companion.getFIRMWARE()));
        set(SystemParams.Companion.getSYSVER(), params.getString(SystemParams.Companion.getSYSVER()));
        set(SystemParams.Companion.getVERSION(), params.getString(SystemParams.Companion.getVERSION()));
        set(SystemParams.Companion.getROOT(), params.getString(SystemParams.Companion.getROOT()));
        set(SystemParams.Companion.getDEVICE_ID(), params.getString(SystemParams.Companion.getDEVICE_ID()));
        set(SystemParams.Companion.getSN(), params.getString(SystemParams.Companion.getSN()));
        set(SystemParams.Companion.getIMEI(), params.getString(SystemParams.Companion.getIMEI()));
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

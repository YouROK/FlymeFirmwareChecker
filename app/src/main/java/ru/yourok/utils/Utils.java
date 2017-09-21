package ru.yourok.utils;

import org.json.JSONObject;

/**
 * Created by yourok on 20.09.17.
 */

public class Utils {
    static public boolean has(JSONObject json, String... names) {
        try {
            JSONObject js = json;
            for (int i = 0; i < names.length - 1; i++) {
                if (js.has(names[i]))
                    js = js.getJSONObject(names[i]);
                else
                    return false;
            }
            return js.has(names[names.length - 1]) && !js.isNull(names[names.length - 1]);
        } catch (Exception e) {
            return false;
        }
    }
}

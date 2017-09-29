package ru.yourok.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

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

    static public String readFile(File file) throws Exception {
        StringBuilder text = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }
        br.close();
        return text.toString();
    }

    static public void writeFile(File file, String txt) throws Exception {
        if (file.exists())
            file.delete();
        FileWriter fw = new FileWriter(file);
        fw.write(txt);
        fw.close();
    }
}

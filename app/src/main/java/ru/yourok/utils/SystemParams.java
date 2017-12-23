package ru.yourok.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by yourok on 19.09.17.
 */

public class SystemParams {
    public static final String DEVICE_ID = "deviceId";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String FIRMWARE = "firmware";
    public static final String MD5_SIGN = "2635881a7ab0593849fe89e685fc56cd";
    public static final String ROOT = "root";
    public static final String SN = "sn";
    public static final String SYSVER = "sysVer";
    public static final String VERSION = "version";
    public static final String IMEI = "imei";

    public static final String U_HOST = "https://u.meizu.com";
    public static final String U_HOST_INTERNATIONAL = "https://u.in.meizu.com";

    private Context context;

    public SystemParams(Context context) {
        this.context = context;
    }

    public JSONObject getSystemParams() throws Exception {
        JSONObject basicParams = new JSONObject();
        basicParams.put(DEVICE_TYPE, getDeviceType());
        basicParams.put(FIRMWARE, getFirmware());
        basicParams.put(ROOT, isRooted());
        basicParams.put(SYSVER, getSysver());
        basicParams.put(VERSION, getSysver());
        basicParams.put(DEVICE_ID, getDeviceId());
        basicParams.put(SN, getSN());
        basicParams.put(IMEI, getDeviceId());
        return basicParams;
    }

    public String getDeviceType() {
        return SystemPropertiesProxy.get(context, "ro.meizu.product.model", "");
    }

    public String getFirmware() {
        return SystemPropertiesProxy.get(context, "ro.build.version.release", "");
    }

    public String getSysver() {
        return SystemPropertiesProxy.get(context, "ro.build.mask.id", "");
    }

    public String isRooted() {
        if (new File("/system/xbin/su").exists())
            return "1";
        return "0";
    }

    public String getDeviceId() {//imei
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public String getSN() {
        return Build.SERIAL;
    }

    static public String getHost(boolean international) {
        if (international)
            return U_HOST_INTERNATIONAL;
        return U_HOST;
    }

    static public String getSign(String params) {
        return MD5(params + MD5_SIGN);
    }


    static public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}

package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yh on 2016/12/12.
 */

public class SpUtil {
    private static SharedPreferences sp;

    public static void putBoolean(Context context, String key, boolean Value) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", context.MODE_PRIVATE);//被当前程序读写
        }
        sp.edit().putBoolean(key, Value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", context.MODE_PRIVATE);//被当前程序读写
        }
        return sp.getBoolean(key, defValue);
    }

    public static void putString(Context context, String key, String  Value) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", context.MODE_PRIVATE);//被当前程序读写
        }
        sp.edit().putString(key, Value).commit();
    }

    public static String  getString(Context context, String key, String  defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", context.MODE_PRIVATE);//被当前程序读写
        }
        return sp.getString(key, defValue);
    }

    public static void putInt(Context context, String key, int Value) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", context.MODE_PRIVATE);//被当前程序读写
        }
        sp.edit().putInt(key, Value).commit();
    }

    public static int getInt(Context context, String key, int defValue) {
         if (sp == null) {
            sp = context.getSharedPreferences("config", context.MODE_PRIVATE);//被当前程序读写
        }
        return sp.getInt(key, defValue);
    }


}

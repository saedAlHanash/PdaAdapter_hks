package com.example.gxwl.rederdemo.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    private static SharedPreferences sp;

    private static SharedPreferencesUtil util;

    private SharedPreferencesUtil(Context paramContext, String paramString) {
        sp = paramContext.getSharedPreferences(paramString, 0);
    }

    public static Object getData(String paramString, Object paramObject) {
        String str1 = null;
        String str2 = paramObject.getClass().getSimpleName();
        byte b = -1;
        String str3 = null;
        try {
            Long long_ = null;
            switch (str2.hashCode()) {
                case 1729365000:
                    if (str2.equals("Boolean"))
                        b = 0;
                    break;
                case 67973692:
                    if (str2.equals("Float"))
                        b = 2;
                    break;
                case 2374300:
                    if (str2.equals("Long"))
                        b = 1;
                    break;
                case -672261858:
                    if (str2.equals("Integer"))
                        b = 4;
                    break;
                case -1808118735:
                    if (str2.equals("String"))
                        b = 3;
                    break;
            }
            if (b != 0) {
                Float float_ = null;
                if (b != 1) {
                    if (b != 2) {
                        Integer integer = null;
                        if (b != 3) {
                            if (b != 4) {
                                paramString = str3;
                            } else {
                                integer = Integer.valueOf(sp.getInt(paramString, ((Integer)paramObject).intValue()));
                            }
                        } else {
                            str1 = sp.getString(String.valueOf(integer), String.valueOf(paramObject));
                        }
                    } else {
                        float_ = Float.valueOf(sp.getFloat(str1, ((Float)paramObject).floatValue()));
                    }
                } else {
                    long_ = Long.valueOf(sp.getLong(String.valueOf(float_), ((Long)paramObject).longValue()));
                }
            } else {
                Boolean bool = Boolean.valueOf(sp.getBoolean(String.valueOf(long_), ((Boolean)paramObject).booleanValue()));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            str1 = str3;
        }
        return str1;
    }

    public static void getInstance(Context paramContext, String paramString) {
        if (util == null)
            util = new SharedPreferencesUtil(paramContext, paramString);
    }

    public static boolean putData(String paramString, Object paramObject) {
        SharedPreferences.Editor editor = sp.edit();
        String str = paramObject.getClass().getSimpleName();
        byte b = -1;
        boolean bool = false;
        try {
            switch (str.hashCode()) {
                case 1729365000:
                    if (str.equals("Boolean"))
                        b = 0;
                    break;
                case 67973692:
                    if (str.equals("Float"))
                        b = 2;
                    break;
                case 2374300:
                    if (str.equals("Long"))
                        b = 1;
                    break;
                case -672261858:
                    if (str.equals("Integer"))
                        b = 4;
                    break;
                case -1808118735:
                    if (str.equals("String"))
                        b = 3;
                    break;
            }
            if (b != 0) {
                if (b != 1) {
                    if (b != 2) {
                        if (b != 3) {
                            if (b == 4)
                                editor.putInt(paramString, ((Integer)paramObject).intValue());
                        } else {
                            editor.putString(paramString, String.valueOf(paramObject));
                        }
                    } else {
                        editor.putFloat(paramString, ((Float)paramObject).floatValue());
                    }
                } else {
                    editor.putLong(paramString, ((Long)paramObject).longValue());
                }
            } else {
                editor.putBoolean(paramString, ((Boolean)paramObject).booleanValue());
            }
            bool = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        editor.apply();
        return bool;
    }
}

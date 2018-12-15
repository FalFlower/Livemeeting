package cn.edu.sdnu.i.livemeeting.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

public class SharedPreferencesUtil {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesUtil(Context context) {
        preferences=  PreferenceManager.getDefaultSharedPreferences(context);
        editor=preferences.edit();
    }
    public void doPutString(String key,String date){
        editor.putString(key,date);
        editor.apply();
    }
    public String doGetString(String key,String def){
        return  preferences.getString(key,def);
    }
}

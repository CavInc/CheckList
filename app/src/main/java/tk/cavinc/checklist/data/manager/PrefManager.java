package tk.cavinc.checklist.data.manager;

import android.content.SharedPreferences;

import java.util.ArrayList;

import tk.cavinc.checklist.utils.App;

/**
 * Created by cav on 02.08.18.
 */

public class PrefManager {

    private static final String YANDEX_LOGIN = "YA_LOGIN";
    private static final String YANDEX_PASS = "YA_PASS";
    private static final String LAST_SEND_FILE = "LAST_SEND_FILE";
    private SharedPreferences mSharedPreferences;

    public PrefManager(){
        mSharedPreferences = App.getSharedPreferences();
    }

    public void setLoginPassword(String login,String pass){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(YANDEX_LOGIN,login);
        editor.putString(YANDEX_PASS,pass);
        editor.apply();
    }

    public ArrayList<String> getLoginPassword(){
        String login = mSharedPreferences.getString(YANDEX_LOGIN,null);
        String pass = mSharedPreferences.getString(YANDEX_PASS,null);
        ArrayList<String> rec = new ArrayList<>();
        rec.add(login);
        rec.add(pass);
        return rec;
    }

    // работа с текущими позициями
    public void setCountWorkTime(String key,int count){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key,count);
        editor.apply();
    }

    public int getCountWorkTime(String key){
        return mSharedPreferences.getInt(key,0);
    }

    // последний автоматически оправленный файл
    public String getLastSendFile(){
        return mSharedPreferences.getString(LAST_SEND_FILE,"1907-01-01");
    }

    public void setLastSendFile(String file) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(LAST_SEND_FILE, file);
        editor.apply();
    }

}

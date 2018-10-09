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
    private static final String WORK_DATA = "WORK_DATA";
    private static final String ALL_COUNT = "ALL_COUNT";
    private static final String CHECK_ACCOUNT = "CHECK_ACCOUNT";
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

    // рабочая дата
    public void setWorkData(String data){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(WORK_DATA,data);
        editor.apply();
    }

    public String getWorkData(){
        return mSharedPreferences.getString(WORK_DATA,null);
    }

    public int getAllQuestionCount(){
        return mSharedPreferences.getInt(ALL_COUNT,0);
    }

    public void setAllQuestionCount(int count){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(ALL_COUNT,count);
        editor.apply();
    }

    public boolean getCheckAccount(){
        return mSharedPreferences.getBoolean(CHECK_ACCOUNT,false);
    }

    public void setCheckAccount(boolean checkAccount){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(CHECK_ACCOUNT,checkAccount);
        editor.apply();
    }

    // флаг о удалении
    public boolean isDeleteInStore(){
        return mSharedPreferences.getBoolean("delete_in_store",false);
    }

}

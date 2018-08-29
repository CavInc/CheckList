package tk.cavinc.checklist.data.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import tk.cavinc.checklist.data.database.DBConnect;
import tk.cavinc.checklist.utils.App;

/**
 * Created by cav on 02.08.18.
 */

public class DataManager {
    private static DataManager INSTANCE = null;

    private Context mContext;
    private PrefManager mPrefManager;
    private DBConnect mDB;

    public static DataManager getInstance() {
        if (INSTANCE==null){
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public DataManager(){
        mContext = App.getContext();
        mPrefManager = new PrefManager();
        mDB = new DBConnect(mContext);
    }

    public Context getContext() {
        return mContext;
    }

    public PrefManager getPrefManager() {
        return mPrefManager;
    }

    public DBConnect getDB() {
        return mDB;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // возвращает путь к локальной папки приложения
    public String getStorageAppPath(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return null;
        File path = new File (Environment.getExternalStorageDirectory(), "CheckList");
        if (! path.exists()) {
            if (!path.mkdirs()){
                return null;
            }
        }
        return path.getPath();
    }

    public File getStoragePath(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return null;
        File path = new File (Environment.getExternalStorageDirectory(), "CheckList");
        if (! path.exists()) {
            if (!path.mkdirs()){
                return null;
            }
        }
        return path;
    }

    // проверяем включен ли интернетик
    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // общее количество вопросов
    public int getAllQuestionCount(){
        int count = 0;
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray jArr = obj.getJSONArray("items");
            for (int i=0;i<jArr.length();i++) {
                JSONObject quest = jArr.getJSONObject(i);
                JSONArray jCheck = quest.getJSONArray("check");
                for (int j=0 ; j<jCheck.length() ; j++) {
                    JSONObject checkItem = jCheck.getJSONObject(j);
                    JSONArray wt =  checkItem.getJSONArray("time_check");
                    if (checkItem.has("photo") && checkItem.getBoolean("photo")) {

                    }else {
                        for (int k = 0; k < wt.length(); k++) {
                            if (wt.get(k) == 1) {
                             count += 1;
                            }
                        }
                    }
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }

        return count;
    }

}

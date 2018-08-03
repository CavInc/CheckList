package tk.cavinc.checklist.data.manager;

import android.content.SharedPreferences;

import tk.cavinc.checklist.utils.App;

/**
 * Created by cav on 02.08.18.
 */

public class PrefManager {

    private SharedPreferences mSharedPreferences;

    public PrefManager(){
        mSharedPreferences = App.getSharedPreferences();
    }

}

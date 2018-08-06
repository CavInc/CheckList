package tk.cavinc.checklist.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by cav on 02.08.18.
 */

public class DBConnect {
    private SQLiteDatabase database;
    private DBHelper mDBHelper;

    public DBConnect(Context context){
        mDBHelper = new DBHelper(context,DBHelper.DATABASE_NAME,null,DBHelper.DATABASE_VERSION);
    }

    public void open(){
        database = mDBHelper.getWritableDatabase();
    }
    public  void close(){
        if (database!=null) {
            database.close();
        }
    }

}

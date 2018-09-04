package tk.cavinc.checklist.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cav on 02.08.18.
 */

public class DBHelper  extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1 ;
    public static final String DATABASE_NAME = "checklist.db3";
    public static final String CHECKED = "check_data";
    public static final String CHECKED_HEAD ="check_head";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db,0,DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db,oldVersion,newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion<1){
            db.execSQL("create table "+CHECKED+"(" +
                    "create_date text," +
                    "check_time text," +
                    "check_group text," +
                    "check_item integer," +
                    "photo_file integer," +
                    "checked integer default 0,"+
                    "comment text," +
                    "photo_send integer default 0," + // фото оправлено на YD
                    "primary key (create_date,check_time,check_group,check_item))");

            db.execSQL("create table "+CHECKED_HEAD+"(" +
                    "create_data text," +
                    "count_check integer default 0," +
                    "primary key (create_data))");

        } else if (oldVersion<2){
            db.execSQL("create table "+CHECKED_HEAD+"(" +
                    "create_data text," +
                    "count_check integer default 0," +
                    "primary key (create_data))");
        }
    }
}

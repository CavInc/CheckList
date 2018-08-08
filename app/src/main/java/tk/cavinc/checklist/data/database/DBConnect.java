package tk.cavinc.checklist.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import tk.cavinc.checklist.data.models.CheckItemModel;

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

    // сохраняем данные
    public void addCheckRec(CheckItemModel model,String data,String time){
        open();
        ContentValues values = new ContentValues();
        values.put("create_date",data);
        values.put("check_time",true);
        values.put("check_group",model.getGroupID());
        values.put("check_item",model.getId());
        values.put("photo_file",model.getPhotoName());
        values.put("comment",model.getComment());
        values.put("checked",(model.isCheck() ? 0:1));
        database.insertWithOnConflict(DBHelper.CHECKED,null,values,SQLiteDatabase.CONFLICT_REPLACE);

        close();
    }

}

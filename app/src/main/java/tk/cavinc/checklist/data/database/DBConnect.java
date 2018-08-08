package tk.cavinc.checklist.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

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
        values.put("check_time",time);
        values.put("check_group",model.getGroupID());
        values.put("check_item",model.getId());
        values.put("photo_file",model.getPhotoName());
        values.put("comment",model.getComment());
        values.put("checked",(model.isCheck() ? 1:0));
        database.insertWithOnConflict(DBHelper.CHECKED,null,values,SQLiteDatabase.CONFLICT_REPLACE);

        close();
    }

    // список сохраненных чек листов
    public ArrayList<String> getArhiveChech(){
        ArrayList<String> rec = new ArrayList<>();
        open();
        String sql = "select distinct create_date from "+DBHelper.CHECKED+" order by create_date";
        Cursor cursor = database.rawQuery(sql,null);
        while (cursor.moveToNext()){
            rec.add(cursor.getString(0));
        }
        close();
        return rec;
    }

}

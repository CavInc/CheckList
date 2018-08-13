package tk.cavinc.checklist.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import tk.cavinc.checklist.data.models.ArhiveModel;
import tk.cavinc.checklist.data.models.CheckItemModel;
import tk.cavinc.checklist.data.models.CountTimeModel;

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
    public ArrayList<ArhiveModel> getArhiveChech(){
        ArrayList<ArhiveModel> rec = new ArrayList<>();
        open();
        String sql = "select distinct create_date from "+DBHelper.CHECKED+" order by create_date";
        Cursor cursor = database.rawQuery(sql,null);
        while (cursor.moveToNext()){
            rec.add(new ArhiveModel(cursor.getString(0),false));
        }
        close();
        return rec;
    }

    public ArrayList<CheckItemModel> getCheckInDate(String date){
        ArrayList<CheckItemModel> rec = new ArrayList<>();
        open();
        Cursor cursor = database.query(DBHelper.CHECKED,
                new String[] {"check_time","check_group","check_item","photo_file","comment","checked"},
                "create_date=?",new String[]{date},null,null,"check_group,check_item");
        while (cursor.moveToNext()){
            boolean photo = false;
            if (cursor.getString(cursor.getColumnIndex("photo_file")) != null) {
                if (cursor.getString(cursor.getColumnIndex("photo_file")).length() != 0) {
                    photo = true;
                }
            }
            rec.add(new CheckItemModel(
                    cursor.getInt(cursor.getColumnIndex("check_group")),
                    cursor.getInt(cursor.getColumnIndex("check_item")),
                    "",
                    (cursor.getInt(cursor.getColumnIndex("checked")) == 1 ? true : false),
                    photo,
                    cursor.getString(cursor.getColumnIndex("photo_file")),
                    cursor.getString(cursor.getColumnIndex("comment")),
                    cursor.getString(cursor.getColumnIndex("check_time"))
            ));

        }
        close();
        return rec;
    }

    // количество обработанных записей
    public int getCheckCount(String date,String time) {
        int res  = 0;
        open();
        String sql = "select create_date,check_time,count(1) from check_data\n" +
                "where create_date='"+date+"'\n" +
                "group by create_date,check_time";
        Cursor cursor = database.rawQuery(sql,null);
        while (cursor.moveToNext()){
            res = cursor.getInt(2);
        }
        close();
        return res;
    }

    //
    public ArrayList<CountTimeModel> getCountAll(String date) {
        ArrayList<CountTimeModel> rec = new ArrayList<>();
        open();
        String sql = "select check_time,count(1) from check_data\n" +
                "where create_date='"+date+"'\n" +
                "group by check_time";
        Cursor cursor = database.rawQuery(sql,null);
        while (cursor.moveToNext()){
            rec.add(new CountTimeModel(cursor.getString(0),
                    cursor.getInt(1)));
        }

        close();
        return rec;
    }

    // ставим стату оправки фотографии
    public void setPhotoStatus(){
        open();
        ContentValues values = new ContentValues();
        values.put("photo_send",1);
        database.update(DBHelper.CHECKED,values,"",new String[]{});
        close();
    }

}

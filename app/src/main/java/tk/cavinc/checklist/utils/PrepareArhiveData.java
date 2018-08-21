package tk.cavinc.checklist.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import tk.cavinc.checklist.data.manager.DataManager;
import tk.cavinc.checklist.data.models.ArhiveDocModel;
import tk.cavinc.checklist.data.models.ArhiveHeadModel;
import tk.cavinc.checklist.data.models.ArhiveItemCommentModel;
import tk.cavinc.checklist.data.models.ArhiveItemModel;
import tk.cavinc.checklist.data.models.CheckItemModel;

/**
 * Created by cav on 15.08.18.
 */

public class PrepareArhiveData {
    private static final String TAG = "PAD";
    private String argiveName;
    private Context mContext;
    private DataManager mDataManager;

    public PrepareArhiveData (String arhiveName){
        this.argiveName = arhiveName;
        mDataManager = DataManager.getInstance();
        mContext = mDataManager.getContext();
    }

    // формируем и возвращаем объекст с данными
    public ArhiveDocModel get(){
        ArhiveDocModel result = null;

        ArrayList<ArhiveHeadModel> rec = new ArrayList<>();

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
            return result;
        }

        ArrayList<CheckItemModel> storeData = mDataManager.getDB().getCheckInDate(argiveName);

        ArrayList<ArhiveItemCommentModel> commentData = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray jArr = obj.getJSONArray("items");
            for (int i=0;i<jArr.length();i++){
                JSONObject quest = jArr.getJSONObject(i);
                int groupID = quest.getInt("id");
                String groupTitle = quest.getString("title");

                ArrayList<ArrayList<ArhiveItemModel>> items_pos = new ArrayList<>();

                JSONArray jCheck = quest.getJSONArray("check");
                for (int j=0 ; j<jCheck.length() ; j++){
                    ArrayList <ArhiveItemModel> item = new ArrayList<>();
                    JSONObject checkItem = jCheck.getJSONObject(j);
                    JSONArray wt =  checkItem.getJSONArray("time_check"); // время
                    String time = null;
                    for (int k=0 ; k<wt.length(); k++){
                        time = ConstantManager.TIME_CHECK[k];
                        int pos = storeData.indexOf(new CheckItemModel(time,groupID,checkItem.getInt("id"),"",false,false));
                        boolean check = false;
                        boolean photo = false;
                        if (pos != -1) {
                            CheckItemModel itm = storeData.get(pos);
                            check = itm.isCheck();
                            photo = itm.isPhoto();
                            if (itm.getComment() != null){
                                commentData.add(new ArhiveItemCommentModel(groupTitle,checkItem.getString("title"),itm.getComment()));
                            }
                        }
                        ArhiveItemModel aItem = new ArhiveItemModel(checkItem.getInt("id"),checkItem.getString("title"),time,check,photo);
                        item.add(aItem);
                    }
                    items_pos.add(item);
                }
                rec.add(new ArhiveHeadModel(groupID,groupTitle,items_pos)); // добавили группу опроса
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return result;
        }
        result = new ArhiveDocModel(rec,commentData);
        return result;
    }

}

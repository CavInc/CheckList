package tk.cavinc.checklist.data.models;

import java.util.ArrayList;

/**
 * Created by cav on 15.08.18.
 */

public class ArhiveHeadModel {
    private int mId;
    private String mTitle;
    private ArrayList<ArrayList<ArhiveItemModel>> mItems;

    public ArhiveHeadModel(int id, String title, ArrayList<ArrayList<ArhiveItemModel>> items) {
        mId = id;
        mTitle = title;
        mItems = items;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public ArrayList<ArrayList<ArhiveItemModel>> getItems() {
        return mItems;
    }
}

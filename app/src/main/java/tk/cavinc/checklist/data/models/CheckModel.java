package tk.cavinc.checklist.data.models;

import java.util.ArrayList;

public class CheckModel {
    private int mId;
    private String mTitle;
    private ArrayList<CheckItemModel> mItems;

    public CheckModel(int id, String title, ArrayList<CheckItemModel> items) {
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

    public ArrayList<CheckItemModel> getItems() {
        return mItems;
    }
}
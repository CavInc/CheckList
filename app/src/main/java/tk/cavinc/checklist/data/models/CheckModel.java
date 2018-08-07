package tk.cavinc.checklist.data.models;

import java.util.ArrayList;

public class CheckModel {
    private int mId;
    private String mTitle;
    private boolean mCheck;
    private boolean mPhoto;
    private String mPhotoName;
    private ArrayList<CheckItemModel> mItems;

    public CheckModel(int id, String title, ArrayList<CheckItemModel> items) {
        mId = id;
        mTitle = title;
        mItems = items;
    }

    public CheckModel(int id, String title, boolean check, boolean photo) {
        mId = id;
        mTitle = title;
        mCheck = check;
        mPhoto = photo;
    }

    public CheckModel(int id, String title, boolean check) {
        mId = id;
        mTitle = title;
        mCheck = check;
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

    public boolean isCheck() {
        return mCheck;
    }

    public boolean isPhoto() {
        return mPhoto;
    }

    public void setCheck(boolean check) {
        mCheck = check;
    }

    public String getPhotoName() {
        return mPhotoName;
    }

    public void setPhotoName(String photoName) {
        mPhotoName = photoName;
    }
}
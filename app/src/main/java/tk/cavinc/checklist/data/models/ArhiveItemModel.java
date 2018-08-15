package tk.cavinc.checklist.data.models;

import java.util.ArrayList;

/**
 * Created by cav on 15.08.18.
 */

public class ArhiveItemModel {
    private int mId;
    private String mTitle;
    private String mTime;
    private boolean mCheck;
    private boolean isPhoto;

    public ArhiveItemModel(int id, String title, String time, boolean check, boolean isPhoto) {
        mId = id;
        mTitle = title;
        mTime = time;
        mCheck = check;
        this.isPhoto = isPhoto;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTime() {
        return mTime;
    }

    public boolean isCheck() {
        return mCheck;
    }

    public boolean isPhoto() {
        return isPhoto;
    }
}

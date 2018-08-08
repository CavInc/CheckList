package tk.cavinc.checklist.data.models;

import java.util.ArrayList;

public class CheckItemModel {
    private String mTime;
    private int mGroupID;
    private int mId;
    private String mTitle;
    private boolean mCheck = false;
    private boolean mPhoto = false;
    private String mPhotoName;
    private String mComment;


    public CheckItemModel(int groupID,int id, String title, boolean check, boolean photo) {
        mGroupID = groupID;
        mId = id;
        mTitle = title;
        mCheck = check;
        mPhoto = photo;
    }

    public CheckItemModel(int groupID,int id, String title, boolean check) {
        mGroupID = groupID;
        mId = id;
        mTitle = title;
        mCheck = check;
    }

    public CheckItemModel(int groupID, int id, String title, boolean check, boolean photo, String photoName, String comment,String time) {
        mGroupID = groupID;
        mId = id;
        mTitle = title;
        mCheck = check;
        mPhoto = photo;
        mPhotoName = photoName;
        mComment = comment;
        mTime = time;
    }

    public int getGroupID() {
        return mGroupID;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
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

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }
}
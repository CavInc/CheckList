package tk.cavinc.checklist.data.models;

import android.util.Log;

import java.util.ArrayList;

public class CheckItemModel {
    private static final String TAG = "CIM";
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

    public CheckItemModel(String time,int groupID,int id, String title, boolean check, boolean photo) {
        mTime = time;
        mGroupID = groupID;
        mId = id;
        mTitle = title;
        mCheck = check;
        mPhoto = photo;
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

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
                /* Удостоверимся, что ссылки имеют тот же самый тип */
        if(!(getClass() == obj.getClass())) {
            return false;
        }else {
            CheckItemModel tmp = (CheckItemModel) obj;
            //Log.d(TAG,"THIS "+mTime+" "+mGroupID+" "+mId);
            //Log.d(TAG,"THIS "++" "+mGroupID+" "+mId);
            if (tmp.mTime.equals(mTime) && tmp.getGroupID() == mGroupID && tmp.getId() == mId) {
                return true;
            }
        }
        return false;
    }
}
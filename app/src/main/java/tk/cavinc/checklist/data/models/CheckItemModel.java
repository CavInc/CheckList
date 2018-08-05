package tk.cavinc.checklist.data.models;

public class CheckItemModel {
    private int mId;
    private String mTitle;
    private boolean[] mCheck;

    public CheckItemModel(int id, String title, boolean[] check) {
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

    public boolean[] getCheck() {
        return mCheck;
    }
}
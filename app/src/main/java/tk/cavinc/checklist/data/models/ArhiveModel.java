package tk.cavinc.checklist.data.models;

/**
 * Created by cav on 13.08.18.
 */

public class ArhiveModel {
    private String mTitle;
    private boolean mCheck;

    public ArhiveModel(String title, boolean check) {
        mTitle = title;
        mCheck = check;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isCheck() {
        return mCheck;
    }

    public void setCheck(boolean check) {
        mCheck = check;
    }
}

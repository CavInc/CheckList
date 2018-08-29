package tk.cavinc.checklist.data.models;

/**
 * Created by cav on 13.08.18.
 */

public class ArhiveModel {
    private String mTitle;
    private boolean mCheck;
    private boolean mAllCheck;

    public ArhiveModel(String title, boolean check) {
        mTitle = title;
        mCheck = check;
    }

    public ArhiveModel(String title, boolean check, boolean allCheck) {
        mTitle = title;
        mCheck = check;
        mAllCheck = allCheck;
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

    public boolean isAllCheck() {
        return mAllCheck;
    }

    public void setAllCheck(boolean allCheck) {
        mAllCheck = allCheck;
    }
}

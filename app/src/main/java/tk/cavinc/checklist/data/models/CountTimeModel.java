package tk.cavinc.checklist.data.models;

public class CountTimeModel {
    private String mTime;
    private int mCount;

    public CountTimeModel(String time, int count) {
        mTime = time;
        mCount = count;
    }

    public String getTime() {
        return mTime;
    }

    public int getCount() {
        return mCount;
    }
}
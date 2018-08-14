package tk.cavinc.checklist.data.models;

import android.util.Log;

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

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;

        if (obj.getClass() == String.class) {
            if (((String) obj).equals(mTime)){
                return true;
            }
            return false;
        }

        if(!(getClass() == obj.getClass())) {
            return false;
        }else {
            CountTimeModel tmp = (CountTimeModel) obj;
            Log.d("MODEL",tmp.getTime());
            if (tmp.getTime().equals(mTime)) {
                return true;
            }
        }
        return false;
    }

}
package tk.cavinc.checklist.ui.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tk.cavinc.checklist.R;
import tk.cavinc.checklist.data.models.ArhiveModel;

/**
 * Created by cav on 08.08.18.
 */

public class ArhiveAdapter extends ArrayAdapter<ArhiveModel> {
    private LayoutInflater mInflater;
    private int resLayout;

    public ArhiveAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ArhiveModel> objects) {
        super(context, resource, objects);
        resLayout = resource;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;
        if (row == null) {
            row = mInflater.inflate(resLayout, parent, false);
            holder = new ViewHolder();
            holder.mTitle = row.findViewById(R.id.arhive_title);
            holder.mChekImg = row.findViewById(R.id.arhive_check);
            holder.mAllCountImg = row.findViewById(R.id.arhive_all);
            row.setTag(holder);
        } else {
            holder = (ViewHolder)row.getTag();
        }
        ArhiveModel rec = getItem(position);
        holder.mTitle.setText(rec.getTitle());
        if (rec.isCheck()) {
            holder.mChekImg.setVisibility(View.VISIBLE);
        } else {
            holder.mChekImg.setVisibility(View.GONE);
        }
        if (rec.isAllCheck()) {
            holder.mAllCountImg.setVisibility(View.VISIBLE);
        } else {
            holder.mAllCountImg.setVisibility(View.GONE);
        }
        return row;
    }

    /*
    public ArrayList<ArhiveModel> getData(){

    }
    */


    class ViewHolder {
        public TextView mTitle;
        public ImageView mChekImg;
        public ImageView mAllCountImg;
    }

}

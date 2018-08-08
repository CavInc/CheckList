package tk.cavinc.checklist.ui.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import tk.cavinc.checklist.R;

/**
 * Created by cav on 08.08.18.
 */

public class ArhiveAdapter extends ArrayAdapter<String> {
    private LayoutInflater mInflater;
    private int resLayout;

    public ArhiveAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
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
            row.setTag(holder);
        } else {
            holder = (ViewHolder)row.getTag();
        }
        String rec = getItem(position);
        holder.mTitle.setText(rec);
        return row;
    }

    class ViewHolder {
        public TextView mTitle;

    }

}

package com.example.hp.heartrytcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.BloodPressureData;
import com.example.hp.heartrytcare.db.HeartRateData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class HrBpListAdapter extends BaseAdapter {

    private List<HeartRateData> hrList;
    private List<BloodPressureData> bpList;
    private Context context;

    public HrBpListAdapter(List<HeartRateData> hrList, List<BloodPressureData> bpList, Context context) {
        this.hrList = hrList;
        this.bpList = bpList;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (hrList != null) {
            return hrList.size();
        } else if (bpList != null) {
            return bpList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (hrList != null) {
            return hrList.get(position);
        } else if (bpList != null) {
            return bpList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bphrlist, parent, false);

            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.measure = (TextView) convertView.findViewById(R.id.measure);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a");

        if (hrList != null) {
            cal.setTimeInMillis(hrList.get(position).getTimestamp());
            viewHolder.date.setText(sdf.format(cal.getTime()));
            viewHolder.measure.setText(hrList.get(position).getBpm() + " BPM");
        }

        if (bpList != null) {
            cal.setTimeInMillis(bpList.get(position).getTimestamp());
            viewHolder.date.setText(sdf.format(cal.getTime()));
            viewHolder.measure.setText(bpList.get(position).getSystolic() + "/"
                    + bpList.get(position).getDiastolic());
        }

        return convertView;
    }

    private class ViewHolder {
        TextView date;
        TextView measure;
    }
}

package com.example.hp.heartrytcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.Appointment;

import java.util.List;

public class AppointmentsAdapter extends BaseAdapter {

    private Context context;
    private List<Appointment> appointments;

    public AppointmentsAdapter(List<Appointment> appointments, Context context) {
        this.context = context;
        this.appointments = appointments;
    }

    @Override
    public int getCount() {
        return appointments.size();
    }

    @Override
    public Object getItem(int position) {
        return appointments.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_appt_entry, parent, false);

            viewHolder.header = (TextView) convertView.findViewById(R.id.header);
            viewHolder.sched = (TextView) convertView.findViewById(R.id.sched);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.header.setText(appointments.get(position).getHeader());
        viewHolder.sched.setText("Scheduled on: " + appointments.get(position).getDate() + " " + appointments.get(position).getTime());

        return convertView;
    }

    private class ViewHolder {
        TextView header;
        TextView sched;
    }
}

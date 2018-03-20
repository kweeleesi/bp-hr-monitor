package com.example.hp.heartrytcare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.Medication;

import java.util.ArrayList;

public class MedicationSchedAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Medication> medications;

    public MedicationSchedAdapter(ArrayList<Medication> medications, Context context) {
        this.context = context;
        this.medications = medications;
    }

    @Override
    public int getCount() {
        return medications.size();
    }

    @Override
    public Object getItem(int position) {
        return medications.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_medsched_entry, parent, false);

            viewHolder.medName = (TextView) convertView.findViewById(R.id.medName);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.medName.setText(medications.get(position).getNameOfMed());

        return convertView;
    }

    private class ViewHolder {
        TextView medName;
    }
}

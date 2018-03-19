package com.example.hp.heartrytcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.Journal;

import java.util.List;

public class JournalListAdapter extends BaseAdapter {

    private List<Journal> journalList;
    private Context context;

    public JournalListAdapter(List<Journal> journalList, Context context) {
        this.journalList = journalList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return journalList.size();
    }

    @Override
    public Object getItem(int position) {
        return journalList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_journal_entry, parent, false);

            viewHolder.journalTitle = (TextView) convertView.findViewById(R.id.journlTitle);
            viewHolder.entryDate = (TextView) convertView.findViewById(R.id.dateOfEntry);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.journalTitle.setText("Journal Entry No. " + (position + 1));
        viewHolder.entryDate.setText(journalList.get(position).getEntry_date());

        return convertView;
    }

    private class ViewHolder {
        TextView journalTitle;
        TextView entryDate;
    }
}

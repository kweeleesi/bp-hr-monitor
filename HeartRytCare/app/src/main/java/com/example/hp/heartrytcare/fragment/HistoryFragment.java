package com.example.hp.heartrytcare.fragment;

import android.app.DownloadManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.adapter.HrBpListAdapter;
import com.example.hp.heartrytcare.db.BloodPressureData;
import com.example.hp.heartrytcare.db.BloodPressureDataDao;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.HeartRateData;
import com.example.hp.heartrytcare.db.HeartRateDataDao;
import com.example.hp.heartrytcare.helper.Constants;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;

public class HistoryFragment extends Fragment implements View.OnClickListener{

    private View view;
    private ListView historyList;
    private Button hrBtn, bpBtn;
    private TextView emptyList;

    private HeartRateDataDao hrDao;
    private BloodPressureDataDao bpDao;
    private ArrayList<HeartRateData> hrList = new ArrayList<>();
    private ArrayList<BloodPressureData> bpList = new ArrayList<>();
    private HrBpListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);

        initializeFields();

        hrBtn.setOnClickListener(this);
        bpBtn.setOnClickListener(this);

        DaoSession daoSession = ((HeartRytCare) getActivity().getApplicationContext()).getDaoSession();
        hrDao = daoSession.getHeartRateDataDao();
        bpDao = daoSession.getBloodPressureDataDao();

        showHrHistory();

        return view;
    }

    private void initializeFields() {
        historyList = (ListView) view.findViewById(R.id.historyList);
        hrBtn = (Button) view.findViewById(R.id.hrBtn);
        bpBtn = (Button) view.findViewById(R.id.bpBtn);
        emptyList = (TextView) view.findViewById(R.id.empty_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hrBtn:
                showHrHistory();
                break;
            case R.id.bpBtn:
                showBpHistory();
                break;
        }
    }

    private void showHrHistory() {
        hrBtn.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.red_textcolor));
        hrBtn.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
        bpBtn.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.gray_text));
        bpBtn.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));

        QueryBuilder<HeartRateData> query = hrDao.queryBuilder();
        query.where(HeartRateDataDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
        hrList.clear();
        hrList.addAll(query.list());

        if (hrList != null && hrList.size() != 0) {
            adapter = new HrBpListAdapter(hrList, null, getActivity().getApplicationContext());
            adapter.notifyDataSetChanged();
            historyList.setAdapter(adapter);
            historyList.invalidate();
            emptyList.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.VISIBLE);
            emptyList.setText("Heart Rate history empty.");
        }
    }

    private void showBpHistory() {
        bpBtn.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.red_textcolor));
        bpBtn.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
        hrBtn.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.gray_text));
        hrBtn.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));

        QueryBuilder<BloodPressureData> query = bpDao.queryBuilder();
        query.where(BloodPressureDataDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
        bpList.clear();
        bpList.addAll(query.list());

        if (bpList != null && bpList.size() != 0) {
            adapter = new HrBpListAdapter(null, bpList, getActivity().getApplicationContext());
            adapter.notifyDataSetChanged();
            historyList.setAdapter(adapter);
            historyList.invalidate();
            emptyList.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.VISIBLE);
            emptyList.setText("Blood Pressure history empty.");
        }
    }
}

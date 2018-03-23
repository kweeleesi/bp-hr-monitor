package com.example.hp.heartrytcare.fragment;


import android.os.Bundle;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.HeartRateData;
import com.example.hp.heartrytcare.db.HeartRateDataDao;
import com.example.hp.heartrytcare.helper.Constants;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatFragment extends Fragment {

    private View view;
    private LineChart chart;
    private HeartRateDataDao hrDao;
    private ArrayList<HeartRateData> hrList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stat, container, false);

        chart = (LineChart) view.findViewById(R.id.chart);

        fetchHrData();

        return view;
    }

    private void fetchHrData() {
        DaoSession daoSession = ((HeartRytCare) getActivity().getApplicationContext()).getDaoSession();
        hrDao = daoSession.getHeartRateDataDao();
        QueryBuilder<HeartRateData> query = hrDao.queryBuilder();
        query.where(HeartRateDataDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
        hrList.clear();
        hrList.addAll(query.list());

        Log.e("StatFragment", "hrlist size : " + hrList.size());
    }
}
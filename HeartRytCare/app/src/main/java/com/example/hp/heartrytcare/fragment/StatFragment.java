package com.example.hp.heartrytcare.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.BloodPressureData;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.HeartRateData;
import com.example.hp.heartrytcare.db.HeartRateDataDao;
import com.example.hp.heartrytcare.helper.Constants;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatFragment extends Fragment {

    private LineChart chart;
    private HeartRateDataDao hrDao;
    private ArrayList<HeartRateData> hrList = new ArrayList<>();
    private ArrayList<BloodPressureData> bpList = new ArrayList<>();
    private long earliest, latest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat, container, false);

        chart = (LineChart) view.findViewById(R.id.chart);
        Button share = (Button) view.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chart.saveToGallery("Chart", 100);
                // TODO: 3/24/2018 open ang share fragment
            }
        });

        Button history = (Button) view.findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 3/25/2018 open history 
            }
        });

        //fetch data
        fetchHrData();
        fetchBpData();

        getEarliestAndLatestDate();

        drawChart();

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

    private void fetchBpData() {

    }

    private void getEarliestAndLatestDate() {
        if (hrList != null && bpList != null &&
                hrList.size() != 0 && bpList.size() != 0) {
            if (hrList.get(0).getTimestamp() < bpList.get(0).getTimestamp()) {
                earliest = hrList.get(0).getTimestamp();
            } else {
                earliest = bpList.get(0).getTimestamp();
            }
            if (hrList.get(hrList.size() - 1).getTimestamp() > bpList.get(bpList.size() - 1).getTimestamp()) {
                latest = hrList.get(hrList.size() - 1).getTimestamp();
            } else {
                latest = bpList.get(bpList.size() - 1).getTimestamp();
            }
        } else {
            if (hrList == null || hrList.size() == 0) {
                earliest = bpList.get(0).getTimestamp();
                latest = bpList.get(bpList.size() - 1).getTimestamp();
            }
            if (bpList == null || bpList.size() == 0) {
                earliest = hrList.get(0).getTimestamp();
                latest = hrList.get(hrList.size() - 1).getTimestamp();
            }
        }

        Log.e("StatFragment", "earliest : " + earliest + " | latest : " + latest);
    }

    private void drawChart() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(earliest);

        Calendar lastDate = Calendar.getInstance();
        lastDate.setTimeInMillis(latest);
        String ld = new SimpleDateFormat("MM/dd").format(lastDate.getTime());
        Log.e("StatFragment", "!!!!!!!!!!!! ld : " + ld);

        String currDate = "";
        List<String> dates = new ArrayList<>();
        int day = 0;
        do {
            date.add(Calendar.DATE, day);
            currDate = new SimpleDateFormat("MM/dd").format(date.getTime());
            Log.e("StatFragment", "!!!!!!!! date : " + currDate);
            dates.add(currDate);
            day++;
        } while (!ld.equals(currDate));

       /*  int x = 0;
        if(hrList != null && hrList.size() != 0) {
            List<Entry> hrEntries = new ArrayList<>();
            List<Entry> systolicEntries = new ArrayList<>();
            List<Entry> diastolicEntries = new ArrayList<>();

            for (HeartRateData hr : hrList) {
                hrEntries.add(new Entry(x++, hr.getBpm()));
            }

//            temp systolic data
            systolicEntries.add(new Entry(0, 120));
            systolicEntries.add(new Entry(1, 110));
            systolicEntries.add(new Entry(2, 0));
            systolicEntries.add(new Entry(3, 115));

//            temp diastolic data
            diastolicEntries.add(new Entry(0, 80));
            diastolicEntries.add(new Entry(1, 90));
            diastolicEntries.add(new Entry(2, 100));
//            diastolicEntries.add(new Entry(3, 70));

            LineDataSet hrDataSet = new LineDataSet(hrEntries, "Heart Rate (bpm)");
            hrDataSet.setColor(Color.RED);
            hrDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

            LineDataSet sysDataSet = new LineDataSet(systolicEntries, "SBP");
            sysDataSet.setColor(Color.BLUE);
            sysDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            LineDataSet diasDataSet = new LineDataSet(diastolicEntries, "DBP");
            diasDataSet.setColor(Color.GREEN);
            diasDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            List<ILineDataSet> datasets = new ArrayList<>();
            datasets.add(hrDataSet);
            datasets.add(sysDataSet);
            datasets.add(diasDataSet);

            Description description = new Description();
            description.setText(" ");

            LineData lineData = new LineData(datasets);
            chart.setData(lineData);
            chart.setGridBackgroundColor(Color.WHITE);
            chart.setNoDataText("No Data");
            chart.setDescription(description);
            chart.invalidate();

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return hrList.get((int) value).getDate();
                }
            };

            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            YAxis yAxis = chart.getAxis(YAxis.AxisDependency.RIGHT);
            yAxis.setTextColor(Color.RED);
        }*/
    }
}
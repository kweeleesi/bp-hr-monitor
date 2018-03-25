package com.example.hp.heartrytcare.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.DynamicLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.Manifest;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.BloodPressureData;
import com.example.hp.heartrytcare.db.BloodPressureDataDao;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.HeartRateData;
import com.example.hp.heartrytcare.db.HeartRateDataDao;
import com.example.hp.heartrytcare.helper.Constants;
import com.example.hp.heartrytcare.helper.DailyRecord;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

import de.greenrobot.dao.query.QueryBuilder;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatFragment extends Fragment {

    private final String TAG = "StatFragment";

    private LineChart chart;
    private HeartRateDataDao hrDao;
    private BloodPressureDataDao bpDao;
    private ArrayList<HeartRateData> hrList = new ArrayList<>();
    private DaoSession daoSession;
    private ArrayList<BloodPressureData> bpList = new ArrayList<>();
    private long earliest, latest;
    private List<String> dates = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat, container, false);
        daoSession = ((HeartRytCare) getActivity().getApplicationContext()).getDaoSession();
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
        hrDao = daoSession.getHeartRateDataDao();
        QueryBuilder<HeartRateData> query = hrDao.queryBuilder();
        query.where(HeartRateDataDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
        hrList.clear();
        hrList.addAll(query.list());

        Log.e(TAG, "hrlist size : " + hrList.size());
    }

    private void fetchBpData() {
        bpDao = daoSession.getBloodPressureDataDao();
        QueryBuilder<BloodPressureData> query = bpDao.queryBuilder();
        query.where(BloodPressureDataDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
        bpList.clear();
        bpList.addAll(query.list());

        Log.e(TAG, "bplist size : " + bpList.size());
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
        } /*else {
            if (hrList == null || hrList.size() == 0) {
                earliest = bpList.get(0).getTimestamp();
                latest = bpList.get(bpList.size() - 1).getTimestamp();
            }
            if (bpList == null || bpList.size() == 0) {
                earliest = hrList.get(0).getTimestamp();
                latest = hrList.get(hrList.size() - 1).getTimestamp();
            }
        }*/

        Log.e(TAG, "earliest : " + earliest + " | latest : " + latest);
    }

    private void drawChart() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(earliest);

        Calendar lastDate = Calendar.getInstance();
        lastDate.setTimeInMillis(latest);
        lastDate.add(Calendar.DATE, 1);
        String ld = new SimpleDateFormat("MM/dd").format(lastDate.getTime());

        String currDate = "";
        //add one more date before
        date.add(Calendar.DATE, -1);
        dates.add(new SimpleDateFormat("MM/dd").format(date.getTime()));
        date.add(Calendar.DATE, 1);
        int day = 0;
        do {
            date.add(Calendar.DATE, day);
            currDate = new SimpleDateFormat("MM/dd").format(date.getTime());
            dates.add(currDate);
            day++;
        } while (!ld.equals(currDate));

        for (String d : dates) {
            Log.e(TAG, "dates = " + d);
        }

        int x = 0;
        if (dates.size() != 0) {
            List<Entry> hrEntries = new ArrayList<>();
            List<Entry> systolicEntries = new ArrayList<>();
            List<Entry> diastolicEntries = new ArrayList<>();

            LinkedHashMap<String, DailyRecord> recordMap = new LinkedHashMap<>();
            int tempHr, tempSys, tempDia;
            String prevDateHr, prevDateBp;
            for (String d : dates) {
                DailyRecord dailyRecord = new DailyRecord();
                prevDateHr = d;
                prevDateBp = d;
                tempHr = -1;
                tempSys = -1;
                tempDia = -1;
                for (HeartRateData hr : hrList) {
                    if (d.equals(hr.getDate())) {
                        if (prevDateHr.equals(hr.getDate())) {
                            tempHr = hr.getBpm();
                        }
                        prevDateHr = d;
                    }
                }
                dailyRecord.hr = tempHr;
                Log.e(TAG, "daily record hr : " + d + " | " + dailyRecord.hr);

                for (BloodPressureData bp : bpList) {
                    if (d.equals(bp.getDate())) {
                        if (prevDateBp.equals(bp.getDate())) {
                            tempSys = bp.getSystolic();
                            tempDia = bp.getDiastolic();
                        }
                        prevDateBp = d;
                    }
                }
                dailyRecord.sys = tempSys;
                dailyRecord.dia = tempDia;
                dailyRecord.index = x;
                x += 1;
                Log.e(TAG, "daily record dia : + " + d + " | " + dailyRecord.sys + "/" + dailyRecord.dia);
                recordMap.put(d, dailyRecord);
            }

            for (LinkedHashMap.Entry<String, DailyRecord> map : recordMap.entrySet()) {
                Log.e(TAG, "!!!!!! hello : " + map.getKey() + " | " + map.getValue().hr
                        + " | " + map.getValue().sys + " | " + map.getValue().dia + " | " + map.getValue().index);

                if (map.getValue().hr != -1) {
                    Log.e(TAG, "here" + map.getValue().index);
                    hrEntries.add(new Entry(map.getValue().index, map.getValue().hr));
                }

                if (map.getValue().sys != -1 && map.getValue().dia != -1) {
                    Log.e(TAG, "here sd");
                    systolicEntries.add(new Entry(map.getValue().index, map.getValue().sys));
                    diastolicEntries.add(new Entry(map.getValue().index, map.getValue().dia));
                }

            }

            final List<ILineDataSet> datasets = new ArrayList<>();

            if (hrEntries.size() != 0) {
                LineDataSet hrDataSet = new LineDataSet(hrEntries, "Heart Rate (bpm)");
                hrDataSet.setColor(Color.RED);
                hrDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                datasets.add(hrDataSet);
            }

            if (systolicEntries.size() != 0 && diastolicEntries.size() != 0) {
                LineDataSet sysDataSet = new LineDataSet(systolicEntries, "SBP");
                sysDataSet.setColor(Color.BLUE);
                sysDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                LineDataSet diasDataSet = new LineDataSet(diastolicEntries, "DBP");
                diasDataSet.setColor(Color.GREEN);
                diasDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                datasets.add(sysDataSet);
                datasets.add(diasDataSet);
            }

            if (hrEntries.size() != 0 || systolicEntries.size() != 0 || diastolicEntries.size() != 0) {
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
                        return dates.get((int) value);
                    }
                };

                XAxis xAxis = chart.getXAxis();
                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                xAxis.setValueFormatter(formatter);

                YAxis yAxis = chart.getAxis(YAxis.AxisDependency.RIGHT);
                yAxis.setTextColor(Color.RED);
            }
        }
    }
}
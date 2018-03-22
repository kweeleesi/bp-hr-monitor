package com.example.hp.heartrytcare.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.adapter.AppointmentsAdapter;
import com.example.hp.heartrytcare.db.Appointment;
import com.example.hp.heartrytcare.db.AppointmentDao;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.fragment.SchedAppointment;
import com.example.hp.heartrytcare.helper.Constants;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;

public class AppointmentActivity extends AppCompatActivity {

    private ListView apptList;
    private Button addAppt;
    private TextView emptyList;
    private AppointmentDao appointmentDao;
    private ArrayList<Appointment> appointments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_entry);

        apptList = (ListView) findViewById(R.id.list);
        addAppt = (Button) findViewById(R.id.addEntry);
        emptyList = (TextView) findViewById(R.id.empty_list);

        addAppt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new SchedAppointment());
                transaction.addToBackStack("");
                transaction.commit();
            }
        });

        fetchAppointment();
        if (appointments != null && appointments.size() != 0) {
            emptyList.setVisibility(View.GONE);
        }

        apptList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                SchedAppointment fragment = new SchedAppointment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack("");
                transaction.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        fetchAppointment();
        super.onBackPressed();
    }

    private void fetchAppointment() {
        DaoSession daoSession = ((HeartRytCare) getApplication()).getDaoSession();
        appointmentDao = daoSession.getAppointmentDao();
        QueryBuilder<Appointment> query = appointmentDao.queryBuilder();
        query.where(AppointmentDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
        appointments.clear();
        appointments.addAll(query.list());

        AppointmentsAdapter adapter = new AppointmentsAdapter(appointments, getApplicationContext());
        apptList.setAdapter(adapter);
        apptList.invalidate();

        Log.e("AppointmentActivity", "appointment size : " + appointments.size());
    }
}

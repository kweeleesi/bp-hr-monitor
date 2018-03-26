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
import com.example.hp.heartrytcare.adapter.MedicationSchedAdapter;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.Medication;
import com.example.hp.heartrytcare.db.MedicationDao;
import com.example.hp.heartrytcare.fragment.ScheduleMed;
import com.example.hp.heartrytcare.helper.Constants;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;

public class MedicationActivity extends AppCompatActivity {

    private ListView medList;
    private Button addEntry;
    private TextView emptyList;
    private MedicationDao medicationDao;
    private ArrayList<Medication> sched = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_entry);
        setTitle("Schedule");

        initializeFields();

        addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new ScheduleMed());
                transaction.addToBackStack("");
                transaction.commit();
            }
        });

        fetchMedicationSched();

        medList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                ScheduleMed fragment = new ScheduleMed();
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
        fetchMedicationSched();
        super.onBackPressed();
    }

    private void initializeFields() {
        medList = (ListView) findViewById(R.id.list);
        addEntry = (Button) findViewById(R.id.addEntry);
        emptyList = (TextView) findViewById(R.id.empty_list);
    }

    private void fetchMedicationSched() {
        DaoSession daoSession = ((HeartRytCare) getApplication()).getDaoSession();
        medicationDao = daoSession.getMedicationDao();
        QueryBuilder<Medication> queryMedSched = medicationDao.queryBuilder();
        queryMedSched.where(MedicationDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
        sched.clear();
        if (queryMedSched.list() != null && queryMedSched.list().size() != 0) {
            sched.addAll(queryMedSched.list());
        }

        MedicationSchedAdapter adapter = new MedicationSchedAdapter(sched, getApplicationContext());
        medList.setAdapter(adapter);
        medList.invalidate();

        if (sched != null && sched.size() != 0) {
            emptyList.setVisibility(View.GONE);
        }

        Log.e("MedicationSchedAdapter", "med sched size : " + sched.size());
    }
}

package com.example.hp.heartrytcare.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.adapter.JournalListAdapter;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.Journal;
import com.example.hp.heartrytcare.db.JournalDao;
import com.example.hp.heartrytcare.fragment.AddJournalFragment;
import com.example.hp.heartrytcare.helper.Constants;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;

public class JournalActivity extends AppCompatActivity {

    private ListView journalList;
    private Button addJournal;
    private TextView emptyList;
    private JournalDao journalDao;
    private ArrayList<Journal> journals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_entry);
        setTitle("Journal");

        journalList = (ListView) findViewById(R.id.list);
        addJournal = (Button) findViewById(R.id.addEntry);
        emptyList = (TextView) findViewById(R.id.empty_list);

        addJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new AddJournalFragment());
                transaction.addToBackStack("");
                transaction.commit();
            }
        });

        fetchJournalEntries();

        journalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                AddJournalFragment fragment = new AddJournalFragment();
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
        fetchJournalEntries();
        super.onBackPressed();
    }

    private void fetchJournalEntries() {
        DaoSession daoSession = ((HeartRytCare) getApplication()).getDaoSession();
        journalDao = daoSession.getJournalDao();
        QueryBuilder<Journal> queryJournal = journalDao.queryBuilder();
        queryJournal.where(JournalDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
        journals.clear();
        journals.addAll(queryJournal.list());

        JournalListAdapter adapter = new JournalListAdapter(journals, getApplicationContext());
        journalList.setAdapter(adapter);
        journalList.invalidate();

        if (journals != null && journals.size() != 0) {
            emptyList.setVisibility(View.GONE);
        }

        Log.e("JournalActivity", "journal size : " + journals.size());
    }
}

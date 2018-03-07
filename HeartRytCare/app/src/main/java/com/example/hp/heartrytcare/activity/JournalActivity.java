package com.example.hp.heartrytcare.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.fragment.AddJournalFragment;

public class JournalActivity extends AppCompatActivity {

    private ListView journalList;
    private ImageView addJournal;
    private TextView emptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        journalList = (ListView) findViewById(R.id.journal_list);
        addJournal = (ImageView) findViewById(R.id.addJournalEntry);
        emptyList = (TextView) findViewById(R.id.empty_list);

        addJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.addJournalContainer, new AddJournalFragment());
                transaction.addToBackStack("");
                transaction.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

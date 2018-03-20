package com.example.hp.heartrytcare.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.Journal;
import com.example.hp.heartrytcare.db.JournalDao;
import com.example.hp.heartrytcare.helper.Constants;

import java.util.Date;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddJournalFragment extends Fragment implements View.OnClickListener{

    private View view;
    private EditText meals, heartrate, systolic, diastolic, temperature, weight, medicineName,
                dosage, numberOfMed, howOften, notes;
    private Button save, cancel, update;
    private JournalDao journalDao;
    private Journal journal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_journal, container, false);

        initializeFields();

        DaoSession daoSession = ((HeartRytCare) getActivity().getApplication()).getDaoSession();
        journalDao = daoSession.getJournalDao();

        Bundle bundle = getArguments();
        if (bundle != null) {
            QueryBuilder<Journal> queryJournal = journalDao.queryBuilder();
            queryJournal.where(JournalDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
            journal = queryJournal.list().get(bundle.getInt("position", 0));

            meals.setText(journal.getMeals_taken());
            heartrate.setText(journal.getHeart_rate());
            systolic.setText(journal.getSystolic());
            diastolic.setText(journal.getDiastolic());
            temperature.setText(journal.getTemperature());
            weight.setText(journal.getWeight());
            medicineName.setText(journal.getMedicine_name());
            dosage.setText(journal.getDosage());
            numberOfMed.setText(journal.getPieces());
            howOften.setText(journal.getHow_often());
            notes.setText(journal.getNotes());

            update.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
        }

        return view;
    }

    private void initializeFields() {
        meals = (EditText) view.findViewById(R.id.et_meals);
        heartrate = (EditText) view.findViewById(R.id.et_heartrate);
        systolic = (EditText) view.findViewById(R.id.et_systolic);
        diastolic = (EditText) view.findViewById(R.id.et_diastolic);
        temperature = (EditText) view.findViewById(R.id.et_temp);
        weight = (EditText) view.findViewById(R.id.et_weight);
        medicineName = (EditText) view.findViewById(R.id.et_medname);
        dosage = (EditText) view.findViewById(R.id.et_dosage);
        numberOfMed = (EditText) view.findViewById(R.id.et_numberofmed);
        howOften = (EditText) view.findViewById(R.id.howOften);
        notes = (EditText) view.findViewById(R.id.et_othernotes);
        save = (Button) view.findViewById(R.id.btn_journalsave);
        cancel = (Button) view.findViewById(R.id.btn_journalcancel);
        update = (Button) view.findViewById(R.id.btn_journalupdate);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        update.setOnClickListener(this);
    }

    private void saveJournal() {
        try {
            Date d = new Date();
            CharSequence date  = DateFormat.format("EEEE, MMMM d, yyyy\n hh:mm aa", d.getTime());

            Journal journal = new Journal();
            journal.setFirebase_user_id(Constants.FIREBASE_UID);
            journal.setMeals_taken(meals.getText().toString());
            journal.setHeart_rate(heartrate.getText().toString());
            journal.setSystolic(systolic.getText().toString());
            journal.setDiastolic(diastolic.getText().toString());
            journal.setTemperature(temperature.getText().toString());
            journal.setWeight(weight.getText().toString());
            journal.setMedicine_name(medicineName.getText().toString());
            journal.setDosage(dosage.getText().toString());
            journal.setPieces(numberOfMed.getText().toString());
            journal.setHow_often(howOften.getText().toString());
            journal.setNotes(notes.getText().toString());
            journal.setEntry_date(date.toString());
            journalDao.insert(journal);

            Toast.makeText(getActivity(), "Journal Entry Saved.", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    private void updateJournal() {
        Date d = new Date();
        CharSequence date  = DateFormat.format("EEEE, MMMM d, yyyy\n hh:mm aa", d.getTime());

        journal.setMeals_taken(meals.getText().toString());
        journal.setHeart_rate(heartrate.getText().toString());
        journal.setSystolic(systolic.getText().toString());
        journal.setDiastolic(diastolic.getText().toString());
        journal.setTemperature(temperature.getText().toString());
        journal.setWeight(weight.getText().toString());
        journal.setMedicine_name(medicineName.getText().toString());
        journal.setDosage(dosage.getText().toString());
        journal.setPieces(numberOfMed.getText().toString());
        journal.setHow_often(howOften.getText().toString());
        journal.setNotes(notes.getText().toString());
        journal.setEntry_date("Updated at:\n" + date.toString());
        journalDao.insertOrReplace(journal);

        Toast.makeText(getActivity(), "Journal Entry Updated.", Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_journalsave:
                saveJournal();
                break;
            case R.id.btn_journalcancel:
                getActivity().onBackPressed();
                break;
            case R.id.btn_journalupdate:
                updateJournal();
                break;
        }
    }
}

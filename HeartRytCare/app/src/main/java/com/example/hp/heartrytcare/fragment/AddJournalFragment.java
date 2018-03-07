package com.example.hp.heartrytcare.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import de.greenrobot.dao.DaoException;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddJournalFragment extends Fragment implements View.OnClickListener{

    private View view;
    private EditText meals, heartrate, systolic, diastolic, temperature, weight, medicineName,
                dosage, numberOfMed, howOften, notes;
    private Button save, cancel;
    private JournalDao journalDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_journal, container, false);

        initializeFields();

        DaoSession daoSession = ((HeartRytCare) getActivity().getApplication()).getDaoSession();
        journalDao = daoSession.getJournalDao();

        return view;
    }

    private void initializeFields() {
        meals = (EditText) view.findViewById(R.id.et_meals);
        heartrate = (EditText) view.findViewById(R.id.heartRate);
        systolic = (EditText) view.findViewById(R.id.et_systolic);
        diastolic = (EditText) view.findViewById(R.id.et_diastolic);
        temperature = (EditText) view.findViewById(R.id.et_temp);
        weight = (EditText) view.findViewById(R.id.weight);
        medicineName = (EditText) view.findViewById(R.id.et_medname);
        dosage = (EditText) view.findViewById(R.id.et_dosage);
        numberOfMed = (EditText) view.findViewById(R.id.et_numberofmed);
        howOften = (EditText) view.findViewById(R.id.howOften);
        notes = (EditText) view.findViewById(R.id.et_othernotes);
        save = (Button) view.findViewById(R.id.btn_journalsave);
        cancel = (Button) view.findViewById(R.id.btn_journalcancel);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void saveJournal() {
        try {
            Journal journal = new Journal();
            journal.setMeals_taken(meals.getText().toString());
            journal.setHeart_rate(Integer.parseInt(heartrate.getText().toString()));
            journal.setSystolic(systolic.getText().toString());
            journal.setDiastolic(diastolic.getText().toString());
            journal.setTemperature(Integer.parseInt(temperature.getText().toString()));
            journal.setWeight(Double.parseDouble(weight.getText().toString()));
            journal.setMedicine_name(medicineName.getText().toString());
            journal.setDosage(dosage.getText().toString());
            journal.setPieces(numberOfMed.getText().toString());
            journal.setHow_often(howOften.getText().toString());
            journal.setNotes(notes.getText().toString());
            journalDao.insert(journal);

            Toast.makeText(getActivity(), "Journal Entry Saved.", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } catch (DaoException e) {
            e.printStackTrace();
        }
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
        }
    }
}

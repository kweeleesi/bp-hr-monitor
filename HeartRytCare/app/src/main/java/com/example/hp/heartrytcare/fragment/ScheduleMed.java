package com.example.hp.heartrytcare.fragment;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.media.RemoteController;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.Journal;
import com.example.hp.heartrytcare.db.JournalDao;
import com.example.hp.heartrytcare.db.Medication;
import com.example.hp.heartrytcare.db.MedicationDao;
import com.example.hp.heartrytcare.helper.Constants;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleMed extends Fragment {

    private static final String TAG = "ScheduleMed";

    private View view;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ImageView back;
    private Button save, update;
    private MedicationDao medicationDao;
    private EditText name, dosage, pieces, method, howOften, duration;
    private TextView startDate, medTime;
    private Switch alert;
    private Medication medication;

    public ScheduleMed() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule_med, container, false);

        initializeFields();

        DaoSession daoSession = ((HeartRytCare)getActivity().getApplication()).getDaoSession();
        medicationDao = daoSession.getMedicationDao();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        month, day, year);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            QueryBuilder<Medication> queryMedSched = medicationDao.queryBuilder();
            queryMedSched.where(MedicationDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
            medication = queryMedSched.list().get(bundle.getInt("position", 0));

            name.setText(medication.getNameOfMed());
            dosage.setText(medication.getDosage());
            pieces.setText(medication.getNumberOfMed());
            method.setText(medication.getMethod());
            howOften.setText(medication.getHowOften());
            duration.setText(medication.getDuration());
            medTime.setText(medication.getTime());
            startDate.setText(medication.getStartDate());

            update.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
        }

        return view;
    }

    private void initializeFields() {
        back = (ImageView) view.findViewById(R.id.img_arrowback);
        mDisplayDate = (TextView) view.findViewById(R.id.tv_scheddate);
        save = (Button) view.findViewById(R.id.addSched);
        update = (Button) view.findViewById(R.id.updateSched);
        name = (EditText) view.findViewById(R.id.et_medname);
        dosage = (EditText) view.findViewById(R.id.et_dosage);
        pieces = (EditText) view.findViewById(R.id.et_number);
        method = (EditText) view.findViewById(R.id.et_taken);
        howOften = (EditText) view.findViewById(R.id.et_times);
        duration = (EditText) view.findViewById(R.id.et_numofdays);
        medTime = (TextView) view.findViewById(R.id.et_settime);
        startDate = (TextView) view.findViewById(R.id.tv_scheddate);
        alert = (Switch) view.findViewById(R.id.alertSwitch);
    }

    private void save() {
        Medication med = new Medication();
        med.setFirebase_user_id(Constants.FIREBASE_UID);
        med.setNameOfMed(name.getText().toString());
        med.setDosage(dosage.getText().toString());
        med.setNumberOfMed(pieces.getText().toString());
        med.setMethod(method.getText().toString());
        med.setHowOften(howOften.getText().toString());
        med.setTime(medTime.getText().toString());
        med.setStartDate(startDate.getText().toString());
        med.setDuration(duration.getText().toString());
        med.setAlert(alert.isChecked());
        medicationDao.insert(med);

        Toast.makeText(getActivity(), "Schedule successfully added.", Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    private void update() {
        medication.setFirebase_user_id(Constants.FIREBASE_UID);
        medication.setNameOfMed(name.getText().toString());
        medication.setDosage(dosage.getText().toString());
        medication.setNumberOfMed(pieces.getText().toString());
        medication.setMethod(method.getText().toString());
        medication.setHowOften(howOften.getText().toString());
        medication.setTime(medTime.getText().toString());
        medication.setStartDate(startDate.getText().toString());
        medication.setDuration(duration.getText().toString());
        medication.setAlert(alert.isChecked());
        medicationDao.insertOrReplace(medication);

        Toast.makeText(getActivity(), "Schedule successfully updated.", Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }
}
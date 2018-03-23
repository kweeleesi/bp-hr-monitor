package com.example.hp.heartrytcare.fragment;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.activity.AlarmActivity;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.Medication;
import com.example.hp.heartrytcare.db.MedicationDao;
import com.example.hp.heartrytcare.helper.Constants;

import java.util.Calendar;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleMed extends Fragment implements View.OnClickListener{

    private static final String TAG = "ScheduleMed";

    private View view;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private ImageView back;
    private Button save, update;
    private MedicationDao medicationDao;
    private EditText name, dosage, pieces, method, howOften, duration;
    private TextView startDate, medTime;
    private Switch alert;
    private Medication medication;
    private AlarmManager alarm;
    private PendingIntent alarmIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule_med, container, false);

        initializeFields();

        DaoSession daoSession = ((HeartRytCare)getActivity().getApplication()).getDaoSession();
        medicationDao = daoSession.getMedicationDao();

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

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                startDate.setText(date);
            }
        };

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d(TAG, "onTimeSet: hh:mm: " + hourOfDay + ":" + minute);

                String time = hourOfDay + ":" + minute;
                medTime.setText(time);
            }
        };

        return view;
    }

    private void initializeFields() {
        back = (ImageView) view.findViewById(R.id.img_arrowback);
        startDate = (TextView) view.findViewById(R.id.tv_scheddate);
        save = (Button) view.findViewById(R.id.addSched);
        update = (Button) view.findViewById(R.id.updateSched);
        name = (EditText) view.findViewById(R.id.et_medname);
        dosage = (EditText) view.findViewById(R.id.et_dosage);
        pieces = (EditText) view.findViewById(R.id.et_number);
        method = (EditText) view.findViewById(R.id.et_taken);
        howOften = (EditText) view.findViewById(R.id.et_times);
        duration = (EditText) view.findViewById(R.id.et_numofdays);
        medTime = (TextView) view.findViewById(R.id.et_settime);
        alert = (Switch) view.findViewById(R.id.alertSwitch);

        back.setOnClickListener(this);
        save.setOnClickListener(this);
        update.setOnClickListener(this);
        startDate.setOnClickListener(this);
        medTime.setOnClickListener(this);
        alert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Set the alarm to start at approximately 2:00 p.m.
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, 12);
                    calendar.set(Calendar.MINUTE, 23);

                    // With setInexactRepeating(), you have to use one of the AlarmManager interval
                    // constants--in this case, AlarmManager.INTERVAL_DAY.
                    alarm = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getActivity(), AlarmActivity.class);
                    alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                    alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, alarmIntent);

                    Toast.makeText(getActivity(), "SUCCESSFUL", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        switch (v.getId()) {
            case R.id.img_arrowback:
                getActivity().onBackPressed();
                break;
            case R.id.tv_scheddate:
                DatePickerDialog datePicker = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        month, day, year);
                datePicker.updateDate(year, month, day);
                datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePicker.show();
                break;
            case R.id.et_settime:
                TimePickerDialog timePicker = new TimePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListener,
                        hour, minute, false);
                timePicker.updateTime(hour, minute);
                timePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePicker.show();
                break;
            case R.id.addSched:
                save();
                break;
            case R.id.updateSched:
                update();
                break;
        }
    }
}
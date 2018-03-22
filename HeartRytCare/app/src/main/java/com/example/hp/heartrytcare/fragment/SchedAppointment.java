package com.example.hp.heartrytcare.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.Appointment;
import com.example.hp.heartrytcare.db.AppointmentDao;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.helper.Constants;

import java.util.Calendar;

import de.greenrobot.dao.query.QueryBuilder;


/**
 * A simple {@link Fragment} subclass.
 */
public class SchedAppointment extends Fragment implements View.OnClickListener{

    private final String TAG = getClass().getSimpleName();

    private View view;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private ImageView back;
    private EditText header, notes;
    private TextView time, date;
    private Button save, update;
    private AppointmentDao appointmentDao;
    private Appointment appointment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sched_appointment, container, false);

        initializeFields();

        DaoSession daoSession = ((HeartRytCare) getActivity().getApplication()).getDaoSession();
        appointmentDao = daoSession.getAppointmentDao();

        Bundle bundle = getArguments();
        if (bundle != null) {
            QueryBuilder<Appointment> query = appointmentDao.queryBuilder();
            query.where(AppointmentDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
            appointment = query.list().get(bundle.getInt("position", 0));

            header.setText(appointment.getHeader());
            notes.setText(appointment.getNotes());
            time.setText(appointment.getTime());
            date.setText(appointment.getDate());

            update.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
        }

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyyy: " + month + "/" + day + "/" + year);

                String d = month + "/" + day + "/" + year;
                date.setText(d);
            }
        };

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d(TAG, "onTimeSet: hh:mm: " + hourOfDay + ":" + minute);

                String t = hourOfDay + ":" + minute;
                time.setText(t);
            }
        };

        return view;
    }

    private void initializeFields()  {
        back = (ImageView) view.findViewById(R.id.img_arrowback);
        header = (EditText) view.findViewById(R.id.et_header);
        notes = (EditText) view.findViewById(R.id.et_notes);
        time = (TextView) view.findViewById(R.id.et_time);
        date = (TextView) view.findViewById(R.id.et_date);
        save = (Button) view.findViewById(R.id.addSched);
        update = (Button) view.findViewById(R.id.updateSched);

        back.setOnClickListener(this);
        time.setOnClickListener(this);
        date.setOnClickListener(this);
        save.setOnClickListener(this);
        update.setOnClickListener(this);
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
            case R.id.et_date:
                DatePickerDialog datePicker = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        month, day, year);
                datePicker.updateDate(year, month, day);
                datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePicker.show();
                break;
            case R.id.et_time:
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

    private void save() {
        Appointment appt = new Appointment();
        appt.setFirebase_user_id(Constants.FIREBASE_UID);
        appt.setHeader(header.getText().toString());
        appt.setNotes(notes.getText().toString());
        appt.setTime(time.getText().toString());
        appt.setDate(date.getText().toString());
        appointmentDao.insert(appt);

        Toast.makeText(getActivity(), "Appointment saved.", Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    private void update() {
        appointment.setFirebase_user_id(Constants.FIREBASE_UID);
        appointment.setHeader(header.getText().toString());
        appointment.setNotes(notes.getText().toString());
        appointment.setTime(time.getText().toString());
        appointment.setDate(date.getText().toString());
        appointmentDao.insertOrReplace(appointment);

        Toast.makeText(getActivity(), "Appointment updated.", Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }
}
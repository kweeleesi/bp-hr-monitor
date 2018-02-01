package com.example.hp.heartrytcare.fragment;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.DoctorCode;
import com.example.hp.heartrytcare.db.DoctorCodeDao;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

public class PatientFragment extends Fragment implements View.OnClickListener{

    private View view;
    private Button addPatient;
    private Button sendCode;
    private Button cancelSend;
    private Dialog dialog;
    private EditText contactNumber;
    private DoctorCodeDao doctorCodeDao;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_patient, container, false);

        mAuth = FirebaseAuth.getInstance();
        DaoSession daoSession = ((HeartRytCare) getActivity().getApplication()).getDaoSession();
        doctorCodeDao = daoSession.getDoctorCodeDao();

        initializeFields();

        return view;
    }

    private void initializeFields() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_send_code);
        dialog.setCancelable(false);
        addPatient = (Button) view.findViewById(R.id.addPatient);
        sendCode = (Button) dialog.findViewById(R.id.sendCode);
        cancelSend = (Button) dialog.findViewById(R.id.cancelSend);
        contactNumber = (EditText) dialog.findViewById(R.id.contactNumber);

        addPatient.setOnClickListener(this);
        sendCode.setOnClickListener(this);
        cancelSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPatient:
                dialog.show();
                break;
            case R.id.sendCode:
                send();
                break;
            case R.id.cancelSend:
                dialog.dismiss();
                break;
        }
    }

    private void send() {
        if (TextUtils.isEmpty(contactNumber.getText())) {
            contactNumber.setError("This item cannot be empty");
            return;
        }

        Random rand = new Random();
        int randNo = rand.nextInt(10000);
        String msg = ("Your verification code is ").concat(String.format("%04d%n", randNo));

        try {
            PendingIntent pi = PendingIntent.getActivity(getActivity(), 0,
                    new Intent(getActivity(), DoctorFragment.class), 0);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(contactNumber.getText().toString(), null, msg, pi, null);
            // TODO: 1/30/2018 handle error codes | check number validity
            dialog.dismiss();
            Toast.makeText(getActivity(), "Message Sent", Toast.LENGTH_SHORT).show();

            DoctorCode doctorCode = new DoctorCode();
            doctorCode.setDoctor_code(randNo);
            doctorCodeDao.insert(doctorCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
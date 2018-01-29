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

import com.example.hp.heartrytcare.R;

public class DoctorFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Button addPatient;
    private Button sendCode;
    private Button cancelSend;
    private Dialog dialog;
    private EditText contactNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_doctor, container, false);

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
                verify();
                break;
            case R.id.cancelSend:
                dialog.dismiss();
                break;
        }
    }

    private void verify() {
        if (TextUtils.isEmpty(contactNumber.getText())) {
            contactNumber.setError("This item cannot be empty");
            return;
        }

        PendingIntent pi = PendingIntent.getActivity(getActivity(), 0,
                new Intent(getActivity(), DoctorFragment.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(contactNumber.getText().toString(), null, "Test", pi, null);
        // TODO: 1/30/2018 handle error codes | check number validity
        dialog.dismiss();
    }
}

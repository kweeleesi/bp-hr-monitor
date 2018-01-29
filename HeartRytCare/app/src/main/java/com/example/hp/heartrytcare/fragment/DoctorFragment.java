package com.example.hp.heartrytcare.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hp.heartrytcare.R;

public class DoctorFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Button addDoctor;
    private Button verifyCode;
    private Button cancelVerification;
    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_doctor, container, false);

        initializeFields();

        return view;
    }

    private void initializeFields() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_verify_code);
        dialog.setCancelable(false);
        addDoctor = (Button) view.findViewById(R.id.addDoctor);
        verifyCode = (Button) dialog.findViewById(R.id.verifyCode);
        cancelVerification = (Button) dialog.findViewById(R.id.cancelVerification);

        addDoctor.setOnClickListener(this);
        verifyCode.setOnClickListener(this);
        cancelVerification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addDoctor:
                dialog.show();
                break;
            case R.id.verifyCode:
                verify();
                break;
            case R.id.cancelVerification:
                dialog.dismiss();
                break;
        }
    }

    private void verify() {

    }

}

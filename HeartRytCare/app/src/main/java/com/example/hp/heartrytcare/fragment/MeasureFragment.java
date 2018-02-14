package com.example.hp.heartrytcare.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.activity.HeartRateMonitor;
import com.example.hp.heartrytcare.fragment.BloodPressureFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeasureFragment extends Fragment implements View.OnClickListener {

    private View view;
    private RelativeLayout heartRate, bloodPressure;
    private AlertDialog.Builder dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_measure, container, false);

        heartRate = (RelativeLayout) view.findViewById(R.id.heartRate);
        bloodPressure = (RelativeLayout) view.findViewById(R.id.bloodPressure);

        heartRate.setOnClickListener(this);
        bloodPressure.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Fragment fragment;
        switch (view.getId()) {
            case R.id.heartRate:
                dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(R.string.heart_rate_desc);
                dialog.setMessage(R.string.instruction);
                dialog.setPositiveButton("START", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getActivity(), HeartRateMonitor.class);
                        getActivity().startActivity(i);
                    }
                });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.create();
                dialog.show();
                break;

            case R.id.bloodPressure:
                fragment = new BloodPressureFragment();
                replaceFragment(fragment);
                break;

        }
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.relativeLayout_for_fragment, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

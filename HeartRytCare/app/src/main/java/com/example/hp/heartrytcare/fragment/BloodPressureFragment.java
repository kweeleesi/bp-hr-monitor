package com.example.hp.heartrytcare.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hp.heartrytcare.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class BloodPressureFragment extends Fragment {


    public BloodPressureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View FragmentView = inflater.inflate(R.layout.fragment_blood_pressure, container, false);

        ImageView back = (ImageView) FragmentView.findViewById(R.id.img_arrowback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasureFragment measureFragment = new MeasureFragment();
                // ScheduleMed scheduleMed = new ScheduleMed();

                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.relativeLayout_for_fragment, measureFragment, measureFragment.getTag()).commit();
            }
        });


        Button bpsettings = (Button) FragmentView.findViewById(R.id.btn_bpSettings);
        bpsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Settings");
                alert.setMessage("It will display settings on configuring blood pressure measurement.");
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();

            }
        });

        Button bphistory = (Button) FragmentView.findViewById(R.id.btn_bpHistory);
        bphistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("History");
                alert.setMessage("It will show blood pressure measurement history.");
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();

            }
        });


        Button bpconnect = (Button) FragmentView.findViewById(R.id.btn_bpconnect);
        bpconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Initiate blood pressure device bluetooth connection");
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();

            }
        });


        return FragmentView;
    }

}

package com.example.hp.heartrytcare.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hp.heartrytcare.R;


public class HeartRateFragment extends Fragment {


    public HeartRateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View FragmentView = inflater.inflate(R.layout.fragment_heart_rate, container, false);

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












       /* Button hrsettings = (Button) FragmentView.findViewById(R.id.btn_hrSettings);
        hrsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Settings");
                alert.setMessage("It will display settings on configuring heart rate measurement.");
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();

            }
        });

        Button hrhistory = (Button) FragmentView.findViewById(R.id.btn_hrHistory);
        hrhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("History");
                alert.setMessage("It will show heart rate measurement history.");
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();

            }
        });


        Button hrstart = (Button) FragmentView.findViewById(R.id.btn_hrStart);
        hrstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Initiate heart rate measurement");
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();

            }
        });

*/

        return FragmentView;
    }
}

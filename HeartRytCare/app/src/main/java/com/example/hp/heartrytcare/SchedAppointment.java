package com.example.hp.heartrytcare;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SchedAppointment extends Fragment {


    public SchedAppointment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View FragmentView = inflater.inflate(R.layout.fragment_sched_appointment, container, false);

        ImageView back = (ImageView) FragmentView.findViewById(R.id.img_arrowback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SchedFragment schedFragment = new SchedFragment();
                // ScheduleMed scheduleMed = new ScheduleMed();

                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.relativeLayout_for_fragment, schedFragment, schedFragment.getTag()).commit();
            }
        });
        return FragmentView;

    }

}

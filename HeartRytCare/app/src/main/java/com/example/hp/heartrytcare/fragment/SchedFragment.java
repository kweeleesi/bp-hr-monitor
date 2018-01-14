package com.example.hp.heartrytcare.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.widget.RelativeLayout;

import com.example.hp.heartrytcare.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SchedFragment extends Fragment implements View.OnClickListener  {

    private View view;
    private RelativeLayout medication, appointment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sched, container, false);
        //context = FragmentView.getContext();

        medication = (RelativeLayout) view.findViewById(R.id.medication);
        appointment = (RelativeLayout) view.findViewById(R.id.appointment);

        medication.setOnClickListener(this);
        appointment.setOnClickListener(this);

        return view;
    }

        @Override
        public void onClick(View view) {
            Fragment fragment = null;
            switch (view.getId()) {
                case R.id.medication:
                    fragment = new ScheduleMed();
                    replaceFragment(fragment);
                    break;
                case R.id.appointment:
                    fragment = new SchedAppointment();
                    replaceFragment(fragment);
                    break;
            }
        }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.relativeLayout_for_fragment, someFragment);
        transaction.addToBackStack("");
        transaction.commit();
    }
}

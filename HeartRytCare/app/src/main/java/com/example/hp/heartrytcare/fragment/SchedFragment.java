package com.example.hp.heartrytcare.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;

import com.example.hp.heartrytcare.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SchedFragment extends Fragment implements View.OnClickListener  {


    public SchedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View FragmentView = inflater.inflate(R.layout.fragment_sched, container, false);
        //context = FragmentView.getContext();

        CardView schedmed = (CardView) FragmentView.findViewById(R.id.card_schedmed);
        CardView schedappointment = (CardView) FragmentView.findViewById(R.id.card_schedappointment);

        schedmed.setOnClickListener(this);
        schedappointment.setOnClickListener(this);

        return FragmentView;
    }

        @Override
        public void onClick(View view) {
            Fragment fragment = null;
            switch (view.getId()) {
                case R.id.card_schedmed:
                    fragment = new ScheduleMed();
                    replaceFragment(fragment);
                    break;

                case R.id.card_schedappointment:
                    fragment = new SchedAppointment();
                    replaceFragment(fragment);
                    break;

            }
        }

 /*       schedmed.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                ScheduleMed scheduleMed = new ScheduleMed();
                SchedFragment schedFragment = new SchedFragment();
                FragmentManager manager = getFragmentManager();
               // manager.beginTransaction().remove(schedFragment);
                manager.beginTransaction().replace(R.id.relativeLayout_for_fragment, scheduleMed, scheduleMed.getTag()).commit();
    }
        });
        return FragmentView;*/

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.relativeLayout_for_fragment, someFragment);
        transaction.addToBackStack("");
        transaction.commit();
    }
}

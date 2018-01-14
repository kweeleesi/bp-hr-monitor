package com.example.hp.heartrytcare.fragment;


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
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.heartRate:
                //context = FragmentView.getContext();
                Intent i = new Intent(getActivity(), HeartRateMonitor.class);
                getActivity().startActivity(i);
               /* fragment = new HeartRateFragment();
                replaceFragment(fragment); */
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

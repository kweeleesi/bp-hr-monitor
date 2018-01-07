package com.example.hp.heartrytcare.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.activity.HeartRateMonitor;
import com.example.hp.heartrytcare.fragment.BloodPressureFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeasureFragment extends Fragment implements View.OnClickListener {

    public MeasureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View FragmentView = inflater.inflate(R.layout.fragment_measure, container, false);


        CardView heartrate = (CardView) FragmentView.findViewById(R.id.card_hr);
        CardView bloodpressure = (CardView) FragmentView.findViewById(R.id.card_bp);

        heartrate.setOnClickListener(this);
        bloodpressure.setOnClickListener(this);

        return FragmentView;
    }


    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.card_hr:
                //context = FragmentView.getContext();
                Intent i = new Intent(getActivity(), HeartRateMonitor.class);
                getActivity().startActivity(i);
               /* fragment = new HeartRateFragment();
                replaceFragment(fragment); */
                break;

            case R.id.card_bp:
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

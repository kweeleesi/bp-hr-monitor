package com.example.hp.heartrytcare;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.DatePicker;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleMed extends Fragment {

    private static final String TAG = "ScheduleMed";

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    public ScheduleMed() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View FragmentView = inflater.inflate(R.layout.fragment_schedule_med, container, false);
        //context = FragmentView.getContext();
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


      mDisplayDate = (TextView) FragmentView.findViewById(R.id.tv_scheddate);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view){
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        month, day, year);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day){
                month = month +1;
                Log.d(TAG, "onDateSet: mm/dd/yyyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        return FragmentView;
    }
}
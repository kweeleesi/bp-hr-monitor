package com.example.hp.heartrytcare.fragment;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.adapter.RelationModelListAdapter;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.RelationModel;
import com.example.hp.heartrytcare.db.UserFirebase;
import com.example.hp.heartrytcare.helper.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DoctorFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "DoctorFragment";

    private View view;
    private Button addDoctor;
    private ListView relationListView;

    private List<UserFirebase> doctorRelatedList = new ArrayList<>();
    private List<UserFirebase> doctorList = new ArrayList<>();
    private List<RelationModel> doctorRelations = new ArrayList<>();

    private RelationModelListAdapter relationModelListAdapter;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_doctor, container, false);

        initializeFields();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDoctorList();
    }

    private void initializeFields() {
        addDoctor = (Button) view.findViewById(R.id.addDoctor);
        relationListView = (ListView) view.findViewById(R.id.doctorList);

        addDoctor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addDoctor:
                AddDoctorDialogFragment doctorDialogFragment = AddDoctorDialogFragment.newInstance();
                doctorDialogFragment.show(getFragmentManager(), "addDoctor");
                break;
        }
    }

    private void getDoctorList() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("user");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (doctorList != null) {
                    doctorList.clear();
                }
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserFirebase model = snapshot.getValue(UserFirebase.class);
                        if (model != null) {
                            if (model.user_type == Constants.TYPE_USER_DOCTOR) {
                                doctorList.add(model);
                            }
                        }
                    }
                    getRelatedDoctorList();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void getRelatedDoctorList() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("relations");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (doctorRelatedList != null) {
                    doctorRelatedList.clear();
                }
                if (doctorRelations != null) {
                    doctorRelations.clear();
                }
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        RelationModel model = snapshot.getValue(RelationModel.class);
                        if (model != null) {
                            if (model.patientUID.equals(Constants.FIREBASE_UID) && model.verified) {
                                doctorRelations.add(model);
                                addDoctorRelation(model);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addDoctorRelation(RelationModel model) {
        if (doctorList != null) {
            for (UserFirebase userFirebase : doctorList) {
                if (userFirebase.firebase_user_id.equals(model.doctorUID) && model.verified) {
                    doctorRelatedList.add(userFirebase);
                }
            }
            setupRelatedDoctorList();
        }
    }

    private void setupRelatedDoctorList() {
        relationModelListAdapter = new RelationModelListAdapter(getActivity(), R.layout.listview_relation_items, doctorRelations, doctorRelatedList);
        relationListView.setAdapter(relationModelListAdapter);
    }
}
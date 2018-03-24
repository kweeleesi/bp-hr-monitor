package com.example.hp.heartrytcare.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.RelationModel;
import com.example.hp.heartrytcare.db.UserFirebase;
import com.example.hp.heartrytcare.helper.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PatientFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    private static final String TAG = "PatientFragment";

    private View view;
    private Button addPatient;
    private ListView patientList;

    private DatabaseReference databaseReference;
    private FirebaseDatabase database;

    private UserFirebase userModel;
    private List<RelationModel> relationModelList;
    private ArrayAdapter<String> patientRelationAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_doctor, container, false);

        initializeFields();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPatient:
                addPatient();
                break;
            case R.id.verifyCode:
                verify();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////
    private void initializeFields() {
        addPatient = (Button) view.findViewById(R.id.addPatient);
        addPatient.setOnClickListener(this);

        patientList = (ListView) view.findViewById(R.id.patientList);
        patientList.setOnItemClickListener(this);

        database = FirebaseDatabase.getInstance();
        relationModelList = new ArrayList<>();
        getMyPatientRelationList(Constants.FIREBASE_UID);
    }

    private void getMyPatientRelationList(final String selfUID) {
        databaseReference = database.getReference("relations");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        RelationModel model = snapshot.getValue(RelationModel.class);
                        if (model != null) {
                            if (model.doctorUID.equals(selfUID)) {
                                relationModelList.add(model);
                            }
                        } else {
                            Log.w(TAG, "onDataChange: model is null!");
                        }
                    }
                    getRelatedUserInformation();
                } else {
                    Log.d(TAG, "onDataChange: no records on connected patients!");
                    Toast.makeText(getActivity(), "No records on connected patients!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void getRelatedUserInformation() {
        if (relationModelList != null) {
            databaseReference = database.getReference("user");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> userList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserFirebase user = snapshot.getValue(UserFirebase.class);
                        if (user != null) {
                            boolean found = false;
                            for (RelationModel model : relationModelList) {
                                if (model.patientUID.equals(user.firebase_user_id)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                userList.add(user.last_name.toUpperCase(Locale.getDefault()) + ", " + user.first_name);
                            }
                        }
                    }
                    if (userList.size() > 0) {
                        setupRelatedPatientList(userList);
                    } else {
                        Log.d(TAG, "onDataChange: empty relation list");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });
        }
    }

    private void setupRelatedPatientList(List<String> relatedPatients) {
        if (relationModelList != null) {
            patientRelationAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
            patientRelationAdapter.addAll(relatedPatients);
            patientList.setAdapter(patientRelationAdapter);
        } else {
            Toast.makeText(getActivity(), "No Patients registered!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addPatient() {

    }

    private void verify() {

    }
}

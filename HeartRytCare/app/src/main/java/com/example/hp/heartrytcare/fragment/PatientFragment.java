package com.example.hp.heartrytcare.fragment;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.adapter.RelationModelListAdapter;
import com.example.hp.heartrytcare.db.RelationModel;
import com.example.hp.heartrytcare.db.UserFirebase;
import com.example.hp.heartrytcare.helper.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class PatientFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AddPatientDialogFragment.OnUserSelected {

    private static final String TAG = "PatientFragment";

    private View view;
    private Button addPatient;
    private ListView patientList;

    private DatabaseReference databaseReference;
    private FirebaseDatabase database;

    private UserFirebase userModel;
    private List<RelationModel> relationModelList;
    private List<UserFirebase> fullPatientList;
    private RelationModelListAdapter patientRelationAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_patient, container, false);

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

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onSelected(UserFirebase userModel) {
        if (userModel != null) {
            RelationModel model = generateRelationModel(Constants.FIREBASE_UID, userModel);
            databaseReference = database.getReference("relations");
            String relationId = databaseReference.push().getKey();
            databaseReference.child(relationId).setValue(model);
            send(userModel, model.verficationCode);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////
    private void initializeFields() {
        addPatient = (Button) view.findViewById(R.id.addPatient);
        addPatient.setEnabled(false);
        addPatient.setOnClickListener(this);

        patientList = (ListView) view.findViewById(R.id.patientList);
        patientList.setOnItemClickListener(this);

        database = FirebaseDatabase.getInstance();
        relationModelList = new ArrayList<>();
        fullPatientList = new ArrayList<>();
        getPatientList();
        getMyPatientRelationList(Constants.FIREBASE_UID);
    }

    private void getPatientList() {
        databaseReference = database.getReference("user");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (fullPatientList != null) {
                    fullPatientList.clear();
                }
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot != null) {
                            UserFirebase userModel = snapshot.getValue(UserFirebase.class);
                            if (userModel != null) {
                                if (userModel.user_type == 0) {
                                    fullPatientList.add(userModel);
                                }
                            } else {
                                Log.w(TAG, "onDataChange: userModel is null!");
                            }
                        } else {
                            Log.w(TAG, "onDataChange: snapshot is null!");
                        }
                    }
                    addPatient.setEnabled(true);
                    addPatient.invalidate();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void getMyPatientRelationList(final String selfUID) {
        databaseReference = database.getReference("relations");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (relationModelList != null) {
                    relationModelList.clear();
                }
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
                    relationModelList = new ArrayList<>();
                    setupRelatedPatientList(new ArrayList<UserFirebase>());
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
                    List<UserFirebase> userList = new ArrayList<>();
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
                                userList.add(user);
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

    private void setupRelatedPatientList(List<UserFirebase> relatedPatients) {
        if (relationModelList != null) {
            patientRelationAdapter = new RelationModelListAdapter(getContext(), R.layout.listview_relation_items, relationModelList, relatedPatients);
            patientList.setAdapter(patientRelationAdapter);
        } else {
            Toast.makeText(getActivity(), "No Patients registered!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addPatient() {
        AddPatientDialogFragment addPatientDialogFragment = AddPatientDialogFragment
                .newInstance(fullPatientList, this);
        addPatientDialogFragment.show(getFragmentManager(), "addPatient");
    }

    private RelationModel generateRelationModel(String doctorUID, UserFirebase userModel) {
        RelationModel model = new RelationModel();
        model.doctorUID = doctorUID;
        model.patientUID = userModel.firebase_user_id;

        Date dt = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(dt);
        Date time = calendar.getTime();
        String hash = md5(String.valueOf(time.getTime()));
        model.verficationCode = hash.substring(hash.length() - 6, hash.length());
        model.validity = time.getTime() + 300000;

        return model;
    }

    private String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void send(UserFirebase userFirebase, String verificationCode) {
        if (TextUtils.isEmpty(userFirebase.contact_number)) {
            Log.d(TAG, "send: contact number is empty, cannot send verification code");
            Toast.makeText(getActivity(), "Contact number is empty. Cannot send verification code", Toast.LENGTH_LONG).show();
            return;
        }

        String msg = ("Your verification code is ").concat(verificationCode);

        try {
            PendingIntent pi = PendingIntent.getActivity(getActivity(), 0,
                    new Intent(getActivity(), PatientFragment.class), 0);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(userFirebase.contact_number, null, msg, pi, null);
            // TODO: 1/30/2018 handle error codes | check number validity
            Toast.makeText(getActivity(), "Message Sent", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

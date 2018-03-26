package com.example.hp.heartrytcare.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.LimitValues;
import com.example.hp.heartrytcare.db.LimitValuesDao;
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

import javax.net.ssl.SSLContextSpi;

import de.greenrobot.dao.query.QueryBuilder;

public class DoctorFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "DoctorFragment";

    private View view;
    private Button addDoctor, saveLimit, updateLimit;
    private LinearLayout drDetail;
    private TextView drName;
    private EditText bpLimit, hrLimit;

    private List<UserFirebase> doctorRelatedList = new ArrayList<>();
    private List<UserFirebase> doctorList = new ArrayList<>();
    private List<RelationModel> doctorRelations = new ArrayList<>();

    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private LimitValuesDao lvDao;
    private LimitValues limitValues;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_doctor, container, false);

        initializeFields();

        DaoSession daoSession = ((HeartRytCare) getActivity().getApplicationContext()).getDaoSession();
        lvDao = daoSession.getLimitValuesDao();

        Log.e("DoctorFragment", "!!!!!!!!! 1");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDoctorList();
        getLimitValues();
        Log.e("DoctorFragment", "!!!!!!!!! 2");
        if (limitValues == null) {
            Log.e("DoctorFragment", "!!!!!!!!! 3");
            saveLimit.setVisibility(View.VISIBLE);
            updateLimit.setVisibility(View.GONE);
        } else {
            Log.e("DoctorFragment", "!!!!!!!!! 4");
            saveLimit.setVisibility(View.GONE);
            updateLimit.setVisibility(View.VISIBLE);

            hrLimit.setText(limitValues.getHrLimit());
            bpLimit.setText(limitValues.getBpLimit());
        }
    }

    private void initializeFields() {
        addDoctor = (Button) view.findViewById(R.id.addDoctor);
        saveLimit = (Button) view.findViewById(R.id.saveLimit);
        updateLimit = (Button) view.findViewById(R.id.updateLimit);
        drName = (TextView) view.findViewById(R.id.drName);
        drDetail = (LinearLayout) view.findViewById(R.id.drDetail);
        bpLimit = (EditText) view.findViewById(R.id.bpLimit);
        hrLimit = (EditText) view.findViewById(R.id.hrLimit);

        addDoctor.setOnClickListener(this);
        saveLimit.setOnClickListener(this);
        updateLimit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addDoctor:
                AddDoctorDialogFragment doctorDialogFragment = AddDoctorDialogFragment.newInstance();
                doctorDialogFragment.show(getFragmentManager(), "addDoctor");
                break;
            case R.id.saveLimit:
                if (TextUtils.isEmpty(hrLimit.getText())) {
                    hrLimit.setError("This item cannot be empty");
                    return;
                }

                if (TextUtils.isEmpty(bpLimit.getText())) {
                    bpLimit.setError("This item cannot be empty");
                    return;
                }

                LimitValues lv = new LimitValues();
                lv.setFirebase_user_id(Constants.FIREBASE_UID);
                lv.setBpLimit(bpLimit.getText().toString());
                lv.setHrLimit(hrLimit.getText().toString());
                lvDao.insert(lv);

                Toast.makeText(getActivity(), "Saved.", Toast.LENGTH_SHORT).show();
//                getActivity().onBackPressed();
                break;
            case R.id.updateLimit:
                getLimitValues();

                limitValues.setBpLimit(bpLimit.getText().toString());
                limitValues.setHrLimit(hrLimit.getText().toString());
                lvDao.insertOrReplace(limitValues);

                Toast.makeText(getActivity(), "Updated.", Toast.LENGTH_SHORT).show();
//                getActivity().onBackPressed();
                break;
        }
    }

    private void getLimitValues() {
        QueryBuilder<LimitValues> query = lvDao.queryBuilder();
        query.where(LimitValuesDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
        if (query.list() != null && query.list().size() != 0) {
            limitValues = query.list().get(0);
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
            setupRelatedDoctorDetail();
        }
    }

    private void setupRelatedDoctorDetail() {
        if (doctorList == null || doctorList.size() == 0) {
            addDoctor.setVisibility(View.VISIBLE);
            drDetail.setVisibility(View.GONE);
        } else {
            addDoctor.setVisibility(View.GONE);
            drDetail.setVisibility(View.VISIBLE);

            drName.setText("Dr. " + doctorList.get(0).first_name + " " + doctorList.get(0).last_name);
        }
    }
}
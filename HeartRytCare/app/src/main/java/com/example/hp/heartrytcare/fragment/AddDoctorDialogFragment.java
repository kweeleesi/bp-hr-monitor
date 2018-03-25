package com.example.hp.heartrytcare.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.RelationModel;
import com.example.hp.heartrytcare.helper.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddDoctorDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "AddDoctorDialogFragment";

    EditText enterVCode;
    Button cancelBtn;
    Button confirmBtn;

    DatabaseReference databaseReference;
    FirebaseDatabase database;

    public static AddDoctorDialogFragment newInstance() {

        Bundle args = new Bundle();

        AddDoctorDialogFragment fragment = new AddDoctorDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogfragment_add_doctor, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirmBtn:
                if (enterVCode != null) {
                    verifyCode(enterVCode.getText().toString());
                } else {
                    Log.w(TAG, "onClick: enterVCode is null!");
                }
                break;
            case R.id.cancelBtn:
                dismiss();
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////
    private void initialize(View v) {
        enterVCode = (EditText) v.findViewById(R.id.enterVCode);
        confirmBtn = (Button) v.findViewById(R.id.confirmBtn);
        cancelBtn = (Button) v.findViewById(R.id.cancelBtn);

        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    private void verifyCode(final String code) {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("relations");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        RelationModel model = snapshot.getValue(RelationModel.class);
                        if (model != null) {
                            if (model.patientUID.equals(Constants.FIREBASE_UID) && model.verficationCode.equals(code)) {
                                setValidDoctorRelation(model, snapshot.getKey());
                            } else {
                                Log.d(TAG, "onDataChange: skip");
                            }
                        } else {
                            Log.w(TAG, "onDataChange: model is null!");
                        }
                    }
                } else {
                    Log.w(TAG, "onDataChange: relation model is null or has " + dataSnapshot.hasChildren() + " children");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setValidDoctorRelation(RelationModel model, String childKey) {
        databaseReference = database.getReference("relations").child(childKey);
        model.verified = true;
        model.validity = 0;
        databaseReference.setValue(model)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dismiss();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "onFailure: " + e.getMessage());
                }
            });
    }
}

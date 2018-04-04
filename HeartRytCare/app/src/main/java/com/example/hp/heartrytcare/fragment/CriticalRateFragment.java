package com.example.hp.heartrytcare.fragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.MessageData;
import com.example.hp.heartrytcare.db.NotificationData;
import com.example.hp.heartrytcare.db.RelationModel;
import com.example.hp.heartrytcare.db.UserFirebase;
import com.example.hp.heartrytcare.helper.Constants;
import com.example.hp.heartrytcare.helper.NotificationHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CriticalRateFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "CriticalRateFragment";

    public static final String CASE_TYPE_HR = "caseHR";
    public static final String CASE_TYPE_BP = "caseBP";

    private final static String BUNDLE_KEY_FORMATTED_ADVISED = "formattedAdv";
    private final static String BUNDLE_KEY_FORMATTED_CURRENT = "formattedCur";
    private final static String BUNDLE_KEY_CASE_TYPE = "caseType";
    private final static String BUNDLE_KEY_RANGE_STATE = "state";

    private String formattedAdv;
    private String formattedCur;
    private String caseType;
    private int state;
    private List<UserFirebase> doctorRelatedList = new ArrayList<>();
    private List<UserFirebase> doctorList = new ArrayList<>();

    private EditText composeMessage;
    private Button ignoreBtn;
    private Button sendBtn;
    private TextView advisedRate;
    private TextView currentReading;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    /**
     * New instance for CriticalRateFragment.
     *
     * @param caseType             See {@link CriticalRateFragment} for case types
     * @param formattedAdvisedRate
     * @param formattedCurrentRate
     * @return
     */
    public static CriticalRateFragment newInstance(String caseType, String formattedAdvisedRate, int state, String formattedCurrentRate) {

        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_CASE_TYPE, caseType);
        args.putString(BUNDLE_KEY_FORMATTED_ADVISED, formattedAdvisedRate);
        args.putString(BUNDLE_KEY_FORMATTED_CURRENT, formattedCurrentRate);
        args.putInt(BUNDLE_KEY_RANGE_STATE, state);


        CriticalRateFragment fragment = new CriticalRateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formattedAdv = getArguments().getString(BUNDLE_KEY_FORMATTED_ADVISED, "");
        formattedCur = getArguments().getString(BUNDLE_KEY_FORMATTED_CURRENT, "");
        caseType = getArguments().getString(BUNDLE_KEY_CASE_TYPE, "");
        state = getArguments().getInt(BUNDLE_KEY_RANGE_STATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogfragment_criticalhbp, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ignoreBtn = (Button) view.findViewById(R.id.ignoreBtn);
        sendBtn = (Button) view.findViewById(R.id.sendBtn);
        advisedRate = (TextView) view.findViewById(R.id.advisedRate);
        advisedRate.setText(formattedAdv);
        currentReading = (TextView) view.findViewById(R.id.currentReading);
        currentReading.setText(formattedCur);

        composeMessage = (EditText) view.findViewById(R.id.composeMessage);
        if (caseType.equals(CASE_TYPE_HR)) {
            composeMessage.setText(getActivity().getString(
                    R.string.heartrate_msg_template,
                    formattedCur));
        } else if (caseType.equals(CASE_TYPE_BP)) {
            composeMessage.setText(getActivity().getString(
                    R.string.blood_pressure_msg_template,
                    (state == Constants.RANGE_HIGH ? "high" : "low"),
                    formattedCur));
        }

        ignoreBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        getDoctorList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendBtn:
                send();
                break;
            case R.id.ignoreBtn:
                dismiss();
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////
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
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        RelationModel model = snapshot.getValue(RelationModel.class);
                        if (model != null) {
                            if (model.patientUID.equals(Constants.FIREBASE_UID) && model.verified) {
                                addDoctorRelation(model);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: " + databaseError.getMessage());
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
        }
    }

    private void send() {
        if (doctorRelatedList != null && doctorRelatedList.get(0) != null) {
            if (TextUtils.isEmpty(doctorRelatedList.get(0).contact_number)) {
                Log.d(TAG, "send: contact number is empty, cannot send alert");
                Toast.makeText(getActivity(), "Contact number is empty. Cannot send alert message", Toast.LENGTH_LONG).show();
                return;
            }

            String msg = composeMessage.getText().toString();

            try {
                PendingIntent pi = PendingIntent.getActivity(getActivity(), 0,
                        new Intent(getActivity(), PatientFragment.class), 0);
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(doctorRelatedList.get(0).contact_number, null, msg, pi, null);
                Toast.makeText(getActivity(), "MessageData Sent", Toast.LENGTH_SHORT).show();

                NotificationHelper helper = new NotificationHelper(getActivity());
                String patientName = Constants.FIREBASE_USER_DATA.first_name + " " + Constants.FIREBASE_USER_DATA.last_name;
                NotificationData notificationData = new NotificationData(
                        getActivity().getString(R.string.patient_label_template, patientName),
                        msg);
                MessageData messageData = new MessageData(
                        doctorRelatedList.get(0)._FCMtoken,
                        notificationData);
                helper.sendNotification(messageData);

                dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Unable to fetch doctor information", Toast.LENGTH_LONG).show();
        }
    }
}

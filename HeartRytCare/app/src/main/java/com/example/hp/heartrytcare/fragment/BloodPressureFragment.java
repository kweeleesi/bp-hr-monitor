package com.example.hp.heartrytcare.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LogWriter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.BloodPressureData;
import com.example.hp.heartrytcare.db.BloodPressureDataDao;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.LimitValues;
import com.example.hp.heartrytcare.db.LimitValuesDao;
import com.example.hp.heartrytcare.helper.BTMessageReceiver;
import com.example.hp.heartrytcare.helper.BluetoothBPHelper;
import com.example.hp.heartrytcare.helper.Constants;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.dao.query.QueryBuilder;


/**
 * A simple {@link Fragment} subclass.
 */
public class BloodPressureFragment extends Fragment implements View.OnClickListener, BTMessageReceiver{

    public static IncomingMessageHandler incomingMessageHandler;

    private static final String TAG = "BloodPressureFragment";
    private static final int UNITS_HIGH = 10;
    private static final int UNITS_LOW = 10;

    private BluetoothBPHelper btHelper;
    private BloodPressureDataDao bpDao;
    private LimitValuesDao lvDao;
    private LimitValues lv;

    private TextView systolicBPValue;
    private TextView diastolicBPValue;
    private TextView dateTaken;
    private ArcProgress arcProgress;
    private AVLoadingIndicatorView loading;
    private TextView bpFinal;
    private TextView bpLabel;

    private Boolean isSaved = false;
    private String[] sysDia;
    public float percentage = 0f;
    public float progress = 0f;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        incomingMessageHandler = new IncomingMessageHandler(this);
        btHelper = BluetoothBPHelper.getInstance();

        DaoSession daoSession = ((HeartRytCare) getActivity().getApplicationContext()).getDaoSession();
        bpDao = daoSession.getBloodPressureDataDao();
        lvDao = daoSession.getLimitValuesDao();

        QueryBuilder<LimitValues> query = lvDao.queryBuilder();
        query.where(LimitValuesDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
        if (query.list() != null && query.list().size() != 0) {
            lv = query.list().get(0);
            sysDia = lv.getBpLimit().split("/");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_blood_pressure, container, false);

        systolicBPValue = (TextView) fragmentView.findViewById(R.id.systolicValue);
        diastolicBPValue = (TextView) fragmentView.findViewById(R.id.diastolicValue);
        dateTaken = (TextView) fragmentView.findViewById(R.id.dateTaken);

        arcProgress = (ArcProgress) fragmentView.findViewById(R.id.arc_progress);
        loading = (AVLoadingIndicatorView) fragmentView.findViewById(R.id.loading);
        bpFinal = (TextView) fragmentView.findViewById(R.id.bpFinal);
        bpLabel = (TextView) fragmentView.findViewById(R.id.bpLabel);
        arcProgress.setMax(100);

        ImageView back = (ImageView) fragmentView.findViewById(R.id.img_arrowback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasureFragment measureFragment = new MeasureFragment();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.relativeLayout_for_fragment, measureFragment, measureFragment.getTag()).commit();
            }
        });

        Button bpconnect = (Button) fragmentView.findViewById(R.id.btn_bpconnect);
        bpconnect.setOnClickListener(this);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btHelper.bluetoothOn(getActivity());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bpconnect:
                FragmentManager fm = getFragmentManager();
                BluetoothConnectDialogFragment bluetoothConnectDialogFragment = BluetoothConnectDialogFragment.newInstance();
                bluetoothConnectDialogFragment.show(fm, "connectFragment");
                break;
        }
    }

    @Override
    public void onMessageReceived(String string) {
        if (getActivity() != null) {
        String message = parseMessage(string);
        if (message == null) {
            systolicBPValue.setText(getActivity().getString(R.string.blood_pressure_error));
            diastolicBPValue.setText(getActivity().getString(R.string.blood_pressure_error));
            dateTaken.setVisibility(View.GONE);
            arcProgress.setProgress(0);
            progress = 0f;
            loading.setVisibility(View.GONE);
            bpLabel.setVisibility(View.GONE);
            bpFinal.setVisibility(View.GONE);
        } else {
            try {
                    String[] arrayInputs = string.split("\\n");
                    String[] bpValues = parseMessage(arrayInputs[0]).split(",");
                if (Integer.parseInt(bpValues[0]) == 0 || Integer.parseInt(bpValues[1]) == 0) {
                    float tempProgress = progress;
                    if ((tempProgress + 1) > progress) {
                        //this means, user is now re-reading BP from device OR reading of device was interrupted;
                        progress = 0f;
                    }
                    if (Integer.parseInt(bpValues[0]) == 0 && Integer.parseInt(bpValues[1]) == 0) {
                        //premature stop
                        progress = 0f;
                        percentage = 0f;
                    } else {
                        progress = (percentage++ / 46f) * 100f;
                    }
                    arcProgress.setProgress((int) progress);
                    systolicBPValue.setText(getActivity().getString(R.string.blood_pressure_reading));
                    diastolicBPValue.setText(getActivity().getString(R.string.blood_pressure_reading));
                    dateTaken.setText("");
                    isSaved = false;
                    loading.setVisibility(View.VISIBLE);
                    bpLabel.setVisibility(View.GONE);
                    bpFinal.setVisibility(View.GONE);
                } else {
                    if (systolicBPValue != null || diastolicBPValue != null || dateTaken != null) {
                        arcProgress.setProgress(arcProgress.getMax());
                        systolicBPValue.setText(getActivity().getString(R.string.blood_pressure_systolic) + " " + bpValues[0]);
                        diastolicBPValue.setText(getActivity().getString(R.string.blood_pressure_diastolic) + " " + bpValues[1]);
                        setFormattedDate(dateTaken, System.currentTimeMillis());
                        dateTaken.setVisibility(View.VISIBLE);

                        if (!isSaved) {
                            Date d = new Date();
                            CharSequence date  = DateFormat.format("MM/dd", d.getTime());

                            BloodPressureData bp = new BloodPressureData();
                            bp.setFirebase_user_id(Constants.FIREBASE_UID);
                            bp.setSystolic(Integer.parseInt(bpValues[0]));
                            bp.setDiastolic(Integer.parseInt(bpValues[1]));
                            bp.setDate(date.toString());
                            bp.setTimestamp(System.currentTimeMillis());
                            bpDao.insert(bp);
                            isSaved = true;
                            bpFinal.setVisibility(View.VISIBLE);
                            bpFinal.setText((bpValues[0] + "/" + bpValues[1]));
                            loading.setVisibility(View.GONE);

                            if (lv != null && !lv.getBpLimit().equals("")) {
                                bpLabel.setVisibility(View.VISIBLE);
                                int highSys = (Integer.parseInt(sysDia[0]) + UNITS_HIGH);
                                int highDia = (Integer.parseInt(sysDia[1]) + UNITS_HIGH);
                                int lowSys = (Integer.parseInt(sysDia[0]) - UNITS_LOW);
                                int lowDia = (Integer.parseInt(sysDia[1]) - UNITS_LOW);
                                if (bp.getSystolic() >= highSys ||
                                        bp.getDiastolic() >= highDia) {
                                    checkBPAlert(bp.getSystolic(), bp.getDiastolic(), Constants.RANGE_HIGH);
                                    bpLabel.setText("HIGH");
                                    bpLabel.setTextColor(Color.RED);
                                }
                                //guard for negative result inputs
                                else if (lowSys > UNITS_LOW && lowDia > UNITS_LOW) {
                                    if (bp.getSystolic() <= lowSys ||
                                            bp.getDiastolic() <= lowDia) {
                                        checkBPAlert(bp.getSystolic(), bp.getDiastolic(), Constants.RANGE_LOW);
                                        bpLabel.setText("LOW");
                                        bpLabel.setTextColor(Color.BLUE);
                                    }
                                    }
                                //within normal range
                                else {
                                    bpLabel.setText("NORMAL");
                                    bpLabel.setTextColor(Color.GREEN);
                                }
                            }

                            Log.e(TAG, "!!!!!!!!!!!! SAVED!");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////
    private String parseMessage(String message) {
        String trimmedString = "";
        if (message.contains("error")) {
            return null;
        }
        //trim message
        for (int i = 0; i < message.length(); i++) {
            if (Character.isDigit(message.charAt(i)) || message.charAt(i) == ',') {
                trimmedString = trimmedString.concat(String.valueOf(message.charAt(i)));
            }
        }
        return trimmedString;
    }

    private void setFormattedDate(TextView dateTextView, long l) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.getDefault());
        Date resultDate = new Date(l);
        dateTextView.setText("Date taken:" + sdf.format(resultDate));
        dateTextView.invalidate();
    }

    /**
     *
     * @param systolic
     * @param diastolic
     * @param state 1 = HIGH, 0 = LOW
     */
    private void checkBPAlert(int systolic, int diastolic, int state) {
        CriticalRateFragment criticalRateFragment = CriticalRateFragment.newInstance(
                CriticalRateFragment.CASE_TYPE_BP,
                lv.getBpLimit(),
                state,
                systolic + "/" + diastolic);
        criticalRateFragment.show(getFragmentManager(), "criticalState");
    }

    ///////////////////////////////////////////////////////////////////////////
    // STATIC CLASS
    ///////////////////////////////////////////////////////////////////////////
    public static class IncomingMessageHandler extends Handler {

        private BTMessageReceiver listener;

        public IncomingMessageHandler(BTMessageReceiver listener) {
            this.listener = listener;
        }

        public void handleMessage(android.os.Message msg){
            if(msg.what == Constants.MESSAGE_READ){
                String readMessage = null;
                try {
                    readMessage = new String((byte[]) msg.obj, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // TODO: Mar 24, 0024 callback here...
                Log.d(TAG, "handleMessage: " + readMessage);
                this.listener.onMessageReceived(readMessage);
            }

            if(msg.what == Constants.CONNECTING_STATUS){
                // TODO: Mar 24, 0024 update connection status here...
                if(msg.arg1 == 1) {
                    Log.d(TAG, "handleMessage: " + "Connected to Device: " + (String)(msg.obj));
                } else {
                    Log.d(TAG, "handleMessage: " + "Connection Failed");
                }
            }
        }
    }

}

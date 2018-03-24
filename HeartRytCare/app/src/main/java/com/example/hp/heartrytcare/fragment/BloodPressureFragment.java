package com.example.hp.heartrytcare.fragment;


import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.helper.BluetoothBPHelper;
import com.example.hp.heartrytcare.helper.ConnectedThread;
import com.example.hp.heartrytcare.helper.Constants;

import java.io.UnsupportedEncodingException;


/**
 * A simple {@link Fragment} subclass.
 */
public class BloodPressureFragment extends Fragment implements View.OnClickListener{

    public static IncomingMessageHandler incomingMessageHandler;

    private static final String TAG = "BloodPressureFragment";

    public ConnectedThread connectedThread;
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path
    private BluetoothBPHelper btHelper;

    public BloodPressureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        incomingMessageHandler = new IncomingMessageHandler();
        btHelper = BluetoothBPHelper.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View FragmentView = inflater.inflate(R.layout.fragment_blood_pressure, container, false);

        ImageView back = (ImageView) FragmentView.findViewById(R.id.img_arrowback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasureFragment measureFragment = new MeasureFragment();
                // ScheduleMed scheduleMed = new ScheduleMed();

                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.relativeLayout_for_fragment, measureFragment, measureFragment.getTag()).commit();
            }
        });

        Button bpsettings = (Button) FragmentView.findViewById(R.id.btn_bpSettings);
        bpsettings.setOnClickListener(this);

        Button bphistory = (Button) FragmentView.findViewById(R.id.btn_bpHistory);
        bphistory.setOnClickListener(this);

        Button bpconnect = (Button) FragmentView.findViewById(R.id.btn_bpconnect);
        bpconnect.setOnClickListener(this);

        return FragmentView;
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
                BluetoothConnectFragment bluetoothConnectFragment = BluetoothConnectFragment.newInstance();
                bluetoothConnectFragment.show(fm, "connectFragment");
                break;
            case R.id.btn_bpSettings:
                /*AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Settings");
                alert.setMessage("It will display settings on configuring blood pressure measurement.");
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();*/
                break;
            case R.id.btn_bpHistory:

                break;
        }
    }

    private void parseMessage(String message) {

    }

    public static class IncomingMessageHandler extends Handler {
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
                BloodPressureFragment.parseMessage(readMessage);
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

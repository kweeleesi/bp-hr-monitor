package com.example.hp.heartrytcare.fragment;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.helper.BluetoothBPHelper;
import com.example.hp.heartrytcare.helper.ConnectedThread;
import com.example.hp.heartrytcare.helper.Constants;

import java.io.IOException;

/**
 * Created by OMIPLEKEVIN on March 24, 2018.
 * HeartRytCare
 * com.example.hp.heartrytcare.fragment
 */

public class BluetoothConnectDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener{

    private ListView bluetoothDevices;
    private ArrayAdapter<String> btArrayAdapter;

    private BluetoothBPHelper btHelper;
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private ConnectedThread mConnectedThread;

    public static BluetoothConnectDialogFragment newInstance() {

        Bundle args = new Bundle();

        BluetoothConnectDialogFragment fragment = new BluetoothConnectDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        btHelper = BluetoothBPHelper.getInstance();
        return inflater.inflate(R.layout.dialogfragment_bluetooth_connect, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bluetoothDevices = (ListView) view.findViewById(R.id.bluetoothDevices);
        btArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        btArrayAdapter.addAll(btHelper.listPairedDevices(getActivity()));

        bluetoothDevices.setAdapter(btArrayAdapter);
        bluetoothDevices.setOnItemClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(!btHelper.getBluetoothAdapter().isEnabled()) {
            Toast.makeText(getActivity(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the device MAC address, which is the last 17 chars in the View
        String info = ((TextView) view).getText().toString();
        final String address = info.substring(info.length() - 17);
        final String name = info.substring(0,info.length() - 17);

        // Spawn a new thread to avoid blocking the GUI one
        new Thread()
        {
            public void run() {
                boolean fail = false;

                BluetoothDevice device = btHelper.getBluetoothAdapter().getRemoteDevice(address);

                try {
                    mBTSocket = btHelper.createBluetoothSocket(device);
                } catch (IOException e) {
                    fail = true;
                    Toast.makeText(getActivity(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    mBTSocket.connect();
                } catch (IOException e) {
                    try {
                        fail = true;
                        mBTSocket.close();
                        Toast.makeText(getActivity(), "Failed connecting to " + name, Toast.LENGTH_SHORT).show();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Toast.makeText(getActivity(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if(!fail) {
                    mConnectedThread = new ConnectedThread(mBTSocket, BloodPressureFragment.incomingMessageHandler);
                    mConnectedThread.start();

                    BloodPressureFragment.incomingMessageHandler.obtainMessage(Constants.CONNECTING_STATUS, 1, -1, name)
                            .sendToTarget();
                    dismissAllowingStateLoss();
                }
            }
        }.start();
    }
}

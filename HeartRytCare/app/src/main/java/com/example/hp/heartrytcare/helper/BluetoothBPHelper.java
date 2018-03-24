package com.example.hp.heartrytcare.helper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothBPHelper {

    private static final String TAG = "BluetoothBPHelper";
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    private static volatile BluetoothBPHelper _instance;

    private ArrayAdapter<String> mBTArrayAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver blReceiver;

    public static BluetoothBPHelper getInstance() {
        if (_instance == null) {
            synchronized (BluetoothBPHelper.class) {
                if (_instance == null) {
                    _instance = new BluetoothBPHelper();
                }
            }
        }

        return _instance;
    }

    private BluetoothBPHelper() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        blReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(TAG, "onReceive: " + action);
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // add the name to the list
                    mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mBTArrayAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void bluetoothOn(Activity activity) {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
            Toast.makeText(activity.getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(activity.getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    public void bluetoothOff(Activity activity){
        bluetoothAdapter.disable(); // turn off
        // TODO: Mar 24, 0024 update state here...
        Toast.makeText(activity.getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    public void discover(Activity activity){
        // Check if the device is already discovering
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Toast.makeText(activity.getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(bluetoothAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                bluetoothAdapter.startDiscovery();
                Toast.makeText(activity.getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                activity.registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(activity.getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public List<String> listPairedDevices(Activity activity){
        List<String> pairedList = new ArrayList<>();
        mPairedDevices = bluetoothAdapter.getBondedDevices();
        if(bluetoothAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices) {
                pairedList.add(device.getName() + "\n" + device.getAddress());
            }
            return pairedList;
        }
        else {
            Toast.makeText(activity.getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
}

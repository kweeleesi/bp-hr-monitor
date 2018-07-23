package com.example.hp.heartrytcare.helper;

public interface BTMessageReceiver {
    void onMessageReceived(String string);

    void onConnectedState(boolean isConnected);
}
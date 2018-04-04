package com.example.hp.heartrytcare.service;

import com.example.hp.heartrytcare.db.MessageData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FirebaseAPI {

    @POST("/fcm/send")
    Call<MessageData> sendMessage(@Body MessageData messageData);
}

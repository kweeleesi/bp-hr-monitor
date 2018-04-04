package com.example.hp.heartrytcare.helper;

import android.content.Context;
import android.util.Log;

import com.example.hp.heartrytcare.BuildConfig;
import com.example.hp.heartrytcare.db.MessageData;
import com.example.hp.heartrytcare.service.FirebaseAPI;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationHelper {

    private static final String TAG = "NotificationHelper";

    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void sendNotification(MessageData messageData) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "key=" + BuildConfig.fcmLegacyKey); // <-- this is the important line
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        httpClient.addInterceptor(logging);
        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com")//url of FCM messageData server
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())//use for convert JSON file into object
                .build();

        // prepare call in Retrofit 2.0
        FirebaseAPI firebaseAPI = retrofit.create(FirebaseAPI.class);
        Call<MessageData> call2 = firebaseAPI.sendMessage(messageData);
        call2.enqueue(new Callback<MessageData>() {
            @Override
            public void onResponse(Call<MessageData> call, Response<MessageData> response) {
                Log.d(TAG, "onResponse");
            }

            @Override
            public void onFailure(Call<MessageData> call, Throwable t) {
                Log.d(TAG, "onFailure");
            }
        });
    }
}

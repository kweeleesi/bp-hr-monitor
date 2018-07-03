package com.example.hp.heartrytcare.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.UserFirebase;
import com.example.hp.heartrytcare.helper.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.SEND_SMS, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_REQUESTS_CODE = 1001;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        setTitle(null);

        checkPermission();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("PERMISSION_REQ_RESULT", "onRequestPermissionsResult: " + requestCode + ", " + Arrays.deepToString(permissions) + ", " + grantResults.toString());
        if (requestCode == PERMISSION_REQUESTS_CODE) {
            String[] required = new String[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                if (permissions.length <= grantResults.length) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        required[i] = permissions[i];
                    }
                } else {
                    Log.w("REQUEST_PERMISSION_FAILED", "onRequestPermissionsResult: OUT OF BOUNDS!");
                }
            }

            String permissionsNeeded = "";
            for (int i = 0; i < required.length; i++) {
                if (required[i] != null) {
                    permissionsNeeded += required[i] + (i < required.length-1 ? ", " : "");
                }
            }

            if (!permissionsNeeded.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permission Required");
                builder.setMessage(permissionsNeeded);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
                dialog.show();
            } else {
                startAuthViaFirebase();
            }
        }
    }

    private void startAuthViaFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userInfoJson = getSharedPreferences(Constants.SHARED_PREFS_TABLE, Context.MODE_PRIVATE).getString(Constants.SHARED_PREFS_FIELD_USER_INFO_JSON, "");
        if (!userInfoJson.isEmpty()) {
            Constants.FIREBASE_USER_DATA = new Gson().fromJson(userInfoJson, UserFirebase.class);
        } else {
            currentUser = null;
        }
        if (currentUser == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
            Constants.FIREBASE_UID = currentUser.getUid();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    private void checkPermission() {
        if (!hasCompletePermissions(REQUIRED_PERMISSIONS)) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUESTS_CODE);
        } else {
            startAuthViaFirebase();
        }
    }

    private boolean hasCompletePermissions(String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}

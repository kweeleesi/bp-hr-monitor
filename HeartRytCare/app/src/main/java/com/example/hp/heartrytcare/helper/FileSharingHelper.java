package com.example.hp.heartrytcare.helper;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


public class FileSharingHelper {

    private static final String TAG = "FileSharingHelper";
    private static final String DIR_COMMONS_FILES = "files/";

    private Context context;

    private FirebaseStorage storage;
    private StorageReference reference;
    private UploadTask uploadTask;
    
    public FileSharingHelper(Context context) {
        this.context = context;

        initialize();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHOD
    ///////////////////////////////////////////////////////////////////////////
    private void initialize() {
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////
    public void uploadFile(File file, OnProgressListener<UploadTask.TaskSnapshot> listener) {
        Uri fileUri = Uri.fromFile(file);
        StorageReference newFileReference = reference.child(DIR_COMMONS_FILES + fileUri.getLastPathSegment());
        uploadTask = newFileReference.putFile(fileUri);
        uploadTask.addOnProgressListener(listener);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: " + e.getMessage());
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: " + taskSnapshot.getDownloadUrl().toString());
                Toast.makeText(context, taskSnapshot.getDownloadUrl().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

}

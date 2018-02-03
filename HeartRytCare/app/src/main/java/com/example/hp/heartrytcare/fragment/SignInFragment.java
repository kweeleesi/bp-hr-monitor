package com.example.hp.heartrytcare.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.activity.MainMenuActivity;
import com.example.hp.heartrytcare.helper.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wang.avi.AVLoadingIndicatorView;

public class SignInFragment extends Fragment {

    private View view;
    private EditText eMail;
    private EditText password;
    private FirebaseAuth mAuth;
    private Button signIn;
    private AVLoadingIndicatorView loading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin, null);

        mAuth = FirebaseAuth.getInstance();
        initializeFields();

        return view;
    }

    private void initializeFields() {
        eMail = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        signIn = (Button) view.findViewById(R.id.signIn);
        loading = (AVLoadingIndicatorView) view.findViewById(R.id.loading);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        loading.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(eMail.getText().toString(), password.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(getClass().getSimpleName(), "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Constants.FIREBASE_UID = user.getUid();

                            Intent intent = new Intent(getActivity(), MainMenuActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            loading.setVisibility(View.GONE);
                            Log.w(getClass().getSimpleName(), "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Incorrect email or password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

package com.example.hp.heartrytcare.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.User;
import com.example.hp.heartrytcare.db.UserDao;
import com.example.hp.heartrytcare.fragment.SignInFragment;
import com.example.hp.heartrytcare.helper.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wang.avi.AVLoadingIndicatorView;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signUp;
    private RadioButton patientRb, doctorRb, maleRb, femaleRb;
    private UserDao userDao;
    private EditText firstName, lastName, licenseNumber, contactNumber, eMail, password, age, height, weight;
    private LinearLayout licenseLinear;
    private TextView signIn;
    private AVLoadingIndicatorView loading;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        initializeFields();

        DaoSession daoSession = ((HeartRytCare) getApplication()).getDaoSession();
        userDao = daoSession.getUserDao();
    }

    private void initializeFields() {
        signUp = (Button) findViewById(R.id.signUp);
        signIn = (TextView) findViewById(R.id.signIn);
        doctorRb = (RadioButton) findViewById(R.id.doctorRB);
        patientRb = (RadioButton) findViewById(R.id.patientRB);
        maleRb = (RadioButton) findViewById(R.id.maleRb);
        femaleRb = (RadioButton) findViewById(R.id.femaleRb);
        licenseLinear = (LinearLayout) findViewById(R.id.licenseLinear);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        licenseNumber = (EditText) findViewById(R.id.licenseNumber);
        contactNumber = (EditText) findViewById(R.id.contactNumber);
        eMail = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        age = (EditText) findViewById(R.id.age);
        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        loading = (AVLoadingIndicatorView) findViewById(R.id.loading);

        Spannable wordToSpan = new SpannableString(getResources().getString(R.string.signin));
        wordToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_link)), 3, wordToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordToSpan.setSpan(new UnderlineSpan(), 3, wordToSpan.length(), 0);
        signIn.setText(wordToSpan);

        signUp.setOnClickListener(this);
        signIn.setOnClickListener(this);
        doctorRb.setOnClickListener(this);
        patientRb.setOnClickListener(this);
        doctorRb.setChecked(true);
        maleRb.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUp:
                signUp();
                break;
            case R.id.signIn:
                signIn();
                break;
            case R.id.doctorRB:
                licenseLinear.setVisibility(View.VISIBLE);
                break;
            case R.id.patientRB:
                licenseLinear.setVisibility(View.GONE);
                break;
        }
    }

    private void signUp() {
        if (TextUtils.isEmpty(firstName.getText())) {
            firstName.setError("This item cannot be empty");
            return;
        }

        if (TextUtils.isEmpty(lastName.getText())) {
            lastName.setError("This item cannot be empty");
            return;
        }

        if (TextUtils.isEmpty(eMail.getText())) {
            eMail.setError("This item cannot be empty");
            return;
        }

        if (TextUtils.isEmpty(password.getText())) {
            password.setError("This item cannot be empty");
            return;
        }

        loading.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(eMail.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(getClass().getSimpleName(), "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            Constants.FIREBASE_UID = firebaseUser.getUid();
                            User user = new User();
                            user.setFirebase_user_id(firebaseUser.getUid());
                            user.setUser_type(doctorRb.isChecked() ? 1 : 0); //1 = doctor, 0 = patient
                            user.setFirst_name(firstName.getText().toString());
                            user.setLast_name(lastName.getText().toString());
                            user.setLicense_number(doctorRb.isChecked() ? licenseNumber.getText().toString() : "NA");
                            user.setContact_number(contactNumber.getText().toString());
                            user.setEmail(eMail.getText().toString());
                            user.setPassword(password.getText().toString());
                            user.setAge(Integer.parseInt(age.getText().toString()));
                            user.setHeight(Integer.parseInt(height.getText().toString()));
                            user.setWeight(Integer.parseInt(weight.getText().toString()));
                            user.setGender(maleRb.isChecked() ? 1 : 0); //1 = male, 0 = female
                            long rowID = userDao.insert(user);
                            Log.d("signup", "onComplete: " + rowID);

                            Intent intent = new Intent(SignupActivity.this, MainMenuActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            loading.setVisibility(View.GONE);
                            Log.w(getClass().getSimpleName(), "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.signinContainer, new SignInFragment());
        ft.addToBackStack("signin");
        ft.commit();
    }
}

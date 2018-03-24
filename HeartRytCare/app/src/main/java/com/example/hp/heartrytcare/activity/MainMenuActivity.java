package com.example.hp.heartrytcare.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.UserFirebase;
import com.example.hp.heartrytcare.fragment.PatientFragment;
import com.example.hp.heartrytcare.fragment.MeasureFragment;
import com.example.hp.heartrytcare.fragment.MessagesFragment;
import com.example.hp.heartrytcare.fragment.DoctorFragment;
import com.example.hp.heartrytcare.fragment.SchedFragment;
import com.example.hp.heartrytcare.fragment.ShareFragment;
import com.example.hp.heartrytcare.fragment.StatFragment;
import com.example.hp.heartrytcare.helper.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = getClass().getSimpleName();
//    private UserDao userDao;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            toolbar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainMenuActivity.this);
                    builder.setTitle("Help");
//                    builder.setMessage("Setting up the connection between your blood pressure monitor and your mobile device: " + " " +
//                            " When the application is first started, it will check if this is the very first time the application has started or if the application has been updated since it was last started");
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                //    Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                                }
                            });
//                    builder.show();
                }
            });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainMenuActivity.this, MainMenuActivity.class);
                startActivity(in);

                //manager.beginTransaction().replace(R.id.relativeLayout_for_fragment, home).commit();
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");
        getUserDetailsFromFirebase();
    }

    @Override
    public void onBackPressed() {
        setTitle("HeartRytCare");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_journal) {
            Intent intent = new Intent(MainMenuActivity.this, JournalActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sched) {
            setTitle("Schedule");
            replaceFragment(new SchedFragment());
        } else if (id == R.id.nav_measure) {
            setTitle("Measure");
            replaceFragment(new MeasureFragment());
        } else if (id == R.id.nav_stat) {
            setTitle("Statistics");
            replaceFragment(new StatFragment());
        } else if (id == R.id.nav_messages) {
            setTitle("Messages");
            replaceFragment(new MessagesFragment());
        } else if (id == R.id.nav_share) {
            setTitle("Share");
            replaceFragment(new ShareFragment());
        } else if (id == R.id.nav_patient) {
            setTitle("Patient");
            replaceFragment(new PatientFragment());
        } else if (id == R.id.nav_doctor) {
            setTitle("Doctor");
            replaceFragment(new DoctorFragment());
        } else if (id == R.id.nav_signout) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sign Out");
            builder.setMessage("Are you sure you want to sign out?");
            builder.setCancelable(false);
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((HeartRytCare) getApplicationContext()).getFirebaseAuth().signOut();
                    Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.create();
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setUserInformation(UserFirebase userFirebase) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        if (userFirebase.user_type == 0) { //patient
            menu.findItem(R.id.nav_doctor).setVisible(true);
            menu.findItem(R.id.nav_patient).setVisible(false);
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.userType)).setText(R.string.patient);
            ((ImageView)navigationView.getHeaderView(0).findViewById(R.id.userIcon)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.patient));
        } else { //doctor
            menu.findItem(R.id.nav_patient).setVisible(true);
            menu.findItem(R.id.nav_doctor).setVisible(false);
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.userType)).setText(R.string.doctor);
            ((ImageView)navigationView.getHeaderView(0).findViewById(R.id.userIcon)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.doctor));
        }
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.relativeLayout_for_fragment, someFragment);
        transaction.addToBackStack("");
        transaction.commit();
    }

    private void getUserDetailsFromFirebase() {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                UserFirebase user = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = snapshot.getValue(UserFirebase.class);
                    if (user != null) {
                        if (Constants.FIREBASE_UID.equals(user.firebase_user_id)) {
                            setUserInformation(user);
                        }
                    }
                }
                if (user == null) {
                    Toast.makeText(MainMenuActivity.this, "User detail not found.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}

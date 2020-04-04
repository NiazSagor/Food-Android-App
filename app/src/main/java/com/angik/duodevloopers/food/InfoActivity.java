package com.angik.duodevloopers.food;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.angik.duodevloopers.food.Model.BottomSheet;
import com.angik.duodevloopers.food.Model.SliderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class InfoActivity extends AppCompatActivity implements SendOTPFragment.OnFragmentInteractionListener,
        InputOTPFragment.OnFragmentInteractionListener, InfoFragment.OnFragmentInteractionListener, BottomSheet.BottomSheetListener {

    EditText name;
    EditText id;

    SharedPreferences sharedPreferences;
    private SharedPreferences putUserInRest;

    private SliderAdapter adapterViewPager;

    private Toolbar toolbar;

    private String verificationID;

    private FirebaseAuth mAuth;

    private ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("hasGone", MODE_PRIVATE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vpPager = findViewById(R.id.viewpager);
        adapterViewPager = new SliderAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        vpPager.setCurrentItem(0);
    }

    @Override
    public void onStart() {
        super.onStart();

        putUserInRest = getSharedPreferences("logoutEvent", MODE_PRIVATE);

        if (putUserInRest.getBoolean("isLoggedOut", false)) {
            Intent intent = new Intent(InfoActivity.this, RestingActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && sharedPreferences.getBoolean("gone", false)) {
            //If user is authenticated and also completed the info
            startActivity(new Intent(InfoActivity.this, MainActivity.class));
            finish();
        } else if (user != null && !sharedPreferences.getBoolean("gone", false)) {
            //If user is authenticated but not yet completed the profile
            openBottomSheet();
        } else if (user == null) {
            vpPager.setCurrentItem(0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences scannedId = getSharedPreferences("id", MODE_PRIVATE);
        scannedId.edit().clear().apply();
    }

    public String getVerificationID() {
        return verificationID;
    }

    public void setVerificationID(String verificationID) {
        this.verificationID = verificationID;
    }

    private void openBottomSheet() {
        BottomSheet bottomSheet = new BottomSheet();

        Bundle bundle = new Bundle();
        bundle.putString("userPhoneNumber", Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber());
        bottomSheet.setArguments(bundle);

        bottomSheet.show(getSupportFragmentManager(), "bottom");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onButtonClick() {
        vpPager.setCurrentItem(2);
    }
}

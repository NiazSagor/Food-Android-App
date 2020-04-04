package com.angik.duodevloopers.food;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.angik.duodevloopers.food.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView profile;
    private TextView name;
    private TextView phone;
    private TextView email;
    private TextView currentVersion;
    private Button logOut;

    User user;

    private DatabaseReference databaseReference;

    private SharedPreferences putUserInRest;
    private SharedPreferences sp_user;
    SharedPreferences sp_hasGone;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);

        getUser();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);//Setting custom icon at the toolbar
        setSupportActionBar(toolbar);
        toolbar.setTitle("Profile");


        setProfile();
        profile = findViewById(R.id.profilePic);
        setImage(profile);

        try {
            setCurrentVersion();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        logOut = findViewById(R.id.button3);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kickOut();
            }
        });
    }

    private void setProfile() {
        name = findViewById(R.id.userName);
        name.setText(user.getName());

        phone = findViewById(R.id.userPhone);
        phone.setText(user.getPhoneNumber());

        email = findViewById(R.id.userEmail);
        email.setText(user.getEmail());
    }

    private void setImage(final ImageView imageView) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getID()).child("Profile Image URL");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String s = Objects.requireNonNull(dataSnapshot.getValue()).toString();//Download Image url
                    Picasso.get().load(s).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void kickOut() {

        putUserInRest = getSharedPreferences("logoutEvent", MODE_PRIVATE);
        putUserInRest.edit().putBoolean("isLoggedOut", true).apply();

        Intent intent = new Intent(ProfileActivity.this, RestingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        ProfileActivity.this.finish();
    }

    private void getUser() {
        sp_user = getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp_user.getString("user", "");//Getting json string
        user = gson.fromJson(json, User.class);
    }

    @SuppressLint("SetTextI18n")
    private void setCurrentVersion() throws PackageManager.NameNotFoundException {
        currentVersion = findViewById(R.id.currentVersion);
        PackageManager manager = this.getPackageManager();
        PackageInfo info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);

        if (info.versionName != null) {
            currentVersion.setText("Current Version : " + info.versionName);
        }
    }
}

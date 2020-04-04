package com.angik.duodevloopers.food;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angik.duodevloopers.food.Model.DatabaseHelper;
import com.angik.duodevloopers.food.Model.FeaturedItemAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ItemActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView toolbarTitle;
    private TextView variant;
    private TextView see;

    private DatabaseReference databaseReference;

    //For item information
    private ArrayList<String> name;
    private ArrayList<String> price;
    private ArrayList<String> details;
    private ArrayList<String> image;

    //Items list
    private RecyclerView mRecyclerView;
    private FeaturedItemAdapter mAdapter;

    private LinearLayout linearLayout;

    private DatabaseHelper helper;//Where we save selected items

    FloatingActionButton button;//Going to ordered summary activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        helper = new DatabaseHelper(this);//Initializing SQLite database

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);//Setting custom icon at the toolbar
        setSupportActionBar(toolbar);

        /* Here toolbar title is another text view inside Toolbar widget
         * That's why we declared a text view separately
         * And did not use toolbar.setTitle()
         */
        toolbarTitle = findViewById(R.id.toolbar_title);
        setToolbarTitle(toolbarTitle);//Setting toolbar title according to the item name

        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemActivity.this, ScrollingActivity.class);
                intent.putExtra("storeName", getIntent().getStringExtra("storeName"));//Getting the current store from the intent and also sending it to the ScrollingActivity
                startActivity(intent);
            }
        });
        see = findViewById(R.id.see);
        changeFont(see);

        variant = findViewById(R.id.variant);//Shows variant of the current item

        /*button = findViewById(R.id.floatingActionButton);//On click floating action button
        button.setAnimation((AnimationUtils.loadAnimation(ItemActivity.this, R.anim.fade_animation_right)));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemActivity.this, ScrollingActivity.class);
                startActivity(intent);
            }
        });*/

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    /*@SuppressLint("SetTextI18n")
    private void setToolbarTitle(TextView textView) {
        Intent intent = getIntent();
        int i = intent.getIntExtra("itemNo", 0);
        switch (i) {
            case 0:
                textView.setText("Beef");
                getData("Beef");//Getting data according the item name
                break;
            case 1:
                textView.setText("Rice");
                getData("Rice");
                break;
            case 2:
                textView.setText("Mejbani");
                getData("Mejbani");
                break;
            case 3:
                textView.setText("Biryani");
                getData("Biryani");
                break;
            default:
                textView.setText("Item Name");
        }
    }*/
    @SuppressLint("SetTextI18n")
    private void setToolbarTitle(TextView textView) {
        Intent intent = getIntent();
        String name = intent.getStringExtra("storeName");
        switch (name) {
            case "Shaowal Restora":
                textView.setText("Shaowal Restora");
                getData("Shaowal Restora");//Getting data according the item name
                break;
            case "Kaftan":
                textView.setText("Kaftan");
                getData("Kaftan");
                break;
            case "Dulavai":
                textView.setText("Dulavai er Dokan");
                getData("Cooling Corner");
                break;
            case "Kutub":
                textView.setText("Kutub");
                getData("Allahr Daan");
                break;
            default:
                textView.setText("Store Name");
        }
    }

    private void getData(String node) {
        databaseReference = FirebaseDatabase.getInstance().getReference(node);
        databaseReference.child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name = new ArrayList<>();
                    price = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String s = Objects.requireNonNull(postSnapshot.getValue()).toString();//Values
                        price.add(s);
                        name.add(postSnapshot.getKey());//Keys
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("Detail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    details = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String s = Objects.requireNonNull(postSnapshot.getValue()).toString();//Values
                        details.add(s);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("Image").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    image = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String s = Objects.requireNonNull(postSnapshot.getValue()).toString();//Image links
                        image.add(s);
                    }
                    //After collecting all 4 array list from database we make our adapter
                    mAdapter = new FeaturedItemAdapter(ItemActivity.this, name, price, details, image);
                    mRecyclerView.setAdapter(mAdapter);
                    variant.setText("Items (" + mAdapter.getItemCount() + ")");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void changeFont(TextView textView) {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/bontserrat_bold.otf");
        textView.setTypeface(custom_font);
    }
}

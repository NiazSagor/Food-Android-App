package com.angik.duodevloopers.food;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angik.duodevloopers.food.Model.MyAdapter;
import com.angik.duodevloopers.food.Model.OrderAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class TodayActivity extends AppCompatActivity implements BurgerFragment.OnFragmentInteractionListener,
        FastFoodFragment.OnFragmentInteractionListener, RollFragment.OnFragmentInteractionListener, SamusaFragment.OnFragmentInteractionListener {

    private TabLayout tabLayout;//Tab layout
    @SuppressWarnings("FieldCanBeLocal")
    private Toolbar toolbar;//Toolbar where title is
    private ViewPager viewPager;//View pager

    private ArrayList<String> names;//Contains all the names of the items' categories

    private LinearLayout layout;//Layout at the bottom of the screen

    private TextView textView;
    private CardView cart;

    private DatabaseReference db_available_items;
    private MyAdapter adapter;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Today's Breakfast");

        viewPager = findViewById(R.id.viewpager);
        int defaultValue = 0;
        int page = getIntent().getIntExtra("TAB", defaultValue);
        viewPager.setCurrentItem(page);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }

        textView = findViewById(R.id.see);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/bontserrat_bold.otf");
        textView.setTypeface(custom_font);

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodayActivity.this, ScrollingActivity.class);
                startActivity(intent);
            }
        });

        layout = findViewById(R.id.layout);
        //On click layout
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodayActivity.this, ScrollingActivity.class);
                startActivity(intent);
            }
        });

        tabLayout = findViewById(R.id.tabs);
        db_available_items = FirebaseDatabase.getInstance().getReference("Available Items Today");
        db_available_items.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Getting from the database to see what items are available for today
                    names = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String s = Objects.requireNonNull(postSnapshot.getValue()).toString();//Values
                        names.add(s);//Contains the names of the items available today
                        tabLayout.addTab(tabLayout.newTab().setText(s));//According to that number of items we add new tab to the tab layout
                        /* This means not all the tabs will be shown, but only those which are available that day
                         * This is controlled by the authority
                         */
                    }
                    //And according to the available names we are making our tab  layout and view pager
                    adapter = new MyAdapter(TodayActivity.this, getSupportFragmentManager(), tabLayout.getTabCount(), names);
                    viewPager.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);//Gravity of the titles
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        //This defines the color for selected and unselected state
        tabLayout.setTabTextColors(Color.parseColor("#757575"), Color.parseColor("#03A9F4"));
        tabLayout.setTabRippleColorResource(R.color.lightGrey);//Adds ripple effect

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setCurrentItem(0);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

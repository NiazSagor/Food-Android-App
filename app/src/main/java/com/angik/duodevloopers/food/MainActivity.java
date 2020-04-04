package com.angik.duodevloopers.food;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.angik.duodevloopers.food.Model.Analytic;
import com.angik.duodevloopers.food.Model.StoreAdapter;
import com.angik.duodevloopers.food.Model.User;
import com.angik.duodevloopers.food.Utility.FontUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {
    private SharedPreferences spUser;//For getting the saved object form InfoActivity

    private CardView today;
    private CardView bellycard;
    private CardView offerCardView;


    private TextView textView;
    private TextView textView1;
    TextView textView2;
    TextView balance;
    private TextView averageExpense;
    private TextView averageQuantity;
    private TextView good;
    private TextView time;
    private TextView orderTime;
    private TextView totalOrder;
    private TextView timeForLunchBreakfast;

    private TextView offerHeaderTextView;
    private TextView offerSubtitleTextView;

    private TextView orderTime2;
    private TextView orderTime3;

    private CircleImageView imageView;

    private ViewFlipper viewFlipper;

    Spinner spinner;

    private TextView name;
    private TextView ID;

    private ArrayList<String> offerImage;

    private DatabaseReference databaseReference;

    User user;

    private RecyclerView mRecyclerView;
    private StoreAdapter mAdapter;

    private ArrayList<String> arrayList = new ArrayList<>();

    private String[] imageAddress = {"https://firebasestorage.googleapis.com/v0/b/food-86e25.appspot.com/o/Food%20Images%2Fstore1-01.jpg?alt=media&token=9c04b5cd-e06c-4c3e-ac02-79990f1ee3d8",
            "https://firebasestorage.googleapis.com/v0/b/food-86e25.appspot.com/o/Food%20Images%2Fstore2-01.jpg?alt=media&token=00d01f04-9ddb-4e76-b543-e781648fbdfb",
            "https://firebasestorage.googleapis.com/v0/b/food-86e25.appspot.com/o/Food%20Images%2Fstore3-01.jpg?alt=media&token=b0401425-2c2f-433b-bf68-13334d111929",
            "https://firebasestorage.googleapis.com/v0/b/food-86e25.appspot.com/o/Food%20Images%2Fstore4-01.jpg?alt=media&token=30b4d072-0d53-4a35-a4de-3a10e0e3b721",
            "https://firebasestorage.googleapis.com/v0/b/food-86e25.appspot.com/o/Food%20Images%2Fstore%205.png?alt=media&token=a1af6a87-fede-4026-a05f-d61dbcf9069b"};

    private String[] imageAddress2 = {"https://firebasestorage.googleapis.com/v0/b/food-86e25.appspot.com/o/Food%20Images%2FOffer1-01.jpg?alt=media&token=21300ab6-8ba9-4afa-8a4f-5eaa3d47ac0d",
            "https://firebasestorage.googleapis.com/v0/b/food-86e25.appspot.com/o/Food%20Images%2FOffer1-01.jpg?alt=media&token=21300ab6-8ba9-4afa-8a4f-5eaa3d47ac0d"
    };

    private ArrayList<String> delivery = new ArrayList<>();

    private Calendar calendar;

    String buildingName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        getUser();//Gets current user via SP

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getID()).child("Status");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String status = dataSnapshot.getValue().toString();
                    if (status.equals("Pending")) {
                        startActivity(new Intent(MainActivity.this, OrderActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Getting the text views and changing their font
        textView = findViewById(R.id.belly);
        textView1 = findViewById(R.id.check);

        orderTime3 = findViewById(R.id.orderTime3);

        averageExpense = findViewById(R.id.amount);
        averageQuantity = findViewById(R.id.quantity);
        good = findViewById(R.id.good);
        time = findViewById(R.id.time);
        orderTime = findViewById(R.id.orderTime);
        timeForLunchBreakfast = findViewById(R.id.stores);
        greetingStatus(good, time, orderTime, timeForLunchBreakfast);

        //Clicking on text view shows animation and reveals another text view
        orderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderTime3.isShown()) {
                    slide_up(MainActivity.this, orderTime3);
                    orderTime3.setVisibility(View.GONE);
                    slide_down(MainActivity.this, orderTime);
                    orderTime.setVisibility(View.VISIBLE);
                } else {
                    orderTime.setVisibility(View.GONE);
                    slide_up(MainActivity.this, orderTime);
                    orderTime3.setVisibility(View.VISIBLE);
                    slide_down(MainActivity.this, orderTime3);
                }
            }
        });

        orderTime3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!orderTime.isShown()) {
                    slide_up(MainActivity.this, orderTime3);
                    orderTime3.setVisibility(View.GONE);
                    slide_down(MainActivity.this, orderTime);
                    orderTime.setVisibility(View.VISIBLE);
                } else {
                    slide_up(MainActivity.this, orderTime);
                    orderTime.setVisibility(View.GONE);
                    orderTime3.setVisibility(View.VISIBLE);
                    slide_down(MainActivity.this, orderTime3);
                }
            }
        });

        new FontUtility(this, textView).changeToBold();
        new FontUtility(this, textView1).changeToBold();
        new FontUtility(this, good).changeToBold();

        imageView = findViewById(R.id.profilePic);
        //setImage(imageView);//Sets image separately as image url is not a part of our User class's object
        imageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_transition_animation));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        //Getting name, id and balance text views and setting texts with the help of database, check database method does this
        name = findViewById(R.id.name);
        ID = findViewById(R.id.ID);
        totalOrder = findViewById(R.id.totalOrder);
        checkDatabase(name, "name");
        checkDatabase(ID, "id");
        //checkDatabase(balance, "balance");
        getTotalOrder(totalOrder, "Per Order Count");
        getAverageExpense(averageExpense, "Total Orders");//Gets per order amount and calculates average and displays in text view
        getAverageCount(averageQuantity, "Per Order Count");//Gets per order count and calculates average and displays in text view

        today = findViewById(R.id.today);
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                startActivity(new Intent(MainActivity.this, ResDemoActivity.class));
            }
        });

        bellycard = findViewById(R.id.bellycard);
        bellycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStoreAndPlaceSPEmpty();
            }
        });

        LinearLayout wallet = findViewById(R.id.wallet);
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Have to open a dialog box about the wallet system and have to add a button for feedback
                showAlertDialogWalletInfo();
            }
        });

        arrayList.add("Shaowal Restora");
        arrayList.add("Kaftan");
        arrayList.add("Dulavai");
        arrayList.add("Kutub");
        arrayList.add("Janota");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        setDeliveryAvailabilityMessage();

        //mRecyclerView.setItemViewCacheSize(0);//Deletes the saved recycled views from the cache state

        //Slide show of food images
        viewFlipper = findViewById(R.id.viewFlipper);

        offerHeaderTextView = findViewById(R.id.needDelivery);
        offerSubtitleTextView = findViewById(R.id.fromCentralCafeteria);
        offerCardView = findViewById(R.id.offerCardView);

        getOffersFromDatabase();

        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setDeliveryAvailabilityMessage() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Delivery");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String s = Objects.requireNonNull(postSnapshot.getValue()).toString();
                        delivery.add(s);
                    }

                    mAdapter = new StoreAdapter(MainActivity.this, arrayList, imageAddress, delivery);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new StoreAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            //Launching lunch activity
                /*String currentTime = new Analytic().returnCurrentTime();
                if (currentTime.compareTo("08:00") >= 0 && currentTime.compareTo("10:00") <= 0) {
                    startActivity(new Intent(MainActivity.this, TodayActivity.class));
                } else {
                    //Launching lunch items activity
                    Intent intent = new Intent(MainActivity.this, ItemActivity.class);
                    intent.putExtra("storeName", arrayList.get(position));
                    startActivity(intent);
                }*/
                            checkAvailability(position);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getOffersFromDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Offers");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    offerHeaderTextView.setVisibility(View.VISIBLE);
                    offerSubtitleTextView.setVisibility(View.VISIBLE);
                    offerCardView.setVisibility(View.VISIBLE);

                    offerImage = new ArrayList<>();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String image = Objects.requireNonNull(postSnapshot.getValue()).toString();//Values
                        flipImage(image);
                    }
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

    private void checkDatabase(final TextView textView, final String node) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getID()).child(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String string = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    if (node.equals("id")) {
                        textView.setText("ID : " + string);
                    } else {
                        textView.setText(string);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAverageExpense(final TextView textView, String node) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getID()).child(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Analytic analytic = dataSnapshot.getValue(Analytic.class);//Analytic class grabs 2 information at the same time, count and total
                    assert analytic != null;
                    long average = analytic.getTotal() / analytic.getCount();
                    animateTextView(average, textView);//Passes the average and text view to the method to animate
                } else {
                    textView.setText("0");//If there is no data in the node
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAverageCount(final TextView textView, String node) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getID()).child(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Analytic analytic = dataSnapshot.getValue(Analytic.class);
                    assert analytic != null;
                    long average = analytic.getTotal() / analytic.getCount();
                    animateTextView(average, textView);
                } else {
                    textView.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Gets the save User object and put in the Common.currentUser variable
    private void getUser() {
        spUser = getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = spUser.getString("user", "");//Getting json string
        user = gson.fromJson(json, User.class);
    }

    //Gets image and sets it to the image view
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

    private void getTotalOrder(final TextView textView, String node) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getID()).child(node);
        databaseReference.child("Count").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long count = (long) dataSnapshot.getValue();
                    animateTextView(count, textView);
                } else {
                    textView.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void animateTextView(long average, final TextView textView) {
        ValueAnimator animator = new ValueAnimator();
        int i = (int) average;//Casting long value into the int type variable
        animator.setObjectValues(0, i);//Here the 2nd parameter is the range of counting
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator.setDuration(2000); //Duration of the anim, which is 2 seconds
        animator.start();
    }

    private void checkAvailability(final int position) {
        switch (position) {
            case 0:
                check(position, "Shaowal Restora");
                return;
            case 1:
                check(position, "Kaftan");
                return;
            case 2:
                check(position, "Cooling Corner");
                return;
            case 3:
                check(position, "Allahr Daan");
            case 4:
                check(position, "Janota");
                return;
        }
    }

    private void check(final int position, String node) {
        databaseReference = FirebaseDatabase.getInstance().getReference(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Items").exists()) {
                    Intent intent = new Intent(MainActivity.this, ItemActivity.class);
                    intent.putExtra("storeName", arrayList.get(position));
                    startActivity(intent);
                } else if (!dataSnapshot.child("Items").exists()) {
                    showAlertDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void slide_down(Context ctx, TextView v) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context ctx, TextView v) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Sorry, Nothing available!");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void showAlertDialogWalletInfo() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage(R.string.walletInfo);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    //Image slide show
    private void flipImage(String imageAddress) {
        ImageView imageView = new ImageView(this);//Making an image view

        //Picasso.get().load(imageAddress).fit().centerCrop().into(imageView);//Setting our image into the image view, fits to the window and crops to the center

        Picasso.get().load(imageAddress).fit().into(imageView);

        viewFlipper.addView(imageView);//Image view is added to view flipper

        viewFlipper.setInAnimation(MainActivity.this, R.anim.fade_animation_right);//Comes from right
        viewFlipper.setOutAnimation(MainActivity.this, R.anim.fade_animation_left);//Goes to left
    }

    private void greetingStatus(TextView textView, TextView textView1, TextView textView2, TextView textView3) {
        calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour <= 10) {
            String s = "Good Morning";
            String s1 = "It's time for some breakfast!";
            String s2 = "Lunch : Booking Time 9 AM to 12 PM";
            String s3 = "Today's Lunch";
            textView.setText(s);
            textView1.setText(s1);
            textView2.setText(s2);
            textView3.setText(s3);
        } else if (hour >= 11 && hour <= 13) {
            String s = "Good Afternoon";
            String s1 = "Have some lunch";
            String s2 = "Lunch : Booking Time 9 AM to 12 PM";
            String s3 = "Today's Lunch";
            textView.setText(s);
            textView1.setText(s1);
            textView2.setText(s2);
            textView3.setText(s3);
        } else if (hour >= 14 && hour <= 20) {
            String s = "Good Evening";
            String s1 = "Time for some snacks!";
            String s3 = "See you tomorrow";
            textView.setText(s);
            textView1.setText(s1);
            textView2.setText(s1);
            textView3.setText(s3);
        } else if (hour > 20 && hour <= 24) {
            String s = "Good Night!";
            String s1 = "Dinner's on the table";
            String s3 = "Hey! What are you doing?";
            textView.setText(s);
            textView1.setText(s1);
            textView2.setText(s1);
            textView3.setText(s3);
        } else {
            String s = "Go to Sleep! Have a good dream";
            String s1 = "Look for something in the freeze!";
            String s3 = "Twinkle twinkle little star!";
            textView.setText(s);
            textView1.setText(s1);
            textView2.setText(s1);
            textView3.setText(s3);
        }
    }

    private void isStoreAndPlaceSPEmpty() {
        SharedPreferences deliveryPlace = getSharedPreferences("place", MODE_PRIVATE);
        SharedPreferences storeName = getSharedPreferences("store", MODE_PRIVATE);

        if (deliveryPlace.getString("place", null) == null || storeName.getString("store", null) == null) {
            showAlertDialog();
        } else {
            startActivity(new Intent(MainActivity.this, OrderActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getUser();
    }
}

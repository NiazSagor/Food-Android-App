package com.angik.duodevloopers.food;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.angik.duodevloopers.food.Model.TodayOrderAdapter;
import com.angik.duodevloopers.food.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

@SuppressWarnings("ALL")
public class OrderActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;

    User user;

    private TextView totalPrice;
    private TextView count;
    private TextView orderNo;
    private TextView date;
    private TextView timeMessage;
    private Button button;

    private ArrayList<String> itemName = new ArrayList<>();
    private ArrayList<Integer> itemCount = new ArrayList<>();

    private DatabaseReference databaseReference;
    private SharedPreferences spUser;//For getting the saved object form InfoActivity
    private SharedPreferences deliveryPlace;
    private SharedPreferences storeName;

    private TodayOrderAdapter adapter;

    public static final String CHANNEL_1_ID = "FoodUp Notification Channel";
    private NotificationManagerCompat notificationManagerCompat;
    private Notification notification;

    private String messageNotification;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        notificationManagerCompat = NotificationManagerCompat.from(this);
        getUser();

        deliveryPlace = getSharedPreferences("place", MODE_PRIVATE);
        storeName = getSharedPreferences("store", MODE_PRIVATE);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);//Setting custom icon at the toolbar
        setSupportActionBar(toolbar);
        toolbar.setTitle("Today's Order");
        toolbar.setTitleTextColor(Color.BLACK);

        listView = findViewById(R.id.bellyList);
        totalPrice = findViewById(R.id.totalPrice);
        changeFont(totalPrice);
        setTotalPrice(totalPrice);

        count = findViewById(R.id.count);

        orderNo = findViewById(R.id.orderNo);
        orderNo.setText("#0");

        date = findViewById(R.id.date);

        timeMessage = findViewById(R.id.timeMessage);
        button = findViewById(R.id.button);
        getStatus(button, timeMessage);

        Calendar c = Calendar.getInstance();

        String s = DateFormat.getDateInstance(DateFormat.MONTH_FIELD).format(c.getTime());
        date.setText(s);

        getData();
    }

    private void changeFont(TextView textView) {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/bontserrat_bold.otf");
        textView.setTypeface(custom_font);
    }

    private void getData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child("Total Orders");//Getting total order
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String s = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    int i = Integer.parseInt(s);
                    orderNo.setText("#" + i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child(user.getID());//User database
        //Finding the items
        databaseReference.child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int j = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String string = Objects.requireNonNull(postSnapshot.getValue()).toString();
                        itemName.add(j, string);
                        j++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Finding the quantity
        databaseReference.child("Quantity").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int j = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        int i = Integer.parseInt(Objects.requireNonNull(postSnapshot.getValue()).toString());
                        itemCount.add(j, i);
                        j++;
                    }
                    adapter = new TodayOrderAdapter(OrderActivity.this, itemName, itemCount);
                    listView.setAdapter(adapter);
                    count.setText("Ordered Items (" + adapter.getCount() + ")");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Getting current total ordered price
    private void setTotalPrice(final TextView textView) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getID()).child("Current Order");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String s = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    textView.setText(s + " Tk");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Getting button status or order status
    private void getStatus(final Button button, final TextView textView) {
        final Calendar calendar = Calendar.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getID()).child("Status");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String s = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    if (s.equals("Served")) {
                        button.setBackground(getResources().getDrawable(R.drawable.button_bg_2));
                        button.setText("BOOKED");
                        button.setTextColor(Color.WHITE);
                        toolbar.setTitle("Previous Order");
                        textView.setVisibility(View.VISIBLE);

                        if (deliveryPlace.getString("place", null).equals("Pick Up From Store")) {
                            messageNotification = "Your lunch is ready, collect it from " + storeName.getString("store", null).toUpperCase() + " at your own time.";
                            textView.setText(messageNotification);
                        } else {
                            messageNotification = "Your lunch from " + storeName.getString("store", null) + " is ready. Please be at " + deliveryPlace.getString("place", null).toUpperCase() + ". Delivery will be arriving soon.";
                            textView.setText(messageNotification);
                        }

                        sendPushNotification();

                    } else if (s.equals("Pending")) {
                        textView.setVisibility(View.VISIBLE);
                    }
                } else if (!dataSnapshot.exists()) {
                    button.setText("NO ORDERS RIGHT NOW");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUser() {
        spUser = getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = spUser.getString("user", "");//Getting json string
        user = gson.fromJson(json, User.class);
    }

    private void sendPushNotification() {
        String title = "Hello " + user.getName();

        //This intent is for if the notification is clicked by the user
        //Defining where the user should go
        Intent resultIntent = new Intent(this, OrderActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);//TaskStackBuilder is used if we want to add other activities in back stack
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.order);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageNotification)
                    .setLargeIcon(bitmap)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setColor(Color.GREEN)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(resultPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build();

            notificationManagerCompat.notify(1, notification);
        } else {
            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title)
                    .setContentText(messageNotification)
                    .setLargeIcon(bitmap)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setColor(Color.GREEN)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(resultPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build();

            notificationManagerCompat.notify(1, notification);
        }
    }
}
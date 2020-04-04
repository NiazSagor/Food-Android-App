package com.angik.duodevloopers.food;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.angik.duodevloopers.food.Model.Analytic;
import com.angik.duodevloopers.food.Model.BottomSheetOrderConfirmation;
import com.angik.duodevloopers.food.Model.Common;
import com.angik.duodevloopers.food.Model.DatabaseHelper;
import com.angik.duodevloopers.food.Model.NonScrollListView;
import com.angik.duodevloopers.food.Model.OrderAdapter;
import com.angik.duodevloopers.food.Model.User;
import com.angik.duodevloopers.food.Utility.FontUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import static com.angik.duodevloopers.food.Model.DatabaseHelper.TABLE_NAME;

@SuppressWarnings("ALL")
public class ScrollingActivity extends AppCompatActivity implements BottomSheetOrderConfirmation.BottomSheetListener {

    private NonScrollListView listView;//List view which shows the orders

    private OrderAdapter adapter;//Making a OrderAdapter to set the orders into our custom layout

    private TextView totalPrice;//Shows total price
    private TextView rp;//Shows total RP
    private TextView additionalCharge;
    private TextView additionalChargeAmount;

    private TextView totalWithDiscount;

    private String DB_Ref;

    private CardView cartCardView;
    private NestedScrollView nestedScrollView;

    private SharedPreferences sharedPreferences;//Loads saved data from OrderAdapter
    private SharedPreferences spUser;//For getting the saved object form InfoActivity
    private SharedPreferences deliveryPlace;//Delivery Place
    private SharedPreferences storeName;//Store name from where the order is happened

    private Button commit;//Order upload

    private DatabaseReference db_building;
    private DatabaseReference db_order;
    private DatabaseReference db_total;
    private DatabaseReference db_user;
    private DatabaseReference db_offer;

    User user;

    //Name and price
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> price = new ArrayList<>();
    //Item quantity
    private ArrayList<Integer> quantity = new ArrayList<>();

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private DatabaseHelper databaseHelper;//SQLite database where selected items are saved
    private Spinner spinner;
    private String buildingName = null;

    private int total;
    private Handler handler;

    private LinearLayout offerLayout;
    private LinearLayout additionalChargeLayout;


    //new test
    int totalPriceAmount = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        getUser();

        additionalCharge = findViewById(R.id.additionalCharge);

        handler = new Handler();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("You've Craved");
        toolbar.setTitleTextColor(Color.BLACK);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/montserrat.otf");

        //Changing the type face of collapsing toolbar
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setCollapsedTitleTypeface(custom_font);
        collapsingToolbarLayout.setExpandedTitleTypeface(custom_font);

        databaseHelper = new DatabaseHelper(this);
        Cursor data = databaseHelper.getListContents();
        while (data.moveToNext()) {
            name.add(data.getString(1));
            price.add(data.getString(2));
        }

        //References to the order database with the ID
        db_order = FirebaseDatabase.getInstance().getReference("Orders").child(user.getID());
        db_user = FirebaseDatabase.getInstance().getReference("Users").child(user.getID());

        //Loading sp which has the total amount
        sharedPreferences = getSharedPreferences("total", MODE_PRIVATE);

        deliveryPlace = getSharedPreferences("place", MODE_PRIVATE);
        storeName = getSharedPreferences("store", MODE_PRIVATE);

        storeName.edit().putString("store", getIntent().getStringExtra("storeName")).apply();

        //Getting the saved ArrayLists which were saved at ItemsAdapter, via getArrayList method
        //name = getArrayList("name");
        //price = getArrayList("price");

        ArrayList<Integer> numbers = new ArrayList<>();//Making a new ArrayList of integers where price array list is converted to integer

        for (int i = 0; i < price.size(); i++) {
            numbers.add(Integer.parseInt(price.get(i)));//Converting from string to integer
        }

        listView = findViewById(R.id.listView);
        //Making and setting the adapter to the list view
        adapter = new OrderAdapter(ScrollingActivity.this, name, numbers);
        listView.setAdapter(adapter);
        setAdapter(adapter);//Saving this adapter to the method for later use
        adapter.execute();

        totalPrice = findViewById(R.id.totalPrice);

        spinner = findViewById(R.id.spinner1);

        if (getIntent().getStringExtra("storeName").equals("Dulavai")) {
            String[] names = getResources().getStringArray(R.array.building);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names);
            spinner.setAdapter(spinnerAdapter);
        } else {
            String[] self = getResources().getStringArray(R.array.onlySelf);
            ArrayAdapter<String> onlySelfAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, self);
            spinner.setAdapter(onlySelfAdapter);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                buildingName = adapterView.getItemAtPosition(i).toString();
                if (buildingName.equals("Pick Up From Store")) {
                    Common.currentSelectedDeliveryType = buildingName;
                    additionalCharge = findViewById(R.id.additionalCharge);
                    additionalCharge.setText("Packaging Charge : Per Item 3 Tk");
                    adapter.execute();
                } else {
                    Common.currentSelectedDeliveryType = buildingName;

                    additionalCharge.setText("Delivery Charge : Per Item 5 Tk");

                    additionalChargeLayout = findViewById(R.id.additionalChargeLayout);

                    adapter.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(ScrollingActivity.this, "Please select a delivery type", Toast.LENGTH_SHORT).show();
            }
        });

        commit = findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentTime = new Analytic().returnCurrentTime();
                //Shows error if the current time is past 12 PM of the day.
                if (currentTime.compareTo("12:01") >= 0 || currentTime.compareTo("08:59") <= 0) {
                    showAlertDialogOnTimeError("Rest of the day");
                    if (adapter.getCount() != 0) {
                        SQLiteDatabase db = openOrCreateDatabase("mylist.db", MODE_PRIVATE, null);
                        db.delete(TABLE_NAME, null, null);
                        sharedPreferences.edit().clear().apply();
                    }
                    return;
                }
                //Shows error if the current time is past 9:31 AM
                /*if (currentTime.compareTo("09:31") >= 0 && currentTime.compareTo("10:59") <= 0) {
                    showAlertDialogOnTimeError("After breakfast time is over");
                    if (adapter.getCount() != 0) {
                        SQLiteDatabase db = openOrCreateDatabase("mylist.db", MODE_PRIVATE, null);
                        db.delete(TABLE_NAME, null, null);
                        sharedPreferences.edit().clear().apply();
                    }
                    return;
                }*/
                //Shows error if there is nothing in cart
                if (adapter.getCount() == 0) {
                    Snackbar.make(view, "Your belly seems empty!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                //If above issues are dealt then proceed for the order placement
                //Moving the values saved in count array to a array list
                for (int i = 0; i < Common.count.length; i++) {
                    quantity.add(i, Common.count[i]);
                }

                openConfirmationBottomSheet();
            }
        });

        //TODO : Have to create a method where current RP is checked from database before submitting an order
        rp = findViewById(R.id.rp);

        //Info about the RP, on click shows a alert dialog
        ImageView rpInfo = findViewById(R.id.rpInfo);
        rpInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogRP();
            }
        });

        //Offer
        offerLayout = findViewById(R.id.offerLayout);
        checkIfDiscountAvailable();
    }

    private void checkIfDiscountAvailable() {
        final TextView eligibilityTextView = findViewById(R.id.discountEligibleText);
        new FontUtility(ScrollingActivity.this, eligibilityTextView).changeToBold();
        totalWithDiscount = findViewById(R.id.totalWithDiscount);
        final LinearLayout totalWithDiscountLayout = findViewById(R.id.totalWithDiscountLayout);

        db_offer = FirebaseDatabase.getInstance().getReference("Discount Offer");

        db_offer.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getChildrenCount() <= 21) {
                        offerLayout.setVisibility(View.VISIBLE);
                        eligibilityTextView.setVisibility(View.VISIBLE);
                        totalWithDiscountLayout.setVisibility(View.VISIBLE);

                    } else {
                        offerLayout.setVisibility(View.GONE);
                        eligibilityTextView.setVisibility(View.GONE);
                        totalWithDiscountLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Return array list from sp
    /*public ArrayList<String> getArrayList(String key) {
        SharedPreferences prefs = getSharedPreferences("items", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }*/

    private void sendOrder(final int amount, final View view) {
        db_order.child("Items").setValue(name);
        db_order.child("Quantity").setValue(quantity);
        db_user.child("Status").setValue("Pending");

        switch (getIntent().getStringExtra("storeName")) {
            case "Shaowal Restora":
                db_building = FirebaseDatabase.getInstance().getReference("Order Shaowal");
                break;
            case "Kaftan":
                db_building = FirebaseDatabase.getInstance().getReference("Order Kaftan");
                break;
            case "Dulavai":
                db_building = FirebaseDatabase.getInstance().getReference("Building");
                break;
            case "Kutub":
                db_building = FirebaseDatabase.getInstance().getReference("Order Kutub");
                break;
            case "Janota":
                db_building = FirebaseDatabase.getInstance().getReference("Order Janota");
                break;

            default:
                db_building = FirebaseDatabase.getInstance().getReference("");
        }

        db_building.child(buildingName).child(user.getID()).setValue(user.getID());

        addLifeTimeExpense(amount);
        addLifeTimeOrderCount(quantity);

        Snackbar.make(view, "Yeah! Done. Now sit tight!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        totalOrderCountDB();//Increases the total order amount in the database

        overallTotalOfItems();

        clearDB();
    }

    //Returns saved adapter
    public OrderAdapter getAdapter() {
        return adapter;
    }

    //Stores currently set adapter
    private void setAdapter(OrderAdapter adapter) {
        this.adapter = adapter;
    }

    private void totalOrderCountDB() {
        db_total = FirebaseDatabase.getInstance().getReference("Orders").child("Total Orders");
        db_total.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                int i = Integer.parseInt(s);
                i++;
                db_total.setValue(String.valueOf(i));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addLifeTimeExpense(final int price) {
        final DatabaseReference lifeTimePrice = FirebaseDatabase.getInstance().getReference("Users").child(user.getID()).child("Total Orders");
        lifeTimePrice.child("Count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String s = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    int i = Integer.parseInt(s);
                    i++;
                    lifeTimePrice.child("Count").setValue(i);
                } else {
                    lifeTimePrice.child("Count").setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lifeTimePrice.child("Total").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String s = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    int i = Integer.parseInt(s);
                    i = i + price;
                    lifeTimePrice.child("Total").setValue(i);
                } else {
                    lifeTimePrice.child("Total").setValue(price);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Adding the current ordered amount to the database to use it to show total amount in the order activity
        final DatabaseReference currentOrderPrice = FirebaseDatabase.getInstance().getReference("Users").child(user.getID()).child("Current Order");
        currentOrderPrice.setValue(price);
    }

    private void addLifeTimeOrderCount(ArrayList<Integer> arrayList) {
        int count = 0;
        //Getting the total items count from the current order
        for (int i = 0; i < arrayList.size(); i++) {
            count = count + arrayList.get(i);
        }
        final DatabaseReference lifeTimeCount = FirebaseDatabase.getInstance().getReference("Users").child(user.getID()).child("Per Order Count");
        lifeTimeCount.child("Count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String s = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    int i = Integer.parseInt(s);
                    i++;
                    lifeTimeCount.child("Count").setValue(i);
                } else {
                    lifeTimeCount.child("Count").setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final int finalCount = count;
        lifeTimeCount.child("Total").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String s = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    int i = Integer.parseInt(s);
                    i = i + finalCount;
                    lifeTimeCount.child("Total").setValue(i);
                } else {
                    lifeTimeCount.child("Total").setValue(finalCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Updates the node Order Amount node where quantity for each items ordered is stored
    private void overallTotalOfItems() {
        final DatabaseReference databaseReference;

        switch (getIntent().getStringExtra("storeName")) {
            case "Shaowal Restora":
                databaseReference = FirebaseDatabase.getInstance().getReference("Ordered Amount Shaowal");
                break;
            case "Kaftan":
                databaseReference = FirebaseDatabase.getInstance().getReference("Ordered Amount Kaftan");
                break;
            case "Dulavai":
                databaseReference = FirebaseDatabase.getInstance().getReference("Ordered Amount");
                break;
            case "Kutub":
                databaseReference = FirebaseDatabase.getInstance().getReference("Ordered Amount Faisal");
                break;
            case "Janota":
                databaseReference = FirebaseDatabase.getInstance().getReference("Ordered Amount Janota");
                break;
            default:
                databaseReference = FirebaseDatabase.getInstance().getReference("aaaa");
        }

        //Database ref of Order Amount

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Making a loop which goes on for the size of the array list name, means how many items have been ordered by the user
                for (int i = 0; i < name.size(); i++) {
                    //If the item is already present in the node then get the value and increase it by the current amount
                    if (dataSnapshot.child(name.get(i)).exists()) {
                        //Now one thing has to be in consideration, if the item is present already, then we have to make another DB REF to get
                        //to that item's value
                        final int finalI = i;
                        //That's what we are doing right here
                        databaseReference.child(name.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String s = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                int convertedValueInt = Integer.parseInt(s);
                                convertedValueInt = convertedValueInt + quantity.get(finalI);//Current ordered amount of the item
                                databaseReference.child(name.get(finalI)).setValue(convertedValueInt);//Setting the updated value to that node
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        //If the item is not present then simply make the node and put the value of it
                        databaseReference.child(name.get(i)).setValue(quantity.get(i));
                    }
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

    private void clearDB() {
        SQLiteDatabase db = openOrCreateDatabase("mylist.db", MODE_PRIVATE, null);
        db.delete(TABLE_NAME, null, null);
        deliveryPlace.edit().putString("place", buildingName).apply();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ScrollingActivity.this);
        builder1.setMessage("Discard Selected Item?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SQLiteDatabase db = openOrCreateDatabase("mylist.db", MODE_PRIVATE, null);
                        //db.execSQL("DROP TABLE IF EXISTS user");
                        db.delete(TABLE_NAME, null, null);
                        ScrollingActivity.this.finish();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();//If no close the dialog box
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void showAlertDialogRP() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ScrollingActivity.this);
        builder1.setMessage(R.string.rpinfo);
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

    private void showAlertDialogOnTimeError(String timeReference) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ScrollingActivity.this);

        //Controlling the dialog message according to the time of the day
        if (timeReference.equals("Rest of the day")) {
            builder1.setMessage("Ordering is not available during this time and will be available from 9 AM to 12 PM");
        } else if (timeReference.equals("After breakfast time is over")) {
            builder1.setMessage("Ordering is not available during this time and will be available at 11 AM");
        }

        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        ScrollingActivity.this.finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void showProgressDialog() {
        ProgressDialog mDialog = new ProgressDialog(getApplicationContext());
        mDialog.setMessage("Please Wait");
        mDialog.show();
    }

    private void openConfirmationBottomSheet() {
        BottomSheetOrderConfirmation bottomSheetOrderConfirmation = new BottomSheetOrderConfirmation();

        Bundle bundle = new Bundle();

        if (buildingName.equals("Pick Up From Store")) {
            bundle.putString("type", "book");
        } else {
            bundle.putString("type", "order");
        }

        bottomSheetOrderConfirmation.setArguments(bundle);

        bottomSheetOrderConfirmation.show(getSupportFragmentManager(), "bottomSheetOrderConfirmation");
    }

    @Override
    public void onBackPressed() {
        if (adapter.getCount() == 0) {
            super.onBackPressed();
        } else if (adapter.getCount() >= 1) {
            showAlertDialog();
        }
    }


    private boolean checkItemCountIsZero() {
        for (int i = 0; i < Common.count.length; ) {
            if (Common.count[i] == 0) {
                i++;
            } else if (Common.count[i] > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onButtonClick() {

        if (offerLayout.getVisibility() == View.VISIBLE) {
            total = (int) (Integer.parseInt(totalWithDiscount.getText().toString()));

            if (total == 0) {
                Toast.makeText(this, "Sorry internel error occured! Please try again", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else {
                db_offer.child("User").child(user.getID()).setValue(user.getID());
            }
        } else {
            total = Integer.parseInt(totalPrice.getText().toString());
        }

        sendOrder(total, getWindow().getDecorView());

        Intent intent = new Intent(ScrollingActivity.this, OrderActivity.class);
        startActivity(intent);

        finish();
    }
}

package com.angik.duodevloopers.food.Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.angik.duodevloopers.food.R;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class ItemsAdapter extends ArrayAdapter<String> {
    private Activity mC;//Getting current activity

    private ArrayList<String> mItemName;
    private ArrayList<String> mItemPrice;

    private DatabaseHelper databaseHelper;//Which is our SQLite database helper that helps to store selected items and prices in database

    public ItemsAdapter(Activity c, ArrayList<String> itemName, ArrayList<String> itemPrice) {
        super(c, R.layout.items);

        this.mC = c;
        this.mItemName = itemName;
        this.mItemPrice = itemPrice;

        databaseHelper = new DatabaseHelper(mC);
    }

    static class ViewHolder {
        TextView itemName, itemPrice, itemCount;
    }

    @Override
    public int getCount() {
        return mItemName.size();
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    public View getView(final int position, View view, final ViewGroup parent) {

        final ViewHolder holder = new ViewHolder();
        final String s = "ADDED";
        final TextView textView = mC.findViewById(R.id.count);//Shows the current amount of items selected by the user
        textView.setText("" + databaseHelper.getListContents().getCount());//Getting it from the SQLite database

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mC.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.items, parent, false);

            view.setAnimation(AnimationUtils.loadAnimation(mC, R.anim.fade_animation));//Setting an animation left to right
        }
        holder.itemName = view.findViewById(R.id.itemName);
        holder.itemPrice = view.findViewById(R.id.itemPrice);
        holder.itemCount = view.findViewById(R.id.count);
        holder.itemCount.setVisibility(View.INVISIBLE);//Primarily setting it invisible

        //Setting the values we got from the array list, in their view position
        holder.itemName.setText(mItemName.get(position));
        holder.itemPrice.setText(mItemPrice.get(position) + " Tk");

        //After using custom adapter all the click events now handled in the adapter class itself, not in fragment or activity
        //So we are setting a on click listener in our custom view, if the view is clicked (not set on item click listener)
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking if the total selected items are greater than 5, if yes we do not let user add another one further
                if (databaseHelper.getListContents().getCount() == 5) {
                    Toast.makeText(mC, "Can not order more than 5 items at a time", Toast.LENGTH_SHORT).show();
                    return;
                }

                //If total selected items are not greater than 5 then we let the user to proceed and add item to the bucket
                holder.itemCount.setVisibility(View.VISIBLE);//If clicked then text view is visible which indicates that the item is added
                holder.itemCount.setText(s);//Setting text ADDED

                LinearLayout layout = mC.findViewById(R.id.layout);
                layout.setVisibility(View.VISIBLE);//Making the bottom layout visible

                addData(mItemName.get(position), mItemPrice.get(position));//Adds selected items and their prices to the database

                //Updating the counter text view as user adds another item to the bucket
                mC.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("" + databaseHelper.getListContents().getCount());
                        //textView.setAnimation(AnimationUtils.loadAnimation(mC, R.anim.text));
                    }
                });

            }
        });

        return view;
    }

    //Adds data to the SQLite database
    private void addData(String item1, String item2) {
        databaseHelper.addData(item1, item2);
        /*boolean insertData = databaseHelper.addData(item1);
        if (insertData) {
            Toast.makeText(mC, "Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mC, "Unsuccessful", Toast.LENGTH_SHORT).show();
        }*/
    }
}

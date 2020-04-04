package com.angik.duodevloopers.food.Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.angik.duodevloopers.food.R;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("ALL")
public class OrderAdapter extends ArrayAdapter<String> {
    private Activity c;//Current activity

    private int totalPrice = 0;

    private ArrayList<String> mItemName;
    private ArrayList<Integer> mItemPrice;

    private OrderAdapter orderAdapter;

    private SharedPreferences sharedPreferences;//Storing total price

    private int[] count;

    public OrderAdapter(final Activity c, ArrayList<String> itemName, ArrayList<Integer> itemPrice) {
        super(c, R.layout.orderlistview);

        this.c = c;
        this.mItemName = itemName;
        this.mItemPrice = itemPrice;

        /* We want to calculate the initial price of the selected items from the activity
         * So we are calculating that in the constructor by the time of OrderAdapter is first invoked in ScrollingActivity
         * We make a sp to store the calculated total amount
         */
        sharedPreferences = c.getSharedPreferences("total", Context.MODE_PRIVATE);


        for (int i = 0; i < mItemPrice.size(); i++) {
            totalPrice = totalPrice + mItemPrice.get(i);
        }
        //sharedPreferences.edit().putInt("nowTotal", totalPrice).apply();//Updates the total amount in sp

        count = new int[getCount()];
        Arrays.fill(count, 1);
        Common.count = count;
    }

    static class ViewHolder {
        TextView itemName, itemPrice, count;
        Button removeButton;
        ImageView minusButton, plusButton;
    }

    @Override
    public int getCount() {
        return mItemName.size();
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    public View getView(final int position, View view, final ViewGroup parent) {
        LayoutInflater inflater;
        final ViewHolder holder;

        if (view == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.orderlistview, parent, false);
            holder = new ViewHolder();

            holder.itemName = view.findViewById(R.id.itemName);//Item name
            changeFont(holder.itemName);
            holder.itemPrice = view.findViewById(R.id.itemPrice);//Item price

            holder.count = view.findViewById(R.id.quantity);//This view is in the middle of plus and minus button

            //holder.removeButton = view.findViewById(R.id.removeButton);//Remove button in the list, deletes an entry
            holder.minusButton = view.findViewById(R.id.minusButton);//Minus button, decreases the quantity
            holder.plusButton = view.findViewById(R.id.plusButton);//Plus button, increases the quantity

            //Setting the values we got from the array list, in their view position
            holder.itemName.setText(mItemName.get(position));
            holder.itemPrice.setText("" + mItemPrice.get(position) + " Tk");

            //Reduces quantity by one
            holder.minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String plusMinusQuantity = holder.count.getText().toString();//Getting the current value of current view's by using position
                    int itemCount = Integer.parseInt(plusMinusQuantity);//Making the value integer type so that we can change the value

                    if (itemCount == 0) {
                        //Toast toast = Toast.makeText(getContext(), "Can not have less than 1", Toast.LENGTH_SHORT);//Using getContext() as we are using Toast in class not in activity or fragment
                        //toast.show();
                        remove(mItemName.get(position));
                        notifyDataSetChanged();
                        return;
                    }
                    itemCount--;//Changing the value by one

                    count[position] = itemCount;

                    Common.count = count;

                    //Setting the updated value in the quantity and price TextViews
                    holder.count.setText("" + itemCount);
                    holder.itemPrice.setText("" + itemCount * mItemPrice.get(position) + " Tk");
                    totalPriceSubtraction(mItemPrice.get(position));//This invokes the method which reduces the current total price
                }
            });

            //Increases quantity by one
            holder.plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String plusMinusQuantity = holder.count.getText().toString();
                    int itemCount = Integer.parseInt(plusMinusQuantity);

                    if (itemCount == 5) {
                        Toast toast = Toast.makeText(getContext(), "Can not have more than 5", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    itemCount++;

                    count[position] = itemCount;
                    Common.count = count;

                    holder.count.setText("" + itemCount);
                    holder.itemPrice.setText("" + itemCount * mItemPrice.get(position) + " Tk");
                    totalPriceAddition(mItemPrice.get(position));//This invokes the method which increases the current total price
                }
            });

            //Removes an entry from the cart
            /*holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Deleting the values in the ArrayLists
                    String plusMinusQuantity = holder.count.getText().toString();//Getting current view's item count
                    int itemCount = Integer.parseInt(plusMinusQuantity);
                    totalPriceSubtraction(itemCount * mItemPrice.get(position));//With the count total amount is calculated which needs to subtracted from total amount

                    //Removes entry
                    mItemName.remove(position);
                    mItemPrice.remove(position);
                    ((ScrollingActivity) c).getAdapter().notifyDataSetChanged();//Getting the adapter which was saved at ScrollingActivity to notify the change
                }
            });*/
        }
        return view;
    }

    //Adds price with the increasing item quantity
    private void totalPriceAddition(int price) {
        int i = c.getSharedPreferences("total", Context.MODE_PRIVATE).getInt("nowTotal", 0);//Gets the current total saved price
        i = i + price;//Increases

        totalPrice = totalPrice + price;

        final int finalI = i;
        //Running it on the ui thread to change the data from this adapter class directly to the UI in real time
        c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Finding and setting the text in the text view
                TextView totalPriceTextView = c.findViewById(R.id.totalPrice);

                TextView totalWithDiscount = c.findViewById(R.id.totalWithDiscount);

                LinearLayout offerLayout = c.findViewById(R.id.offerLayout);

                TextView additionalChargeTextView = c.findViewById(R.id.additionalChargeAmount);
                if (Common.currentSelectedDeliveryType != null) {
                    if (Common.currentSelectedDeliveryType.equals("Pick Up From Store")) {
                        int currentAdditionalCharge = Integer.parseInt(additionalChargeTextView.getText().toString());
                        currentAdditionalCharge += 3;
                        additionalChargeTextView.setText("" + currentAdditionalCharge);

                        //Total price with packaging charge
                        //textView.setText("" + (totalPrice + Integer.parseInt(additionalChargeTextView.getText().toString())));

                        //Changed in 24 nov 22:34, not counting the packaging chanrge
                        totalPriceTextView.setText("" + totalPrice);

                        if (offerLayout.getVisibility() == View.VISIBLE) {
                            int discoutAmount = (int) (Integer.parseInt(totalPriceTextView.getText().toString()) * 0.1);
                            int discountedPrice = (Integer.parseInt(totalPriceTextView.getText().toString()) - discoutAmount);
                            totalWithDiscount.setText("" + discountedPrice);
                        } else {
                            totalPriceTextView.setText("" + totalPrice);
                        }

                    } else {
                        int currentAdditionalCharge = Integer.parseInt(additionalChargeTextView.getText().toString());
                        currentAdditionalCharge += 5;
                        additionalChargeTextView.setText("" + currentAdditionalCharge);
                        //Changed in 24 nov 22:34, not counting the packaging charge
                        //totalPriceTextView.setText("" + (totalPrice + Integer.parseInt(additionalChargeTextView.getText().toString())));
                        totalPriceTextView.setText("" + totalPrice);
                        if (offerLayout.getVisibility() == View.VISIBLE) {
                            int discoutAmount = (int) (Integer.parseInt(totalPriceTextView.getText().toString()) * 0.1);
                            int discountedPrice = (Integer.parseInt(totalPriceTextView.getText().toString()) - discoutAmount);
                            totalWithDiscount.setText("" + discountedPrice);
                        } else {
                            totalPriceTextView.setText("" + totalPrice);
                        }
                    }
                } else {
                    Toast.makeText(c, "Is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sharedPreferences.edit().putInt("nowTotal", Common.totalSpend).apply();//Updates the saved data with new one
    }

    private void totalPriceSubtraction(int price) {
        int i = c.getSharedPreferences("total", Context.MODE_PRIVATE).getInt("nowTotal", 0);
        i = i - price;

        assert totalPrice > 0;
        totalPrice = totalPrice - price;

        final int finalI = i;
        c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Finding and setting the text in the text view
                TextView totalPriceTextView = c.findViewById(R.id.totalPrice);

                TextView totalWithDiscount = c.findViewById(R.id.totalWithDiscount);

                LinearLayout offerLayout = c.findViewById(R.id.offerLayout);

                TextView additionalChargeTextView = c.findViewById(R.id.additionalChargeAmount);
                if (Common.currentSelectedDeliveryType != null) {
                    if (Common.currentSelectedDeliveryType.equals("Pick Up From Store")) {
                        int currentAdditionalCharge = Integer.parseInt(additionalChargeTextView.getText().toString());
                        currentAdditionalCharge -= 3;
                        additionalChargeTextView.setText("" + currentAdditionalCharge);

                        //Total price with packaging charge
                        //textView.setText("" + (totalPrice + Integer.parseInt(additionalChargeTextView.getText().toString())));

                        totalPriceTextView.setText("" + totalPrice);

                        if (offerLayout.getVisibility() == View.VISIBLE) {
                            int discoutAmount = (int) (Integer.parseInt(totalPriceTextView.getText().toString()) * 0.1);
                            int discountedPrice = (Integer.parseInt(totalPriceTextView.getText().toString()) - discoutAmount);
                            totalWithDiscount.setText("" + discountedPrice);
                        } else {
                            //Changed in 24 nov 22:34, not counting the packaging chanrge
                            totalPriceTextView.setText("" + totalPrice);
                        }

                    } else {
                        int currentAdditionalCharge = Integer.parseInt(additionalChargeTextView.getText().toString());
                        currentAdditionalCharge -= 5;
                        additionalChargeTextView.setText("" + currentAdditionalCharge);

                        //totalPriceTextView.setText("" + (totalPrice + Integer.parseInt(additionalChargeTextView.getText().toString())));
                        totalPriceTextView.setText("" + totalPrice);
                        if (offerLayout.getVisibility() == View.VISIBLE) {
                            int discoutAmount = (int) (Integer.parseInt(totalPriceTextView.getText().toString()) * 0.1);
                            int discountedPrice = (Integer.parseInt(totalPriceTextView.getText().toString()) - discoutAmount);
                            totalWithDiscount.setText("" + discountedPrice);
                        } else {
                            //Changed in 24 nov 22:34, not counting the packaging chanrge
                            totalPriceTextView.setText("" + totalPrice);
                        }
                    }
                } else {
                    Toast.makeText(c, "Is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sharedPreferences.edit().putInt("nowTotal", i).apply();
    }

    public void execute() {
        c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView additionalChargeAmount = c.findViewById(R.id.additionalChargeAmount);
                TextView totalWithDiscount = c.findViewById(R.id.totalWithDiscount);
                TextView totalPriceTextView = c.findViewById(R.id.totalPrice);
                LinearLayout offerLayout = c.findViewById(R.id.offerLayout);
                if (Common.currentSelectedDeliveryType != null) {
                    if (Common.currentSelectedDeliveryType.equals("Pick Up From Store")) {
                        additionalChargeAmount.setText("" + 3 * getCount());

                        //Change on 24 nov
                        /*totalAmount.setText("" + (totalPrice + Integer.parseInt(
                                additionalChargeAmount.getText().toString()
                        )));*/
                        totalPriceTextView.setText("" + totalPrice);
                        if (offerLayout.getVisibility() == View.VISIBLE) {
                            int discoutAmount = (int) (Integer.parseInt(totalPriceTextView.getText().toString()) * 0.1);
                            int discountedPrice = (Integer.parseInt(totalPriceTextView.getText().toString()) - discoutAmount);
                            totalWithDiscount.setText("" + discountedPrice);
                        } else {
                            //Changed in 24 nov 22:34, not counting the packaging chanrge
                            totalPriceTextView.setText("" + totalPrice);
                        }

                    } else {
                        additionalChargeAmount.setText("" + 5 * getCount());

                        //Change on 24 nov
                        /*totalAmount.setText("" + (totalPrice + Integer.parseInt(
                                additionalChargeAmount.getText().toString()
                        )));*/
                        totalPriceTextView.setText("" + totalPrice);
                        if (offerLayout.getVisibility() == View.VISIBLE) {
                            int discoutAmount = (int) (Integer.parseInt(totalPriceTextView.getText().toString()) * 0.1);
                            int discountedPrice = (Integer.parseInt(totalPriceTextView.getText().toString()) - discoutAmount);
                            totalWithDiscount.setText("" + discountedPrice);
                        } else {
                            //Changed in 24 nov 22:34, not counting the packaging chanrge
                            totalPriceTextView.setText("" + totalPrice);
                        }
                    }
                } else {
                    Toast.makeText(c, "Don't forget to select a delivery type", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeFont(TextView textView) {
        Typeface custom_font = Typeface.createFromAsset(c.getAssets(), "fonts/bontserrat_bold.otf");
        textView.setTypeface(custom_font);
    }
}

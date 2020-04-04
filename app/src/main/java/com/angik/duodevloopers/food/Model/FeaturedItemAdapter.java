package com.angik.duodevloopers.food.Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.angik.duodevloopers.food.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class FeaturedItemAdapter extends RecyclerView.Adapter<FeaturedItemAdapter.FeaturedItemViewHolder> {

    private ArrayList<String> mImage;
    private ArrayList<String> mItemName;
    private ArrayList<String> mItemDetail;
    private ArrayList<String> mItemPrice;

    private String currentStoreName;

    private Activity mContext;

    private DatabaseHelper databaseHelper;

    private OnItemClickListener mListener;

    //Own interface to handle click listener
    public interface OnItemClickListener {
        void onItemClick(int position);//Default method to override which takes an argument which is position
    }

    //Public constructor for the on click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    static class FeaturedItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView itemName;
        private TextView itemDetail;
        private TextView itemPrice;
        private TextView addedTextView;

        private LinearLayout layout;

        //As View holder class is normally static, so we are getting listener from store view holder default constructor
        //Other parameters are required in the constructor that's why we are getting those
        FeaturedItemViewHolder(@NonNull View itemView, final OnItemClickListener listener, final DatabaseHelper databaseHelper,
                               final Activity context, final ArrayList<String> mItemName, final ArrayList<String> mItemPrice) {
            super(itemView);
            itemName = itemView.findViewById(R.id.name);
            itemPrice = itemView.findViewById(R.id.price);
            itemDetail = itemView.findViewById(R.id.detail);
            imageView = itemView.findViewById(R.id.image);

            layout = itemView.findViewById(R.id.layout);

            addedTextView = itemView.findViewById(R.id.add);
            addedTextView.setVisibility(View.INVISIBLE);

            //Then setting on click listener on the item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    //This code if for making own on click listener
                    if (listener != null) {
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);//Which invokes the method in the interface and passes the current position
                        }
                    }
                    if (databaseHelper.getListContents().getCount() == 3) {
                        Toast.makeText(context, "Can not order more than 3 items at a time", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //If Added text view is invisible, adds the item to the SQLite database
                    if (addedTextView.getVisibility() == View.INVISIBLE) {
                        addedTextView.setVisibility(View.VISIBLE);
                        addedTextView.setText("ADDED");

                        databaseHelper.addData(mItemName.get(position), mItemPrice.get(position));
                    } else {
                        //If Added text view is already visible, shows a toast that the item is already added
                        Toast.makeText(context, "You've already added this item", Toast.LENGTH_SHORT).show();
                    }

                    LinearLayout layout = context.findViewById(R.id.linearLayout);

                    if (layout.getVisibility() == View.GONE) {
                        layout.setVisibility(View.VISIBLE);
                    }


                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = context.findViewById(R.id.count);
                            textView.setText("" + databaseHelper.getListContents().getCount());
                            //textView.setAnimation(AnimationUtils.loadAnimation(mC, R.anim.text));
                        }
                    });
                }
            });
        }
    }

    public FeaturedItemAdapter(Activity context, ArrayList<String> names, ArrayList<String> price, ArrayList<String> detail, ArrayList<String> image) {
        mContext = context;
        mItemName = names;
        mItemPrice = price;
        mItemDetail = detail;
        mImage = image;
        databaseHelper = new DatabaseHelper(mContext);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public FeaturedItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hello_style, viewGroup, false);
        TextView currentItemCount = mContext.findViewById(R.id.count);
        currentItemCount.setText("" + databaseHelper.getListContents().getCount());
        return new FeaturedItemViewHolder(v, mListener, databaseHelper, mContext, mItemName, mItemPrice);//Passing listener as well
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final FeaturedItemViewHolder featuredItemViewHolder, final int i) {
        Picasso.get().load(mImage.get(i)).into(featuredItemViewHolder.imageView);
        featuredItemViewHolder.imageView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_animation));
        //featuredItemViewHolder.layout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_animation));

        featuredItemViewHolder.itemName.setText(mItemName.get(i));
        changeFont(featuredItemViewHolder.itemPrice);
        featuredItemViewHolder.itemPrice.setText(mItemPrice.get(i) + " Tk");
        featuredItemViewHolder.itemDetail.setText(mItemDetail.get(i));
    }

    @Override
    public int getItemCount() {
        return mItemName.size();
    }

    private void changeFont(TextView textView) {
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/bontserrat_bold.otf");
        textView.setTypeface(custom_font);
    }
}

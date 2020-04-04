package com.angik.duodevloopers.food.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.angik.duodevloopers.food.R;
import com.angik.duodevloopers.food.Utility.FontUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {


    //private int[] mImage;
    @SuppressWarnings("CanBeFinal")
    private String[] mImage;
    private ArrayList<String> storeNames;
    private ArrayList<String> mDelivery;
    private Context mContext;
    private OnItemClickListener mListener;

    //Own interface to handle click listener
    public interface OnItemClickListener {
        void onItemClick(int position);//Default method to override which takes an argument which is position
    }

    //Public constructor for the on click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView itemname;
        private CardView cardView;

        private TextView deliveryAvailability;

        //As View holder class is normally static, so we are getting listener from store view holder default constructor
        @SuppressLint("SetTextI18n")
        StoreViewHolder(@NonNull View itemView, final OnItemClickListener listener, ArrayList<String> storeNames, Context mContext) {
            super(itemView);
            itemname = itemView.findViewById(R.id.name);
            deliveryAvailability = itemView.findViewById(R.id.delivery);
            new FontUtility(mContext, deliveryAvailability).changeToBold();
            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.image);

            //Then setting on click listener on the item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);//Which invokes the method in the interface and passes the current position
                        }
                    }
                }
            });
        }
    }

    public StoreAdapter(Context context, ArrayList<String> names, String[] image, ArrayList<String> delivery) {
        mContext = context;
        storeNames = names;
        mImage = image;
        mDelivery = delivery;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stores, viewGroup, false);
        return new StoreViewHolder(v, mListener, storeNames, mContext);//Passing listener as well
    }

    @Override
    public void onBindViewHolder(@NonNull final StoreViewHolder storeViewHolder, final int i) {
        storeViewHolder.cardView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
        Picasso.get().load(mImage[i]).into(storeViewHolder.imageView);
        storeViewHolder.itemname.setText(storeNames.get(i));
        storeViewHolder.deliveryAvailability.setText(mDelivery.get(i));//Delivery available for only one store
        changeFont(storeViewHolder.itemname);
    }

    @Override
    public int getItemCount() {
        return storeNames.size();
    }

    private void changeFont(TextView textView) {
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/bontserrat_bold.otf");
        textView.setTypeface(custom_font);
    }
}

package com.angik.duodevloopers.food.Utility;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class FontUtility {

    private TextView mTextView;
    private Context mContext;

    public FontUtility(Context context, TextView textView) {
        mTextView = textView;
        mContext = context;
    }

    public void changeToMedium() {
        mTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/montserrat_medium.ttf"));
    }

    public void changeToBold() {
        mTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/bontserrat_bold.otf"));
    }
}

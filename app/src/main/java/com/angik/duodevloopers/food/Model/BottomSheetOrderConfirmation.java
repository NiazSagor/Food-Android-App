package com.angik.duodevloopers.food.Model;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.angik.duodevloopers.food.R;
import com.angik.duodevloopers.food.Utility.FontUtility;

public class BottomSheetOrderConfirmation extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;

    private TextView conHeadline;
    private TextView conText;
    private Button conButton;

    private static String BOOK_HEADLINE = "Booking Confirmation";
    private static String ORDER_HEADLINE = "Order Confirmation";

    private static String BOOK_TEXT = "Do you want to continue to book this order?";
    private static String ORDER_TEXT = "Do you want to continue to place this order?";

    private String orderType = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_order_confirmation, container, false);

        conHeadline = view.findViewById(R.id.confirmationHeadline);

        new FontUtility(getContext(), conHeadline).changeToMedium();

        conText = view.findViewById(R.id.confirmationText);
        conButton = view.findViewById(R.id.confirmationButton);

        if (getArguments() != null) {
            if (getArguments().getString("type").equals("book")) {
                conHeadline.setText(BOOK_HEADLINE);
                conText.setText(BOOK_TEXT);
            } else {
                conHeadline.setText(ORDER_HEADLINE);
                conText.setText(ORDER_TEXT);
            }

        }

        /*if (orderType.equals("book")) {
            conHeadline.setText(BOOK_HEADLINE);
            conText.setText(BOOK_TEXT);
        } else if (orderType.equals("order")) {
            conHeadline.setText(ORDER_HEADLINE);
            conText.setText(ORDER_TEXT);
        }*/

        conButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClick();
                dismiss();
            }
        });

        return view;
    }

    public interface BottomSheetListener {
        void onButtonClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}

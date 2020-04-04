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

import com.angik.duodevloopers.food.R;

public class BottomSheet extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;//Our listener to handle the button click inside the bottom sheet

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        TextView helloUser = view.findViewById(R.id.helloUser);
        assert getArguments() != null;
        String s = "Hello, owner of " + getArguments().getString("userPhoneNumber");
        helloUser.setText(s);

        Button infoFragment = view.findViewById(R.id.okButton);
        infoFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClick();//Invoking the onButtonClick method inside BottomSheetListener interface
                dismiss();
            }
        });

        return view;
    }

    //This needs to be implemented by the activity where we want to show the bottom sheet, to @Override the method
    public interface BottomSheetListener {
        void onButtonClick();
    }

    //We have to attach the context of the activity with the listener
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

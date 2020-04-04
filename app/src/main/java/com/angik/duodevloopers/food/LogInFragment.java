package com.angik.duodevloopers.food;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogInFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogInFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText phoneNumber;
    private EditText userID;
    private EditText userPassword;

    private Button proceedButton;

    private SharedPreferences sp_buttonIsClicked;
    private SharedPreferences scannedID;

    private OnFragmentInteractionListener mListener;

    public LogInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogInFragment newInstance(String param1, String param2) {
        LogInFragment fragment = new LogInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        sp_buttonIsClicked = Objects.requireNonNull(getActivity()).getSharedPreferences("clicked", Context.MODE_PRIVATE);
        scannedID = getActivity().getSharedPreferences("id", Context.MODE_PRIVATE);
        //scannedID.edit().putBoolean("isClicked", false).apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);
        phoneNumber = view.findViewById(R.id.editText);
        userID = view.findViewById(R.id.userIdInput);
        userPassword = view.findViewById(R.id.userPassword);

        proceedButton = view.findViewById(R.id.button4);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumber.getText().toString().trim().length() < 10 || phoneNumber.getText().toString().equals("") || userID.getText().toString().equals("")) {
                    phoneNumber.setError("Please enter valid information");
                } else {
                    if (!sp_buttonIsClicked.getBoolean("isClicked", false)) {
                        checkInDatabase("+880" + phoneNumber.getText().toString().trim(), userID.getText().toString().trim());
                    } else {
                        if (!Objects.equals(scannedID.getString("id", null), userID.getText().toString().trim())) {
                            userID.setError("Metric Number didn't match with the scan. Please try again");
                            userID.setText("");
                            userID.requestFocus();
                        } else {
                            final ProgressDialog mDialog = new ProgressDialog(getContext());
                            mDialog.setMessage("Please Wait");
                            mDialog.show();

                            //startActivity(new Intent(getActivity(), MainActivity.class));
                            //Objects.requireNonNull(getActivity()).finish();
                            //sp_buttonIsClicked.edit().clear().apply();
                        }
                    }
                }
            }
        });
        return view;
    }

    private void checkInDatabase(final String number, final String id) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()) {
                    databaseReference.child(id).child("phoneNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String numberFromDatabase = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                if (numberFromDatabase.equals(number)) {
                                    startActivity(new Intent(getActivity(), CameraActivity.class));
                                    sp_buttonIsClicked.edit().putBoolean("isClicked", true).apply();
                                } else {
                                    Toast.makeText(getContext(), "Phone Number didn't match", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Your ID is not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

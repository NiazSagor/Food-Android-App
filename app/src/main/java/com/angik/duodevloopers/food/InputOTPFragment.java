package com.angik.duodevloopers.food;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;

import cdflynn.android.library.checkview.CheckView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InputOTPFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InputOTPFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("ALL")
public class InputOTPFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText codeEditText;
    private Button checkCodeButton;
    private FirebaseAuth mAuth;

    private String verificationID;
    SharedPreferences sharedPreferences;

    private OnFragmentInteractionListener mListener;

    public InputOTPFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InputOTPFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InputOTPFragment newInstance(String param1, String param2) {
        InputOTPFragment fragment = new InputOTPFragment();
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
        //sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("id", Context.MODE_PRIVATE);
        //verificationID = sharedPreferences.getString("id", null);
        //Toast.makeText(getContext(), "" + verificationID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        assert getArguments() != null;
        mAuth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_input_ot, container, false);
        codeEditText = view.findViewById(R.id.editText2);
        checkCodeButton = view.findViewById(R.id.button5);
        checkCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("id", Context.MODE_PRIVATE);
                verificationID = sharedPreferences.getString("id", null);
                //Toast.makeText(getContext(), "" + verificationID, Toast.LENGTH_SHORT).show();

                String code = codeEditText.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {
                    codeEditText.setError("Please input a valid code");
                    codeEditText.requestFocus();
                    return;
                }
                ViewPager viewPager = Objects.requireNonNull(getActivity()).findViewById(R.id.viewpager);
                viewPager.setCurrentItem(2);
                verifyCode(verificationID, code);
            }
        });
        return view;
    }

    private void verifyCode(String verificationID, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showAlertDialog("start");
                            String number = Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber();
                            ViewPager viewPager = Objects.requireNonNull(getActivity()).findViewById(R.id.viewpager);
                            viewPager.setCurrentItem(2);
                        } else {
                            Toast.makeText(getContext(), "" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showAlertDialog(String command) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        if (command.equals("start")) {
            alertDialog.show();
        } else {
            alertDialog.dismiss();
        }
        CheckView mCheck = view.findViewById(R.id.check);
        mCheck.check();
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

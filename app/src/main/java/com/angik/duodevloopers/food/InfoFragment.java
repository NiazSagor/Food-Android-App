package com.angik.duodevloopers.food;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.angik.duodevloopers.food.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("ALL")
public class InfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView userInfo;
    private EditText name;
    private EditText id;
    private EditText email;
    private EditText password;
    private EditText passwordToEnter;

    private Button submit;
    private Button verifyMetricId;

    private ImageView profilePic;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageTask uploadTask;

    private FirebaseAuth mAuth;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private SharedPreferences sp_hasGone;
    private SharedPreferences spUser;
    private SharedPreferences scannedId;

    private OnFragmentInteractionListener mListener;

    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
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
        mAuth = FirebaseAuth.getInstance();
        scannedId = getActivity().getSharedPreferences("id", MODE_PRIVATE); //Details in line 187
        sp_hasGone = getActivity().getSharedPreferences("hasGone", MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        //Submit button which handles the barcode matching and uploading photo
        submit = view.findViewById(R.id.button2);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");//User database
        storageReference = FirebaseStorage.getInstance().getReference("Profile Pictures");//Storage for the profile pics

        String number = null;
        if (mAuth.getCurrentUser() != null) {
            //If the user is authenticated retieve the phone number
            number = Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber();
            //Toast.makeText(getContext(), "" + number, Toast.LENGTH_SHORT).show();
        } else if (mAuth.getCurrentUser() == null) {
            SharedPreferences sp_number_from_FirebaseUser = getActivity().getSharedPreferences("phoneNumber", MODE_PRIVATE);
            number = sp_number_from_FirebaseUser.getString("number", null);
        }

        name = view.findViewById(R.id.name);
        id = view.findViewById(R.id.userID);
        email = view.findViewById(R.id.email);

        /*profilePic = view.findViewById(R.id.imageView);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });*/

        verifyMetricId = view.findViewById(R.id.button3);
        verifyMetricId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Launches camera activity to scan the barcode
                startActivity(new Intent(getActivity(), CameraActivity.class));
                submit.setEnabled(true);
            }
        });

        final String finalNumber = number;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If the edit texts are null then shows a message
                if (name.getText().toString().equals("") || id.getText().toString().equals("")
                        || email.getText().toString().equals("")) {
                    name.setError("Please provide a first and a last name");
                    id.setError("Please provide a valid metric number");
                    email.setError("Please provide a valid email");
                } else {
                    //If the input fields are not null, proceed and do this

                    /* Date : 29.09.19 Time : 20:02
                     * We are changing some of the layout
                     * Verify metric button is gone now
                     * We want to do some install outside IIUC students
                     * So we are ignoring the id card verification for now
                     * So the following code where scanned code and input code are checked, is commented out*/

                    if (scannedId.getString("id", null) == null) {
                        id.setError("Scan was not successful");
                        id.requestFocus();
                        return;
                    }

                    if (!scannedId.getString("id", null).equals(id.getText().toString().trim())) {
                        id.setError("Metric Number didn't match with the scan. Please try again");
                        id.setText("");
                        id.requestFocus();
                        return;
                    }


                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //If the user id is already in the database
                            if (dataSnapshot.child(id.getText().toString()).exists()) {
                                String metric = id.getText().toString();
                                //Then getting the id input by the user, and check it in the User Id node to get the user id provided by the Firebase
                                databaseReference = FirebaseDatabase.getInstance().getReference("User Id");
                                databaseReference.child(metric).child("UID").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String userIdFromDatabase = dataSnapshot.getValue().toString();//User id provided by the Firebase Auth
                                            if (!mAuth.getCurrentUser().getUid().equals(userIdFromDatabase)) {
                                                //Metric is in the database but not matched with the UID
                                                showAlrtOnUserIdWrong();
                                            } else {
                                                //Proceed normally
                                                ifUserAlreadyInDatabase(finalNumber);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                spUser = Objects.requireNonNull(getActivity()).getSharedPreferences("user", MODE_PRIVATE);//Getting our SP
                                //Making new object containing current info of the user
                                User user = new User("0", id.getText().toString().trim(), name.getText().toString(), finalNumber, email.getText().toString());
                                setUserIDForNewUser(id.getText().toString().trim());//Sets user id provided by the Firebase for each User in another node
                                uploadFile(user);//Uploads image in the storage
                                saveToSP(user);//Keeping user object in the SP
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        return view;
    }

    public void changeFont(TextView textView) {
        Typeface custom_font = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/bontserrat_bold.otf");
        textView.setTypeface(custom_font);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(profilePic);//Loads selected image to the image view
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = Objects.requireNonNull(getActivity()).getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(final User user) {
        //If image url is not null, otherwise if user selects an image then proceed
        if (mImageUri != null) {
            StorageReference fileReference = storageReference.child(id.getText().toString()
                    + "." + getFileExtension(mImageUri));//File name which will be saved in the database

            uploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //User database ref
                            databaseReference.child(id.getText().toString().trim()).setValue(user);//Setting values in database

                            //This helps to get the download url of the uploaded image
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            assert downloadUrl != null;
                            //After getting the download url setting this value in the user database to load profile pic
                            databaseReference.child(id.getText().toString().trim()).child("Profile Image URL").setValue(downloadUrl.toString());

                            sp_hasGone.edit().putBoolean("gone", true).apply();//Sp contains value indicating that the user has successfully signed in

                            //This works after 5 seconds after clicking Submit, while image is being uploaded to the storage
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getActivity(), MainActivity.class));//Intent to home screen

                                    Toast.makeText(getContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();//Scuccessful message

                                    Objects.requireNonNull(getActivity()).finish();//Finishing the current activity so that it doesn't stay in the back stack

                                    scannedId.edit().clear().apply();//Clearing the saved scanned id
                                }
                            }, 500);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            final ProgressDialog mDialog = new ProgressDialog(getContext());
                            mDialog.setMessage("Please Wait");
                            mDialog.show();
                        }
                    });

        } else {
            final ProgressDialog mDialog = new ProgressDialog(getContext());
            mDialog.setMessage("Please Wait");
            mDialog.show();


            databaseReference.child(id.getText().toString().trim()).setValue(user);//Setting values in database

            sp_hasGone.edit().putBoolean("gone", true).apply();

            startActivity(new Intent(getActivity(), MainActivity.class));//Intent to home screen

            Toast.makeText(getContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();//Scuccessful message

            Objects.requireNonNull(getActivity()).finish();//Finishing the current activity so that it doesn't stay in the back stack

            scannedId.edit().clear().apply();//Clearing the saved scanned id
        }
    }

    private void showAlrtOnUserIdWrong() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Sorry we couldn't verify that the Metric Number belongs to this phone number");
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

    private void ifUserAlreadyInDatabase(String finalNumber) {
        final ProgressDialog mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("Welcome Back!");
        mDialog.show();

        spUser = Objects.requireNonNull(getActivity()).getSharedPreferences("user", MODE_PRIVATE);//Getting our SP

        //Making new object containing current info of the user
        User user = new User("0", id.getText().toString().trim(), name.getText().toString(), finalNumber, email.getText().toString());

        saveToSP(user);//Keeping user object in the SP
        sp_hasGone.edit().putBoolean("gone", true).apply();

        startActivity(new Intent(getActivity(), MainActivity.class));//Intent to go to home screen

        scannedId.edit().clear().apply();//Scanned id Sp is cleared

        getActivity().finish();
    }

    private void setUserIDForNewUser(String metric) {
        //This database ref is for only user id provided by the Firebase Auth on each successful sign up
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Id");
        databaseReference.child(metric).child("UID").setValue(mAuth.getCurrentUser().getUid());
    }

    private void saveToSP(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);//Convert object into a string value
        spUser.edit().putString("user", json).apply();
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

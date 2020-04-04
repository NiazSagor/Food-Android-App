package com.angik.duodevloopers.food;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.angik.duodevloopers.food.Model.User;
import com.google.gson.Gson;

public class RestingActivity extends AppCompatActivity {

    private Button backLogIn;

    private CardView logoCardView;
    private TextView userName;
    private EditText idEditText;

    private User user;

    private SharedPreferences putUserInRest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resting);

        getUser();

        userName = findViewById(R.id.userName);

        final StringBuilder stringBuilder = new StringBuilder(100);
        stringBuilder.append("Hello, ").append(user.getName());

        userName.setText(stringBuilder);
        changeFont(userName);

        idEditText = findViewById(R.id.idEdiText);
        backLogIn = findViewById(R.id.backLogInButton);

        logoCardView = findViewById(R.id.cardView);
        logoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slide_down(RestingActivity.this, idEditText);
                idEditText.setVisibility(View.VISIBLE);
                idEditText.requestFocus();
                idEditText.setError("Enter your Metric ID to continue");
                showKeyboard();
            }
        });

        idEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().equals(user.getID())) {
                    slide_down(RestingActivity.this, backLogIn);
                    backLogIn.setVisibility(View.VISIBLE);
                    hideKeyboard();
                    slide_up(RestingActivity.this, idEditText);

                    backLogIn.setText("CORRECT! YOU CAN PROCEED");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        backLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                putUserInRest = getSharedPreferences("logoutEvent", MODE_PRIVATE);
                putUserInRest.edit().putBoolean("isLoggedOut", false).apply();

                finish();
            }
        });
    }

    private void changeFont(TextView textView) {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/bontserrat_bold.otf");
        textView.setTypeface(custom_font);
    }

    public static void slide_down(Context ctx, View v) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context ctx, View v) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void getUser() {
        SharedPreferences sp_user = getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp_user.getString("user", "");//Getting json string
        user = gson.fromJson(json, User.class);
    }
}

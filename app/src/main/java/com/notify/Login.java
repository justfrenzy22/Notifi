package com.notify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;


import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Login extends AuthUtils {

    private static final String TAG = "SignInActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE);
            String accessToken = sharedPreferences.getString("accessToken", ""); // name of the element, the default value of the element




            emailInp = findViewById(R.id.emailInput);
            emailLayout = findViewById(R.id.emailLayout);
            passInp = findViewById(R.id.passInput);
            passLayout = findViewById(R.id.passLayout);
            Button submitBtn = findViewById(R.id.submitBtn);
            Button redirectBtn = findViewById(R.id.redirectBtn);
            statusTxtLayout = findViewById(R.id.statusTxtLayout);
            statusTxt = findViewById(R.id.statusTxt);

            statusTxtLayout.setVisibility(View.GONE);

            String finalFcmToken = getFCMToken(sharedPreferences);

            submitBtn.setOnClickListener(v -> {
                try {
                    statusTxtLayout.setVisibility(View.VISIBLE);
                    statusTxt.setText(getString(R.string.load));
                    Log.d(TAG, "submitBtn clicked" + statusTxt.getText());
                    handleLogin(finalFcmToken);
                } catch (JSONException e) {
                    Log.w(TAG, "handleSubmit: ", e);
                    throw new RuntimeException(e);
                }
            });

            redirectBtn.setOnClickListener(v -> {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            });
        }
        catch (Exception e) {
            Log.w(TAG, "onCreate: " + e.getMessage());
        }
    }

    private void handleLogin(String fcmToken) throws JSONException {
        Map<EditText, TextInputLayout> inps = new HashMap<>();
        inps.put(emailInp, emailLayout);
        inps.put(passInp, passLayout);

        if (validateInputs(inps)) {
            String email = emailInp.getText().toString();
            String pass = passInp.getText().toString();

            String url = "http://87.227.174.139:8080/user/login" +
                    "?email=" + email +
                    "&password=" + pass +
                    "&fcmToken=" + fcmToken;


            fetchData(
                    url,
                    (status, _null) -> {
                        if (status == 200) {
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                Intent intent = new Intent(Login.this, UserActivity.class);
                                startActivity(intent);
                                finish();
                            }, 3000);
                        }
                    },
                    () -> statusTxt.setText(getString(R.string.wrong))
            );
        }
    }


}

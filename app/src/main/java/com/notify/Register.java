package com.notify;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Register extends AuthUtils {
    private static final String TAG = "RegisterActivity";

    private EditText firstNameInp;
    private TextInputLayout firstNameLayout;
    private EditText lastNameInp;
    private TextInputLayout lastNameLayout;
    private static final String endpoint = "/user/register";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {

            emailInp = findViewById(R.id.emailInput);
            emailLayout = findViewById(R.id.emailLayout);
            passInp = findViewById(R.id.passInput);
            passLayout = findViewById(R.id.passLayout);
            firstNameInp = findViewById(R.id.firstNameInput);
            firstNameLayout = findViewById(R.id.firstNameLayout);
            lastNameInp = findViewById(R.id.lastNameInput);
            lastNameLayout = findViewById(R.id.lastNameLayout);
            Button submitBtn = findViewById(R.id.submitBtn);
            Button redirectBtn = findViewById(R.id.redirectBtn);
            statusTxtLayout = findViewById(R.id.statusTxtLayout);
            statusTxt = findViewById(R.id.statusTxt);

            statusTxtLayout.setVisibility(View.GONE);

            String finalFcmToken = getFCMToken(getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE));

            submitBtn.setOnClickListener(v -> {
                try {
                    statusTxtLayout.setVisibility(View.VISIBLE);
                    statusTxt.setText(getString(R.string.load));
                    Log.d(TAG, "submitBtn clicked" + statusTxt.getText());
                    handleRegistration(finalFcmToken);
                } catch (JSONException e) {
                    Log.w(TAG, "handleRegistration: ", e);
                    throw new RuntimeException(e);
                }
            });

            redirectBtn.setOnClickListener(v -> {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            });
        }
        catch (Exception e) {
            Log.w(TAG, "onCreate: " + e.getMessage());
        }
    }

    private void handleRegistration(String fcmToken) throws JSONException {
        Map<EditText, TextInputLayout> inps = new HashMap<>();
        inps.put(emailInp, emailLayout);
        inps.put(passInp, passLayout);
        inps.put(firstNameInp, firstNameLayout);
        inps.put(lastNameInp, lastNameLayout);

        if (validateInputs(inps)) {
            String url = buildUrl(fcmToken);

            fetchData(
                    url,
                    (status, _null) -> {
                        if (status == 200) {
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                Intent intent = new Intent(Register.this, UserActivity.class);
                                startActivity(intent);
                                finish();
                            }, 3000);
                        }
                    },
                    () -> statusTxt.setText(getString(R.string.wrong))
            );
        }
    }

    private @NonNull String buildUrl(String fcmToken) {
        String email = emailInp.getText().toString();
        String pass = passInp.getText().toString();
        String firstName = firstNameInp.getText().toString();
        String lastName = lastNameInp.getText().toString();

        String api_base_url = "http://87.227.174.139:8080";
        return api_base_url + endpoint +
                "?email=" + email +
                "&password=" + pass +
                "&firstName=" + firstName +
                "&lastName=" + lastName +
                "&fcmToken=" + fcmToken;
    }

//    private static final String TAG = "Register";
//    private EditText emailInp;
//    private TextInputLayout emailLayout;
//    private EditText passInp;
//    private TextInputLayout passLayout;
//    private EditText firstNameInp;
//    private TextInputLayout firstNameLayout;
//    private EditText lastNameInp;
//    private TextInputLayout lastNameLayout;
//    private LinearLayout statusTxtLayout;
//    private TextView statusTxt;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.register);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        try {
//            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE);
//            String fcmToken = sharedPreferences.getString("fcmToken", "");
//            //TODO ot tuka moje da accure-ne problem
//            if (Objects.equals(fcmToken, "")) {
//                fetchFCMToken();
//                Log.d(TAG, "FCM token is null");
//                fcmToken = getFCMToken();
//
//                sharedPreferences.edit()
//                        .putString("fcmToken", fcmToken)
//                        .apply();
//            }
//            else {
//                Log.d(TAG, "Saved FCM token: " + fcmToken);
//            }
//
//            emailInp = findViewById(R.id.emailInput);
//            emailLayout = findViewById(R.id.emailLayout);
//            passInp = findViewById(R.id.passInput);
//            passLayout = findViewById(R.id.passLayout);
//            firstNameInp = findViewById(R.id.firstNameInput);
//            firstNameLayout = findViewById(R.id.firstNameLayout);
//            lastNameInp = findViewById(R.id.lastNameInput);
//            lastNameLayout = findViewById(R.id.lastNameLayout);
//            Button submitBtn = findViewById(R.id.submitBtn);
//            Button redirectBtn = findViewById(R.id.redirectBtn);
//            statusTxtLayout = findViewById(R.id.statusTxtLayout);
//            statusTxt = findViewById(R.id.statusTxt);
//
//            statusTxtLayout.setVisibility(View.GONE);
//
//            String finalFcmToken = fcmToken;
//            submitBtn.setOnClickListener(v -> {
//                try {
//                    statusTxtLayout.setVisibility(View.VISIBLE);
//                    statusTxt.setText(getString(R.string.load));
//                    Log.d(TAG, "submitBtn clicked" + statusTxt.getText());
//                    handleSubmit(finalFcmToken);
//                } catch (JSONException e) {
//                    Log.w(TAG, "handleSubmit: ", e);
//                    throw new RuntimeException(e);
//                }
//            });
//
//            redirectBtn.setOnClickListener(v -> {
//                Intent intent = new Intent(Register.this, Login.class);
//                startActivity(intent);
//                finish();
//            });
//        }
//        catch (Exception e) {
//            Log.w(TAG, "onCreate: " + e.getMessage());
//        }
//
//    }
//
//    private void handleSubmit(String fcmToken) throws JSONException {
//
//        if (validateInputs()) {
//            String email = emailInp.getText().toString();
//            String pass = passInp.getText().toString();
//            String firstName = firstNameInp.getText().toString();
//            String lastName = lastNameInp.getText().toString();
//
//            String url = "http://87.227.174.139:8080/user/register" +
//                    "?email=" + email +
//                    "&password=" + pass +
//                    "&firstName=" + firstName +
//                    "&lastName=" + lastName +
//                    "&fcmToken=" + fcmToken;
//
//            fetchData(url);
//        }
//    }
//
//    private boolean validateInputs () {
//        Map<EditText, TextInputLayout> inpMap = new HashMap<>();
//        inpMap.put(emailInp, emailLayout);
//        inpMap.put(passInp, passLayout);
//        inpMap.put(firstNameInp, firstNameLayout);
//        inpMap.put(lastNameInp, lastNameLayout);
//
//        boolean isValid = true;
//
//        for (Map.Entry<EditText, TextInputLayout> entry : inpMap.entrySet()) {
//            EditText inp = entry.getKey();
//            TextInputLayout layout = entry.getValue();
//            String inpText = inp.getText().toString();
//
//            if (TextUtils.isEmpty(inpText)) {
//                layout.setError(layout.getHint() + " е задължително");
//                isValid = false;
//            } else {
//                layout.setError(null);
//            }
//        }
//
//        return isValid;
//    }
//
//    @SuppressLint("SetTextI18n")
//    private void fetchData (String url) {
//        OkHttpClient client = new OkHttpClient();
//
//        new Thread(() -> {
//            try {
//                Request req = new Request.Builder()
//                        .url(url)
//                        .build();
//
//                Response res = client.newCall(req).execute();
//
//                if (res.isSuccessful()) {
//                    assert res.body() != null;
//                    String resBody = res.body().string();
//                    Log.d(TAG, "Successful response : " + resBody);
//
//                    JSONObject resJson = new JSONObject(resBody);
//
//                    int status = resJson.getInt("status");
//                    String accessToken = resJson.getString("accessToken");
//                    String msg = resJson.getString("msg");
//
//                    if (status == 200) {
//
//                        saveToken(accessToken);
//                        runOnUiThread(() -> {
//                            statusTxt.setText(msg);
//                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                                Intent intent = new Intent(Register.this, User.class);
//                                startActivity(intent);
//                                finish();
//                            }, 3000);
//                        });
//                    } else if (status == 401) {
//                        runOnUiThread(() -> {
//                            statusTxt.setText(msg);
//                            statusTxtLayout.setVisibility(View.VISIBLE);
//                        });
//
//                    }
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "OKHTTP Exception: " + e.getMessage());
//
//                runOnUiThread(() -> {
//                    statusTxt.setText("Error: " + getString(R.string.error));
//                    statusTxtLayout.setVisibility(View.VISIBLE);
//                });
//            }
//        }).start();
//    }
//
//    private void saveToken (String accessToken) {
//        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE);
//
//        sharedPreferences.edit()
//                .putString("accessToken", accessToken)
//                .apply();
//
//        Log.d(TAG, "saveToken: " + accessToken);
//    }

}


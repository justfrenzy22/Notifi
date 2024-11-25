package com.notify;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AuthUtils extends AppCompatActivity {
    private static final String TAG = "AuthUtils";
    protected EditText emailInp;
    protected TextInputLayout emailLayout;
    protected EditText passInp;
    protected TextInputLayout passLayout;
    protected LinearLayout statusTxtLayout;
    protected TextView statusTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FCMHelper.fetchFCMToken();
    }

    protected String getFCMToken(SharedPreferences sharedPreferences) {
        String fcmToken = sharedPreferences.getString("fcmToken", "");

        if (Objects.equals(fcmToken, "")) {
            fcmToken = FCMHelper.getFCMToken();
            sharedPreferences.edit()
                    .putString("fcmToken", fcmToken)
                    .apply();
        } else {
            Log.d(TAG, "Saved FCM token: " + fcmToken);
        }

        return fcmToken;
    }

    protected boolean validateInputs(Map<EditText, TextInputLayout> inpMap) {
        boolean isValid = true;

        for (Map.Entry<EditText, TextInputLayout> entry : inpMap.entrySet()) {
            EditText inp = entry.getKey();
            TextInputLayout layout = entry.getValue();
            String inpText = inp.getText().toString();

            if (TextUtils.isEmpty(inpText)) {
                layout.setError(layout.getHint() + " is required");
                isValid = false;
            } else {
                layout.setError(null);
            }
        }

        return isValid;
    }

    protected void fetchData(String url, OnSuccess onSuccess, Runnable onFailure) {
        OkHttpClient client = new OkHttpClient();

        new Thread(() -> {
            try {
                Request req = new Request.Builder().url(url).build();
                Response res = client.newCall(req).execute();

                assert res.body() != null;
                String resBody = res.body().string();
                Log.d(TAG, "Response: " + resBody);

                if (res.isSuccessful()) {
                    JSONObject resJson = new JSONObject(resBody);

                    int status = resJson.getInt("status");
                    String msg = resJson.getString("msg");
                    String accessToken = resJson.getString("accessToken");

                    runOnUiThread(() -> {
                        statusTxt.setText(msg);
                        if (status == 200) {
                            saveToken(accessToken);
                            Log.d(TAG, "Successful response: " + resBody);
                            onSuccess.set(status);
                        } else if (status == 400) {
                            Log.d(TAG, "Bad request response (status): " + status);
                            onSuccess.set(status);
                        }
                    });
                } else {
                    Log.d(TAG, "Unsuccessful HTTP response: " + res.code());
                    runOnUiThread(onFailure);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching data: " + e.getMessage());
                runOnUiThread(() -> statusTxt.setText(getString(R.string.error)));
            }
        }).start();
    }

    protected interface OnSuccess {
        void set(int status);
    }

    protected interface onSuccessLoad {
        void set (int status, User user);
    }

    protected void saveToken(String accessToken) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("accessToken", accessToken);
        editor.apply();
        Log.d(TAG, "Token saved in SharedPreferences: " + accessToken);
    }

    protected void loadUser(String accessToken, onSuccessLoad onSuccess, Runnable onFailure) {
        String url = "http://87.227.174.139:8080/user/load";
        OkHttpClient client = new OkHttpClient();

        new Thread(() -> {
            try {
                Request req = new Request.Builder().url(url).addHeader("authorization", accessToken).build();
                Response res = client.newCall(req).execute();

                assert res.body() != null;
                String resBody = res.body().string();
                Log.d(TAG, "Response: " + resBody);

                if (res.isSuccessful()) {
                    JSONObject resJson = new JSONObject(resBody);
                    int status = resJson.getInt("status");

                    runOnUiThread(() -> {
                        if (status == 200) {
                            try {
                                JSONObject userJson = resJson.getJSONObject("user");
                                String firstName = userJson.getString("firstName");
                                String lastName = userJson.getString("lastName");
                                String email = userJson.getString("email");
                                Boolean isAlarmOn = userJson.getBoolean("isAlarmOn");

                                JSONArray nodeMCUArray = resJson.getJSONArray("nodemcus");
                                NodeMCU[] nodemcus = new NodeMCU[nodeMCUArray.length()];

                                if (nodeMCUArray.length() > 0) {
                                    for (int i = 0; i < nodeMCUArray.length(); i++) {
                                        JSONObject nodeMCUJson = nodeMCUArray.getJSONObject(i);  // Store the JSONObject for reuse
                                        nodemcus[i] = new NodeMCU(
                                                nodeMCUJson.getString("name"),
                                                nodeMCUJson.getString("authToken")
                                        );
                                    }
                                }

                                Log.d(TAG, "User loaded: " + userJson);
                                Log.d(TAG, "Nodemcus loaded: " + nodeMCUArray);
                                User user;
                                if (nodemcus.length > 0) {
                                    user = new User(firstName, lastName, email, isAlarmOn, nodemcus);
                                } else {
                                    user = new User(firstName, lastName, email, isAlarmOn, new NodeMCU[0]);
                                }

                                onSuccess.set(status, user);
                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                            }
                        } else {
                            Log.d(TAG, "Bad request: " + status);
                            onFailure.run();
                        }
                    });
                } else {
                    Log.e(TAG, "Unsuccessful response: " + res.code());
                    runOnUiThread(onFailure);
                }
            } catch (JSONException | IOException e) {
                Log.e(TAG, "Error fetching data: " + e.getMessage());
                runOnUiThread(onFailure);
            }
        }).start();
    }

}

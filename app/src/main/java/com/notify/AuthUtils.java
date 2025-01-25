package com.notify;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    protected void fetchData(String url, onSuccess<Void> onSuccess, Runnable onFailure) {
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
                            onSuccess.set(status, null);
                        } else if (status == 400) {
                            Log.d(TAG, "Bad request response (status): " + status);
                            onSuccess.set(status, null);
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

    protected void alarmChanged(String url, String accessToken, onSuccess<Void> res, Runnable onFailure) {

            Map<String, String> headers = new HashMap<>();
            headers.put("authorization", accessToken);

            http(url, headers,
                    (resBody) -> {
                        try {
                            JSONObject resJson = new JSONObject(resBody);

                            int status = resJson.getInt("status");
                            String msg = resJson.getString("msg");

                            Log.d(TAG, "Response message: " + msg);

                            if (status == 200) {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    res.set(status, null);
                                });}
                        }
                        catch (JSONException e) {
                            Log.d(TAG, "Error parsing JSON: " + e.getMessage());

                        }
                    },
                    () -> runOnUiThread(onFailure));

    }

    protected interface onSuccess<T> {
        void set(int status, T t);
    }

    protected void saveToken(String accessToken) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("accessToken", accessToken);
        editor.apply();
        Log.d(TAG, "Token saved in SharedPreferences: " + accessToken);
    }

    protected void loadUser(String accessToken, onSuccess<User> onSuccess, Runnable onFailure) {
        String url = "http://87.227.174.139:8080/user/load";
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", accessToken);

        http(
                url, headers,
                (resBody) -> {
                    try {
                        JSONObject resJson = new JSONObject(resBody);

                        int status = resJson.getInt("status");
                        String msg = resJson.getString("msg");

                        if (status == 200) {
                            try {
                                JSONObject userJson = resJson.getJSONObject("user");
                                String firstName = userJson.getString("firstName");
                                String lastName = userJson.getString("lastName");
                                String email = userJson.getString("email");
                                Boolean isAlarmOn = userJson.getBoolean("isAlarmOn");

                                JSONArray nodeMCUArray = resJson.getJSONArray("nodemcus");

                                List<NodeMCU> nodemcus = new ArrayList<>();

                                if (nodeMCUArray.length() > 0) {
                                    for (int i = 0; i < nodeMCUArray.length(); i++) {
                                        JSONObject nodeMCUJson = nodeMCUArray.getJSONObject(i);

                                        JSONArray triggeredAtArr = nodeMCUJson.getJSONArray("alarmTriggeredAt");
                                        Date[] triggeredAtDates = new Date[triggeredAtArr.length()];
                                        for (int j = 0; j < triggeredAtArr.length(); j++) {
                                            String dateString = triggeredAtArr.getString(j);
                                            triggeredAtDates[j] = Date.from(java.time.Instant.parse(dateString));
                                        }

                                        Date createdAt = Date.from(java.time.Instant.parse(nodeMCUJson.getString("createdAt")));

                                        NodeMCU nodeMCU = new NodeMCU(
                                                nodeMCUJson.getString("_id"),
                                                nodeMCUJson.getString("userId"),
                                                nodeMCUJson.getString("name"),
                                                nodeMCUJson.getString("authToken"),
                                                triggeredAtDates,
                                                createdAt
                                        );

                                        nodemcus.add(nodeMCU);
                                    }
                                }
                                else {
                                    nodemcus = Collections.emptyList();
                                }

                                final List<NodeMCU> finalNodemcus = nodemcus;
                                runOnUiThread(() -> {
                                    Log.d(TAG, "User loaded: " + userJson);
                                    onSuccess.set(status, new User(firstName, lastName, email, isAlarmOn, finalNodemcus));
                                });

                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                            }
                        }
                    } catch (JSONException e) {
                        Log.w(TAG, "Error parsing JSON: " + e.getMessage());
                        runOnUiThread(onFailure);
                    }
                },
                () -> runOnUiThread(onFailure)
        );

    }

    protected void createNodeMCU (String name,String accessToken, onSuccess<String> onSuccess, Runnable onFailure) {
//        OkHttpClient client = new OkHttpClient();
        String url = "http://87.227.174.139:8080/nodemcu/create?name=" + name;
        Map<String, String> headers = Map.of("authorization", accessToken);
        http(url, headers,
            (resBody) -> {
                try {
                    JSONObject resJson = new JSONObject(resBody);

                    int status = resJson.getInt("status");
                    String msg = resJson.getString("msg");

                    if (status == 200) {
                        onSuccess.set(status, msg);
                    }
                    else {
                        Toast.makeText(AuthUtils.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e) {
                    runOnUiThread(onFailure);
                }
            },
            () -> runOnUiThread(onFailure)
        );
    }

    protected void deleteNodeMCU (String _id, String accessToken, onSuccess<String> onSuccess, Runnable onFailure) {
        String url = "http://87.227.174.139:8080/nodemcu/delete?nodeMCUId=" + _id;
        Map<String, String> headers = Map.of("authorization", accessToken);

        http(
                url, headers,

                (resBody) -> {
                    try {
                        JSONObject resJson = new JSONObject(resBody);

                        int status = resJson.getInt("status");
                        String msg = resJson.getString("msg");

                        if (status == 200) {
                            runOnUiThread(() -> onSuccess.set(status, msg));
                        }
                        else {
                            runOnUiThread(() -> Toast.makeText(AuthUtils.this, msg, Toast.LENGTH_SHORT).show());
                        }
                    }
                    catch (JSONException e) {
                        runOnUiThread(onFailure);
                    }
                },
                () -> runOnUiThread(onFailure)
        );
    }

    protected void refreshNodeMCU (String _id, String accessToken, onSuccess<NodeMCURefreshResponse> onSuccess, Runnable onFailure) {
        String url = "http://87.227.174.139:8080/nodemcu/refresh?nodeMCUId=" + _id;

        Map<String, String> headers = Map.of("authorization", accessToken);

        http(
                url, headers,
                (resBody) -> {
                    try {
                        Log.d(TAG, "Refresh NodeMCU Response: " + resBody);
                        JSONObject resJson = new JSONObject(resBody);
                        int status = resJson.getInt("status");
                        String msg = resJson.getString("msg");

                        if (status == 200) {
                            String authToken = resJson.getString("nodeMCUToken");
                            runOnUiThread(() -> onSuccess.set(status, new NodeMCURefreshResponse(msg, authToken)));
                        }
                        else {
                            runOnUiThread(() -> Toast.makeText(AuthUtils.this, msg, Toast.LENGTH_SHORT).show());
                        }
                    }
                    catch (JSONException e) {
                        runOnUiThread(onFailure);
                    }
                },
                () -> runOnUiThread(onFailure)
        );
    }

    protected void http (String url, Map<String, String> headers, handleI success, Runnable onFailure) {
        OkHttpClient client = new OkHttpClient();
        new Thread(() -> {
            try {
                Request.Builder reqBuilder = new Request.Builder().url(url);

                if (headers != null) {
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        reqBuilder.addHeader(header.getKey(), header.getValue());
                    }
                }

                Request req = reqBuilder.build();
                Response res = client.newCall(req).execute();

                assert res.body() != null;
                String resBody = res.body().string();
                Log.d(TAG, "Response : " + resBody);

                if (res.isSuccessful()) {
                    success.set(resBody);
                }
                else {
                    Log.e(TAG, "Unsuccessful response: " + res.code());
                    onFailure.run();
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Unsuccessful response: " + e.getMessage());
                onFailure.run();
            }
        }).start();
    }

    protected interface handleI {
        void set(String resBody) throws JSONException;
    }
}

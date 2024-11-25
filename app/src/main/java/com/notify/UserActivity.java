package com.notify;



//import static com.notify.FCMHelper.getFCMToken;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Map;
import java.util.Objects;

public class UserActivity extends AuthUtils {
    private static final String TAG = "UserActivityLoad";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        fetchFCMToken();
        super.onCreate(savedInstanceState);

        try {

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String accessToken = sharedPreferences.getString("accessToken", ""); // name of the element, the default value of the element
            String fcmToken = sharedPreferences.getString("fcmToken", "");

            if (Objects.equals(accessToken, "")) {
                Intent intent = new Intent(UserActivity.this, Login.class);
                startActivity(intent);
                finish();
            }

            if (Objects.equals(fcmToken, "")) {
                fcmToken = getFCMToken(sharedPreferences);
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("fcmToken", fcmToken);
            editor.apply();

            EdgeToEdge.enable(this);
            setContentView(R.layout.user);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.userMain), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            loadUser(
                    accessToken,
                    (status, usr) -> {
                        if (status == 200) {
                            this.user = usr;
                            updateUserInfo();
                        }
                        else {
                            Log.d(TAG, "Failed to load user credentials with status: " + status);
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                Intent intent = new Intent(UserActivity.this, Login.class);
                                startActivity(intent);
                                finish();
                            }, 3000);
                        }
                    },
                    () -> {
                        Log.e(TAG, "Failed to fetch user data.");
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            Intent intent = new Intent(UserActivity.this, Login.class);
                            startActivity(intent);
                            finish();
                        }, 3000);
                    }
            );

            Button addNodeMCUsBtn = findViewById(R.id.addNodeMCUsBtn);
            addNodeMCUsBtn.setOnClickListener(v -> {


            });

            Button logOutBtn = findViewById(R.id.logOutBtn);
            logOutBtn.setOnClickListener(v -> {



                PopupMenu popupMenu = new PopupMenu(UserActivity.this, logOutBtn);
                popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    Toast.makeText(UserActivity.this, "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                });
                popupMenu.show();












//                Snackbar snackbar = Snackbar.make(findViewById(R.id.userMain), "Are you sure you want to log out?", Snackbar.LENGTH_SHORT);
//                snackbar.setAction("Log Out", view -> {
//                    editor.remove("accessToken");
//                    editor.apply();
//                    Intent intent = new Intent(UserActivity.this, Login.class);
//                    startActivity(intent);
//                    finish();
//                });
//                snackbar.setAction("Settings", view -> {
//                    Intent intent = new Intent(UserActivity.this, Login.class);
//                    startActivity(intent);
//                });
//
//                snackbar.show();
//                editor.remove("accessToken");
//                editor.apply();
//                Intent intent = new Intent(UserActivity.this, Login.class);
//                startActivity(intent);
//                finish();
            });

            Snackbar snackbar;


//            updateUserInfo();

//            LinearLayout noNodemcuSDiv = findViewById(R.id.noNodemcuSDiv);
//            noNodemcuSDiv.setVisibility(View.VISIBLE);
//            LinearLayout nodemcu_renderDiv = findViewById(R.id.nodemcu_renderDiv);
//            nodemcu_renderDiv.setVisibility(View.GONE);



        }
        catch (Exception e) {
            Log.d(TAG, "Error : " + e.getMessage());
        }
    }

    private void updateUserInfo() {

        String fullNames = user.getFirstName() + " " + user.getLastName();

        TextView userName = findViewById(R.id.userName);
        userName.setText(fullNames);
        SwitchCompat alarmStatusSwitch = findViewById(R.id.alarmStatusSwitch);
        alarmStatusSwitch.setChecked(user.getIsAlarmOn());
        LinearLayout noNodemcusDiv = findViewById(R.id.noNodemcuSDiv);
        LinearLayout nodemcu_renderDiv = findViewById(R.id.nodemcu_renderDiv);

        if (user.getNodeMCUArray().length == 0) {
            nodemcu_renderDiv.setVisibility(View.GONE);
            noNodemcusDiv.setVisibility(View.VISIBLE);
        }
        else {
            nodemcu_renderDiv.setVisibility(View.VISIBLE);
            noNodemcusDiv.setVisibility(View.GONE);
        }


    }

//    private void updateUserInfo () {
//        String fullNames = user.getFirstName() + " " + user.getLastName();
//
//    }
}

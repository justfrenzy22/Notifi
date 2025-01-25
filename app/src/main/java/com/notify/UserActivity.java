package com.notify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class UserActivity extends AuthUtils implements BottomSheet.Listener {
    private static final String TAG = "UserActivityLoad";
    private User user;
    private Boolean ignore = false;
    private Boolean initAlarmStatus = false;

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCMSDK (and your app) can post notifications.
                    Log.i(TAG, "Notification permission granted");
                } else {
                    // Inform user that that your app will not show notifications.
                    Log.i(TAG, "Notification permission not granted");
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Notification permission already granted");
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }

        try {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String accessToken = sharedPreferences.getString("accessToken", "");
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

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.userMain), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            loadUser(
                    accessToken,
                    (status, newUsr) -> {
                        if (status == 200) {
                            Log.d(TAG, "Loaded user credentials with status: " + status);
                            this.user = newUsr;
                            userLiveData.setValue(newUsr);
                            updateUserInfo();
                            this.initAlarmStatus = true;
                            RenderNodeMCUs();
                        } else {
                            Log.d(TAG, "Failed to load user credentials with status: " + status);
                            redirectToLogin();
                        }
                    },
                    this::redirectToLogin
            );


            Button addNodeMCUsBtn = findViewById(R.id.addNodeMCUsBtn);
            addNodeMCUsBtn.setOnClickListener(v -> {
                BottomSheetDialogFragment bottomSheet = new BottomSheet();
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                bottomSheet.setCancelable(false);
            });

            ImageButton logOutBtn = findViewById(R.id.logOutBtn);
            logOutBtn.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(UserActivity.this, logOutBtn);
                popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());
                popupMenu.getMenu().findItem(R.id.menuEmail).setTitle(user.getEmail());
                try {
                    @SuppressLint("DiscouragedPrivateApi") Field popup = PopupMenu.class.getDeclaredField("mPopup");
                    popup.setAccessible(true);
                    Object menuPopupHelper = popup.get(popupMenu);
                    assert menuPopupHelper != null;
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getDeclaredMethod("setForceShowIcon", boolean.class);
                    setForceIcons.setAccessible(true);
                    setForceIcons.invoke(menuPopupHelper, true);


                } catch (NoSuchFieldException | ClassNotFoundException | InvocationTargetException |
                         IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                popupMenu.setOnMenuItemClickListener(item -> {

                    if (item.getItemId() == R.id.menuLogOut) {
                        LogOut();
                        return true;
                    }
                    else {
                        return false;
                    }
                });
                popupMenu.show();
            });

            SwitchCompat alarmStatusSwitch = findViewById(R.id.alarmStatusSwitch);
            alarmStatusSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (!initAlarmStatus) {
                    ignore = false;
                    return;
                }

                if (ignore) {
                    ignore = false;
                    return;
                }
                String title = "Alarm Status";
                String msg = "Are you sure you want to turn " + (isChecked ? "on" : "off") + " the alarm?";

                AlertBox alertBox = new AlertBox(this,title, msg, this::setAlarm);
                alertBox.show();
            });

            ImageButton dropDownBtn = findViewById(R.id.dropDownBtn);
            dropDownBtn.setOnClickListener(v -> {
                if (user == null) return;
                if (user.getNodeMCUList().isEmpty()) return;

                LinearLayout nodemcu_renderDiv = findViewById(R.id.nodemcu_renderDiv);

                if (nodemcu_renderDiv.getVisibility() == View.VISIBLE) {
                    nodemcu_renderDiv.setVisibility(View.GONE);
                    dropDownBtn.setImageResource(R.drawable.up_arrow);
                }
                else {
                    nodemcu_renderDiv.setVisibility(View.VISIBLE);
                    dropDownBtn.setImageResource(R.drawable.down_arrow);
                }
            });
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

        Log.d(TAG, "Updated user info" +  user);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void RenderNodeMCUs () {
        if (user == null) return;

        LinearLayout nodemcu_renderDivView, noNodemcuSDivView;

        nodemcu_renderDivView = findViewById(R.id.nodemcu_renderDiv);
        noNodemcuSDivView = findViewById(R.id.noNodemcuSDiv);


        if (user.getNodeMCUList().isEmpty())  {
            nodemcu_renderDivView.setVisibility(View.GONE);
            noNodemcuSDivView.setVisibility(View.VISIBLE);
        }
        else {
            noNodemcuSDivView.setVisibility(View.GONE);
            List<NodeMCU> nodeMCUList = user.getNodeMCUList();
            RecyclerView nodeMCURecyclerView = findViewById(R.id.NodeMCURecyclerView);
            if (nodeMCUList != null && !nodeMCUList.isEmpty()) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                NodeMCUAdapter adapter = new NodeMCUAdapter(nodeMCUList, accessToken, new AuthUtils());
                nodeMCURecyclerView.setAdapter(adapter);
                nodeMCURecyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter.notifyDataSetChanged();
                nodemcu_renderDivView.setVisibility(View.VISIBLE);
                nodeMCURecyclerView.setVisibility(View.VISIBLE);
            } else {
                nodemcu_renderDivView.setVisibility(View.GONE);
                nodeMCURecyclerView.setVisibility(View.GONE);
            }
        }
    }

    private void LogOut () {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("accessToken");
        editor.apply();
        Toast.makeText(UserActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin () {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(UserActivity.this, Login.class);
            startActivity(intent);
            finish();
        }, 1000);
    }

    private void setAlarm (Boolean isConfirmed) {
        SwitchCompat alarmStatusSwitch = findViewById(R.id.alarmStatusSwitch);

        if (!isConfirmed) {
            ignore = true;
            alarmStatusSwitch.setChecked(!alarmStatusSwitch.isChecked());
        }

        else {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String accessToken = sharedPreferences.getString("accessToken", "");
            setAlarmStatus(alarmStatusSwitch.isChecked(), accessToken);
        }
    }

    private void setAlarmStatus(Boolean status, String accessToken) {

        String url = "http://87.227.174.139:8080/user/setAlarm?isAlarmOn=" + status;


        alarmChanged(url, accessToken, (stat, _null) -> {
            if (stat == 200) {
                Toast.makeText(this, status ? "Alarm system turned on" : "Alarm system turned off" , Toast.LENGTH_LONG).show();
            }
        }, () -> Toast.makeText(this, "Failed to change alarm status", Toast.LENGTH_LONG).show());
    }

    @Override
    public void set(String msg) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", "");

        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
        loadUser(
                accessToken,
                (status, newUsr) -> {
                    if (status == 200) {
                        Log.d(TAG, "Loaded user credentials with status: " + status);
                        this.user = newUsr;
                        userLiveData.setValue(newUsr);
                        updateUserInfo();
                        this.initAlarmStatus = true;
                        RenderNodeMCUs();
                    } else {
                        Log.d(TAG, "Failed to load user credentials with status: " + status);
                        redirectToLogin();
                    }
                },
                this::redirectToLogin
        );
    }
}

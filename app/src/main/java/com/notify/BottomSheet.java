package com.notify;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import javax.annotation.Nullable;

public class BottomSheet extends BottomSheetDialogFragment {
    private final String TAG = "BottomSheet";
    private final AuthUtils auth;

    public BottomSheet() {
        this.auth = new AuthUtils();
    }

    public interface Listener {
        void set(String msg);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable
                             ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet, container, false);

        Button submitBtn = v.findViewById(R.id.submitBtn);
        TextView titleInput = v.findViewById(R.id.titleInput);

        Button closeBtn = v.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(view -> dismiss());

        submitBtn.setOnClickListener(view -> {
            String title = titleInput.getText().toString();
            if (title.isEmpty()) {
                Toast.makeText(getActivity(), "Please put title for NodeMCU", Toast.LENGTH_SHORT).show();
            }
            else {

                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                auth.createNodeMCU(
                        title, accessToken
                        , (status, msg) -> {
                            Log.d(TAG, "Created NodeMCU with status: " + status);
                            if (getActivity() != null) {
                                if (getActivity() instanceof Listener) {
                                    ((Listener) getActivity()).set(msg);
                                }
                            }
                            dismiss();
                        },
                        () -> {
                            if (getActivity() != null) {
                                Log.d(TAG, "Error creating NodeMCU");

                                if (getActivity() instanceof Listener) {
                                    ((Listener) getActivity()).set("Error creating NodeMCU");
                                }
                            }
                            dismiss();
                        }
                );
            }

        });
        return v;
    }
}


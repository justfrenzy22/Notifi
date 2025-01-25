package com.notify;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.Settings.System.getString;
import static androidx.core.content.ContextCompat.getSystemService;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NodeMCUAdapter extends RecyclerView.Adapter<NodeMCUAdapter.NodeMCUViewHolder> {
    private final List<NodeMCU> nodemcuList;
    private final String accessToken;
    private final AuthUtils authUtils;

    public NodeMCUAdapter(List<NodeMCU> nodeMCUList, String accessToken, AuthUtils authUtils) {
        this.nodemcuList = nodeMCUList;
        this.accessToken = accessToken;
        this.authUtils = authUtils;
    }

    @NonNull
    @Override
    public NodeMCUViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nodemcu, parent, false);
        return new NodeMCUViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull NodeMCUViewHolder holder, int position) {
        NodeMCU nodeMCU = nodemcuList.get(position);
        holder.nodemcuName.setText(nodeMCU.getName() != null ? nodeMCU.getName() : "No Name");
        holder.nodemcuLink.setMovementMethod(LinkMovementMethod.getInstance());
        holder.authToken.setText(nodeMCU.getAuthToken() != null ? nodeMCU.getAuthToken() : "No Auth Token");


        holder.authToken.setOnClickListener(v -> clipboard(holder, nodeMCU));

        holder.refreshBtn.setOnClickListener(v ->
                authUtils.refreshNodeMCU(nodeMCU.getId(), accessToken,
                (status, nodemcuRefRes) -> {
                    Toast.makeText(v.getContext(), nodemcuRefRes.getMsg(), Toast.LENGTH_SHORT).show();
                    if (status == 200) {
                        nodeMCU.setAuthToken(nodemcuRefRes.getAuthToken());
                        notifyDataSetChanged();
                    }
                },
                () -> Toast.makeText(v.getContext(), "Failed to refresh NodeMCU", Toast.LENGTH_SHORT).show())
        );

        holder.deleteBtn.setOnClickListener(v -> {
                AlertBox alertBox = new AlertBox((Activity) v.getContext(), "Delete NodeMCU", "Are you sure you want to delete this NodeMCU?", (is) -> setAlarm(is, nodeMCU, v));
                alertBox.show();
        });

    }
    @Override
    public int getItemCount() {
        return nodemcuList.size();
    }

    private void clipboard (NodeMCUViewHolder holder, NodeMCU nodeMCU) {
        ClipboardManager manager = (ClipboardManager) holder.authToken.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Auth Token", nodeMCU.getAuthToken());
        manager.setPrimaryClip(clipData);
        Toast.makeText(holder.authToken.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public static class NodeMCUViewHolder extends RecyclerView.ViewHolder {
        TextView nodemcuName, nodemcuLink, authToken;
        ImageButton refreshBtn;
        Button deleteBtn;


        public NodeMCUViewHolder(@NonNull View item) {
            super(item);
            nodemcuName = item.findViewById(R.id.nodemcuName);
            nodemcuLink = item.findViewById(R.id.nodemcuLink);
            authToken = item.findViewById(R.id.authToken);
            refreshBtn = item.findViewById(R.id.refreshBtn);
            deleteBtn = item.findViewById(R.id.deleteBtn);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAlarm (Boolean isConfirmed, NodeMCU nodeMCU, View v) {


        if (isConfirmed) {
            authUtils.deleteNodeMCU(nodeMCU.getId() ,accessToken,
                    (status, msg) -> {
                        Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
                        if (status == 200) {
                            nodemcuList.remove(nodeMCU);
                            notifyDataSetChanged();
                        }
                    }
                    ,() -> Toast.makeText(v.getContext(), "Failed to delete NodeMCU", Toast.LENGTH_SHORT).show()
            );
        }
    }
}

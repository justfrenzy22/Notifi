package com.notify;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
//

public class AlertBox extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    private final String msg;
    private final String title;

    private final Listener listener;

    public interface Listener {

        void onResult(boolean confirmed);
    }

    public AlertBox(Activity a, String title, String msg, Listener listener) {
        super(a);
        this.c = a;
        this.title = title;
        this.msg = msg;
        this.listener = listener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_box);
        setCanceledOnTouchOutside(false);

        Button yes = findViewById(R.id.positiveBtn);
        Button no = findViewById(R.id.negativeBtn);
        TextView titleTxt = findViewById(R.id.titleTxt);
        TextView msgTxt = findViewById(R.id.msgTxt);
        LinearLayout positiveBtnLayout = findViewById(R.id.positiveBtnLayout);
        LinearLayout negativeBtnLayout = findViewById(R.id.negativeBtnLayout);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        msgTxt.setText(msg);
        titleTxt.setText(title);
        setHoverListener(yes, true, positiveBtnLayout);
        setHoverListener(no, false, negativeBtnLayout);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.positiveBtn) {
            if (listener != null) listener.onResult(true);
            dismiss();
        } else if (id == R.id.negativeBtn) {
            if (listener != null) listener.onResult(false);
            dismiss();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setHoverListener(Button button, Boolean isPositive, LinearLayout layout) {
        button.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    hover(isPositive, layout, true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    hover(isPositive ,layout, false);
                    break;
            }
            return false;
        });
    }

    private void hover (Boolean positive, LinearLayout layout, Boolean isHovered) {
        if (positive) {
            layout.setBackgroundResource(isHovered ? R.drawable.rounded_error_btn_hover : R.drawable.rounded_error_btn);
        }
        else {
            layout.setBackgroundResource(isHovered ? R.drawable.rounded_side_btn_hover : R.drawable.rounded_side_btn);
        }
    }
}

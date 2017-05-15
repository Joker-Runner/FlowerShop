package com.joker.flowershop.utils;

import android.content.Context;
import android.support.annotation.IntDef;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.joker.flowershop.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by joker on 5/15 0015.
 */

public class MyCustomToast {
    /**
     * @hide
     */
    @IntDef({LENGTH_SHORT, LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    public static final int LENGTH_SHORT = 0;

    public static final int LENGTH_LONG = 1;


    public static void show(Context context, String text, @Duration int duration) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.toast_text);
        textView.setText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 300);
        toast.setDuration(duration);
        toast.setView(view);
        toast.show();
    }
}

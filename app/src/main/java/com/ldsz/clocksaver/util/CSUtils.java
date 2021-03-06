package com.ldsz.clocksaver.util;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.ldsz.clocksaver.R;


public class CSUtils {

    public static void setPosition(boolean np, boolean cover, Activity a) {

        TextView t1 = a.findViewById(R.id.textL1);
        TextView t2 = a.findViewById(R.id.textL2);
        TextView tc1 = a.findViewById(R.id.textClock);
        FrameLayout f = a.findViewById(R.id.MainFrame);

        ImageView i = a.findViewById(R.id.ImgCover);

        // Define the position of the element
        if (np) {
            t2.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p2.setMargins(410, 110, 0, 0);
            t1.setLayoutParams(p2);
            FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p3.setMargins(410, 160, 0, 0);
            t2.setLayoutParams(p3);
        } else {
            t2.setVisibility(View.INVISIBLE);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p2.setMargins(410, 110, 0, 0);
            t1.setLayoutParams(p2);
        }
        if (cover) {
            i.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p2.setMargins(410, 110, 0, 0);
            t1.setLayoutParams(p2);
            FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p3.setMargins(410, 160, 0, 0);
            t2.setLayoutParams(p3);
            FrameLayout.LayoutParams p4 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p4.setMargins(300, 0, 0, 0);
            p4.gravity = Gravity.START;
            tc1.setLayoutParams(p4);
            CardView.LayoutParams p5 = new CardView.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p5.setMargins(300, 110, 0, 0);
            p5.gravity = Gravity.START;
            p5.height = 100;
            p5.width = 100;
            i.setLayoutParams(p5);
        } else {
            i.setVisibility(View.INVISIBLE);
            FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p1.setMargins(0, 100, 0, 0);
            p1.gravity = Gravity.CENTER_HORIZONTAL;
            t1.setLayoutParams(p1);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p2.setMargins(0, 150, 0, 0);
            p2.gravity = Gravity.CENTER_HORIZONTAL;
            t2.setLayoutParams(p2);
            FrameLayout.LayoutParams p4 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p4.gravity = Gravity.CENTER_HORIZONTAL;
            tc1.setLayoutParams(p4);
        }
    }

}

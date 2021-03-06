package com.ldsz.clocksaver;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;


public class MainActivity extends Activity {

    private static final String TAG = "Main View";
    public SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout f1 = findViewById(R.id.FrameNotification);
        CheckBox c1 = findViewById(R.id.cb_NP);
        CheckBox c2 = findViewById(R.id.cb_Cover);
        SeekBar s1 = findViewById(R.id.seekBar);
        ImageView i1 = findViewById(R.id.ImgCover);

        boolean np;
        boolean co;

        i1.setImageResource(R.drawable.cover);

        // Check user permission
        if (!NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext()).contains(getApplicationContext().getPackageName())) {
            //service is not enabled try to enabled by calling...
            f1.setVisibility(View.VISIBLE);
            String s = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").getAction();
            Log.i(TAG, "ERROR ? " + s);
            //getApplicationContext().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            f1.setVisibility(View.VISIBLE);
        }

        // get Preferences
        sharedPref = getSharedPreferences("PREF_KEY", Context.MODE_PRIVATE);
        if (sharedPref.getBoolean(getResources().getString(R.string.save_NowPlaying), true)) {
            c1.setChecked(true);
            np = true;
        } else {
            c1.setChecked(false);
            np = false;
        }
        if (sharedPref.getBoolean(getResources().getString(R.string.save_Cover), false)) {
            c2.setChecked(true);
            co = true;
        } else {
            c2.setChecked(false);
            co = false;
        }

        setPosition( np, co);

        int p = sharedPref.getInt("SIZE", 45);

        s1.setProgress(p);
        Log.i(TAG, "Size pref : " + p);

        s1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor editor = sharedPref.edit();
                progressChangedValue = progress;
                Log.i(TAG, "Size : " + progress);
                TextView tc1 = findViewById(R.id.textClock);
                tc1.setTextSize(progress);
                editor.putInt("SIZE", progress);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
    }

    public void onCheckboxClicked(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();

        CheckBox c1 = findViewById(R.id.cb_NP);
        CheckBox c2 = findViewById(R.id.cb_Cover);

        boolean np;
        boolean co;

        if (c1.isChecked()) {
            np = true;
            editor.putBoolean(getResources().getString(R.string.save_NowPlaying), true);
        } else {
            np = false;
            editor.putBoolean(getResources().getString(R.string.save_NowPlaying), false);
        }
        if (c2.isChecked()) {
            co = true;
            editor.putBoolean(getResources().getString(R.string.save_Cover), true);
        } else {
            co = false;
            editor.putBoolean(getResources().getString(R.string.save_Cover), false);
        }

        setPosition(np, co);
        Log.i(TAG, "NP : " + np + " - CO : " + co);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        FrameLayout f1 = findViewById(R.id.FrameNotification);

        // Check user permission
        if (!NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext()).contains(getApplicationContext().getPackageName())) {
            f1.setVisibility(View.VISIBLE);
            //String s = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").getAction();
            //getApplicationContext().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            f1.setVisibility(View.INVISIBLE);
        }
    }

    public void onSeekBarClicked(View view) {
        SeekBar s1 = findViewById(R.id.seekBar);
        Log.i(TAG, "SeekBar" + s1.getProgress());
    }

    public void setPosition(boolean np, boolean cover) {
        TextView t1 = findViewById(R.id.textL1);
        TextView t2 = findViewById(R.id.textL2);
        TextView tc1 = findViewById(R.id.textClock);

        ImageView i = findViewById(R.id.ImgCover);

        // Define the position of the element
        if(np) {
            t2.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p2.setMargins(410, 110, 0, 0);
            t1.setLayoutParams(p2);
            FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p3.setMargins(410, 160, 0, 0);
            t2.setLayoutParams(p3);
        }
        else {
            t2.setVisibility(View.INVISIBLE);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p2.setMargins(410, 110, 0, 0);
            t1.setLayoutParams(p2);
        }
        if(cover) {
            i.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p2.setMargins(410,110,0,0);
            t1.setLayoutParams(p2);
            FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p3.setMargins(410,160,0,0);
            t2.setLayoutParams(p3);
            FrameLayout.LayoutParams p4 = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p4.setMargins(300,0,0,0);
            p4.gravity = Gravity.START;
            tc1.setLayoutParams(p4);
            CardView.LayoutParams p5 = new CardView.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT );
            p5.setMargins(300,110,0,0);
            p5.gravity = Gravity.START;
            p5.height=100;
            p5.width=100;
            i.setLayoutParams(p5);
        }
        else {
            i.setVisibility(View.INVISIBLE);
            FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p1.setMargins(0,100,0,0);
            p1.gravity = Gravity.CENTER_HORIZONTAL;
            t1.setLayoutParams(p1);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p2.setMargins(0,150,0,0);
            p2.gravity = Gravity.CENTER_HORIZONTAL;
            t2.setLayoutParams(p2);
            FrameLayout.LayoutParams p4 = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p4.gravity = Gravity.CENTER_HORIZONTAL;
            tc1.setLayoutParams(p4);
        }
    }

}

 package com.ldsz.clocksaver;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.service.dreams.DreamService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.ldsz.clocksaver.util.LayoutWrapContentUpdater;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ServiceSaver extends DreamService {

    // https://android-developers.googleblog.com/2012/12/daydream-interactive-screen-savers.html
    // https://stackoverflow.com/questions/59052978/is-there-any-way-to-fetch-the-info-of-the-current-playing-song

    private Handler myHandler;
    private final String TAG = "ClockSaver";
    public static String CS_MSG_1 = "CS_MSG_1";
    private static final boolean VERBOSE = true;
    private ServiceReceiver SR;
    private String Media_Title;
    private String Media_Artist;
    private Bitmap Media_Cover;
    private boolean np = false;
    private boolean co = false;
    boolean NowPlaying_Found = false;
    public SharedPreferences sharedPref;

    private final Runnable RunTheMove = new Runnable() {
        @Override
        public void run() {
            MoveClock();
            myHandler.postDelayed(this,100*60*5);
        }
    };

    private Context createDisplayContext() {
        return this;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        Log.i(TAG, "Starting the Clock");

        // Exit dream upon user touch
        setInteractive(false);
        // Hide system UI
        setFullscreen(true);
        // Set Brightness
        setScreenBright(false);
        // Set the dream layout
        setContentView(R.layout.main_layout);

        // Define variable
        TextView t1 = findViewById(R.id.textL1);
        TextView t2 = findViewById(R.id.textL2);
        TextView tc1 = findViewById(R.id.textClock);
        ImageView i = findViewById(R.id.ImgCover);

        //DISPLAY DATE
        int style = DateFormat.LONG;
        Date date = new Date();
        DateFormat df;
        df = DateFormat.getDateInstance(style, Locale.FRANCE);
        t1.setText(df.format(date));

        // GET PLAYING INFO
        NLService myNL = NLService.get();
        if(myNL == null) {
            Log.i(TAG, "No notification");
            NowPlaying_Found = false;
        }
        else
        {
            Log.i(TAG, "Active Notification : ");
            for(StatusBarNotification notification : myNL.getActiveNotifications()){
                if(notification.getTag() != null){
                if(notification.getTag().equals("Notification.NowPlaying")){
                    String Text = notification.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();
                    String title = notification.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();
                    Log.i(TAG, "Cover : " + notification.getNotification().extras.get(Notification.EXTRA_PICTURE));
                    i.setImageBitmap( (Bitmap) notification.getNotification().extras.get(Notification.EXTRA_PICTURE));
                    String[] artist = Text.split("‧");
                    Log.i(TAG, "NowPlaying : " + title + " / " + Text);
                    Media_Title = title;
                    Media_Artist = artist[1].substring(1);
                    Media_Cover = (Bitmap) notification.getNotification().extras.get(Notification.EXTRA_PICTURE);
                    NowPlaying_Found = true;
                    break;
                }
                else {
                    // Now Playing Null, make it invisible
                    t2.setVisibility(View.INVISIBLE);
                    i.setVisibility(View.INVISIBLE);
                    NowPlaying_Found = false;
                }
                }
                else {
                    // No notification, make it invisible
                    t2.setVisibility(View.INVISIBLE);
                    i.setVisibility(View.INVISIBLE);
                    NowPlaying_Found = false;
                }
            }
            Log.i(TAG, "End. Found : " + NowPlaying_Found);
        }

        // Get settings
        sharedPref = getSharedPreferences("PREF_KEY", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean(getResources().getString(R.string.save_NowPlaying), false) & NowPlaying_Found) {
            np = true;
        }
        if(sharedPref.getBoolean(getResources().getString(R.string.save_Cover), false) & NowPlaying_Found) {
            co = true;
        }

        setPosition( np, co);

        // Define Clock Size
        int p = sharedPref.getInt("SIZE", 45);
        tc1.setTextSize(p);

        // Talk to me
        Log.i(TAG, "NP : " + sharedPref.getBoolean(getResources().getString(R.string.save_NowPlaying), false));
        Log.i(TAG, "Cover : " + sharedPref.getBoolean(getResources().getString(R.string.save_Cover), false));
        Log.i(TAG, "Size : " + sharedPref.getInt(getResources().getString(R.string.save_Size), 45));

        //Define Receiver
        SR = new ServiceReceiver();
        registerReceiver(SR, new IntentFilter(CS_MSG_1));

        //Move it
        MoveClock();

        // START TIMER
        if (!NowPlaying_Found) {
            if(VERBOSE) Log.i(TAG, "Starting Handler");
            myHandler = new Handler(Looper.getMainLooper());
            myHandler.postDelayed(RunTheMove, 1000);
        }

    }

    // Data receiver from NL Service
    public class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Define variable
            TextView t2 = findViewById(R.id.textL2);
            ImageView i = findViewById(R.id.ImgCover);

            if (!intent.getExtras().getString("title").equals(Media_Title) && !(intent.getExtras().getString("cover") instanceof String)) {
                if (VERBOSE) Log.i(TAG, "Update Media info." + Media_Title + "-" + Media_Artist);
                Media_Title = intent.getExtras().getString("title");
                Media_Artist = intent.getExtras().getString("artist");
                byte[] A = intent.getExtras().getByteArray("cover");
                Media_Cover = BitmapFactory.decodeByteArray(A, 0, A.length);
                if (Media_Artist != null || Media_Title != null) {
                    MoveClock();
                }

                if(!NowPlaying_Found) {
                    // Now Playing exist, make it visible
                    if(sharedPref.getBoolean(getResources().getString(R.string.save_NowPlaying), false) & NowPlaying_Found) {
                        np = true;
                        t2.setVisibility(View.VISIBLE);
                    }
                    if(sharedPref.getBoolean(getResources().getString(R.string.save_Cover), false) & NowPlaying_Found) {
                        co = true;
                        i.setVisibility(View.VISIBLE);
                    }
                    setPosition(true, true);
                    NowPlaying_Found = true;
                }
            }
            if(intent.getExtras().getString("cover") instanceof String) {
                if (VERBOSE) Log.i(TAG, "Notification removed");

                // Now Playing Null, make it invisible
                t2.setVisibility(View.INVISIBLE);
                i.setVisibility(View.INVISIBLE);

                setPosition(false, false);

                NowPlaying_Found = false;
            }
        }
    }

    // Check user permission
    /*
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

     */

    @Override
    public void onDetachedFromWindow() {
        if(VERBOSE) Log.i(TAG, "Ending the Clock");
         unregisterReceiver(SR);
         //myHandler.removeCallbacks(RunTheMove);
    }

    public void MoveClock(){
        // Move the Clock
        if(VERBOSE) Log.i(TAG, "Moving the Clock !");
        FrameLayout f = findViewById(R.id.MainFrame);

        int[] locationOnScreen = new int[2];
        f.getLocationOnScreen(locationOnScreen);

        //Display NowPlaying
        TextView t2 = findViewById(R.id.textL2);
        t2.setText(getString(R.string.NP_info,Media_Artist, Media_Title));
        ImageView i = findViewById(R.id.ImgCover);
        i.setImageBitmap(Media_Cover);

        //Get Screen Size
        int screenHeight;
        int screenWidth;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            Point size = new Point();
            Display display = getWindowManager().getDefaultDisplay();
            display.getSize(size);

            screenWidth = size.x;
            screenHeight = size.y;
        }
        else
        {
            Context c = createDisplayContext();
            WindowManager WM = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            WindowMetrics m =WM.getCurrentWindowMetrics();

            Rect bounds = m.getBounds();
            screenWidth = bounds.width();
            screenHeight = bounds.height();
        }
        LayoutWrapContentUpdater.wrapContentAgain(f);
        if(VERBOSE) Log.i(TAG, "Screen Width : " + screenWidth + " Height : " + screenHeight + " - Frame Width : " + f.getWidth() + " Height : " + f.getHeight());


        Random random = new Random();
        //f.refreshDrawableState();
        int randomX = random.nextInt(screenWidth - f.getWidth());
        int randomY = random.nextInt(screenHeight - f.getHeight());


        //ANIMATION
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(f, "alpha", 0, 1f);
        fadeIn.setDuration(2000);
        fadeIn.start();

        f.setX(randomX);
        f.setY(randomY);
        if(VERBOSE) Log.i(TAG, "MaxX : "+ (screenWidth - f.getWidth()) + " MaxY : "+ (screenHeight - f.getHeight()) +" - Random X : " +randomX + " Y : " + randomY );

        if(f.getVisibility() == View.INVISIBLE)f.setVisibility(View.VISIBLE);
    }

    public void setPosition(boolean np, boolean cover) {

        TextView t1 = findViewById(R.id.textL1);
        TextView t2 = findViewById(R.id.textL2);
        TextView tc1 = findViewById(R.id.textClock);

        ImageView i = findViewById(R.id.ImgCover);

        // Define the position of the element
        if (np) {
            t2.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p2.setMargins(410, 100, 0, 0);
            t1.setLayoutParams(p2);
            FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p3.setMargins(410, 150, 0, 0);
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
            p2.setMargins(410, 120, 0, 0);
            t1.setLayoutParams(p2);
            FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p3.setMargins(410, 170, 0, 0);
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

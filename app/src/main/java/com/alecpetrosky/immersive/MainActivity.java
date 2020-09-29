package com.alecpetrosky.immersive;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.*;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.*;

public class MainActivity extends AppCompatActivity {

    private boolean isFullScreen;
    private Toolbar toolbar;
    private View bottomActions;
    private ImageView topShadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(systemUiVisibilityChangeListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_back);
        // toolbar.setBackgroundColor(Color.argb(186, 0, 0, 0));
        setSupportActionBar(toolbar);

        topShadow = findViewById(R.id.top_shadow);
        bottomActions = findViewById(R.id.bottom_actions);

    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        topShadow.getLayoutParams().height = getStatusBarHeight(this) + toolbar.getMeasuredHeight();
    }



    // todo: need to consider Multi window mode, too ???
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    // todo: need to consider Multi window mode
    public static int getNavigationBarHeight(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);
        int navigationBarHeight = 0;

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navigationBarHeight = resources.getDimensionPixelSize(resourceId);
            }
        }

        return navigationBarHeight;
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size;
    }

    View.OnSystemUiVisibilityChangeListener systemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            isFullScreen = ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0);
            updateSystemUI();
        }
    };

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            initBottomActionsLayout(true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            initBottomActionsLayout(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSystemUI();
        initBottomActionsLayout(false);
    }

    private void initBottomActionsLayout(boolean skipNavigationBar) {
        int bottomActionsHeight = (int)getResources().getDimension(R.dimen.bottom_actions_height);
        int navigationBarHeight = (skipNavigationBar) ? 0 : getNavigationBarHeight(this);
        bottomActions.getLayoutParams().height = bottomActionsHeight + navigationBarHeight;
    }

    private void updateSystemUI() {
        if (isFullScreen) {
            hideSystemUI();
            fadeOut(toolbar, topShadow, bottomActions);
        } else {
            showSystemUI();
            fadeIn(toolbar, topShadow, bottomActions);
        }
    }

    private void fadeIn(final View... views) {
        for (final View view : views) {
            view.animate().alpha(1f).withStartAction(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void fadeOut(final View... views) {
        for (final View view : views) {
            view.animate().alpha(0f).withEndAction(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.GONE);
                }
            });
        }
    }

    public void toggleSystemUI() {
        isFullScreen = !isFullScreen;
        updateSystemUI();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void onImageViewClick(View view) {
        toggleSystemUI();
    }

    @Override
    public void onUserInteraction() {
        Log.d("xxx","Touch anywhere happened");
        super.onUserInteraction();
    }

    public void testClick(View view) {
        Log.d("xxx","Button clicked");
    }
}
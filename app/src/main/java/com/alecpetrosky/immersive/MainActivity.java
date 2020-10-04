package com.alecpetrosky.immersive;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.appcompat.app.*;
import androidx.appcompat.widget.Toolbar;

import static com.alecpetrosky.immersive.util.UI.fadeIn;
import static com.alecpetrosky.immersive.util.UI.fadeOut;
import static com.alecpetrosky.immersive.util.UI.getNavigationBarHeight;
import static com.alecpetrosky.immersive.util.UI.getStatusBarHeight;
import static com.alecpetrosky.immersive.util.UI.hideSystemUI;
import static com.alecpetrosky.immersive.util.UI.showSystemUI;

public class MainActivity extends AppCompatActivity {

    private boolean mIsFullScreen;
    private int mOrientation;
    private Toolbar toolbar;
    private View bottomActions;
    private ImageView topShadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_back);
        // toolbar.setBackgroundColor(Color.argb(186, 0, 0, 0));
        setSupportActionBar(toolbar);

        topShadow = findViewById(R.id.top_shadow);
        bottomActions = findViewById(R.id.bottom_actions);

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(systemUiVisibilityChangeListener);

    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        updateSystemUI();
    }

    View.OnSystemUiVisibilityChangeListener systemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
        mIsFullScreen = ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0);
        Log.d("xxx", "mIsFullScreen: " + mIsFullScreen);
        updateSystemUI();
        }
    };

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mOrientation = newConfig.orientation;
        updateSystemUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSystemUI();
    }

    private void initBottomActionsLayout() {
        int navigationBarHeight = getNavigationBarHeight(this, mOrientation);
        int bottomActionsHeight = (int)getResources().getDimension(R.dimen.bottom_actions_height);
        bottomActions.getLayoutParams().height = bottomActionsHeight + navigationBarHeight;
    }

    private void updateSystemUI() {
        topShadow.getLayoutParams().height = getStatusBarHeight(this) + toolbar.getMeasuredHeight();
        initBottomActionsLayout();
        if (mIsFullScreen) {
            hideSystemUI(this);
            fadeOut(toolbar, topShadow, bottomActions);
        } else {
            showSystemUI(this);
            fadeIn(toolbar, topShadow, bottomActions);
        }
    }

    public void toggleSystemUI() {
        mIsFullScreen = !mIsFullScreen;
        updateSystemUI();
    }

    public void onImageViewClick(View view) {
        toggleSystemUI();
    }

    public void testClick(View view) {
        // Log.d("xxx","Button clicked");
    }
}
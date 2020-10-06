package com.alecpetrosky.immersive;

import android.content.res.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.appcompat.app.*;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.*;

import com.alecpetrosky.immersive.listener.*;
import com.alecpetrosky.immersive.util.*;

import static com.alecpetrosky.immersive.util.UI.*;

public class MainActivity extends AppCompatActivity {

    private boolean mIsFullScreen;
    // private int mOrientation;
    private Toolbar topToolbar;
    private View bottomToolbar;
    private ImageView topShadow;
    private DisplayCutout displayCutout;
    private boolean hasDisplayCutouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Prevent jumping of the player on devices with cutout
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }


        topToolbar = findViewById(R.id.top_toolbar);
        topToolbar.setNavigationIcon(R.drawable.ic_chevron_back);
        // toolbar.setBackgroundColor(Color.argb(186, 0, 0, 0));
        setSupportActionBar(topToolbar);

        topShadow = findViewById(R.id.top_shadow);
        bottomToolbar = findViewById(R.id.bottom_toolbar);

        initBottomToolbarButtons();
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(systemUiVisibilityChangeListener);
        findViewById(R.id.fullscreen_layout).setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onClick() {
                super.onClick();
                toggleSystemUI();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSystemUI();
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        updateSystemUI();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            displayCutout =
                    getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
            if (displayCutout != null) {
                int safeInsets = displayCutout.getSafeInsetTop() + displayCutout.getSafeInsetBottom()
                        + displayCutout.getSafeInsetLeft() + displayCutout.getSafeInsetRight();
                hasDisplayCutouts = safeInsets > 0;
            }
        }
    }

    View.OnSystemUiVisibilityChangeListener systemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            if ((visibility & View.SYSTEM_UI_FLAG_LOW_PROFILE) == 0) {
                mIsFullScreen = false;
            } else {
                mIsFullScreen = ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0);
            }
        updateSystemUI();
        }
    };

    /**
     * There is no link anymore between Activity orientation changing and device physically being rotated.
     * I think the only reasonable use of Activity orientation changes now is to update your resources.
     * See https://stackoverflow.com/a/41401863/13776879
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateSystemUI();
    }

    private void updateSystemUI() {
        int statusBarHeight = 0;
        int navigationBarHeight = 0;
        int deviceOrientation = getDeviceOrientation(this);

        if (isInSingleWindowMode(this)) {
            statusBarHeight = getStatusBarHeight(this);
            if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT) {
                navigationBarHeight = getNavigationBarHeight(this);
            }
        } else if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT) {
            if (isOnTopScreen(getWindow().getDecorView())) {
                statusBarHeight = getStatusBarHeight(this);
            } else {
                navigationBarHeight = getNavigationBarHeight(this);
            }
        } else {
            statusBarHeight = getStatusBarHeight(this);
        }

        topToolbar.setPadding(0, statusBarHeight, 0, 0);
        topShadow.getLayoutParams().height = topToolbar.getMeasuredHeight();
        bottomToolbar.getLayoutParams().height =
                (int)getResources().getDimension(R.dimen.bottom_toolbar_height) +
                        navigationBarHeight;

        if (mIsFullScreen) {
            fadeOut(topToolbar, topShadow, bottomToolbar);
        } else {
            fadeIn(topToolbar, topShadow, bottomToolbar);
        }

        // "true" fullscreen mode does not exist in multi-window mode
        if ((isInSingleWindowMode(this)) && (mIsFullScreen)) {
            hideSystemUI(this);
        } else {
            showSystemUI(this);
        }
    }

    public void toggleSystemUI() {
        mIsFullScreen = !mIsFullScreen;
        updateSystemUI();
    }

    private void initBottomToolbarButtons() {
        findViewById(R.id.bottom_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toggle_favorite, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.bottom_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.edit, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.bottom_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.share, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.bottom_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.delete, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
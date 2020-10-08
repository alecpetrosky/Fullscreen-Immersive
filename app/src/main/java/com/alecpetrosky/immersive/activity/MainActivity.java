package com.alecpetrosky.immersive.activity;

import android.content.res.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.appcompat.app.*;
import androidx.appcompat.widget.*;
import androidx.appcompat.widget.Toolbar;

import com.alecpetrosky.immersive.*;
import com.alecpetrosky.immersive.listener.*;

import static com.alecpetrosky.immersive.util.UI.*;

public class MainActivity extends AppCompatActivity {

    private boolean mIsFullScreen;

    private View uiContainer;
    private Toolbar topToolbar;
    private View topShadow;
    private View bottomToolbar;
    private View bottomShadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Prevent jumping of the player on devices with cutout
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        topShadow = findViewById(R.id.top_shadow);
        topShadow.setOnClickListener(doNothingOnClick);
        bottomShadow = findViewById(R.id.bottom_shadow);
        bottomShadow.setOnClickListener(doNothingOnClick);

        uiContainer = findViewById(R.id.ui_container);

        bottomToolbar = findViewById(R.id.bottom_toolbar);
        topToolbar = findViewById(R.id.top_toolbar);
        topToolbar.setNavigationIcon(R.drawable.ic_chevron_back_outline);
        setSupportActionBar(topToolbar);

        initBottomToolbarButtons();
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(onSystemUiVisibilityChangeListener);
        findViewById(R.id.fullscreen_layout).setOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
    }

    /**
     * At least, on Lineage OS running Android 10, the activity is not restarted in automatic mode
     * on orientation change. So, let's do it ourselves.
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // coming back (first touch) in multi-window mode will not trigger toggling system UI
        findViewById(R.id.fullscreen_layout).setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onClick() {
                super.onClick();
                toggleSystemUI();
            }
        });
        updateSystemUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        findViewById(R.id.fullscreen_layout).setOnTouchListener(null);
    }

    private View.OnSystemUiVisibilityChangeListener onSystemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {
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

    // See https://stackoverflow.com/a/50775459/13776879
    private View.OnApplyWindowInsetsListener onApplyWindowInsetsListener = new View.OnApplyWindowInsetsListener() {
        @Override
        public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {

            setMargins(topShadow, 0, insets.getSystemWindowInsetTop(), 0, 0);
            setMargins(bottomShadow, 0, 0, 0, insets.getSystemWindowInsetBottom());

            uiContainer.setPadding(
                    insets.getSystemWindowInsetLeft(),
                    insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(),
                    insets.getSystemWindowInsetBottom());
            return insets.consumeSystemWindowInsets();
        }
    };

    // Avoid toggling fullscreen on UI clicks
    private View.OnClickListener doNothingOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {}
    };

    private void updateSystemUI() {
        if (mIsFullScreen) {
            fadeOut(topToolbar, topShadow, bottomToolbar, bottomShadow);
        } else {
            fadeIn(topToolbar, topShadow, bottomToolbar, bottomShadow);
        }

        // "true" fullscreen mode does not exist in multi-window mode
        if ((isInSingleWindowMode(this)) && (mIsFullScreen)) {
            hideSystemUI(this);
        } else {
            showSystemUI(this);
        }
    }

    private void toggleSystemUI() {
        mIsFullScreen = !mIsFullScreen;
        updateSystemUI();
    }

    private void initBottomToolbarButtons() {
        TooltipCompat.setTooltipText(findViewById(R.id.bottom_favorite), findViewById(R.id.bottom_favorite).getContentDescription());
        findViewById(R.id.bottom_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.toggle_favorite, Toast.LENGTH_SHORT).show();
            }
        });
        TooltipCompat.setTooltipText(findViewById(R.id.bottom_edit), findViewById(R.id.bottom_edit).getContentDescription());
        findViewById(R.id.bottom_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.edit, Toast.LENGTH_SHORT).show();
            }
        });
        TooltipCompat.setTooltipText(findViewById(R.id.bottom_share), findViewById(R.id.bottom_share).getContentDescription());
        findViewById(R.id.bottom_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.share, Toast.LENGTH_SHORT).show();
            }
        });
        TooltipCompat.setTooltipText(findViewById(R.id.bottom_delete), findViewById(R.id.bottom_delete).getContentDescription());
        findViewById(R.id.bottom_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.delete, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

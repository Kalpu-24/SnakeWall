
package kalp.snake.wall;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;  // NEW
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.Objects;

import kalp.snake.wall.data.ColorThemesData;
import kalp.snake.wall.models.ColorTheme;
import kalp.snake.wall.models.ColorPrefConfig;
import kalp.snake.wall.service.SnakeWallpaperService;
import kalp.snake.wall.views.PrefsBottomModalSheet;
import kalp.snake.wall.views.SnakePreView;
import kalp.snake.wall.views.ThemesBottomModalSheet;

public class MainActivity extends AppCompatActivity {
    MaterialCardView snakePreViewCard, themesCard, themePreviewCard, settingsCard, aboutCard;
    TextView githubFAB, supportFAB;
    TextView themePreviewText, versionName;
    ColorTheme[] colorThemes;
    @SuppressLint("StaticFieldLeak")
    static SnakePreView snakePreView;
    Handler handler;
    int uiMode;
    int i = 0;

    // === NEW CONSTANTS FOR IMAGE PICKER AND PREFS ===
    private static final int PICK_IMAGE_REQUEST = 101;
    public static final String CUSTOM_BG_URI_KEY = "custom_background_uri";
    public static final String CURRENT_THEME_KEY = "current_theme_key"; 
    public static final String CUSTOM_IMAGE_UI_DARK_KEY = "custom_image_ui_dark";
// save currently selected theme id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        uiMode = getResources().getConfiguration().uiMode;

        themePreviewCard = findViewById(R.id.themePreviewCard);
        themePreviewText = findViewById(R.id.themePreviewText);
        snakePreViewCard = findViewById(R.id.SnakePreViewCard);
        settingsCard = findViewById(R.id.settingsCard);
        themesCard = findViewById(R.id.themesCard);
        aboutCard = findViewById(R.id.aboutCard);
        snakePreView = findViewById(R.id.snakePreView);
        
        // Bind main preview to prefs & enable custom image rendering
        snakePreView.setBindToPrefs(true);
        snakePreView.setEnableCustomImageFromPrefs(true);
        // Initial render so overlay + image apply immediately
        SharedPreferences prefs = getSharedPreferences("SnakeGamePrefs", MODE_PRIVATE);
        if (!prefs.contains(CURRENT_THEME_KEY)) {
            prefs.edit().putInt(CURRENT_THEME_KEY, 0).apply();
            ColorTheme[] themes = ColorThemesData.getThemes();
            ColorPrefConfig config = themes[0].colorPrefConfig;
            prefs.edit()
                .putInt(ColorPrefConfig.snakeBackgroundColorKey, config.getSnakeBackgroundColor())
                .putInt(ColorPrefConfig.snakeColorKey, config.getSnakeColor())
                .putInt(ColorPrefConfig.gridColorKey, config.getGridColor())
                .putInt(ColorPrefConfig.buttonsAndFrameColorKey, config.getButtonsAndFrameColor())
                .putInt(ColorPrefConfig.foodColorKey, config.getFoodColor())
                .apply();
        }
        snakePreView.updateColors();
versionName = findViewById(R.id.versionName);
        githubFAB = findViewById(R.id.githubButton);
        supportFAB = findViewById(R.id.supportButton);

        // set snake preview card height to match aspect ratio of device screen
        GridLayout.LayoutParams params = (GridLayout.LayoutParams) snakePreViewCard.getLayoutParams();
        // get screen aspect ratio
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float aspectRatio = (float) displayMetrics.heightPixels / displayMetrics.widthPixels;
        params.height = (int) (getResources().getDisplayMetrics().widthPixels * (aspectRatio / 2.2));
        snakePreViewCard.setLayoutParams(params);

        GridLayout.LayoutParams aboutParams = (GridLayout.LayoutParams) aboutCard.getLayoutParams();
        aboutParams.height = (int) (getResources().getDisplayMetrics().widthPixels * (aspectRatio / 4.4));
        aboutCard.setLayoutParams(aboutParams);

        supportFAB.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/7DKynMHAK4"));
            startActivity(intent);
        });
        githubFAB.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Kalpu-24/SnakeWall"));
            startActivity(intent);
        });
        try {
            versionName.setText("V".concat(Objects.requireNonNull(getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName)));
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        colorThemes = ColorThemesData.getThemes();
        snakePreViewCard.setOnClickListener(v -> {
            Intent intent = new Intent(
                    WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, SnakeWallpaperService.class));
            startActivity(intent);
        });
        themesCard.setOnClickListener(v -> {
            ThemesBottomModalSheet themesBottomModalSheet = new ThemesBottomModalSheet();
            themesBottomModalSheet.show(getSupportFragmentManager(), "Theme Sheet");
        });
        settingsCard.setOnClickListener(v -> {
            PrefsBottomModalSheet prefsBottomModalSheet = new PrefsBottomModalSheet();
            prefsBottomModalSheet.show(getSupportFragmentManager(), "Pref Sheet");
        });

        handler = new Handler();
        loopPreview();
    }

    // === NEW: Launch system image picker (Storage Access Framework) ===
    public void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        // Request read (and persistable) permission; persistable is declared on the launching intent
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select Background Image"), PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Log.e("MainActivity", "No app available to handle image selection.");
        }
    }

    // === NEW: Handle image picker result ===
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Use the same app-pref file used by the wallpaper service
            SharedPreferences prefs = getSharedPreferences("SnakeGamePrefs", MODE_PRIVATE);
            // 1) Save the URI string
            prefs.edit().putString(CUSTOM_BG_URI_KEY, imageUri.toString()).apply();
            // 2) Persist URI permission so the service can read it long-term
            try {
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
            } catch (SecurityException e) {
                Log.e("MainActivity", "Failed to grant persistence: " + e.getMessage());
            }
            // 3) Update preview (colors won't show image, but keeps UI responsive)
            reDrawView();
        }
    }

    public void loopPreview() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                themePreviewCard.setCardBackgroundColor(colorThemes[i % colorThemes.length].colorPrefConfig.getSnakeBackgroundColor());
                themePreviewCard.setBackgroundTintList(null);
                themePreviewText.setText(colorThemes[i % colorThemes.length].title);
                themePreviewText.setTextColor(colorThemes[i % colorThemes.length].colorPrefConfig.getButtonsAndFrameColor());
                i++;
            }
        }, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure preview reflects latest prefs (theme/image/toggle)
        try { reDrawView(); } catch (Throwable ignore) {}
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(handler);
    }

    public static void reDrawView(){
        Log.d("MainActivity","reDrawView: ");
        snakePreView.updateColors();
    }
}

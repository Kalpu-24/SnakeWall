package kalp.snake.wall;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import kalp.snake.wall.data.ColorThemesData;
import kalp.snake.wall.models.ColorTheme;
import kalp.snake.wall.service.SnakeWallpaperService;
import kalp.snake.wall.views.PrefsBottomModalSheet;
import kalp.snake.wall.views.SnakePreView;
import kalp.snake.wall.views.ThemesBottomModalSheet;


public class MainActivity extends AppCompatActivity {

    MaterialCardView snakePreViewCard, themesCard, themePreviewCard, settingsCard;
    TextView githubFAB, supportFAB;
    TextView themePreviewText, versionName;
    ColorTheme[] colorThemes;
    SnakePreView snakePreView;
    Handler handler;
    int uiMode;

    int i = 0;

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
        snakePreView = findViewById(R.id.snakePreView);
        versionName = findViewById(R.id.versionName);
        githubFAB = findViewById(R.id.githubButton);
        supportFAB = findViewById(R.id.supportButton);

        supportFAB.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Kalpu_24_Games"));
            startActivity(intent);
        });

        githubFAB.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Kalpu-24/SnakeWall"));
            startActivity(intent);
        });

        try {
            versionName.setText("V".concat(getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(),0).versionName));
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
            themesBottomModalSheet.show(getSupportFragmentManager(),"Theme Sheet");
        });

        settingsCard.setOnClickListener(v -> {
            PrefsBottomModalSheet prefsBottomModalSheet = new PrefsBottomModalSheet();
            prefsBottomModalSheet.show(getSupportFragmentManager(),"Pref Sheet");
        });

        handler = new Handler();
        loopPreview();
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
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(handler);
    }
}
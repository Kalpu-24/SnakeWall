package kalp.snake.wall;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import kalp.snake.wall.data.ColorThemesData;
import kalp.snake.wall.models.ColorPrefConfig;
import kalp.snake.wall.models.ColorTheme;


public class MainActivity extends AppCompatActivity {

    MaterialCardView themePreviewCard;
    TextView themePreviewText;
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
        themePreviewCard = findViewById(R.id.themePreviewCard);
        themePreviewText = findViewById(R.id.themePreviewText);
        ColorTheme[] colorThemes = ColorThemesData.getThemes();

        themePreviewCard.setCardBackgroundColor(colorThemes[i].colorPrefConfig.getSnakeBackgroundColor());
        themePreviewCard.setBackgroundTintList(null);
        themePreviewText.setText(colorThemes[i].title);
        themePreviewText.setTextColor(colorThemes[i].colorPrefConfig.getButtonsAndFrameColor());

        loopPreview();
    }

    public void loopPreview() {
        Thread loop = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                i = (i + 1) % 4;
                Log.d("uhh", String.valueOf(i));
            }
        });
        loop.start();
    }
}
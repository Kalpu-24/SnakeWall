package kalp.snake.wall.views;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

import kalp.snake.wall.R;
import kalp.snake.wall.data.ColorThemesData;
import kalp.snake.wall.models.ColorPrefConfig;
import kalp.snake.wall.models.ColorTheme;

public class ThemesBottomModalSheet extends BottomSheetDialogFragment {

    SnakePreView snakePreView;

    public ThemesBottomModalSheet(SnakePreView snakePreView) {
        this.snakePreView = snakePreView;
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View gridView = inflater.inflate(R.layout.themes_sheet,container,false);
        GridLayout gridLayout = gridView.findViewById(R.id.mainGridLayout);
        gridLayout.setColumnCount(2);
        ColorTheme[] colorThemes = ColorThemesData.getThemes();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SnakeGamePrefs",0);

//        typeface
        Typeface mlcdType = Typeface.createFromAsset(requireContext().getAssets(), "font/mlcd.ttf");
        float factor = getResources().getDisplayMetrics().density;


//        Themes title
        TextView themesText = new TextView(requireContext());
        themesText.setText(getResources().getString(R.string.themes));
        themesText.setTypeface(mlcdType);
        themesText.setTextColor(ContextCompat.getColor(requireContext(),R.color.buttons_and_frame_color));
        themesText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f);
        GridLayout.LayoutParams textParams = new GridLayout.LayoutParams();
        textParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        textParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        textParams.bottomMargin = (int) (20 * factor);
        textParams.rowSpec = GridLayout.spec(0);
        textParams.columnSpec = GridLayout.spec(0,2);

        gridLayout.addView(themesText, textParams);

//        system theme
        int mode;
        int uiMode = getResources().getConfiguration().uiMode;
        if (uiMode == 0x20) mode=2;
        else mode=1;

//        linear layout
        LinearLayout themeLayout = new LinearLayout(requireContext());
        themeLayout.setOrientation(LinearLayout.VERTICAL);
        GridLayout.LayoutParams systemParams = new GridLayout.LayoutParams();
        systemParams.height = (int) (420*factor);
        systemParams.width = 0;
        systemParams.bottomMargin = (int) (20 * factor);
        systemParams.columnSpec = GridLayout.spec(0,GridLayout.FILL,1f);
        systemParams.rowSpec = GridLayout.spec(1);

        themeLayout.setOnClickListener(v -> {
            ColorPrefConfig.clearPrefs(sharedPreferences);
            snakePreView.setDefaultTheme();
            dismiss();
        });

//        card
        FrameLayout.LayoutParams systemCardFrameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (380*factor));
        MaterialCardView systemCardView = new MaterialCardView(requireContext());
        systemCardView.setCardElevation(0.0f);
        systemCardView.setStrokeWidth(0);
        systemCardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.activity_card_bg));

//      preview
        FrameLayout.LayoutParams systemFrameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        SnakePreView systemSnakePreView = new SnakePreView(requireContext());
        systemSnakePreView.setSnakeBackgroundColor(colorThemes[mode].colorPrefConfig.getSnakeBackgroundColor());
        systemSnakePreView.setFoodColor(colorThemes[mode].colorPrefConfig.getFoodColor());
        systemSnakePreView.setSnakeColor(colorThemes[mode].colorPrefConfig.getSnakeColor());
        systemSnakePreView.setGridColor(colorThemes[mode].colorPrefConfig.getGridColor());
        systemSnakePreView.setButtonsAndFrameColor(colorThemes[mode].colorPrefConfig.getButtonsAndFrameColor());

//        theme name
        TextView themesTextName = new TextView(requireContext());
        themesTextName.setText("System");
        themesTextName.setTypeface(mlcdType);
        themesTextName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        themesTextName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        themesTextName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        systemCardView.addView(systemSnakePreView,systemFrameParams);
        themeLayout.addView(systemCardView, systemCardFrameParams);
        themeLayout.addView(themesTextName);
        gridLayout.addView(themeLayout,systemParams);


        for (int i = 1; i < colorThemes.length; i++){


            int row = i / 2;
            int column = i % 2;

            //        linear layout
            LinearLayout otherLayout = new LinearLayout(requireContext());
            otherLayout.setOrientation(LinearLayout.VERTICAL);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = (int) (420*factor);
            params.width = 0;
            params.bottomMargin = (int) (20 * factor);
            params.columnSpec = GridLayout.spec(column,GridLayout.FILL,1f);
            params.rowSpec = GridLayout.spec(row+1);

            int finalI = i;
            otherLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    colorThemes[finalI].colorPrefConfig.saveToPrefs(sharedPreferences);
                    snakePreView.setTheme(colorThemes[finalI].colorPrefConfig);
                    dismiss();
                }
            });

            FrameLayout.LayoutParams cardFrameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (380*factor));
            MaterialCardView cardView = new MaterialCardView(requireContext());
            cardView.setCardElevation(0.0f);
            cardView.setStrokeWidth(0);
            cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.activity_card_bg));

            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            SnakePreView snakePreView = new SnakePreView(requireContext());
            snakePreView.setSnakeBackgroundColor(colorThemes[i].colorPrefConfig.getSnakeBackgroundColor());
            snakePreView.setFoodColor(colorThemes[i].colorPrefConfig.getFoodColor());
            snakePreView.setSnakeColor(colorThemes[i].colorPrefConfig.getSnakeColor());
            snakePreView.setGridColor(colorThemes[i].colorPrefConfig.getGridColor());
            snakePreView.setButtonsAndFrameColor(colorThemes[i].colorPrefConfig.getButtonsAndFrameColor());

            //        theme name
            TextView otherThemesTextName = new TextView(requireContext());
            otherThemesTextName.setText(colorThemes[i].title);
            otherThemesTextName.setTypeface(mlcdType);
            otherThemesTextName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
            otherThemesTextName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            otherThemesTextName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            cardView.addView(snakePreView,frameParams);
            otherLayout.addView(cardView, cardFrameParams);
            otherLayout.addView(otherThemesTextName);
            gridLayout.addView(otherLayout,params);
        }
        return gridView;
    }
}

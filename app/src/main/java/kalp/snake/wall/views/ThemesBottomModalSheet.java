
package kalp.snake.wall.views;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
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

import kalp.snake.wall.MainActivity;
import kalp.snake.wall.R;
import kalp.snake.wall.data.ColorThemesData;
import kalp.snake.wall.models.ColorTheme;

public class ThemesBottomModalSheet extends BottomSheetDialogFragment {
    private static final int THEME_CUSTOM_IMAGE_ID = ColorThemesData.getThemes().length;

    @Override
    public int getTheme() { return R.style.BottomSheetDialogTheme; }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View gridView = inflater.inflate(R.layout.themes_sheet, container, false);
        GridLayout gridLayout = gridView.findViewById(R.id.mainGridLayout);
        gridLayout.setColumnCount(2);

        ColorTheme[] colorThemes = ColorThemesData.getThemes();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SnakeGamePrefs", 0);

        // Typeface used in this sheet
        Typeface mlcdType = Typeface.createFromAsset(requireContext().getAssets(), "font/mlcd.ttf");
        float factor = getResources().getDisplayMetrics().density;

        // Title
        TextView themesText = new TextView(requireContext());
        themesText.setText(getResources().getString(R.string.themes));
        themesText.setTypeface(mlcdType);
        themesText.setTextColor(ContextCompat.getColor(requireContext(), R.color.buttons_and_frame_color));
        themesText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f);
        GridLayout.LayoutParams textParams = new GridLayout.LayoutParams();
        textParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        textParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        textParams.bottomMargin = (int) (20 * factor);
        textParams.rowSpec = GridLayout.spec(0);
        textParams.columnSpec = GridLayout.spec(0, 2);
        gridLayout.addView(themesText, textParams);

        // Color theme tiles
        for (int i = 0; i < colorThemes.length; i++){
            int row = i / 2;
            int column = i % 2;

            LinearLayout tileLayout = new LinearLayout(requireContext());
            tileLayout.setOrientation(LinearLayout.VERTICAL);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.width = 0;
            params.bottomMargin = (int) (20 * factor);
            params.columnSpec = GridLayout.spec(column, GridLayout.FILL, 1f);
            params.rowSpec = GridLayout.spec(row + 1);

            final int finalI = i;
            tileLayout.setOnClickListener(v -> {
                // Save color theme, clear custom image, set theme id
                colorThemes[finalI].colorPrefConfig.saveToPrefs(sharedPreferences);
                sharedPreferences.edit()
                        .remove(MainActivity.CUSTOM_BG_URI_KEY)
                        .putInt(MainActivity.CURRENT_THEME_KEY, finalI)
                        .apply();
                MainActivity.reDrawView();
                dismiss();
            });

            DisplayMetrics dm = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            float aspectRatio = (float) dm.heightPixels / dm.widthPixels;
            FrameLayout.LayoutParams cardFrameParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) (getResources().getDisplayMetrics().widthPixels * (aspectRatio / 2.2f))
            );

            MaterialCardView cardView = new MaterialCardView(requireContext());
            // Let theme style the card to match others

            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            // Preview view for tile
            SnakePreView snakePreView = new SnakePreView(requireContext());
            // IMPORTANT: prevent global prefs & custom image from affecting tiles
            snakePreView.setBindToPrefs(false);
            snakePreView.setEnableCustomImageFromPrefs(false);

            // Apply explicit theme colors
            snakePreView.setSnakeBackgroundColor(colorThemes[i].colorPrefConfig.getSnakeBackgroundColor());
            snakePreView.setFoodColor(colorThemes[i].colorPrefConfig.getFoodColor());
            snakePreView.setSnakeColor(colorThemes[i].colorPrefConfig.getSnakeColor());
            snakePreView.setGridColor(colorThemes[i].colorPrefConfig.getGridColor());
            snakePreView.setButtonsAndFrameColor(colorThemes[i].colorPrefConfig.getButtonsAndFrameColor());

            // Theme name under card
            TextView themeName = new TextView(requireContext());
            themeName.setText(colorThemes[i].title);
            themeName.setTypeface(mlcdType);
            themeName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
            themeName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            themeName.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            cardView.addView(snakePreView, frameParams);
            tileLayout.addView(cardView, cardFrameParams);
            tileLayout.addView(themeName);
            gridLayout.addView(tileLayout, params);
        }

        // --- Custom Image tile: center the label in the middle ---
        int newIndex = colorThemes.length;
        int row = newIndex / 2;
        int column = newIndex % 2;

        LinearLayout customLayout = new LinearLayout(requireContext());
        customLayout.setOrientation(LinearLayout.VERTICAL);

        GridLayout.LayoutParams customParams = new GridLayout.LayoutParams();
        customParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        customParams.width = 0;
        customParams.bottomMargin = (int) (20 * factor);
        customParams.columnSpec = GridLayout.spec(column, GridLayout.FILL, 1f);
        customParams.rowSpec = GridLayout.spec(row + 1);

        customLayout.setOnClickListener(v -> {
            sharedPreferences.edit().putInt(MainActivity.CURRENT_THEME_KEY, THEME_CUSTOM_IMAGE_ID).apply();
            ((MainActivity) requireActivity()).openImagePicker();
            dismiss();
        });

        DisplayMetrics dm2 = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(dm2);
        float aspectRatio2 = (float) dm2.heightPixels / dm2.widthPixels;
        FrameLayout.LayoutParams cardFrameParams2 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (getResources().getDisplayMetrics().widthPixels * (aspectRatio2 / 2.2f))
        );

        MaterialCardView customCardView = new MaterialCardView(requireContext());
        // Center container so label is exactly in the middle
        FrameLayout centerContainer = new FrameLayout(requireContext());
        FrameLayout.LayoutParams centerParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        centerContainer.setLayoutParams(centerParams);

        TextView selectImageText = new TextView(requireContext());
        selectImageText.setText("SELECT IMAGE");
        selectImageText.setTypeface(mlcdType);
        selectImageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
        selectImageText.setTextColor(ContextCompat.getColor(requireContext(), R.color.buttons_and_frame_color));
        selectImageText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        selectImageText.setGravity(android.view.Gravity.CENTER);

        FrameLayout.LayoutParams textInCardParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.CENTER
        );
        centerContainer.addView(selectImageText, textInCardParams);
        customCardView.addView(centerContainer);

        TextView customThemesTextName = new TextView(requireContext());
        customThemesTextName.setText("Custom Image");
        customThemesTextName.setTypeface(mlcdType);
        customThemesTextName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        customThemesTextName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        customThemesTextName.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        customLayout.addView(customCardView, cardFrameParams2);
        customLayout.addView(customThemesTextName);
        gridLayout.addView(customLayout, customParams);

        return gridView;
    }
}

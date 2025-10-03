
package kalp.snake.wall.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

import kalp.snake.wall.MainActivity;
import kalp.snake.wall.R;
import kalp.snake.wall.models.WallPrefConfig;

public class PrefsBottomModalSheet extends BottomSheetDialogFragment {
    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View prefListView = inflater.inflate(R.layout.prefs_sheet, container, false);

        // Existing cards + switches
        MaterialCardView gridPrefCard = prefListView.findViewById(R.id.gridToggleCard);
        MaterialCardView vibrationPrefCard = prefListView.findViewById(R.id.soundToggleCard);
        MaterialSwitch gridToggle = prefListView.findViewById(R.id.gridSwitch);
        MaterialSwitch vibrationToggle = prefListView.findViewById(R.id.vibrationSwitch);

        // NEW: Dark UI card + switch from XML
        MaterialCardView darkUiPrefCard = prefListView.findViewById(R.id.darkUiToggleCard);
        MaterialSwitch darkUiSwitch = prefListView.findViewById(R.id.darkUiSwitch);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SnakeGamePrefs", 0);

        // init states
        gridToggle.setChecked(WallPrefConfig.getGridEnabledFromPref(sharedPreferences));
        vibrationToggle.setChecked(WallPrefConfig.getVibrationEnabledFromPref(sharedPreferences));
        boolean dark = sharedPreferences.getBoolean(MainActivity.CUSTOM_IMAGE_UI_DARK_KEY, false);
        darkUiSwitch.setChecked(dark);

        // tap cards = toggle switches
        gridPrefCard.setOnClickListener(v -> gridToggle.setChecked(!gridToggle.isChecked()));
        vibrationPrefCard.setOnClickListener(v -> vibrationToggle.setChecked(!vibrationToggle.isChecked()));
        if (darkUiPrefCard != null) {
            darkUiPrefCard.setOnClickListener(v -> darkUiSwitch.setChecked(!darkUiSwitch.isChecked()));
        }

        // persist on change
        gridToggle.setOnCheckedChangeListener((buttonView, isChecked) -> WallPrefConfig.saveGridEnabledToPref(isChecked, sharedPreferences));
        vibrationToggle.setOnCheckedChangeListener((buttonView, isChecked) -> WallPrefConfig.saveVibrationEnabledToPref(isChecked, sharedPreferences));
        darkUiSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean(MainActivity.CUSTOM_IMAGE_UI_DARK_KEY, isChecked).apply()
        );

        return prefListView;
    }
}

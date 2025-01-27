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

        MaterialCardView gridPrefCard = prefListView.findViewById(R.id.gridToggleCard);
        MaterialCardView vibrationPrefCard = prefListView.findViewById(R.id.soundToggleCard);
        MaterialSwitch gridToggle = prefListView.findViewById(R.id.gridSwitch);
        MaterialSwitch vibrationToggle = prefListView.findViewById(R.id.vibrationSwitch);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SnakeGamePrefs", 0);

        gridToggle.setChecked(WallPrefConfig.getGridEnabledFromPref(sharedPreferences));
        vibrationToggle.setChecked(WallPrefConfig.getVibrationEnabledFromPref(sharedPreferences));

        gridPrefCard.setOnClickListener(v -> gridToggle.setChecked(!gridToggle.isChecked()));
        vibrationPrefCard.setOnClickListener(v -> vibrationToggle.setChecked(!vibrationToggle.isChecked()));

        gridToggle.setOnCheckedChangeListener((buttonView, isChecked) -> WallPrefConfig.saveGridEnabledToPref(isChecked, sharedPreferences));
        vibrationToggle.setOnCheckedChangeListener((buttonView, isChecked) -> WallPrefConfig.saveVibrationEnabledToPref(isChecked, sharedPreferences));

        return prefListView;
    }
}

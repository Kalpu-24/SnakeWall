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
        View prefListView = inflater.inflate(R.layout.prefs_sheet,container,false);
        MaterialCardView prefCard = prefListView.findViewById(R.id.gridToggleCard);
        MaterialSwitch gridToggle = prefListView.findViewById(R.id.gridSwitch);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SnakeGamePrefs",0);
        gridToggle.setChecked(WallPrefConfig.getGridEnabledFromPref(sharedPreferences));
        prefCard.setOnClickListener(v -> {
            gridToggle.setChecked(!gridToggle.isChecked());
        });

        gridToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            WallPrefConfig.saveGridEnabledToPref(isChecked, sharedPreferences);
        });
        return prefListView;
    }
}

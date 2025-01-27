package kalp.snake.wall.models;

import android.content.SharedPreferences;

public class WallPrefConfig {

    private boolean isGridEnabled;

    public static String gridEnabledKey = "gridEnabled";
    public static String vibrationEnabledKey = "vibrationEnabled";

    public static void saveGridEnabledToPref(boolean isGridEnabled, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(gridEnabledKey, isGridEnabled);
        editor.apply();
    }

    public static boolean getGridEnabledFromPref(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(gridEnabledKey, false);
    }

    public static boolean getVibrationEnabledFromPref(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(vibrationEnabledKey, true);
    }

    public static void saveVibrationEnabledToPref(boolean isEnabled, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(vibrationEnabledKey, isEnabled);
        editor.apply();
    }
}

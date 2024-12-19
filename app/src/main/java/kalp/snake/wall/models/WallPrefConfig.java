package kalp.snake.wall.models;

import android.content.SharedPreferences;

public class WallPrefConfig {
    private boolean isGridEnabled;

    public static String gridEnabledKey = "gridEnabled";

    public static void saveGridEnabledToPref(boolean isGridEnabled, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(gridEnabledKey, isGridEnabled);
        editor.apply();
    }

    public static boolean getGridEnabledFromPref(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(gridEnabledKey, false);
    }
}

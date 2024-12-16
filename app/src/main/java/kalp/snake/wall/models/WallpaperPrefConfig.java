package kalp.snake.wall.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import kalp.snake.wall.R;

public class WallpaperPrefConfig {
    private int foodColor;
    private int snakeColor;
    private int snakeBackgroundColor;
    private int buttonsAndFrameColor;
    private int gridColor;

    public WallpaperPrefConfig(int foodColor, int snakeColor, int backgroundColor, int buttonsAndFrameColor, int gridColor) {
        this.foodColor = foodColor;
        this.snakeColor = snakeColor;
        this.snakeBackgroundColor = backgroundColor;
        this.buttonsAndFrameColor = buttonsAndFrameColor;
        this.gridColor = gridColor;
    }

    public int getFoodColor() {
        return foodColor;
    }

    public int getSnakeColor() {
        return snakeColor;
    }

    public int getSnakeBackgroundColor() {
        return snakeBackgroundColor;
    }

    public int getButtonsAndFrameColor() {
        return buttonsAndFrameColor;
    }

    public int getGridColor() {
        return gridColor;
    }

    public void setFoodColor(int foodColor) {
        this.foodColor = foodColor;
    }

    public void setSnakeColor(int snakeColor) {
        this.snakeColor = snakeColor;
    }

    public void setSnakeBackgroundColor(int snakeBackgroundColor) {
        this.snakeBackgroundColor = snakeBackgroundColor;
    }

    public void setButtonsAndFrameColor(int buttonsAndFrameColor) {
        this.buttonsAndFrameColor = buttonsAndFrameColor;
    }

    public void setGridColor(int gridColor) {
        this.gridColor = gridColor;
    }

//    factory method to fetch from prefs and also make it default constructor
    public static WallpaperPrefConfig getFromPrefs(SharedPreferences sharedPreferences, Context context) {
        int foodColor = sharedPreferences.getInt("foodColor", ContextCompat.getColor(context, R.color.food_color));
        int snakeColor = sharedPreferences.getInt("snakeColor", ContextCompat.getColor(context, R.color.snake_color));
        int backgroundColor = sharedPreferences.getInt("snakeBackgroundColor", ContextCompat.getColor(context, R.color.snake_background_color));
        int buttonsAndFrameColor = sharedPreferences.getInt("buttonsAndFrameColor", ContextCompat.getColor(context, R.color.buttons_and_frame_color));
        int gridColor = sharedPreferences.getInt("gridColor", ContextCompat.getColor(context, R.color.grid_color));
        Log.d("ColorConfig", "getFromPrefs: " + foodColor + " " + snakeColor + " " + backgroundColor + " " + buttonsAndFrameColor + " " + gridColor);
        return new WallpaperPrefConfig(foodColor, snakeColor, backgroundColor, buttonsAndFrameColor, gridColor);
    }

    public void saveToPrefs(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("foodColor", foodColor);
        editor.putInt("snakeColor", snakeColor);
        editor.putInt("snakeBackgroundColor", snakeBackgroundColor);
        editor.putInt("buttonsAndFrameColor", buttonsAndFrameColor);
        editor.putInt("gridColor", gridColor);
        editor.apply();
    }

    public static void clearPrefs(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @NonNull
    @Override
    public String toString() {
        return "ColorConfig{" +
                "foodColor=" + foodColor +
                ", snakeColor=" + snakeColor +
                ", backgroundColor=" + snakeBackgroundColor +
                ", buttonsAndFrameColor=" + buttonsAndFrameColor +
                ", gridColor=" + gridColor +
                '}';
    }
}

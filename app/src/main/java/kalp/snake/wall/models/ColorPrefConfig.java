package kalp.snake.wall.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import kalp.snake.wall.R;

public class ColorPrefConfig {
    private int foodColor;
    private int snakeColor;
    private int snakeBackgroundColor;
    private int buttonsAndFrameColor;
    private int gridColor;

    public static String foodColorKey = "foodColor";
    public static String snakeColorKey = "snakeColor";
    public static String snakeBackgroundColorKey = "snakeBackgroundColor";
    public static String buttonsAndFrameColorKey = "buttonsAndFrameColor";
    public static String gridColorKey = "gridColor";

    public ColorPrefConfig(int foodColor, int snakeColor, int backgroundColor, int buttonsAndFrameColor, int gridColor) {
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
    public static ColorPrefConfig getFromPrefs(SharedPreferences sharedPreferences, Context context) {
        int foodColor = sharedPreferences.getInt(foodColorKey, ContextCompat.getColor(context, R.color.food_color));
        int snakeColor = sharedPreferences.getInt(snakeColorKey, ContextCompat.getColor(context, R.color.snake_color));
        int backgroundColor = sharedPreferences.getInt(snakeBackgroundColorKey, ContextCompat.getColor(context, R.color.snake_background_color));
        int buttonsAndFrameColor = sharedPreferences.getInt(buttonsAndFrameColorKey, ContextCompat.getColor(context, R.color.buttons_and_frame_color));
        int gridColor = sharedPreferences.getInt(gridColorKey, ContextCompat.getColor(context, R.color.grid_color));
        return new ColorPrefConfig(foodColor, snakeColor, backgroundColor, buttonsAndFrameColor, gridColor);
    }

    public void saveToPrefs(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(foodColorKey, foodColor);
        editor.putInt(snakeColorKey, snakeColor);
        editor.putInt(snakeBackgroundColorKey, snakeBackgroundColor);
        editor.putInt(buttonsAndFrameColorKey, buttonsAndFrameColor);
        editor.putInt(gridColorKey, gridColor);
        Log.d("ColorPrefConfig", "saveToPrefs: " + this.toString());
        editor.apply();
    }

    public static void clearPrefs(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(foodColorKey, R.color.food_color);
        editor.putInt(snakeColorKey, R.color.snake_color);
        editor.putInt(snakeBackgroundColorKey, R.color.snake_background_color);
        editor.putInt(buttonsAndFrameColorKey, R.color.buttons_and_frame_color);
        editor.putInt(gridColorKey, R.color.grid_color);
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

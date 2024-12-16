package kalp.snake.wall.data;

import kalp.snake.wall.R;
import kalp.snake.wall.models.ColorPrefConfig;
import kalp.snake.wall.models.ColorTheme;

public class ColorThemesData {



    static ColorPrefConfig systemThemeConfig = new ColorPrefConfig(
            R.color.food_color,
            R.color.snake_color,
            R.color.snake_background_color,
            R.color.buttons_and_frame_color,
            R.color.grid_color
    );

    static ColorPrefConfig lightThemeConfig = new ColorPrefConfig(
            0xFFFF0000,
            0xFF1B1B1F,
            0xFFF3EFF4,
            0xFF1B1B1F,
            0x141B1B1F
    );

    static ColorPrefConfig darkThemeConfig = new ColorPrefConfig(
            0xFFFF0000,
            0xFFF3EFF4,
            0xFF1B1B1F,
            0xFFF3EFF4,
            0x14F3EFF4
    );

    static ColorPrefConfig retroThemeConfig = new ColorPrefConfig(
            0xff191a18,
            0xff191a18,
            0xff8eb438,
            0xff191a18,
            0x14191a18
    );

    static ColorTheme systemTheme = new ColorTheme("System", systemThemeConfig);
    static ColorTheme lightTheme = new ColorTheme("Light", lightThemeConfig);
    static ColorTheme darkTheme = new ColorTheme("Dark", darkThemeConfig);
    static ColorTheme retroTheme = new ColorTheme("Retro", retroThemeConfig);

    public static ColorTheme[] getThemes(){
        return new ColorTheme[]{systemTheme, lightTheme, darkTheme, retroTheme};
    }
}

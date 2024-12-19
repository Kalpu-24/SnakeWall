package kalp.snake.wall.data;

import kalp.snake.wall.R;
import kalp.snake.wall.models.ColorPrefConfig;
import kalp.snake.wall.models.ColorTheme;

public class ColorThemesData {

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

    static ColorTheme lightTheme = new ColorTheme("Light", lightThemeConfig);
    static ColorTheme darkTheme = new ColorTheme("Dark", darkThemeConfig);
    static ColorTheme retroTheme = new ColorTheme("Retro", retroThemeConfig);

    public static ColorTheme[] getThemes(){
        return new ColorTheme[]{lightTheme, darkTheme, retroTheme};
    }
}

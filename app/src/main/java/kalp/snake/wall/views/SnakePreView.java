
package kalp.snake.wall.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;                 // NEW
import android.graphics.BitmapFactory;          // NEW
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.RotateDrawable;
import android.net.Uri;                         // NEW
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;                        // NEW
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.io.InputStream;                     // NEW
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Objects;

import kalp.snake.wall.MainActivity;            // NEW (keys)
import kalp.snake.wall.R;
import kalp.snake.wall.data.ColorThemesData;    // NEW (custom id)
import kalp.snake.wall.enums.EDirection;
import kalp.snake.wall.enums.EGameState;
import kalp.snake.wall.models.ColorPrefConfig;
import kalp.snake.wall.models.WallPrefConfig;
import kotlin.Pair;

public class SnakePreView extends FrameLayout implements Serializable {
    private final Context context;
    private final Paint MlcdText;
    private final Paint gameBorder;
    private int bestScore;
    private int currentScore;
    private final String bestScoreKey;
    private final String prefsName;
    private final SharedPreferences sharedPreferences;
    private int cellSize;
    private int gridMargin;
    private final int gridSize;
    private final EGameState gameState;
    private final Paint snakePaint;
    private final Paint foodPaint;
    private final LinkedList<Pair<Integer, Integer>> snake;
    private final int snakeBodySize;
    private long snakeSpeed;
    private final Paint gridPaint;
    private boolean gridView;
    private EDirection direction;
    private Pair<Integer, Integer> foodCoordinates;
    private int insetTop;
    private int gameWidth;
    private int gameStartX;
    private int gameStartY;
    private int gameEndX;
    private int gameEndY;
    private int controllerStartX;
    private int controllerStartY;
    private int controllerEndX;
    private int controllerEndY;
    private int startButtonStartX;
    private int startButtonStartY;
    private int startButtonEndX;
    private int startButtonEndY;
    private int verticalButtonCenterX;
    private int horizontalButtonCenterY;
    private int topButtonCenterY;
    private int bottomButtonCenterY;
    private int leftButtonCenterX;
    private int rightButtonCenterX;
    private int arrowUpButtonStartX;
    private int arrowUpButtonStartY;
    private int arrowUpButtonEndX;
    private int arrowUpButtonEndY;
    private int arrowDownButtonStartX;
    private int arrowDownButtonStartY;
    private int arrowDownButtonEndX;
    private int arrowDownButtonEndY;
    private int arrowLeftButtonStartX;
    private int arrowLeftButtonStartY;
    private int arrowLeftButtonEndX;
    private int arrowLeftButtonEndY;
    private int arrowRightButtonStartX;
    private int arrowRightButtonStartY;
    private int arrowRightButtonEndX;
    private int arrowRightButtonEndY;
    private Color snakeBackgroundColor;
    private Color buttonsAndFrameColor;
    private Color gridColor;
    private Color foodColor;
    private Color snakeColor;
    private int screenWidth, screenHeight;
    float cornerRadius;
    float crownWidth;
    float crownHeight;
    private final ColorPrefConfig colorPrefConfig;

    // ===== NEW: In-app preview custom image support =====
    private Bitmap mPreviewBitmap = null;
    private final Rect srcImageRect = new Rect();
    private final Rect dstImageRect = new Rect();
    private final int THEME_CUSTOM_IMAGE_ID = ColorThemesData.getThemes().length; // next after color themes

    // NEW: control whether this view binds to prefs & uses custom image
    private boolean bindToPrefs = true;                 // default true for main preview
    private boolean enableCustomImageFromPrefs = true;  // default true for main preview

    public SnakePreView(Context context) {
        this(context, null, 0, 6, null);
    }
    public SnakePreView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0, 4, null);
    }
    public SnakePreView(Context context, AttributeSet attributeSet, int defStyle, int flags, Object marker) {
        this(context,(flags & 2) != 0 ? null : attributeSet,(flags & 4) != 0 ? 0 : defStyle);
    }
    public SnakePreView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;
        this.prefsName = "SnakeGamePrefs";
        SharedPreferences sharedPreferences = context.getSharedPreferences(this.prefsName, 0);
        this.sharedPreferences = sharedPreferences;
        this.colorPrefConfig = ColorPrefConfig.getFromPrefs(this.sharedPreferences, this.context);

        TypedArray attr = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.SnakePreView,
                0,0
        );
        try {
            snakeBackgroundColor = Color.valueOf(attr.getColor(R.styleable.SnakePreView_snakeGameBackgroundColor, colorPrefConfig.getSnakeBackgroundColor()));
            buttonsAndFrameColor = Color.valueOf(attr.getColor(R.styleable.SnakePreView_buttonAndFrameColor, colorPrefConfig.getButtonsAndFrameColor()));
            gridColor = Color.valueOf(attr.getColor(R.styleable.SnakePreView_snakeGameGridColor, colorPrefConfig.getGridColor()));
            foodColor = Color.valueOf(attr.getColor(R.styleable.SnakePreView_foodColor, colorPrefConfig.getFoodColor()));
            snakeColor = Color.valueOf(attr.getColor(R.styleable.SnakePreView_snakeColor, colorPrefConfig.getSnakeColor()));
        } finally {
            attr.recycle();
        }

        this.gridSize = 19;
        this.gridMargin = 16;
        this.gridView = sharedPreferences.getBoolean(WallPrefConfig.gridEnabledKey, false);
        this.gameState = EGameState.START;
        this.snakeBodySize = 5;
        this.snakeSpeed = 200L;
        this.snake = new LinkedList<>();
        this.direction = EDirection.RIGHT;
        this.bestScoreKey = "SnakeBestScore";

        Paint gridPaintObj = new Paint();
        gridPaintObj.setColor(gridColor.toArgb());
        gridPaintObj.setStrokeWidth(2.0f);
        this.gridPaint = gridPaintObj;
        Paint foodPaintObj = new Paint();
        foodPaintObj.setColor(foodColor.toArgb());
        this.foodPaint = foodPaintObj;
        Paint paint = new Paint();
        paint.setColor(snakeColor.toArgb());
        this.snakePaint = paint;
        Paint gameBorderPaint = new Paint();
        gameBorderPaint.setColor(buttonsAndFrameColor.toArgb());
        gameBorderPaint.setStyle(Paint.Style.STROKE);
        gameBorderPaint.setStrokeWidth(10.0f);
        gameBorderPaint.setPathEffect(new DashPathEffect(new float[]{2, 30}, 10));
        gameBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        gameBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        this.gameBorder = gameBorderPaint;
        Paint mlcdPaint = new Paint();
        mlcdPaint.setColor(buttonsAndFrameColor.toArgb());
        mlcdPaint.setTextSize(30.0f);
        mlcdPaint.setTextAlign(Paint.Align.CENTER);
        Typeface customFont = ResourcesCompat.getFont(context, R.font.mlcd);
        mlcdPaint.setTypeface(customFont); // Set the custom font
        mlcdPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.MlcdText = mlcdPaint;

        this.sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences2, str) -> {
            if (str == null) return;
            onSharedPreferenceChanged(sharedPreferences2, str);
        });
        this.bestScore = sharedPreferences.getInt(this.bestScoreKey, 0);
        init(context, attributeSet);

        int i = this.gridSize / 2;
        int i2 = 5;
        int i3 = (i2 / 2) + i;
        for (int i4 = 0; i4 < i2; i4++) {
            this.snake.add(new Pair<>(i3 - i4, this.gridSize / 2));
        }
        this.direction = EDirection.RIGHT;
        this.bestScore = 0;
        addFood();
        invalidate();
    }

    private void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!bindToPrefs) return; // NEW: ignore global changes for tile previews

        if (key.equals(this.bestScoreKey)) {
            this.bestScore = sharedPreferences != null ? sharedPreferences.getInt(this.bestScoreKey, 0) : 0;
        }
        if (key.equals(ColorPrefConfig.foodColorKey)) {
            assert sharedPreferences != null;
            this.colorPrefConfig.setFoodColor(sharedPreferences.getInt(ColorPrefConfig.foodColorKey, 0));
            foodColor = Color.valueOf(sharedPreferences.getInt(ColorPrefConfig.foodColorKey, colorPrefConfig.getFoodColor()));
            foodPaint.setColor(foodColor.toArgb());
        }
        if (key.equals(ColorPrefConfig.snakeColorKey)) {
            assert sharedPreferences != null;
            this.colorPrefConfig.setSnakeColor(sharedPreferences.getInt(ColorPrefConfig.snakeColorKey, 0));
            snakeColor = Color.valueOf(sharedPreferences.getInt(ColorPrefConfig.snakeColorKey, colorPrefConfig.getSnakeColor()));
            snakePaint.setColor(snakeColor.toArgb());
        }
        if (key.equals(ColorPrefConfig.snakeBackgroundColorKey)) {
            assert sharedPreferences != null;
            this.colorPrefConfig.setSnakeBackgroundColor(sharedPreferences.getInt(ColorPrefConfig.snakeBackgroundColorKey, 0));
            snakeBackgroundColor = Color.valueOf(sharedPreferences.getInt(ColorPrefConfig.snakeBackgroundColorKey, colorPrefConfig.getSnakeBackgroundColor()));
        }
        if (key.equals(ColorPrefConfig.buttonsAndFrameColorKey)) {
            assert sharedPreferences != null;
            this.colorPrefConfig.setButtonsAndFrameColor(sharedPreferences.getInt(ColorPrefConfig.buttonsAndFrameColorKey, 0));
            buttonsAndFrameColor = Color.valueOf(sharedPreferences.getInt(ColorPrefConfig.buttonsAndFrameColorKey, colorPrefConfig.getButtonsAndFrameColor()));
            gameBorder.setColor(buttonsAndFrameColor.toArgb());
            MlcdText.setColor(buttonsAndFrameColor.toArgb());
        }
        if (key.equals(ColorPrefConfig.gridColorKey)) {
            assert sharedPreferences != null;
            this.colorPrefConfig.setGridColor(sharedPreferences.getInt(ColorPrefConfig.gridColorKey, 0));
            gridColor = Color.valueOf(colorPrefConfig.getGridColor());
            gridPaint.setColor(gridColor.toArgb());
        }
        // React to custom image related prefs only if enabled
        if (enableCustomImageFromPrefs && (key.equals(MainActivity.CURRENT_THEME_KEY) || key.equals(MainActivity.CUSTOM_BG_URI_KEY))) {
            loadCustomBackgroundIfAny();
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = getWidth();
        screenHeight = getHeight();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics metrics = Objects.requireNonNull(ContextCompat.getSystemService(context, WindowManager.class)).getCurrentWindowMetrics();
            Insets insets = metrics.getWindowInsets().getInsets(WindowInsets.Type.systemGestures());
            insetTop = insets.top;
        } else {
            WindowManager windowManager = ContextCompat.getSystemService(context, WindowManager.class);
            if (windowManager != null) {
                Display display = windowManager.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                Rect rect = new Rect();
                display.getRectSize(rect);
                insetTop = rect.top;
            }
        }
        if (insetTop<=10){
            insetTop = 70;
        }
        float baseWidth = 1440.0f; // Width in pixels for reference device
        float baseHeight = 3216.0f; // Height in pixels for reference device
        // Scale factors based on the current screen dimensions
        float widthScale = screenWidth / baseWidth;
        float heightScale = screenHeight / baseHeight;
        float averageScale = (widthScale + heightScale) / 1.75f;
        float smallerDimension = Math.min(screenWidth, screenHeight);
        cornerRadius = smallerDimension * 0.08f;
        crownWidth = dpToPx(20); // e.g., 50dp
        crownHeight = crownWidth / 1.3f;
        MlcdText.setTextSize(60.0f * averageScale);
        gameBorder.setStrokeWidth(15.0f * averageScale);
        gameBorder.setPathEffect(new DashPathEffect(new float[]{2 * averageScale, 30 * averageScale}, 10 * averageScale));
        gameWidth = (int) (screenWidth * 0.8);
        gameStartX = (screenWidth - gameWidth) / 2;
        gameStartY = (int) (insetTop * 0.5f);
        gameEndX = gameStartX + gameWidth;
        gameEndY = gameWidth + gameStartY;
        controllerStartX = screenWidth / 2;
        controllerStartY = (int) (gameEndY + (screenHeight * 0.1));
        controllerEndX = gameEndX;
        controllerEndY = controllerStartX + controllerStartY;
        // re-evaluate background when size changes
        if (bindToPrefs && enableCustomImageFromPrefs) loadCustomBackgroundIfAny();
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (bindToPrefs && enableCustomImageFromPrefs) loadCustomBackgroundIfAny();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        recyclePreviewBitmap();
    }

    /** @noinspection SameParameterValue*/
    float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        drawBackgroundAndFrames(canvas);
        drawControls(canvas);
        canvas.save();
        canvas.translate(gameStartX, gameStartY);
        this.cellSize = (gameWidth - (this.gridMargin * 2)) / this.gridSize;
        drawPreviewScreen(canvas);
        canvas.restore();
    }

    public void updateColors(){
        if (bindToPrefs) {
            checkChange();
            if (enableCustomImageFromPrefs) loadCustomBackgroundIfAny();
            applyOverlayIfCustom();
        }
        invalidate();
    }

    private void checkChange() {
        if (colorPrefConfig.getFoodColor() != sharedPreferences.getInt(ColorPrefConfig.foodColorKey, 0) ){
            onSharedPreferenceChanged(sharedPreferences, ColorPrefConfig.foodColorKey);
        }
        if (colorPrefConfig.getSnakeColor() != sharedPreferences.getInt(ColorPrefConfig.snakeColorKey, 0) ){
            onSharedPreferenceChanged(sharedPreferences, ColorPrefConfig.snakeColorKey);
        }
        if (colorPrefConfig.getSnakeBackgroundColor() != sharedPreferences.getInt(ColorPrefConfig.snakeBackgroundColorKey, 0) ){
            onSharedPreferenceChanged(sharedPreferences, ColorPrefConfig.snakeBackgroundColorKey);
        }
        if (colorPrefConfig.getButtonsAndFrameColor() != sharedPreferences.getInt(ColorPrefConfig.buttonsAndFrameColorKey, 0) ){
            onSharedPreferenceChanged(sharedPreferences, ColorPrefConfig.buttonsAndFrameColorKey);
        }
        if (colorPrefConfig.getGridColor() != sharedPreferences.getInt(ColorPrefConfig.gridColorKey, 0) ){
            onSharedPreferenceChanged(sharedPreferences, ColorPrefConfig.gridColorKey);
        }
    }

    private void drawBackgroundAndFrames(Canvas canvas) {
        // draw custom image if enabled & custom theme is active, else draw color
        int currentTheme = sharedPreferences.getInt(MainActivity.CURRENT_THEME_KEY, -1);
        if (bindToPrefs && enableCustomImageFromPrefs && currentTheme == THEME_CUSTOM_IMAGE_ID && mPreviewBitmap != null && !mPreviewBitmap.isRecycled()) {
            int vw = getWidth();
            int vh = getHeight();
            int bw = mPreviewBitmap.getWidth();
            int bh = mPreviewBitmap.getHeight();
            if (vw > 0 && vh > 0 && bw > 0 && bh > 0) {
                float viewRatio = (float) vw / (float) vh;
                float bmpRatio  = (float) bw / (float) bh;
                if (bmpRatio > viewRatio) {
                    int newWidth = (int) (bh * viewRatio);
                    int left = (bw - newWidth) / 2;
                    srcImageRect.set(left, 0, left + newWidth, bh);
                } else {
                    int newHeight = (int) (bw / viewRatio);
                    int top = (bh - newHeight) / 2;
                    srcImageRect.set(0, top, bw, top + newHeight);
                }
                dstImageRect.set(0, 0, vw, vh);
                canvas.drawBitmap(mPreviewBitmap, srcImageRect, dstImageRect, null);
            } else {
                canvas.drawColor(snakeBackgroundColor.toArgb());
            }
        } else {
            canvas.drawColor(snakeBackgroundColor.toArgb());
        }
        RectF rectF = new RectF(gameStartX, gameStartY, gameEndX, gameEndY);
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, gameBorder);
    }

    private void drawControls(Canvas canvas) {
        VectorDrawableCompat buttonDrawable = VectorDrawableCompat.create(context.getResources(), R.drawable.dotted_circle, null);
        @SuppressLint("UseCompatLoadingForDrawables") RotateDrawable arrowDrawable = (RotateDrawable) getResources().getDrawable(R.drawable.rotate_arrow, null);
        float spacing = (gameWidth/2.0f)/5.0f;
        int buttonRadius = (int) spacing;
        float centerX = (controllerStartX + controllerEndX) / 2.0f;
        float centerY = (controllerStartY + controllerEndY) / 2.0f;
        verticalButtonCenterX = (int) centerX;
        horizontalButtonCenterY = (int) centerY;
        topButtonCenterY = (int) (centerY - buttonRadius - spacing);
        bottomButtonCenterY = (int) (centerY + buttonRadius + spacing);
        leftButtonCenterX = (int) (centerX - buttonRadius - spacing);
        rightButtonCenterX = (int) (centerX + buttonRadius + spacing);
        arrowUpButtonStartX = verticalButtonCenterX - buttonRadius;
        arrowUpButtonStartY = topButtonCenterY - buttonRadius;
        arrowUpButtonEndX = verticalButtonCenterX + buttonRadius;
        arrowUpButtonEndY = topButtonCenterY + buttonRadius;
        arrowDownButtonStartX = verticalButtonCenterX - buttonRadius;
        arrowDownButtonStartY = bottomButtonCenterY - buttonRadius;
        arrowDownButtonEndX = verticalButtonCenterX + buttonRadius;
        arrowDownButtonEndY = bottomButtonCenterY + buttonRadius;
        arrowLeftButtonStartX = leftButtonCenterX - buttonRadius;
        arrowLeftButtonStartY = horizontalButtonCenterY - buttonRadius;
        arrowLeftButtonEndX = leftButtonCenterX + buttonRadius;
        arrowLeftButtonEndY = horizontalButtonCenterY + buttonRadius;
        arrowRightButtonStartX = rightButtonCenterX - buttonRadius;
        arrowRightButtonStartY = horizontalButtonCenterY - buttonRadius;
        arrowRightButtonEndX = rightButtonCenterX + buttonRadius;
        arrowRightButtonEndY = horizontalButtonCenterY + buttonRadius;
        float arrowWidth = buttonRadius*2;
        float buttonWidth = buttonRadius*2;
        if (arrowDrawable != null) {
            arrowDrawable.setBounds(0, 0, (int) arrowWidth, (int) arrowWidth);
            arrowDrawable.setTint(buttonsAndFrameColor.toArgb());
        }
        if (buttonDrawable != null) {
            buttonDrawable.setBounds(0, 0, (int) buttonWidth, (int) buttonWidth);
            buttonDrawable.setTint(buttonsAndFrameColor.toArgb());
        }
        startButtonStartX = gameStartX;
        startButtonStartY = topButtonCenterY;
        startButtonEndX = (int) (gameEndX / 2.2f);
        startButtonEndY = bottomButtonCenterY;
        String startButtonText;
        if (this.gameState == EGameState.PAUSED) {
            startButtonText = "Resume";
        } else if(this.gameState == EGameState.GAME_OVER) {
            startButtonText = "Restart";
        } else if (this.gameState == EGameState.PLAYING) {
            startButtonText = "Pause";
        } else {
            startButtonText = "Start";
        }
        // Draw Start and Pause buttons
        RectF startRect = new RectF(startButtonStartX, startButtonStartY, startButtonEndX, startButtonEndY);
        canvas.drawRoundRect(startRect, cornerRadius, cornerRadius, gameBorder);
        // button text
        canvas.save();
        canvas.translate(startButtonStartX, startButtonStartY);
        drawCenteredText(startButtonText, (startButtonEndX - startButtonStartX) / 2.0f, (startButtonEndY - startButtonStartY) / 2.0f, canvas, this.MlcdText);
        canvas.restore();
        // Draw the buttons
        // Top button (^) 
        assert buttonDrawable != null;
        assert arrowDrawable != null;
        drawArrow(canvas, arrowDrawable, buttonDrawable, arrowUpButtonStartX, arrowUpButtonStartY, 180);
        // Bottom button (v)
        drawArrow(canvas, arrowDrawable, buttonDrawable, arrowDownButtonStartX, arrowDownButtonStartY, 0);
        // Left button (<)
        drawArrow(canvas, arrowDrawable, buttonDrawable, arrowLeftButtonStartX, arrowLeftButtonStartY, 90);
        // Right button (>)
        drawArrow(canvas, arrowDrawable, buttonDrawable, arrowRightButtonStartX, arrowRightButtonStartY, 270);
    }

    private void drawArrow(Canvas canvas, RotateDrawable arrowDrawable, VectorDrawableCompat buttonDrawable, int startX, int startY, int angle) {
        int level = (int) (angle / 360.0f * 10000);
        // Set the rotation level
        arrowDrawable.setLevel(level);
        canvas.save();
        canvas.translate(startX, startY);
        // Draw on the canvas
        buttonDrawable.draw(canvas);
        arrowDrawable.draw(canvas);
        canvas.restore();
    }

    private void drawCenteredText(String text, float xPosition, float yPosition, Canvas canvas, Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        float verticalOffset = textHeight / 2 - fontMetrics.bottom;
        canvas.drawText(text, xPosition, yPosition + verticalOffset, paint);
    }

    private void drawPreviewScreen(Canvas canvas) {
        drawBestScore(canvas);
        drawSnake(canvas, snakePaint);
        String string = context.getString(R.string.snake_tap_to_play);
        //noinspection SuspiciousNameCombination
        drawTextWithLineBreak(string, gameWidth / 2.0f, gameWidth, canvas, this.MlcdText);
        int foodX = (this.foodCoordinates.getFirst() * cellSize) + this.gridMargin;
        int foodY = (this.foodCoordinates.getSecond() * cellSize) + this.gridMargin;
        canvas.drawCircle(
                (foodX + foodX + cellSize) / 2.0f,
                (foodY + foodY + cellSize) / 2.0f,
                cellSize / 2.0f,
                this.foodPaint
        );
    }

    private void drawTextWithLineBreak(String text, float xPosition, float yPosition, Canvas canvas, Paint paint) {
        // Define the maximum text width based on the available canvas width
        int maxTextWidth = getWidth() - (this.gridMargin * 3);
        float textWidth = paint.measureText(text);
        float textHeight = (paint.descent() - paint.ascent())*2;
        // Create a copy of the paint object to adjust text size without altering the original
        Paint adjustablePaint = new Paint(paint);
        // If the text fits within the width, draw it and return
        if (textWidth <= maxTextWidth) {
            canvas.drawText(text, xPosition, yPosition - (textHeight/2), adjustablePaint);
            return;
        }
        // Split the text into words
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float lineWidth;
        for (String word : words) {
            // Calculate the width of the current line
            lineWidth = paint.measureText(line.toString() + " " + word);
            // If the line is too long, draw it and start a new line
            if (lineWidth >= maxTextWidth) {
                canvas.drawText(line.toString(), xPosition, yPosition - textHeight, adjustablePaint);
                yPosition += paint.descent() - paint.ascent();
                line = new StringBuilder();
            }
            // Add the word to the current line
            line.append(word).append(" ");
        }
        // Draw the last line
        // make sure line is above yPosition - text height
        canvas.drawText(line.toString(), xPosition, yPosition - textHeight, adjustablePaint);
    }

    private void drawSnake(Canvas canvas, Paint snakeSegmentPaint) {
        if (this.gridMargin == 0) {
            this.gridMargin = (getWidth() - (this.gridSize * this.cellSize)) / 2;
        }
        for (Pair<Integer, Integer> segment : this.snake) {
            int xPosition = (segment.getFirst() * this.cellSize) + this.gridMargin;
            int yPosition = (segment.getSecond() * this.cellSize) + this.gridMargin;
            float centerX = xPosition + (this.cellSize / 2.0f);
            float centerY = yPosition + (this.cellSize / 2.0f);
            canvas.drawCircle(centerX, centerY, (this.cellSize - (this.cellSize*0.1f)) / 2.0f, snakeSegmentPaint);
        }
    }

    private void drawBestScore(Canvas canvas) {
        // Load the crown drawable resource
        VectorDrawableCompat crownDrawable = VectorDrawableCompat.create(
                getContext().getResources(),
                R.drawable.snake_game_element_crown,
                null
        );
        if (crownDrawable != null) {
            crownDrawable.setBounds(0, 0, (int) crownWidth, (int) crownHeight);
            crownDrawable.setTint(buttonsAndFrameColor.toArgb());
        }
        // Draw best score with crown
        String scoreText = String.valueOf(this.bestScore);
        Paint textPaint = new Paint(this.MlcdText);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(MlcdText.getTextSize()*1.8f);
        float textWidth = textPaint.measureText(scoreText);
        float textHeight = textPaint.descent() - textPaint.ascent();
        float totalWidth = crownWidth + 80.0f + textWidth;
        float startX = (getWidth() - totalWidth) / 2.0f;
        if (crownDrawable != null) {
            drawCrown(canvas, crownDrawable, startX);
        }
        canvas.drawText(scoreText, startX + crownWidth + 10.0f, 20 + (textHeight/1.6f), textPaint);
    }

    private void drawCrown(Canvas canvas, VectorDrawableCompat crownDrawable, float x) {
        canvas.save();
        canvas.translate(x, (float) 20.0);
        crownDrawable.draw(canvas);
        canvas.restore();
    }

    private void addFood() {
        int i = this.gridSize / 2;
        int i2 = 6;
        int i3 = (i2 / 2) + i;
        for (int i4 = 0; i4 < i2; i4++) {
            this.foodCoordinates = new Pair<>(i3-i4+i2,i);
        }
    }

    public long getSnakeSpeed() { return snakeSpeed; }
    public void setSnakeSpeed(long snakeSpeed) { this.snakeSpeed = snakeSpeed; invalidate(); }

    public void setSnakeBackgroundColor(int color){ snakeBackgroundColor = Color.valueOf(color); invalidate(); }
    public void setSnakeColor(int color){ snakeColor = Color.valueOf(color); snakePaint.setColor(snakeColor.toArgb()); invalidate(); }
    public void setGridColor(int color){ gridColor = Color.valueOf(color); gridPaint.setColor(gridColor.toArgb()); invalidate(); }
    public void setButtonsAndFrameColor(int color){ buttonsAndFrameColor = Color.valueOf(color); gameBorder.setColor(buttonsAndFrameColor.toArgb()); MlcdText.setColor(buttonsAndFrameColor.toArgb()); invalidate(); }
    public void setFoodColor(int color){ foodColor = Color.valueOf(color); foodPaint.setColor(foodColor.toArgb()); invalidate(); }

    public void setTheme(ColorPrefConfig colorPrefConfig){
        snakeBackgroundColor = Color.valueOf(colorPrefConfig.getSnakeBackgroundColor());
        snakeColor = Color.valueOf(colorPrefConfig.getSnakeColor());
        gridColor = Color.valueOf(colorPrefConfig.getGridColor());
        buttonsAndFrameColor = Color.valueOf(colorPrefConfig.getButtonsAndFrameColor());
        foodColor = Color.valueOf(colorPrefConfig.getFoodColor());
        foodPaint.setColor(foodColor.toArgb());
        snakePaint.setColor(snakeColor.toArgb());
        gridPaint.setColor(gridColor.toArgb());
        gameBorder.setColor(buttonsAndFrameColor.toArgb());
        MlcdText.setColor(buttonsAndFrameColor.toArgb());
        invalidate();
    }

    public void setDefaultTheme(){
        snakeBackgroundColor = Color.valueOf(ContextCompat.getColor(context, R.color.snake_background_color));
        snakeColor = Color.valueOf(ContextCompat.getColor(context, R.color.snake_color));
        gridColor = Color.valueOf(ContextCompat.getColor(context, R.color.grid_color));
        buttonsAndFrameColor = Color.valueOf(ContextCompat.getColor(context, R.color.buttons_and_frame_color));
        foodColor = Color.valueOf(ContextCompat.getColor(context, R.color.food_color));
        foodPaint.setColor(foodColor.toArgb());
        snakePaint.setColor(snakeColor.toArgb());
        gridPaint.setColor(gridColor.toArgb());
        gameBorder.setColor(buttonsAndFrameColor.toArgb());
        MlcdText.setColor(buttonsAndFrameColor.toArgb());
        invalidate();
    }

    public boolean isGridView() { return gridView; }
    public void setGridView(boolean gridView) { this.gridView = gridView; }

    // ===== NEW: public switches to control behavior from caller =====
    public void setBindToPrefs(boolean bind) {
        this.bindToPrefs = bind;
        if (!bind) {
            recyclePreviewBitmap();
        } else if (enableCustomImageFromPrefs) {
            loadCustomBackgroundIfAny();
        }
        invalidate();
    }
    public void setEnableCustomImageFromPrefs(boolean enable) {
        this.enableCustomImageFromPrefs = enable;
        if (!enable) {
            recyclePreviewBitmap();
        } else if (bindToPrefs) {
            loadCustomBackgroundIfAny();
        }
        invalidate();
    }

    // ===== NEW: helper methods for image loading / recycling =====
    private void loadCustomBackgroundIfAny() {
        try {
            int currentTheme = sharedPreferences.getInt(MainActivity.CURRENT_THEME_KEY, -1);
            String uriString = sharedPreferences.getString(MainActivity.CUSTOM_BG_URI_KEY, null);
            if (currentTheme != THEME_CUSTOM_IMAGE_ID || uriString == null) {
                recyclePreviewBitmap();
                return;
            }
            if (mPreviewBitmap != null && !mPreviewBitmap.isRecycled()) return; // already loaded
            Uri uri = Uri.parse(uriString);
            int reqW = Math.max(getWidth(), 1);
            int reqH = Math.max(getHeight(), 1);
            mPreviewBitmap = decodeSampledBitmapFromUri(uri, reqW, reqH);
        } catch (Exception e) {
            Log.e("SnakePreView", "Failed to load custom preview image: " + e.getMessage());
            recyclePreviewBitmap();
        }
    }

    private void recyclePreviewBitmap() {
        if (mPreviewBitmap != null && !mPreviewBitmap.isRecycled()) {
            try { mPreviewBitmap.recycle(); } catch (Throwable ignore) {}
        }
        mPreviewBitmap = null;
    }

    private Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) throws Exception {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // pass 1: bounds
        opts.inJustDecodeBounds = true;
        try (InputStream is = getContext().getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(is, null, opts);
        }
        int srcW = opts.outWidth, srcH = opts.outHeight;
        if (srcW <= 0 || srcH <= 0) return null;
        // sample
        opts.inSampleSize = 1;
        while ((srcW / opts.inSampleSize) > reqWidth * 2 || (srcH / opts.inSampleSize) > reqHeight * 2) {
            opts.inSampleSize *= 2;
        }
        // pass 2: decode
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try (InputStream is = getContext().getContentResolver().openInputStream(uri)) {
            return BitmapFactory.decodeStream(is, null, opts);
        }
    }

    // Apply light/dark overlay palette when Custom Image theme is active
    private void applyOverlayIfCustom() {
        try {
            int currentTheme = sharedPreferences.getInt(MainActivity.CURRENT_THEME_KEY, -1);
            boolean dark = sharedPreferences.getBoolean(MainActivity.CUSTOM_IMAGE_UI_DARK_KEY, false);
            if (currentTheme == THEME_CUSTOM_IMAGE_ID) {
                if (dark) {
                    buttonsAndFrameColor = Color.valueOf(android.graphics.Color.BLACK);
                    gridColor = Color.valueOf(0x66000000);
                    snakeColor = Color.valueOf(android.graphics.Color.BLACK);
                    foodColor = Color.valueOf(0xFFFF5252);
                } else {
                    buttonsAndFrameColor = Color.valueOf(android.graphics.Color.WHITE);
                    gridColor = Color.valueOf(0x66FFFFFF);
                    snakeColor = Color.valueOf(android.graphics.Color.WHITE);
                    foodColor = Color.valueOf(0xFFFF5252);
                }
                gameBorder.setColor(buttonsAndFrameColor.toArgb());
                MlcdText.setColor(buttonsAndFrameColor.toArgb());
                gridPaint.setColor(gridColor.toArgb());
                snakePaint.setColor(snakeColor.toArgb());
                foodPaint.setColor(foodColor.toArgb());
            }
        } catch (Throwable ignore) { }
    }

}

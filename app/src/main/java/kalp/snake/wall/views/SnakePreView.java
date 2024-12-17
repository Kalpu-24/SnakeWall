package kalp.snake.wall.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
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
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.util.LinkedList;
import java.util.Objects;

import kalp.snake.wall.R;
import kalp.snake.wall.enums.EDirection;
import kalp.snake.wall.enums.EGameState;
import kalp.snake.wall.models.ColorPrefConfig;
import kotlin.Pair;

public class SnakePreView extends FrameLayout  {
    private final Context context;
    private Paint MlcdText;
    private Paint gameBorder;
    private int bestScore;
    private int currentScore;

    private final String bestScoreKey;
    private final String prefsName;
    private final SharedPreferences sharedPreferences;
    private int cellSize;
    private int gridMargin;
    private final int gridSize;
    private final EGameState gameState;
    private Paint snakePaint;
    private Paint foodPaint;
    private final LinkedList<Pair<Integer, Integer>> snake;
    private final int snakeBodySize;
    private long snakeSpeed;
    private Paint gridPaint;
    private final boolean gridView;
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
    private final int buttonRadius;

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

    private final ColorPrefConfig colorPrefConfig;

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
        this.buttonRadius = 50;
        this.gridView = false;

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = getWidth();
        screenHeight = getHeight();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics metrics = Objects.requireNonNull(ContextCompat.getSystemService(context, WindowManager.class)).getCurrentWindowMetrics();
            Insets insets = metrics.getWindowInsets().getInsets(WindowInsets.Type.systemGestures());

            insetTop = insets.top;

        } else {
            WindowManager windowManager = (WindowManager) ContextCompat.getSystemService(context, WindowManager.class);
            if (windowManager != null) {
                Display display = windowManager.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                Rect rect = new Rect();
                display.getRectSize(rect);
                insetTop = rect.top;
            }
        }
        gameWidth = (int) (screenWidth * 0.8);
        gameStartX = (screenWidth - gameWidth) / 2;
        gameStartY = (int) (insetTop * 0.5f);
        gameEndX = gameStartX + gameWidth;
        gameEndY = gameWidth + gameStartY;

        controllerStartX = screenWidth / 2;
        controllerStartY = (int) (gameEndY + (screenHeight * 0.1));
        controllerEndX = gameEndX;
        controllerEndY = controllerStartX + controllerStartY;
        invalidate();
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
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
        super.onDraw(canvas);

        drawBackgroundAndFrames(canvas);
        drawControls(canvas);
        canvas.save();
        canvas.translate(gameStartX, gameStartY);
        this.cellSize = (gameWidth - (this.gridMargin * 2)) / this.gridSize;
        drawPreviewScreen(canvas);
    }

    private void drawBackgroundAndFrames(Canvas canvas) {
        canvas.drawColor(snakeBackgroundColor.toArgb());
        RectF rectF = new RectF(gameStartX, gameStartY, gameEndX, gameEndY);
        canvas.drawRoundRect(rectF, 40, 40, gameBorder);
    }

    private void drawControls(Canvas canvas) {
        VectorDrawableCompat buttonDrawable = VectorDrawableCompat.create(context.getResources(), R.drawable.dotted_circle, null);
        @SuppressLint("UseCompatLoadingForDrawables") RotateDrawable arrowDrawable = (RotateDrawable) getResources().getDrawable(R.drawable.rotate_arrow, null);

        float spacing = 30;
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

//            Draw Start and Pause buttons
        RectF startRect = new RectF(startButtonStartX, startButtonStartY, startButtonEndX, startButtonEndY);
        canvas.drawRoundRect(startRect, 40, 40, gameBorder);
//            button text
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
        drawTextWithLineBreak(string, gameWidth / 2.0f, gameWidth - 70, canvas, this.MlcdText);
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

        // Create a copy of the paint object to adjust text size without altering the original
        Paint adjustablePaint = new Paint(paint);

        // If the text fits within the width, draw it and return
        if (textWidth <= maxTextWidth) {
            canvas.drawText(text, xPosition, yPosition, adjustablePaint);
            return;
        }

        // Split the text into words
        String[] words = text.split(" ");
        StringBuilder firstLine = new StringBuilder();
        StringBuilder secondLine = new StringBuilder();

        // Distribute words across two lines
        for (String word : words) {
            if (adjustablePaint.measureText(firstLine + word) <= maxTextWidth) {
                firstLine.append(word).append(" ");
            } else {
                secondLine.append(word).append(" ");
            }
        }

        // Adjust font size to fit the second line within the width
        while (adjustablePaint.measureText(secondLine.toString()) > maxTextWidth) {
            adjustablePaint.setTextSize(adjustablePaint.getTextSize() - 1.0f);
        }

        // Trim whitespace from lines
        firstLine = new StringBuilder(firstLine.toString().trim());
        secondLine = new StringBuilder(secondLine.toString().trim());

        // Draw text with or without an offset depending on `drawAbove`
        canvas.drawText(firstLine.toString(), xPosition, yPosition, adjustablePaint);
        canvas.drawText(secondLine.toString(), xPosition, yPosition + adjustablePaint.getTextSize(), adjustablePaint);
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
        // Proportional width and height for the crown icon
        float crownWidth = (19 * 20.0f) / 7;
        float crownHeight = crownWidth / 1.3f;

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
        textPaint.setTextSize(40.0f);

        float textWidth = textPaint.measureText(scoreText);
        float totalWidth = crownWidth + 80.0f + textWidth;
        float startX = (getWidth() - totalWidth) / 2.0f;

        if (crownDrawable != null) {
            drawCrown(canvas, crownDrawable, startX);
        }

        canvas.drawText(scoreText, startX + crownWidth + 10.0f, (insetTop*0.35f), textPaint);
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


    public long getSnakeSpeed() {
        return snakeSpeed;
    }

    public void setSnakeSpeed(long snakeSpeed) {
        this.snakeSpeed = snakeSpeed;
        invalidate();
    }

    public void setSnakeBackgroundColor(int color){
        snakeBackgroundColor = Color.valueOf(color);
        invalidate();
    }

    public void setSnakeColor(int color){
        snakeColor = Color.valueOf(color);
        invalidate();
    }

    public void setGridColor(int color){
        gridColor = Color.valueOf(color);
        invalidate();
    }

    public void setButtonsAndFrameColor(int color){
        buttonsAndFrameColor = Color.valueOf(color);
        invalidate();
    }

    public void setFoodColor(int color){
        foodColor = Color.valueOf(color);
        invalidate();
    }

    public void setTheme(ColorPrefConfig colorPrefConfig){
        snakeBackgroundColor = Color.valueOf(colorPrefConfig.getSnakeBackgroundColor());
        snakeColor = Color.valueOf(colorPrefConfig.getSnakeColor());
        gridColor = Color.valueOf(colorPrefConfig.getGridColor());
        buttonsAndFrameColor = Color.valueOf(colorPrefConfig.getButtonsAndFrameColor());
        foodColor = Color.valueOf(colorPrefConfig.getFoodColor());
        invalidate();
    }

    public void setDefaultTheme(){
        snakeBackgroundColor = Color.valueOf(colorPrefConfig.getSnakeBackgroundColor());
        snakeColor = Color.valueOf(colorPrefConfig.getSnakeColor());
        gridColor = Color.valueOf(colorPrefConfig.getGridColor());
        buttonsAndFrameColor = Color.valueOf(colorPrefConfig.getButtonsAndFrameColor());
        foodColor = Color.valueOf(colorPrefConfig.getFoodColor());
        invalidate();
    }
}

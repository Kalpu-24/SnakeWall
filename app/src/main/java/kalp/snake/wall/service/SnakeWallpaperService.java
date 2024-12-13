package kalp.snake.wall.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.util.LinkedList;

import kalp.snake.wall.R;
import kalp.snake.wall.enums.EDirection;
import kalp.snake.wall.enums.EGameState;
import kotlin.Pair;

public class SnakeWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new SnakeWallpaperEngine(this);
    }

    private class SnakeWallpaperEngine extends Engine {
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
        private EGameState gameState;
        private final Paint snakePaint;
        private final LinkedList<Pair<Integer, Integer>> snake;
        private final int snakeBodySize;
        private long snakeSpeed;
        private final Paint gridPaint;
        private final boolean gridView;
        private boolean newBestScore;
        private long lastActionTimeStamp;
        private EDirection nextDirection;
        private EDirection direction;
        private Pair<Integer, Integer> foodCoordinates;
        private final Paint foodPaint;

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

        private boolean visible = true;
        private boolean running = true;
        private boolean initialFrameDrawn = false;
        private boolean stateChanged = false;
        private int screenWidth, screenHeight;

        private Thread gameThread;

        private final Vibrator vibrator;

        public SnakeWallpaperEngine(Context context) {
            this.context = context;
            this.vibrator = context.getSystemService(Vibrator.class);

            this.gridSize = 19;
            this.gridMargin = 16;
            this.buttonRadius = 100;
            this.gridView = false;

            this.gameState = EGameState.START;
            this.snakeBodySize = 5;
            this.snakeSpeed = 200L;
            this.snake = new LinkedList<>();

            EDirection eDirection = EDirection.RIGHT;
            this.direction = eDirection;
            this.nextDirection = eDirection;

            this.prefsName = "SnakeGamePrefs";
            this.bestScoreKey = "SnakeBestScore";

            SharedPreferences sharedPreferences = context.getSharedPreferences(this.prefsName, 0);
            this.sharedPreferences = sharedPreferences;

            Paint gridPaintObj = new Paint();
            gridPaintObj.setColor(Color.argb(40, 255, 255, 255));
            gridPaintObj.setStrokeWidth(2.0f);
            this.gridPaint = gridPaintObj;

            Paint foodPaintObj = new Paint();
            foodPaintObj.setColor(ContextCompat.getColor(context, R.color.food_color));
            this.foodPaint = foodPaintObj;

            Paint paint = new Paint();
            paint.setColor(Color.LTGRAY);
            this.snakePaint = paint;

            Paint gameBorderPaint = new Paint();
            gameBorderPaint.setColor(Color.LTGRAY);
            gameBorderPaint.setStyle(Paint.Style.STROKE);
            gameBorderPaint.setStrokeWidth(15.0f);
            gameBorderPaint.setPathEffect(new DashPathEffect(new float[]{2, 30}, 10));
            gameBorderPaint.setStrokeJoin(Paint.Join.ROUND);
            gameBorderPaint.setStrokeCap(Paint.Cap.ROUND);
            this.gameBorder = gameBorderPaint;


            Paint mlcdPaint = new Paint();
            mlcdPaint.setColor(Color.LTGRAY);
            mlcdPaint.setTextSize(60.0f);
            mlcdPaint.setTextAlign(Paint.Align.CENTER);
            Typeface customFont = ResourcesCompat.getFont(context, R.font.mlcd);
            mlcdPaint.setTypeface(customFont); // Set the custom font
            mlcdPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            this.MlcdText = mlcdPaint;

            this.bestScore = sharedPreferences.getInt(this.bestScoreKey, 0);
            this.newBestScore = false;
            sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences2, str) -> {
                if (str == null) return;
                onSharedPreferenceChanged(sharedPreferences2, str);
            });
        }

        private void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(this.bestScoreKey)) {
                this.bestScore = sharedPreferences != null ? sharedPreferences.getInt(this.bestScoreKey, 0) : 0;
            }
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            visible = true;
            screenWidth = width;
            screenHeight = height;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                WindowMetrics metrics = getSystemService(WindowManager.class).getCurrentWindowMetrics();
                Insets insets = metrics.getWindowInsets().getInsets(WindowInsets.Type.systemGestures());

                insetTop = insets.top;

            } else {
                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                if (windowManager != null) {
                    Display display = windowManager.getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    Rect rect = new Rect();
                    display.getRectSize(rect);
                    insetTop = rect.top;
                }
            }
            gameWidth = (int) (width * 0.8);
            gameStartX = (width - gameWidth) / 2;
            gameStartY = insetTop * 2;
            gameEndX = gameStartX + gameWidth;
            gameEndY = gameWidth + gameStartY;

            controllerStartX = screenWidth / 2;
            controllerStartY = (int) (gameEndY + (screenHeight * 0.1));
            controllerEndX = gameEndX;
            controllerEndY = controllerStartX + controllerStartY;

            resetSnake();
            addFood();
            startGameLoop();
        }

        public boolean isScreenOn(Context context) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                running = true;
                if (gameThread == null || !gameThread.isAlive()) {
                    startGameLoop();
                }
            } else {
                running = false;
                gameState = EGameState.PAUSED;
                stateChanged = true;
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            running = false;
            visible = false;
            if (gameThread != null) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                    Log.e("SnakeWallpaperService", "Error stopping game thread", e);
                }
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
                float x = event.getX();
                float y = event.getY();

//                on start button clicked
                if (x >= startButtonStartX && x <= startButtonEndX && y >= startButtonStartY && y <= startButtonEndY){
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    if (gameState == EGameState.START) {
                        lastActionTimeStamp = System.currentTimeMillis();
                        gameState = EGameState.PLAYING;
                        stateChanged = true;
                    } else if (gameState == EGameState.PLAYING) {
                        gameState = EGameState.PAUSED;
                        stateChanged = true;
                    } else if (gameState == EGameState.PAUSED) {
                        gameState = EGameState.PLAYING;
                        lastActionTimeStamp = System.currentTimeMillis();
                        stateChanged = true;
                    } else if (gameState == EGameState.GAME_OVER) {
                        resetGame();
                        gameState = EGameState.START;
                        stateChanged = true;
                    }
                    return;
                }


                //            on controller button clicked
                if (gameState == EGameState.PLAYING){

//                    on arrow UP
                    if (x >= arrowUpButtonStartX && x <= arrowUpButtonEndX && y >= arrowUpButtonStartY && y <= arrowUpButtonEndY && direction != EDirection.DOWN) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                        if (direction != EDirection.DOWN) {
                            nextDirection = EDirection.UP;
                            manageDirection(direction);
                        }
                        return;
                    }

//                    on arrow DOWN
                    if (x >= arrowDownButtonStartX && x <= arrowDownButtonEndX && y >= arrowDownButtonStartY && y <= arrowDownButtonEndY && direction != EDirection.UP) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                        if (direction != EDirection.UP) {
                            nextDirection = EDirection.DOWN;
                            manageDirection(direction);
                        }
                        return;
                    }

//                    on arrow LEFT
                    if (x >= arrowLeftButtonStartX && x <= arrowLeftButtonEndX && y >= arrowLeftButtonStartY && y <= arrowLeftButtonEndY && direction != EDirection.RIGHT) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                        if (direction != EDirection.RIGHT) {
                            nextDirection = EDirection.LEFT;
                            manageDirection(direction);
                        }
                        return;
                    }

//                    on arrow RIGHT
                    if (x >= arrowRightButtonStartX && x <= arrowRightButtonEndX && y >= arrowRightButtonStartY && y <= arrowRightButtonEndY && direction != EDirection.LEFT) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                        if (direction != EDirection.LEFT) {
                            nextDirection = EDirection.RIGHT;
                            manageDirection(direction);
                        }

                    }
                }
            }
        }

        private void manageDirection(EDirection currentDirection) {
            if (nextDirection != currentDirection) {
                lastActionTimeStamp = System.currentTimeMillis();
            }
        }

        private void startGameLoop() {
            gameThread = new Thread(() -> {
                if (!isScreenOn(context)) {
                    gameState = EGameState.PAUSED;
                    stateChanged = true;
                }
                while (running) {
                    if (gameState != EGameState.START) {
                        if (System.currentTimeMillis() - lastActionTimeStamp > 10000 && gameState == EGameState.PLAYING) {
                            gameState = EGameState.PAUSED;
                            stateChanged = true;
                        }
                        moveSnake();
                        drawFrame();
                        try {
                            //noinspection BusyWait
                            Thread.sleep(snakeSpeed); // 60 FPS
                        } catch (InterruptedException e) {
                            Log.e("SnakeWallpaperService", "Error in game loop", e);
                        }
                    } else {
                        // Draw static frame periodically when not playing
                        if (!initialFrameDrawn || stateChanged) {
                            drawFrame();
                            initialFrameDrawn = true;
                            stateChanged = false;
                        }

                        try {
                            //noinspection BusyWait
                            Thread.sleep(1000); // 1fps
                        } catch (InterruptedException e) {
                            Log.e("SnakeWallpaperService", "Error in game loop", e);
                        }
                    }
                }
            });
            gameThread.start();
        }

        //        gameplay
        private void resetGame() {
            resetSnake();
            EDirection dir = EDirection.RIGHT;
            this.direction = dir;
            this.nextDirection = dir;
            this.snakeSpeed = 300L;
            this.newBestScore = false;
            addFood();
        }

        private void resetSnake() {
            this.snake.clear();
            int i = this.gridSize / 2;
            int i2 = this.snakeBodySize;
            int i3 = (i2 / 2) + i;
            for (int i4 = 0; i4 < i2; i4++) {
                this.snake.add(new Pair<>(i3 - i4, this.gridSize / 2));
            }
        }

        private void moveSnake() {
            Pair<Integer, Integer> newHead;
            Pair<Integer, Integer> adjustedHead;

            // Update direction if there's a change
            if (this.direction != this.nextDirection) {
                this.direction = this.nextDirection;
            }

            // Get the current head of the snake
            Pair<Integer, Integer> head = this.snake.getFirst();

            // Calculate the new head position based on the direction
            switch (this.direction) {
                case UP:
                    newHead = new Pair<>(head.getFirst(), head.getSecond() - 1);
                    break;
                case DOWN:
                    newHead = new Pair<>(head.getFirst(), head.getSecond() + 1);
                    break;
                case LEFT:
                    newHead = new Pair<>(head.getFirst() - 1, head.getSecond());
                    break;
                case RIGHT:
                    newHead = new Pair<>(head.getFirst() + 1, head.getSecond());
                    break;
                default:
                    throw new IllegalStateException("Unexpected direction: " + this.direction);
            }

            // Check if the snake collides with itself
            if (this.snake.contains(newHead)) {
                gameOver();
                return;
            }

            // Handle wrapping around the grid edges
            if (newHead.getFirst() < 0) {
                adjustedHead = new Pair<>(this.gridSize - 1, newHead.getSecond());
            } else if (newHead.getFirst() >= this.gridSize) {
                adjustedHead = new Pair<>(0, newHead.getSecond());
            } else if (newHead.getSecond() < 0) {
                adjustedHead = new Pair<>(newHead.getFirst(), this.gridSize - 1);
            } else if (newHead.getSecond() >= this.gridSize) {
                adjustedHead = new Pair<>(newHead.getFirst(), 0);
            } else {
                adjustedHead = newHead;
            }

            // Move the snake
            this.snake.addFirst(adjustedHead);
            this.snake.removeLast();

            // Check if the snake eats the food
            if (adjustedHead.equals(this.foodCoordinates)) {
                // Add a new segment to the snake
                this.snake.addLast(this.snake.getLast());

                // Adjust speed every 1 points
                if ((this.snake.size() - this.snakeBodySize) % 3 == 0) {
                    if (this.snakeSpeed > 100) {
                        this.snakeSpeed -= 15;
                    } else if (this.snakeSpeed > 50) {
                        this.snakeSpeed -= 5;
                    } else if (this.snakeSpeed > 20) {
                        this.snakeSpeed -= 2;
                    } else if (this.snakeSpeed > 10) {
                        this.snakeSpeed -= 1;
                    }
                }

                // Add new food
                addFood();
            }
        }

        private void gameOver() {
            updateScore();
            this.gameState = EGameState.GAME_OVER;
            stateChanged = true;
        }

        private void updateScore() {
            this.bestScore = this.sharedPreferences.getInt(this.bestScoreKey, 0);
            int size = this.snake.size() - this.snakeBodySize;
            this.currentScore = size;
            if (size > this.bestScore) {
                this.bestScore = size;
                SharedPreferences.Editor edit = this.sharedPreferences.edit();
                edit.putInt(this.bestScoreKey, this.bestScore);
                edit.apply();
                this.newBestScore = true;
            }
        }

        private void addFood() {
            int random;
            int random2;
            random = (int) (Math.random() * this.gridSize);
            random2 = (int) (Math.random() * this.gridSize);
            if (this.snake.contains(new Pair<>(random, random2))) {
                addFood();
            } else {
                this.foodCoordinates = new Pair<>(random, random2);
            }
        }

//       draw part
        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            if (!holder.getSurface().isValid()) {
                return; // Exit if the surface is invalid
            }

            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.BLACK);

                    // Draw grid
                    drawBackgroundAndFrames(canvas);
                    drawControls(canvas);

                    canvas.save();
                    canvas.translate(gameStartX, gameStartY);
                    this.cellSize = (gameWidth - (this.gridMargin * 2)) / this.gridSize;
                    int ordinal = this.gameState.ordinal();
                    if (ordinal == 0) {
                        drawStartScreen(canvas);
                    }
                    if (ordinal == 1) {
                        drawGame(canvas);
                    }
                    if (ordinal == 2) {
                        drawGameOverScreen(canvas);
                    } else {
                        if (ordinal != 3) {
                            return;
                        }
                        drawPausedScreen(canvas);
                    }
                    canvas.restore();
                }
            } catch (Exception e) {
                Log.e("SnakeWallpaperService", "Error drawing frame", e);
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }


        private void drawBackgroundAndFrames(Canvas canvas) {
            canvas.drawColor(Color.DKGRAY);
            RectF rectF = new RectF(gameStartX, gameStartY, gameEndX, gameEndY);
            canvas.drawRoundRect(rectF, 40, 40, gameBorder);
        }

        private void drawControls(Canvas canvas) {
            VectorDrawableCompat buttonDrawable = VectorDrawableCompat.create(context.getResources(), R.drawable.dotted_circle, null);
            @SuppressLint("UseCompatLoadingForDrawables") RotateDrawable arrowDrawable = (RotateDrawable) getResources().getDrawable(R.drawable.rotate_arrow, null);

            float spacing = 100;
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
                arrowDrawable.setTint(Color.LTGRAY);
            }

            if (buttonDrawable != null) {
                buttonDrawable.setBounds(0, 0, (int) buttonWidth, (int) buttonWidth);
                buttonDrawable.setTint(Color.LTGRAY);
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

        private void drawStartScreen(Canvas canvas) {
            drawBestScore(canvas);
            drawSnake(canvas, snakePaint);
            String string = context.getString(R.string.snake_tap_to_play);
            drawTextWithLineBreak(string, gameWidth / 2.0f, gameWidth - 70, canvas, this.MlcdText);
        }

        private void drawBestScore(Canvas canvas) {
            float crownWidth = (19 * 40.0f) / 7;
            float crownHeight = crownWidth / 1.3f;

            VectorDrawableCompat crownDrawable = VectorDrawableCompat.create(
                    context.getResources(),
                    R.drawable.snake_game_element_crown,
                    null
            );


            if (crownDrawable != null) {
                crownDrawable.setBounds(0, 0, (int) crownWidth, (int) crownHeight);
                crownDrawable.setTint(Color.LTGRAY);
            }

            String scoreText = String.valueOf(this.bestScore);
            Paint textPaint = new Paint(this.MlcdText);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setTextSize(85.0f);

            float textWidth = textPaint.measureText(scoreText);
            float totalWidth = crownWidth + 25.0f + textWidth;
            float startX = (gameWidth - totalWidth) / 2.0f;

            if (crownDrawable != null) {
                drawCrown(canvas, crownDrawable, startX);
            }

            canvas.drawText(scoreText, startX + crownWidth + 25.0f, 150.0f, textPaint);
        }

        private void drawCrown(Canvas canvas, VectorDrawableCompat crownDrawable, float x) {
            canvas.save();
            canvas.translate(x, (float) 70.0);
            crownDrawable.draw(canvas);
            canvas.restore();
        }

        private void drawSnake(Canvas canvas, Paint snakeSegmentPaint) {
            if (this.gridMargin == 0) {
                this.gridMargin = (gameWidth - (this.gridSize * this.cellSize)) / 2;
            }
            for (Pair<Integer, Integer> segment : this.snake) {
                int xPosition = (segment.getFirst() * this.cellSize) + this.gridMargin;
                int yPosition = (segment.getSecond() * this.cellSize) + this.gridMargin;

                float centerX = xPosition + (this.cellSize / 2.0f);
                float centerY = yPosition + (this.cellSize / 2.0f);

                canvas.drawCircle(centerX, centerY, (this.cellSize - (this.cellSize * 0.1f)) / 2.0f, snakeSegmentPaint);
            }

        }

        private void drawTextWithLineBreak(String text, float xPosition, float yPosition, Canvas canvas, Paint paint) {
            int maxTextWidth = gameWidth - (this.gridMargin * 3);
            float textWidth = paint.measureText(text);

            Paint adjustablePaint = new Paint(paint);

            if (textWidth <= maxTextWidth) {
                canvas.drawText(text, xPosition, yPosition, adjustablePaint);
                return;
            }

            String[] words = text.split(" ");
            StringBuilder firstLine = new StringBuilder();
            StringBuilder secondLine = new StringBuilder();

            for (String word : words) {
                if (adjustablePaint.measureText(firstLine + word) <= maxTextWidth) {
                    firstLine.append(word).append(" ");
                } else {
                    secondLine.append(word).append(" ");
                }
            }

            while (adjustablePaint.measureText(secondLine.toString()) > maxTextWidth) {
                adjustablePaint.setTextSize(adjustablePaint.getTextSize() - 1.0f);
            }

            firstLine = new StringBuilder(firstLine.toString().trim());
            secondLine = new StringBuilder(secondLine.toString().trim());

            canvas.drawText(firstLine.toString(), xPosition, yPosition, adjustablePaint);
            canvas.drawText(secondLine.toString(), xPosition, yPosition + adjustablePaint.getTextSize(), adjustablePaint);
        }

        private void drawGame(Canvas canvas) {
            int canvasWidth = gameWidth;
            int gridSize = this.gridSize;
            int cellSize = this.cellSize;

            this.gridMargin = (canvasWidth - (cellSize * gridSize)) / 2;

            if (gridView && gridSize > 0) {
                for (int rowOrColumn = 1; rowOrColumn < gridSize; rowOrColumn++) {
                    int marginOffset = this.gridMargin;
                    int position = (rowOrColumn * cellSize) + marginOffset;

                    canvas.drawLine(
                            position,
                            marginOffset,
                            position,
                            (gridSize * cellSize) + marginOffset,
                            this.gridPaint
                    );

                    canvas.drawLine(
                            marginOffset,
                            position,
                            (gridSize * cellSize) + marginOffset,
                            position,
                            this.gridPaint
                    );
                }
            }

            drawSnake(canvas, snakePaint);

            int foodX = (this.foodCoordinates.getFirst() * cellSize) + this.gridMargin;
            int foodY = (this.foodCoordinates.getSecond() * cellSize) + this.gridMargin;
            canvas.drawCircle(
                    (foodX + foodX + cellSize) / 2.0f,
                    (foodY + foodY + cellSize) / 2.0f,
                    cellSize / 2.0f,
                    this.foodPaint
            );
        }

        private void drawGameOverScreen(Canvas canvas) {
            int score = this.snake.size() - this.snakeBodySize;

            drawScore(canvas, score);

            drawSnake(canvas, this.foodPaint);

            String gameOverText = context.getString(R.string.snake_game_over);
            drawCenteredText(gameOverText, gameWidth / 2.0f, gameWidth / 2.0f + 90, canvas, this.MlcdText);

            String restartText = context.getString(R.string.snake_tap_to_restart);
            drawTextWithLineBreak(restartText, gameWidth / 2f, gameWidth - 70, canvas, this.MlcdText);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                resetGame();
                this.gameState = EGameState.START;
                stateChanged = true;
            }, 5000);
        }

        private void drawScore(Canvas canvas, int score) {
            float crownWidth = (19 * 40.0f) / 7;
            float crownHeight = crownWidth / 1.3f;

            VectorDrawableCompat crownDrawable = VectorDrawableCompat.create(
                    context.getResources(),
                    R.drawable.snake_game_element_crown,
                    null
            );


            if (crownDrawable != null) {
                crownDrawable.setBounds(0, 0, (int) crownWidth, (int) crownHeight);
                crownDrawable.setTint(Color.LTGRAY);
            }

            String scoreText = String.valueOf(score);
            Paint textPaint = new Paint(this.MlcdText);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setTextSize(85.0f);

            float textWidth = textPaint.measureText(scoreText);
            float totalWidth = crownWidth + 25.0f + textWidth;
            float startX = (gameWidth - totalWidth) / 2.0f;

            if (crownDrawable != null) {
                drawCrown(canvas, crownDrawable, startX);
            }

            canvas.drawText(scoreText, startX + crownWidth + 25.0f, 150.0f, textPaint);
        }

        private void drawPausedScreen(Canvas canvas) {
            String string = context.getString(R.string.snake_game_paused);
            drawTextWithLineBreak(string, gameWidth / 2.0f, gameWidth / 2.0f, canvas, this.MlcdText);
            String string2 = context.getString(R.string.snake_tap_to_resume);
            drawTextWithLineBreak(string2, gameWidth / 2.0f, gameWidth - 70, canvas, this.MlcdText);
        }

        private void drawCenteredText(String text, float xPosition, float yPosition, Canvas canvas, Paint paint) {
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();

            float textHeight = fontMetrics.bottom - fontMetrics.top;
            float verticalOffset = textHeight / 2 - fontMetrics.bottom;

            canvas.drawText(text, xPosition, yPosition + verticalOffset, paint);
        }

    }
}
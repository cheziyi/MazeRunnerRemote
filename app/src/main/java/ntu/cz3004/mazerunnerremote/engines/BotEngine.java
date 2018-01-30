package ntu.cz3004.mazerunnerremote.engines;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Aung on 1/28/2018.
 */

public class BotEngine extends SurfaceView implements Runnable {

    // Our map thread for the main loop
    private Thread thread = null;

    // For tracking movement Heading
    public enum Heading {UP, RIGHT, DOWN, LEFT}
    // Start by heading to the right
    private Heading heading = Heading.RIGHT;

    private boolean isRunning;
    private boolean isUpdated = true;
    private boolean isAutoUpdating = true;
    private boolean isEditMode = false;

    private int touchX;
    private int touchY;

    // To hold the screen size in pixels
    private int surfaceWidth;
    private int surfaceHeight;

    // The size in pixels of a block
    private int blockWidth;
    private int blockHeight;

    private int botX;
    private int botY;
    private int botCanvasX;
    private int botCanvasY;
    private Point wayPoint;

    // The size in blocks of the runnable area
    private final int NUM_BLOCKS_WIDTH = 15;
    private final int NUM_BLOCKS_HEIGHT = 20;
    private final int BOT_SIZE = 3;
    private final float TILE_BORDER_RATION = 0.1f;

    // Control pausing between updates
    private long nextFrameTime;
    // Update the game 10 times per second
    private final long FPS = 30;

    // There are 1000 milliseconds in a second
    private final long MILLIS_PER_SECOND = 1000;

    // A canvas for our paint
    private Canvas canvas;

    // Required to use canvas
    private SurfaceHolder surfaceHolder;

    // Some paint for our canvas
    private Paint paint;

    public BotEngine(Context context) {
        super(context);
    }

    public BotEngine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BotEngine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BotEngine(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void start() {

        // Initialize the drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        //Initialise Bot position
        botX = 0;
        botY = 0;

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                //get surfaceWidth and surfaceHeight
                surfaceWidth = width;
                surfaceHeight = height;

                blockWidth = surfaceWidth / NUM_BLOCKS_WIDTH;
                blockHeight = surfaceHeight / NUM_BLOCKS_HEIGHT;
                startRunning();

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    private void startRunning() {
        // Setup nextFrameTime so an update is triggered
        nextFrameTime = System.currentTimeMillis();
    }

    private void moveBot(){
        if(checkCollide(heading)){
            return;
        }
        // Move the bot
        switch (heading) {
            case UP:
                botY--;
                break;
            case RIGHT:
                botX++;
                break;
            case DOWN:
                botY++;
                break;
            case LEFT:
                botX--;
                break;
        }
    }

    private boolean checkCollide(Heading heading){
        switch (heading) {
            case UP:
                return (botY == 0);
            case RIGHT:
                return (botX == NUM_BLOCKS_WIDTH - BOT_SIZE);
            case DOWN:
                return (botY == NUM_BLOCKS_HEIGHT - BOT_SIZE);
            case LEFT:
                return (botX == 0);
        }
        return false;
    }

    @Override
    public void run() {
        while (isRunning) {
            // Update 10 times a second
            if(updateRequired()) {
                update();
                if(isAutoUpdating){
                    draw();
                }
            }
        }
    }

    private boolean updateRequired() {
        // Are we due to update the frame
        if(nextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            nextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            // Return true so that the update and draw
            // functions are executed
            return true;
        }

        return false;
    }

    public void draw() {
        // Get a lock on the canvas
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            // Fill the screen with Game Code School blue
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Draw tile
            paint.setColor(Color.argb(255, 0, 0, 255));
            int tileBorderWidth = (int) (TILE_BORDER_RATION * blockWidth);
            for(int x = 0; x < NUM_BLOCKS_WIDTH; x++){
                for(int y = 0; y < NUM_BLOCKS_HEIGHT; y++){
                    if(wayPoint != null && wayPoint.x == x && wayPoint.y == y){
                        paint.setColor(Color.argb(255, 0, 255, 0));
                        canvas.drawRect((x * blockWidth) + tileBorderWidth, (y * blockHeight) + tileBorderWidth, (x * blockWidth) + blockWidth - tileBorderWidth, (y * blockHeight) + blockHeight - tileBorderWidth, paint);
                        paint.setColor(Color.argb(255, 0, 0, 255));
                    }
                    else {
                        canvas.drawRect((x * blockWidth) + tileBorderWidth, (y * blockHeight) + tileBorderWidth, (x * blockWidth) + blockWidth - tileBorderWidth, (y * blockHeight) + blockHeight - tileBorderWidth, paint);
                    }
                }
            }

            // Set the color of the paint to draw the bot
            if(isEditMode){
                paint.setColor(Color.argb(255, 255, 0, 0));
            }
            else{
                paint.setColor(Color.argb(255, 255, 255, 255));
            }


            //draw bot
            canvas.drawRect((botX * blockWidth), (botY * blockHeight), ((botX * blockWidth) + (blockWidth * BOT_SIZE)), ((botY * blockHeight) + (blockHeight * BOT_SIZE)), paint);
            drawArrow();



            //canvas.drawRect(0, 0, 200, 200, paint);

            // Unlock the canvas and reveal the graphics for this frame
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawArrow() {

        Point arrowHead = new Point();
        Point arrowBaseRight = new Point();
        Point arrowBaseLeft = new Point();

        switch (heading){
            case UP:
                arrowHead.set((botX * blockWidth) + (getBotWidth() / 2), (botY * blockHeight));
                arrowBaseRight.set((botX * blockWidth) + getBotWidth(), (botY * blockHeight) + getBotHeight());
                arrowBaseLeft.set((botX * blockWidth), (botY * blockHeight) + getBotHeight());
                break;
            case DOWN:
                arrowHead.set((botX * blockWidth) + (getBotWidth() / 2), (botY * blockHeight) + getBotHeight());
                arrowBaseRight.set((botX * blockWidth), (botY * blockHeight));
                arrowBaseLeft.set((botX * blockWidth) + getBotWidth(), (botY * blockHeight));
                break;
            case LEFT:
                arrowHead.set((botX * blockWidth), (botY * blockHeight) + (getBotHeight() / 2));
                arrowBaseRight.set((botX * blockWidth) + getBotWidth(), (botY * blockHeight));
                arrowBaseLeft.set((botX * blockWidth) + getBotWidth(), (botY * blockHeight) + getBotHeight());
                break;
            case RIGHT:
                arrowHead.set((botX * blockWidth) + getBotWidth(), (botY * blockHeight) + (getBotHeight() / 2));
                arrowBaseRight.set((botX * blockWidth), (botY * blockHeight) + getBotHeight());
                arrowBaseLeft.set((botX * blockWidth), (botY * blockHeight));
                break;
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(1);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(arrowHead.x,arrowHead.y);
        path.lineTo(arrowBaseRight.x,arrowBaseRight.y);
        path.lineTo(arrowBaseLeft.x,arrowBaseLeft.y);
        path.lineTo(arrowHead.x,arrowHead.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    private void update() {
        if(!isUpdated){
            moveBot();
            setUpdated(true);
        }

    }

    public boolean touchBot() {
        return (touchX > (botX * blockWidth)) && (touchX < ((botX * blockWidth) + (blockWidth * BOT_SIZE)) && (touchY > (botY * blockHeight)) && (touchY < ((botY * blockHeight) + (blockHeight * BOT_SIZE))));
    }

    public void pause() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public void setAutoUpdating(boolean autoUpdating) {
        isAutoUpdating = autoUpdating;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setTouchX(int touchX) {
        this.touchX = touchX;
    }

    public void setTouchY(int touchY) {
        this.touchY = touchY;
    }

    private void printLog(String message){
        Log.v("debugBot", message);
    }

    public void setBotPosition(int[] touchCoor) {
        if(isEditMode){
            int[] newBotCoor = getNewBotPosition(touchCoor);
            if(!isBotOutOfArena(newBotCoor[0], newBotCoor[1])){
                botX = newBotCoor[0];
                botY = newBotCoor[1];
            }
            else if(isBotOnBorder()){
                setBotCanvas(touchCoor);
            }
        }
    }

    public int[] getNewBotPosition(int[] touchCoor){
        int[] nomalisedTouch = nomaliseTouch(touchCoor);
        int[] newBotCoor = new int[2];
        newBotCoor[0] = nomalisedTouch[0] / blockWidth;
        newBotCoor[1] = nomalisedTouch[1] / blockHeight;
        return newBotCoor;
    }

    public boolean isBotOutOfArena(int x, int y) {
        return (x < 0) || (y < 0) || (x > NUM_BLOCKS_WIDTH - BOT_SIZE) || (y > NUM_BLOCKS_HEIGHT - BOT_SIZE);
    }

    public boolean isBotOnBorder(){
        return (botX == 0) || (botY == 0) || (botX == NUM_BLOCKS_WIDTH - BOT_SIZE) || (botY == NUM_BLOCKS_HEIGHT - BOT_SIZE);
    }

    private int[] nomaliseTouch(int[] touchCoor) {
        int[] nomalisedTouch = new int[2];
        nomalisedTouch[0] = touchCoor[0] - botCanvasX + (blockWidth / 2);
        nomalisedTouch[1] = touchCoor[1] - botCanvasY + (blockHeight / 2);
        return nomalisedTouch;
    }

    public void setBotCanvas(int[] touchCoor){
        botCanvasX = touchCoor[0] - (botX * blockWidth);
        botCanvasY = touchCoor[1] - (botY * blockHeight);
    }

    private int getBotWidth(){
        return blockWidth * BOT_SIZE;
    }

    private int getBotHeight(){
        return blockHeight * BOT_SIZE;
    }

    public void setWaypoint() {
        this.wayPoint = new Point(touchX / blockWidth, touchY / blockHeight);
    }

}

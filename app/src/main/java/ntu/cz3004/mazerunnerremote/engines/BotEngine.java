package ntu.cz3004.mazerunnerremote.engines;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private Context context;

    // For tracking movement Heading
    public enum Heading {UP, RIGHT, DOWN, LEFT}
    // Start by heading to the right
    private Heading heading = Heading.RIGHT;

    private boolean isRunning;
    private boolean isUpdated = true;

    // To hold the screen size in pixels
    private int surfaceWidth;
    private int surfaceHeight;

    // The size in pixels of a block
    private int blockWidth;
    private int blockHeight;

    private int botX;
    private int botY;

    // The size in blocks of the runnable area
    private final int NUM_BLOCKS_WIDTH = 15;
    private final int NUM_BLOCKS_HEIGHT = 20;

    // Control pausing between updates
    private long nextFrameTime;
    // Update the game 10 times per second
    private final long FPS = 10;

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
        this.context = context;

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
                return (botX == NUM_BLOCKS_WIDTH - 1);
            case DOWN:
                return (botY == NUM_BLOCKS_HEIGHT - 1);
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
                draw();
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

    private void draw() {
        // Get a lock on the canvas
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            // Fill the screen with Game Code School blue
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Set the color of the paint to draw the bot
            paint.setColor(Color.argb(255, 255, 255, 255));

            //draw bot
            canvas.drawRect((botX * blockWidth), (botY * blockHeight), ((botX * blockWidth) + blockWidth), ((botY * blockHeight) + blockHeight), paint);

            //canvas.drawRect(0, 0, 200, 200, paint);

            // Unlock the canvas and reveal the graphics for this frame
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void update() {
        if(!isUpdated){
            moveBot();
            setUpdated(true);
        }

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
}

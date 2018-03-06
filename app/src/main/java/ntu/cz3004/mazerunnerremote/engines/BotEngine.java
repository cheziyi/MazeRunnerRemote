package ntu.cz3004.mazerunnerremote.engines;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.gson.Gson;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import ntu.cz3004.mazerunnerremote.dto.Command;
import ntu.cz3004.mazerunnerremote.dto.Response;
import ntu.cz3004.mazerunnerremote.managers.BluetoothManager;

import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.bt;

/**
 * Created by Aung on 1/28/2018.
 */

public class BotEngine extends SurfaceView implements Runnable, View.OnLongClickListener, View.OnTouchListener {

    public interface OnInteractionChangedListener{
        void OnInteractionChanged(boolean isEnabled);
    }

    private OnInteractionChangedListener onInteractionChangedListener;

    public void setOnInteractionChangedListener(OnInteractionChangedListener onInteractionChangedListener) {
        this.onInteractionChangedListener = onInteractionChangedListener;
    }

    // Our map thread for the main loop
    private Thread thread = null;

    // For tracking movement Heading
    public enum Heading {
        UP, RIGHT, DOWN, LEFT
    }

    // Start by heading to the right
    private Heading heading = Heading.UP;

    private boolean isRunning;
    private boolean isAutoUpdating = false;
    private boolean isEditMode = false;
    private boolean allowInteration = true;

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
    private int[][] grid;

    // The size in blocks of the runnable area
    private final int BOT_START_POSITION_X = 0;
    private final int BOT_START_POSITION_Y = 0;
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
        start();
    }

    public BotEngine(Context context, AttributeSet attrs) {
        super(context, attrs);
        start();
    }

    public BotEngine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        start();
    }

    public BotEngine(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        start();
    }

    public void start() {

        //Initialize the drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        //Initialise Bot position
        botX = BOT_START_POSITION_X;
        botY = BOT_START_POSITION_Y;

        //Add callback to get surface width and height only when the view is created
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
                //Initialise time frame
                nextFrameTime = System.currentTimeMillis();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

        setOnLongClickListener(this);
        setOnTouchListener(this);
    }

    private void moveBot() {
        if (checkCollide(heading)) {
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

    private boolean checkCollide(Heading heading) {
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
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                printLog(message);
                update(message);
            }
        });
        while (isRunning) {
            if (updateRequired()) {
                //if(isAutoUpdating) BluetoothManager.SendCommand(new Command(Command.CommandTypes.SEND_MAP));
                draw();
            }
        }
    }

    private boolean updateRequired() {
        // Are we due to update the frame
        if (nextFrameTime <= System.currentTimeMillis()) {
            // Setup when the next update will be triggered
            nextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;
            // Return true so that the update and draw functions are executed
            return true;
        }
        return false;
    }

    private void update(String messageReceived) {
        Response resp = new Gson().fromJson(messageReceived, Response.class);
        Response.RobotPosition robotPosition = resp.getRobotPosition();
        String event = resp.getEvent();
        if (robotPosition != null) {
            botX = robotPosition.getX();
            botY = robotPosition.getY();
            heading = degreeToDirection(robotPosition.getDirection());
        }
        if (resp.getGrid() != null) {
            grid = resp.getDisplayGrid();
        }
        if(event != null) {
            if(event.equals("endExplore") || event.equals("endFastest")) {
                allowInteration = true;
                onInteractionChangedListener.OnInteractionChanged(allowInteration);
            }
        }
    }

    public void rotateBot(boolean isClockWise) {
        if(allowInteration) {
            int currentBotOrientation = directionToDegree(heading);
            if(isClockWise) {
                currentBotOrientation += 90;
                if(currentBotOrientation > 270) {
                    currentBotOrientation = 0;
                }

            }
            else {
                currentBotOrientation -= 90;
                if(currentBotOrientation < 0) {
                    currentBotOrientation = 270;
                }
            }
            heading = degreeToDirection(currentBotOrientation);
        }
    }

    private int directionToDegree(Heading heading) {
        switch (heading) {
            case UP:
                return 0;
            case RIGHT:
                return 90;
            case DOWN:
                return 180;
            case LEFT:
                return 270;
            default:
                return 0;
        }
    }

    private Heading degreeToDirection(int degree) {
        switch (degree) {
            case 0:
                return Heading.UP;
            case 90:
                return Heading.RIGHT;
            case 180:
                return Heading.DOWN;
            case 270:
                return Heading.LEFT;
            default:
                return null;
        }
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
            for (int x = 0; x < NUM_BLOCKS_WIDTH; x++) {
                for (int y = 0; y < NUM_BLOCKS_HEIGHT; y++) {
                    if (wayPoint != null && wayPoint.x == x && wayPoint.y == y) {
                        paint.setColor(Color.argb(255, 0, 255, 0));
                        canvas.drawRect((x * blockWidth) + tileBorderWidth, (y * blockHeight) + tileBorderWidth, (x * blockWidth) + blockWidth - tileBorderWidth, (y * blockHeight) + blockHeight - tileBorderWidth, paint);
                        paint.setColor(Color.argb(255, 0, 0, 255));
                    } else {
                        canvas.drawRect((x * blockWidth) + tileBorderWidth, (y * blockHeight) + tileBorderWidth, (x * blockWidth) + blockWidth - tileBorderWidth, (y * blockHeight) + blockHeight - tileBorderWidth, paint);
                    }
                    if (grid != null) {
                        printLog(String.valueOf(grid.length));
                        // obstacle
                        if (grid[x][y] == 1) {
                            paint.setColor(Color.argb(255, 255, 255, 0));
                            canvas.drawRect((x * blockWidth) + tileBorderWidth, (y * blockHeight) + tileBorderWidth, (x * blockWidth) + blockWidth - tileBorderWidth, (y * blockHeight) + blockHeight - tileBorderWidth, paint);
                            paint.setColor(Color.argb(255, 0, 0, 255));
                        }
                        //unknown
                        else if (grid[x][y] == -1) {
                            paint.setColor(Color.argb(255, 255, 0, 255));
                            canvas.drawRect((x * blockWidth) + tileBorderWidth, (y * blockHeight) + tileBorderWidth, (x * blockWidth) + blockWidth - tileBorderWidth, (y * blockHeight) + blockHeight - tileBorderWidth, paint);
                            paint.setColor(Color.argb(255, 0, 0, 255));
                        }
                    }
                }
            }

            // Set the color of the paint to draw the bot
            if (isEditMode) {
                paint.setColor(Color.argb(255, 255, 0, 0));
            } else {
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

        switch (heading) {
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
        path.moveTo(arrowHead.x, arrowHead.y);
        path.lineTo(arrowBaseRight.x, arrowBaseRight.y);
        path.lineTo(arrowBaseLeft.x, arrowBaseLeft.y);
        path.lineTo(arrowHead.x, arrowHead.y);
        path.close();

        canvas.drawPath(path, paint);
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

    public void setAutoUpdating(boolean autoUpdating) {
        isAutoUpdating = autoUpdating;
    }

    public void setTouchX(int touchX) {
        this.touchX = touchX;
    }

    public void setTouchY(int touchY) {
        this.touchY = touchY;
    }

    private void printLog(String message) {
        Log.v("debugBot", message);
    }

    public void setBotPosition(int[] touchCoor) {
        if (isEditMode) {
            int[] newBotCoor = getNewBotPosition(touchCoor);
            if (!isBotOutOfArena(newBotCoor[0], newBotCoor[1])) {
                botX = newBotCoor[0];
                botY = newBotCoor[1];
            } else if (isBotOnBorder()) {
                setBotCanvas(touchCoor);
            }
        }
    }

    public int[] getNewBotPosition(int[] touchCoor) {
        int[] nomalisedTouch = nomaliseTouch(touchCoor);
        int[] newBotCoor = new int[2];
        newBotCoor[0] = nomalisedTouch[0] / blockWidth;
        newBotCoor[1] = nomalisedTouch[1] / blockHeight;
        return newBotCoor;
    }

    public boolean isBotOutOfArena(int x, int y) {
        return (x < 0) || (y < 0) || (x > NUM_BLOCKS_WIDTH - BOT_SIZE) || (y > NUM_BLOCKS_HEIGHT - BOT_SIZE);
    }

    public boolean isBotOnBorder() {
        return (botX == 0) || (botY == 0) || (botX == NUM_BLOCKS_WIDTH - BOT_SIZE) || (botY == NUM_BLOCKS_HEIGHT - BOT_SIZE);
    }

    private int[] nomaliseTouch(int[] touchCoor) {
        int[] nomalisedTouch = new int[2];
        nomalisedTouch[0] = touchCoor[0] - botCanvasX + (blockWidth / 2);
        nomalisedTouch[1] = touchCoor[1] - botCanvasY + (blockHeight / 2);
        return nomalisedTouch;
    }

    public void setBotCanvas(int[] touchCoor) {
        botCanvasX = touchCoor[0] - (botX * blockWidth);
        botCanvasY = touchCoor[1] - (botY * blockHeight);
    }

    private int getBotWidth() {
        return blockWidth * BOT_SIZE;
    }

    private int getBotHeight() {
        return blockHeight * BOT_SIZE;
    }

    public void setWaypoint() {
        this.wayPoint = new Point(touchX / blockWidth, touchY / blockHeight);
//        Command waypointCommand = new Command(Command.CommandTypes.PATH_WAYPOINT);
//        waypointCommand.setLocation(wayPoint.x, wayPoint.y);
//        BluetoothManager.SendCommand(waypointCommand);
    }

    public Point getWayPoint() {
        return wayPoint;
    }

    @Override
    public boolean onLongClick(View view) {
        if(allowInteration) {
            if (touchBot()) {
                isEditMode = true;
                return true;
            }
            setWaypoint();
        }
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int[] touchCoor = new int[2];
        touchCoor[0] = (int) motionEvent.getX();
        touchCoor[1] = (int) motionEvent.getY();
        setTouchX((int) motionEvent.getX()); //getX() return coordinate RELATIVE to the view dispatched it
        setTouchY((int) motionEvent.getY());
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBotCanvas(touchCoor);
                return false; //return false so that onLongClick get triggered
            case MotionEvent.ACTION_MOVE:
                setBotPosition(touchCoor);
                return true;
            case MotionEvent.ACTION_UP:
                if (isEditMode) {
//                    Command botLocCommand = new Command(Command.CommandTypes.ROBOT_LOCATION);
//                    botLocCommand.setLocation(botX, botY);
//                    botLocCommand.setDirection(directionToDegree(heading));
//                    BluetoothManager.SendCommand(botLocCommand);
                    isEditMode = false;
                }
                return false;
        }
        return false;
    }

    public int getBotX() {
        return botX;
    }

    public int getBotY() {
        return botY;
    }

    public int getHeadingInDegree() {
        return directionToDegree(heading);
    }

    public void setAllowInteration(boolean allowInteration) {
        this.allowInteration = allowInteration;
    }
}

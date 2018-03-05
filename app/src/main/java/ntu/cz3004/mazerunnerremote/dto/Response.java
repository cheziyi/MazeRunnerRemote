package ntu.cz3004.mazerunnerremote.dto;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.Conversion;

/**
 * Created by calvin on 30/1/2018.
 */

public class Response {
    private int[] robotPos;
    private String gridP1;
    private String gridP2;
    private String status;
    private String event;

    private static final int WIDTH = 15;
    private static final int HEIGHT = 20;

    public Response() {
    }

    public RobotPosition getRobotPosition() {
        if (robotPos == null) {
            return null;
        }
        return new RobotPosition(robotPos[1] - 1, 18 - robotPos[0], robotPos[2]);
    }

    public void setRobotPosition(int[] robotPosition) {
        this.robotPos = robotPosition;
    }

    public int[][] getGrid() {
        if (gridP1 == null || gridP2 == null) {
            return null;
        }

        int[][] gridLayout = new int[HEIGHT][WIDTH];

        ArrayList<Boolean> bitPart1 = new ArrayList<>();
        ArrayList<Boolean> bitPart2 = new ArrayList<>();

        for (int i = 0; i < gridP1.length(); i++) {
            boolean[] bools = Conversion.hexDigitMsb0ToBinary(gridP1.charAt(i));
            for (boolean bool : bools) {
                bitPart1.add(bool);
            }
        }
        // Remove 2 bit padding from start
        bitPart1.remove(0);
        bitPart1.remove(0);

        for (int i = 0; i < gridP2.length(); i++) {
            boolean[] bools = Conversion.hexDigitMsb0ToBinary(gridP2.charAt(i));
            for (boolean bool : bools) {
                bitPart2.add(bool);
            }
        }

        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                if (bitPart1.get((h * WIDTH) + w)) {
                    gridLayout[h][w] = 0;
                } else {
                    gridLayout[h][w] = -1;
                }
            }
        }

        int bitCount = 0;

        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                if (gridLayout[h][w] == -1) {
                    continue;
                }
                if (bitPart2.get(bitCount)) {
                    gridLayout[h][w] = 1;
                } else {
                    gridLayout[h][w] = 0;
                }
                bitCount += 1;
            }
        }

        return gridLayout;
    }


    public int[][] getDisplayGrid() {
        if (getGrid() == null)
            return null;

        int[][] grid = getGrid();
        int[][] dGrid = new int[grid[0].length][grid.length];

        for (int h = 0; h < grid.length; h++) {
            for (int w = 0; w < grid[0].length; w++) {
                dGrid[w][19 - h] = grid[h][w];
            }
        }
        return dGrid;
    }


    public void setGrid(String gridP1, String gridP2) {
        this.gridP1 = gridP1;
        this.gridP2 = gridP2;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public String getEvent() {
        return event;
        //endExplore. endFastest
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public class RobotPosition {
        private int x;
        private int y;
        private int direction;

        public RobotPosition() {
        }

        public RobotPosition(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }
    }
}

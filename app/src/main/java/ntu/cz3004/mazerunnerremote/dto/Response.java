package ntu.cz3004.mazerunnerremote.dto;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.Conversion;

/**
 * Created by calvin on 30/1/2018.
 */

public class Response {
    private int[] robotPosition;
    private String grid;
    private String status;

    private static final int WIDTH = 15;
    private static final int HEIGHT = 20;

    public Response() {
    }

    public RobotPosition getRobotPosition() {
        if (robotPosition == null)
            return null;
        return new RobotPosition(robotPosition[0], robotPosition[1], robotPosition[2]);
    }

    public void setRobotPosition(int[] robotPosition) {
        this.robotPosition = robotPosition;
    }

    public boolean[][] getGrid() {
        if (grid == null)
            return null;

        boolean[][] gridLayout = new boolean[HEIGHT][WIDTH];

        ArrayList<Boolean> bitList = new ArrayList<>();

        for (int i = 0; i < grid.length(); i++) {
            boolean[] bools = Conversion.hexDigitMsb0ToBinary(grid.charAt(i));
            for (boolean bool : bools) {
                bitList.add(bool);
            }
        }

        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                gridLayout[h][w] = bitList.get((h * WIDTH) + w);
            }
        }
        return gridLayout;
    }

    public void setGrid(String grid) {
        this.grid = grid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

package ntu.cz3004.mazerunnerremote.dto;

/**
 * Created by calvin on 30/1/2018.
 */

public class Response {
    private int[] robotPosition;
    private String grid;
    private String status;

    public Response() {
    }

    public int[] getRobotPosition() {
        return robotPosition;
    }

    public void setRobotPosition(int[] robotPosition) {
        this.robotPosition = robotPosition;
    }

    public String getGrid() {
        return grid;
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
}

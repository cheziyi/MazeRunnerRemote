package ntu.cz3004.mazerunnerremote.dto;

/**
 * Created by calvin on 28/1/2018.
 */

public class NavigateCommand extends Command {
    private Location endPoint;
    private Location[] wayPoints;

    public NavigateCommand(Location endPoint, Location[] wayPoints) {
        this.endPoint = endPoint;
        this.wayPoints = wayPoints;
    }

    public Location getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Location endPoint) {
        this.endPoint = endPoint;
    }

    public Location[] getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(Location[] wayPoints) {
        this.wayPoints = wayPoints;
    }
}

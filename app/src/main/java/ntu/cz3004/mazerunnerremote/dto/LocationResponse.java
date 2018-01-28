package ntu.cz3004.mazerunnerremote.dto;

/**
 * Created by calvin on 28/1/2018.
 */

public class LocationResponse extends Response {
    private Location currentLocation;

    public LocationResponse(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}

package ntu.cz3004.mazerunnerremote.dto;

/**
 * Created by calvin on 28/1/2018.
 */

public class MapResponse extends Response {
    private LocationStatus[][] grid;

    public MapResponse(LocationStatus[][] grid) {
        this.grid = grid;
    }

    public LocationStatus[][] getGrid() {
        return grid;
    }

    public void setGrid(LocationStatus[][] grid) {
        this.grid = grid;
    }
}

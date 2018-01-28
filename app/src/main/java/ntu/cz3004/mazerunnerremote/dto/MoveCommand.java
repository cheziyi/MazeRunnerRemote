package ntu.cz3004.mazerunnerremote.dto;

/**
 * Created by calvin on 28/1/2018.
 */

public class MoveCommand extends Command {
    private Direction direction;

    public MoveCommand(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}

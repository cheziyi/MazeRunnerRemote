package ntu.cz3004.mazerunnerremote.dto;

/**
 * Created by calvin on 31/1/2018.
 */

public class Command {
    private String commandString;
    private int h;
    private int w;
    private int direction;

    public Command(CommandTypes commandType) {
        commandString = typeToString(commandType);
    }

    public void setLocation(int h, int w) {
        this.h = h;
        this.w = w;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getCommandString() {
        String resCommand = commandString;
        if (commandString == typeToString(CommandTypes.PATH_WAYPOINT)) {
            resCommand = resCommand + "," + h + "," + w;
        } else if (commandString == typeToString(CommandTypes.ROBOT_LOCATION)) {
            resCommand = resCommand + "," + h + "," + w + "," + direction;
        }
        return resCommand;
    }

    private String typeToString(CommandTypes commandType) {
        switch (commandType) {
            case FORWARD:
                return "f";
            case REVERSE:
                return "r";
            case STRAFE_LEFT:
                return "sl";
            case STRAFE_RIGHT:
                return "sr";
            case ROTATE_LEFT:
                return "tl";
            case ROTATE_RIGHT:
                return "tr";
            case BEGIN_EXPLORE:
                return "beginExplore";
            case BEGIN_FASTEST_PATH:
                return "beginFastest";
            case SEND_MAP:
                return "sendArena";
            case AUTO_START:
                return "autoStart";
            case AUTO_STOP:
                return "autoStop";
            case PATH_WAYPOINT:
                return "wayPoint";
            case ROBOT_LOCATION:
                return "robotLoc";
            default:
                return "";
        }
    }


    public enum CommandTypes {
        FORWARD, REVERSE, STRAFE_LEFT, STRAFE_RIGHT, ROTATE_LEFT, ROTATE_RIGHT,
        BEGIN_EXPLORE, BEGIN_FASTEST_PATH, SEND_MAP,

        // Everything below are not supported by AMT
        AUTO_START, AUTO_STOP, PATH_WAYPOINT, ROBOT_LOCATION
    }
}
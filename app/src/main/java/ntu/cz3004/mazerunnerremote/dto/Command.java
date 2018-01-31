package ntu.cz3004.mazerunnerremote.dto;

/**
 * Created by calvin on 31/1/2018.
 */

public class Command {
    private String commandString;
    private int x;
    private int y;

    public Command(CommandTypes commandType) {
        commandString = typeToString(commandType);
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getCommandString() {
        String resCommand = commandString;
        if (commandString == typeToString(CommandTypes.WAYPOINT)) {
            resCommand = resCommand + "," + x + "," + y;
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
            case BEGIN_FASTPATH:
                return "beginFastest";
            case SEND_INFO:
                return "sendArena";
            case AUTO_START:
                return "autoStart";
            case AUTO_STOP:
                return "autoStop";
            case WAYPOINT:
                return "wayPoint";
            default:
                return "";
        }
    }


    public enum CommandTypes {
        FORWARD, REVERSE, STRAFE_LEFT, STRAFE_RIGHT, ROTATE_LEFT, ROTATE_RIGHT,
        BEGIN_EXPLORE, BEGIN_FASTPATH, SEND_INFO,

        // Everything below are not supported by AMT
        AUTO_START, AUTO_STOP, WAYPOINT
    }
}
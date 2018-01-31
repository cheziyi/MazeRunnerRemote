package ntu.cz3004.mazerunnerremote.dto;

/**
 * Created by calvin on 30/1/2018.
 */

public class Commands {
    public final static String FORWARD = "f";
    public final static String REVERSE = "r";
    public final static String STRAFE_LEFT = "sl";
    public final static String STRAFE_RIGHT = "sr";
    public final static String ROTATE_LEFT = "tl";
    public final static String ROTATE_RIGHT = "tr";
    public final static String BEGIN_EXPLORE = "beginExplore";
    public final static String BEGIN_FASTPATH = "beginFastest";
    public final static String SEND_INFO = "sendArena";

    // Everything below are not supported by AMT
    public final static String AUTO_START = "autoStart";
    public final static String AUTO_STOP = "autoStop";

    public class WaypointCommand {
        private int x;
        private int y;

        public WaypointCommand(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String ToString() {
            return "wayPoint," + x + "," + y;
        }
    }
}

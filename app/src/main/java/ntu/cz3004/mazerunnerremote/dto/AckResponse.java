package ntu.cz3004.mazerunnerremote.dto;

/**
 * Created by calvin on 28/1/2018.
 */

public class AckResponse extends Response {
    private AckType ackType;

    public AckResponse(AckType ackType) {
        this.ackType = ackType;
    }

    public AckType geAckType() {
        return ackType;
    }

    public void setAckType(AckType ackType) {
        this.ackType = ackType;
    }
}

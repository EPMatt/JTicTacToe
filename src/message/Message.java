package message;

import java.util.Arrays;

/**
 * JTicTacToe - Message : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class Message {

    public static final int MSG_SIZE = 13;
    public static final byte CONNECTION = 0;
    public static final byte TICK = 1;
    public static final byte BOARD_UPDATE = 2;
    public static final byte DISCONNECTION=3;
    public static final byte REQUEST = 0;
    public static final byte RESPONSE = 1;
    public static final byte CONNECTION_APPROVAL_RESPONSE = 2;
    protected byte[] buf;
    public final int type;
    public final byte purpose;

    public Message(byte[] buf) {
        this(buf[0], buf[1]);
        this.buf = buf;
    }

    public Message(int type, byte purpose) {
        this.buf = new byte[MSG_SIZE];
        this.type = type;
        buf[0] = (byte) type;
        this.purpose = purpose;
        buf[1]=this.purpose;
    }

    public void setAsError() {
        if (purpose == Message.RESPONSE) {
            for (int i = 1; i < MSG_SIZE; i++) buf[i] = '!';
        } else throw new UnsupportedOperationException();
    }

    public void setSuccessful() {
        if (purpose == Message.RESPONSE) buf[12] = 1;
        else throw new UnsupportedOperationException();
    }

    public byte[] getBuf() {
        return Arrays.copyOf(buf, MSG_SIZE);
    }

    public byte[] getBufByReference() {
        return buf;
    }
    public boolean isSuccessful(){
        return buf[12]==1;
    }
}

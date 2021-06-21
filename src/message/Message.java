package message;

import java.util.Arrays;

/**
 * JTicTacToe - Message : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class Message {

    public enum Type {
        CONNECTION((byte) 0),
        TICK((byte) 1),
        BOARD_UPDATE((byte) 2),
        DISCONNECTION((byte) 3);

        private byte code;

        Type(byte code) {
            this.code = code;
        }

        public byte getCode() {
            return code;
        }

        public static Type fromCode(byte code) {
            for (Type e : Type.values()) {
                if (code == e.code) return e;
            }
            throw new IllegalArgumentException("No message type with code (" + code + ") found");
        }

        public static Type fromCode(int code) {
            return fromCode((byte) code);
        }
    }
    public static final int MSG_SIZE = 13;
    public static final byte REQUEST = 0;
    public static final byte RESPONSE = 1;
    public static final byte CONNECTION_APPROVAL_RESPONSE = 2;
    protected byte[] buf;
    public final int type;
    public final byte purpose;

    public Message(byte[] buf) {
        this(Type.fromCode(buf[0]), buf[1]);
        this.buf = buf;
    }

    public Message(Type type, byte purpose) {
        this.buf = new byte[MSG_SIZE];
        this.type = type.getCode();
        buf[0] = (byte) this.type;
        this.purpose = purpose;
        buf[1] = this.purpose;
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

    public boolean isSuccessful() {
        return buf[12] == 1;
    }
}

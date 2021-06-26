package message;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

        private final byte code;

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

    public enum Purpose {
        REQUEST((byte) 0),
        RESPONSE((byte) 1),
        CONNECTION_APPROVAL_RESPONSE((byte) 2);

        private final byte code;

        Purpose(byte code) {
            this.code = code;
        }

        public byte getCode() {
            return code;
        }

        public static Purpose fromCode(byte code) {
            for (Purpose e : Purpose.values()) {
                if (code == e.code) return e;
            }
            throw new IllegalArgumentException("No message type with code (" + code + ") found");
        }
    }

    public static final int MSG_SIZE = 13;
    public static final byte REQUEST = 0;
    public static final byte RESPONSE = 1;
    public static final byte CONNECTION_APPROVAL_RESPONSE = 2;

    private static final int INDEX_TYPE = 0;
    private static final int INDEX_PURPOSE = 1;
    private static final int INDEX_STATUS = 2;
    protected static final int INDEX_DATA = 3;

    private byte[] buf;
    public final Type type;
    public final byte purpose;

    /**
     * Construct a generic Message from a byte buffer
     *
     * @param buf the buffer to construct the Message from
     */
    public Message(byte[] buf) {
        this(Type.fromCode(buf[INDEX_TYPE]), buf[INDEX_PURPOSE]);
        this.buf = buf;
    }

    /**
     * Construct a generic Message from a byte buffer, checking that the message type described in the buffer
     * corresponds to the given message type
     *
     * @param buf
     * @param type
     * @throws WrongMessageTypeException if the buffer message type doesn't match the provided type
     */
    public Message(byte[] buf, Type type) throws WrongMessageTypeException {
        this(buf);
        if (this.type != type) throw new WrongMessageTypeException();
    }

    /**
     * Construct an empty Message with the given type and purpose
     *
     * @param type
     * @param purpose
     */
    public Message(@NotNull Type type, byte purpose) {
        this.buf = new byte[MSG_SIZE];
        this.type = type;
        buf[INDEX_TYPE] = this.type.getCode();
        this.purpose = purpose;
        buf[INDEX_PURPOSE] = this.purpose;
    }

    public void setAsError() {
        if (purpose == Message.RESPONSE)
            buf[INDEX_STATUS] = 0;
        else
            throw new UnsupportedOperationException();
    }

    public void setSuccessful() {
        if (purpose == Message.RESPONSE)
            buf[INDEX_STATUS] = 1;
        else
            throw new UnsupportedOperationException();
    }

    public byte getAt(int index) {
        return buf[index];
    }

    public void setAt(int index, byte val) {
        buf[index] = val;
    }

    public byte[] getBuf() {
        return Arrays.copyOf(buf, MSG_SIZE);
    }

    public boolean isSuccessful() {
        return buf[INDEX_STATUS] == 1;
    }
}

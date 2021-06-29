package message;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * JTicTacToe - Message : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
abstract public class Message {

    public enum Type {
        CONNECTION((byte) 0),
        TICK((byte) 1),
        GAME_UPDATE((byte) 2),
        DISCONNECTION((byte) 3),
        JOIN_GAME((byte) 4);

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
        RESPONSE((byte) 1);

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

    public static final int PREAMBLE_SIZE = 4;

    private static final int INDEX_TYPE = 0;
    private static final int INDEX_PURPOSE = 1;
    private static final int INDEX_STATUS = 2;
    public static final int INDEX_DATA_LENGTH = 3;
    public static final int INDEX_DATA = 4;


    private byte[] buf;

    /**
     * Construct a generic Message from a byte buffer
     *
     * @param buf the buffer to construct the Message from
     */
    public Message(byte[] buf) {
        this(Type.fromCode(buf[INDEX_TYPE]), Purpose.fromCode(buf[INDEX_PURPOSE]), buf.length - PREAMBLE_SIZE);
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
        if (getType() != type) throw new WrongMessageTypeException();
    }


    /**
     * Construct an empty Message with the given type and purpose
     *
     * @param type
     * @param purpose
     */
    public Message(@NotNull Type type, Purpose purpose, int dataLen) {
        this.buf = new byte[PREAMBLE_SIZE + dataLen];
        buf[INDEX_TYPE] = type.getCode();
        buf[INDEX_PURPOSE] = purpose.getCode();
        buf[INDEX_DATA_LENGTH] = (byte) dataLen;
    }

    public void setAsError() {
        if (getPurpose() == Purpose.RESPONSE)
            buf[INDEX_STATUS] = 0;
        else
            throw new UnsupportedOperationException();
    }


    public void setSuccessful() {
        if (getPurpose() == Purpose.RESPONSE)
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
        return Arrays.copyOf(buf, buf.length);
    }

    public boolean isSuccessful() {
        return buf[INDEX_STATUS] == 1;
    }


    public Type getType() {
        return Type.fromCode(buf[INDEX_TYPE]);
    }

    public Purpose getPurpose() {
        return Purpose.fromCode(buf[INDEX_PURPOSE]);
    }

    /**
     * Construct a message from a byte buffer
     *
     * @param buf
     * @return an instance of a Message subclass, matching the type provided in the buffer
     */
    public static Message fromBuffer(byte[] buf) throws WrongMessageTypeException {
        Message m;
        Type t = Type.fromCode(buf[INDEX_TYPE]);
        switch (t) {
            case CONNECTION -> m = new ConnectionMessage(buf);
            case TICK -> m = new TickCellMessage(buf);
            case GAME_UPDATE -> m = new GameUpdateMessage(buf);
            case JOIN_GAME -> m = new GameJoinMessage(buf);
            case DISCONNECTION -> m = new DisconnectionMessage(buf);
            default -> throw new WrongMessageTypeException();
        }
        return m;
    }
}

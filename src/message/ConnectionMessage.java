package message;

import common.Utils;

import java.util.Arrays;

/**
 * JTicTacToe - ConnectionMessage : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class ConnectionMessage extends Message {

    public final int INDEX_NAME_LENGTH = INDEX_DATA;
    public final int INDEX_NAME = INDEX_DATA + 1;

    public ConnectionMessage(Purpose purpose) {
        super(Message.Type.CONNECTION, purpose, 0);
    }

    public ConnectionMessage(String name) {
        super(Type.CONNECTION, Purpose.REQUEST, 1 + name.length());
        setAt(INDEX_NAME_LENGTH, (byte) name.length());
        for (int i = 0; i < name.length(); i++)
            setAt(INDEX_NAME + i, (byte) name.charAt(i));
    }

    public ConnectionMessage(byte[] buf) throws WrongMessageTypeException {
        super(buf, Type.CONNECTION);
    }

    public String getName() {
        int len = getAt(INDEX_NAME_LENGTH);
        char[] buf = new char[len];
        for (int i = 0; i < len; i++)
            buf[i] = (char) getAt(INDEX_NAME + i);
        System.out.println("Received name: " + Arrays.toString(buf));
        return new String(buf);
    }
}

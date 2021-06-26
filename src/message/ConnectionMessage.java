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
    private static final int INDEX_ID = INDEX_DATA;
    private static final int INDEX_SYMBOL = INDEX_DATA + 4;

    public ConnectionMessage(byte purpose) {
        super(Message.Type.CONNECTION, purpose);
    }

    public ConnectionMessage(byte[] buf) throws WrongMessageTypeException {
        super(buf,Type.CONNECTION);
    }

    public void setGameId(int id) {
        if (purpose == Message.CONNECTION_APPROVAL_RESPONSE) {
            byte[] arrayId = Utils.toByteArray(id);
            for (int i = 0; i < 4; i++)
                setAt(INDEX_ID + i, arrayId[i]);
        } else throw new UnsupportedOperationException();
    }

    public int getGameId() {
        if (purpose == Message.CONNECTION_APPROVAL_RESPONSE) {
            byte[] arrayId = new byte[4];
            for (int i = 0; i < 4; i++)
                arrayId[i] = getAt(INDEX_ID + i);
            return Utils.byteArrayToInt(arrayId);
        } else throw new UnsupportedOperationException();
    }

    public void setSymbol(char symbol) {
        if (purpose == Message.CONNECTION_APPROVAL_RESPONSE) {
            setAt(INDEX_SYMBOL, (byte) symbol);
        } else throw new UnsupportedOperationException();
    }

    public char getSymbol() {
        if (purpose == Message.CONNECTION_APPROVAL_RESPONSE) {
            return (char) getAt(INDEX_SYMBOL);
        } else throw new UnsupportedOperationException();
    }
}

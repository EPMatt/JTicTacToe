package message;

import java.util.Arrays;

/**
 * JTicTacToe - BoardUpdateMessage : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class BoardUpdateMessage extends Message {
    private static final int INDEX_GAME_STATUS = INDEX_DATA;
    private static final int INDEX_BOARD = INDEX_DATA + 1;

    public BoardUpdateMessage(byte purpose) {
        super(Message.Type.BOARD_UPDATE, purpose);
    }

    public BoardUpdateMessage(byte[] buf) throws WrongMessageTypeException {
        super(buf,Type.BOARD_UPDATE);
    }

    public void setBoard(char[] board) {
        if (purpose == Message.RESPONSE)
            for (int i = 0; i < 9; i++)
                setAt(INDEX_BOARD + i, (byte) board[i]);
        else throw new UnsupportedOperationException();
    }

    public void setStatus(char status) {
        if (purpose == Message.RESPONSE)
            setAt(INDEX_GAME_STATUS, (byte) status);
        else throw new UnsupportedOperationException();
    }

    public byte[] getBoard() {
        if (purpose == Message.RESPONSE) {
            byte[] boardArray = new byte[9];
            for (int i = 0; i < 9; i++)
                boardArray[i] = getAt(INDEX_BOARD + i);
            return boardArray;
        } else
            throw new UnsupportedOperationException();
    }

    public char getStatus() {
        if (purpose == Message.RESPONSE)
            return (char) getAt(INDEX_GAME_STATUS);
        else
            throw new UnsupportedOperationException();
    }
}

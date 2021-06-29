package message;

import model.Game;

/**
 * JTicTacToe - GameUpdateMessage : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class GameUpdateMessage extends Message {
    private static final int INDEX_GAME_STATUS = INDEX_DATA;
    private static final int INDEX_TURN = INDEX_DATA + 1;
    private static final int INDEX_BOARD = INDEX_DATA + 2;

    public GameUpdateMessage(Purpose purpose) {
        super(Message.Type.GAME_UPDATE, purpose, 0);
    }

    public GameUpdateMessage(byte[] buf) throws WrongMessageTypeException {
        super(buf, Type.GAME_UPDATE);
    }

    public GameUpdateMessage(Game g) {
        super(Message.Type.GAME_UPDATE, Purpose.RESPONSE, 11);
        setTurn(g.getTurn());
        setStatus(g.getGameStatus());
        setBoard(g.getBoard());
    }

    private void setTurn(char turn) {
        setAt(INDEX_TURN, (byte) turn);
    }

    public char getTurn() {
        if (getPurpose() == Purpose.RESPONSE)
            return (char) getAt(INDEX_TURN);
        else
            throw new UnsupportedOperationException();
    }

    private void setBoard(char[] board) {
        if (getPurpose() == Purpose.RESPONSE)
            for (int i = 0; i < 9; i++)
                setAt(INDEX_BOARD + i, (byte) board[i]);
        else
            throw new UnsupportedOperationException();
    }

    private void setStatus(Game.Status status) {
        if (getPurpose() == Purpose.RESPONSE)
            setAt(INDEX_GAME_STATUS, (byte) status.getCode());
        else
            throw new UnsupportedOperationException();
    }

    public byte[] getBoard() {
        if (getPurpose() == Purpose.RESPONSE) {
            byte[] boardArray = new byte[9];
            for (int i = 0; i < 9; i++)
                boardArray[i] = getAt(INDEX_BOARD + i);
            return boardArray;
        } else
            throw new UnsupportedOperationException();
    }

    public Game.Status getStatus() {
        if (getPurpose() == Purpose.RESPONSE)
            return Game.Status.fromCode((char) getAt(INDEX_GAME_STATUS));
        else
            throw new UnsupportedOperationException();
    }
}

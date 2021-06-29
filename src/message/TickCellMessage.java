package message;

/**
 * JTicTacToe - TickCellMessage : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class TickCellMessage extends Message {
    private static final int INDEX_CELL = INDEX_DATA;

    public TickCellMessage(Purpose purpose) {
        super(Type.TICK, purpose, 0);
    }

    public TickCellMessage(Purpose purpose, int cell){
        super(Type.TICK,purpose,1);
        setCell(cell);
    }

    public TickCellMessage(byte[] buf) throws WrongMessageTypeException {
        super(buf, Type.TICK);
    }

    private void setCell(int cell) {
        if (getPurpose() == Purpose.REQUEST) setAt(INDEX_CELL, (byte) cell);
        else
            throw new UnsupportedOperationException();
    }

    public byte getRequiredCell() {
        if (getPurpose() == Purpose.REQUEST) return getAt(INDEX_CELL);
        else throw new UnsupportedOperationException();
    }
}

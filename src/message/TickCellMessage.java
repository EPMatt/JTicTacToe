package message;

/**
 * JTicTacToe - TickCellMessage : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class TickCellMessage extends Message{
    public static final int INDEX_CELL=2;
    public TickCellMessage(byte purpose) {
        super(Message.TICK, purpose);
    }
     public TickCellMessage(byte[] buf){
        super(buf);
        if(type!=Message.TICK)throw new ClassCastException();
    }
    public void setCell(byte cell){
        if(purpose==Message.REQUEST) buf[INDEX_CELL]=cell;
        else throw new UnsupportedOperationException();
    }
    public byte getRequiredCell(){
           if(purpose==Message.REQUEST) return buf[INDEX_CELL];
        else throw new UnsupportedOperationException();
    }
}

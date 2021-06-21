package message;

import java.util.Arrays;

/**
 * JTicTacToe - BoardUpdateMessage : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class BoardUpdateMessage extends Message {
    public static final int INDEX_STATUS=11;
    public static final int INDEX_BOARD=2;
    public BoardUpdateMessage(byte purpose) {
        super(Message.Type.BOARD_UPDATE, purpose);
    }
 public BoardUpdateMessage(byte[] buf){
        super(buf);
        if(Message.Type.fromCode(type)!=Message.Type.BOARD_UPDATE)throw new ClassCastException();
    }
    public void setBoard(char[] board) {
        if (purpose == Message.RESPONSE) {
            for (int i = 0; i < 9; i++) buf[INDEX_BOARD+i] = (byte)board[i];
        } else throw new UnsupportedOperationException();
    }
    public void setStatus(char status){
        if (purpose == Message.RESPONSE) {
             buf[INDEX_STATUS]=(byte)status;
        } else throw new UnsupportedOperationException();
    }
    public byte[] getBoard() {
        if (purpose == Message.RESPONSE) {
            return Arrays.copyOfRange(buf, INDEX_BOARD, INDEX_BOARD+9);
        } else throw new UnsupportedOperationException();
    }
    public char getStatus(){
          if (purpose == Message.RESPONSE) {
            return (char)buf[INDEX_STATUS];
        } else throw new UnsupportedOperationException();
    }
}

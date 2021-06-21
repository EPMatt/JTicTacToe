package message;
import common.Utils;

import java.util.Arrays;

/**
 * JTicTacToe - ConnectionMessage : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class ConnectionMessage extends Message{
    public static final int INDEX_ID=2;
    public static final int INDEX_SYMBOL=6;

    public ConnectionMessage(byte purpose) {
    super(Message.Type.CONNECTION, purpose);
    }
    public ConnectionMessage(byte[] buf){
        super(buf);
        if(Message.Type.fromCode(type)!=Message.Type.CONNECTION)throw new ClassCastException();
    }
    public void setGameId(int id){
        if(purpose==Message.CONNECTION_APPROVAL_RESPONSE){
            System.arraycopy(Utils.toByteArray(id),0,buf,INDEX_ID,4);
        }else throw new UnsupportedOperationException();
    }
    public int getGameId(){
        if(purpose==Message.CONNECTION_APPROVAL_RESPONSE){
            return Utils.byteArrayToInt(Arrays.copyOfRange(buf,INDEX_ID,INDEX_ID+4));
        }else throw new UnsupportedOperationException();
    }
    public void setSymbol(char symbol){
        if(purpose==Message.CONNECTION_APPROVAL_RESPONSE){
           buf[INDEX_SYMBOL]=(byte)symbol;
        }else throw new UnsupportedOperationException();
    }
    public char getSymbol(){
        if(purpose==Message.CONNECTION_APPROVAL_RESPONSE){
            return (char)buf[INDEX_SYMBOL];
        }else throw new UnsupportedOperationException();
    }
}

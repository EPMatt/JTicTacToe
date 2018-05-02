package message;

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
    super(Message.CONNECTION, purpose);
    }
    public ConnectionMessage(byte[] buf){
        super(buf);
        if(type!=Message.CONNECTION)throw new ClassCastException();
    }
    public void setGameId(int id){
        if(purpose==Message.CONNECTION_APPROVAL_RESPONSE){
            buf[INDEX_ID]=(byte)(id<<24);
            buf[INDEX_ID+1]=(byte)(id<<16);
            buf[INDEX_ID+2]=(byte)(id<<8);
            buf[INDEX_ID+3]=(byte)id;
        }else throw new UnsupportedOperationException();
    }
    public int getGameId(){
        if(purpose==Message.CONNECTION_APPROVAL_RESPONSE){
            return buf[INDEX_ID]>>24|buf[INDEX_ID+1]>>16|buf[INDEX_ID+2]>>8|buf[INDEX_ID+3];
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

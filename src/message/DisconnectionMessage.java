package message;

/**
 * JTicTacToe - DisconnectionMessage : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class DisconnectionMessage extends Message{
    
    public DisconnectionMessage(byte purpose) {
    super(Message.DISCONNECTION, purpose);
        System.out.println("called ctor with purpose= "+purpose);
    }
    public DisconnectionMessage(byte[] buf){
        super(buf);
        if(type!=Message.DISCONNECTION)throw new ClassCastException();
    }
    
}

package message;

import org.jetbrains.annotations.NotNull;

/**
 * JTicTacToe - DisconnectionMessage : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class DisconnectionMessage extends Message {

    public DisconnectionMessage(Purpose purpose) {
        super(Message.Type.DISCONNECTION, purpose,0);
    }

    public DisconnectionMessage(byte[] buf) throws WrongMessageTypeException {
        super(buf, Type.DISCONNECTION);
    }

}

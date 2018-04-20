package gui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.TicTacToeServer;

/**
 * JTicTacToe - ServerReceiverThread : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class ServerReceiverThread extends Thread{
    public final TicTacToeServer t;

    public ServerReceiverThread(TicTacToeServer t) {
        this.t = t;
    }
    @Override
    public void run() {
        try {
            t.receive();
        } catch (IOException ex) {
            Logger.getLogger(ServerReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

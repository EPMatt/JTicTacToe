package gui;

import java.io.IOException;
import model.TicTacToeClient;

/**
 * JTicTacToe - ClientReceiverThread : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class ClientReceiverThread extends Thread {
    public final TicTacToeClient t;
    public final int op;

    public ClientReceiverThread(TicTacToeClient t,int op) {
        this.t = t;
        this.op=op;
    }
    
    @Override
    public void run() {
        try {
            switch (op) {
                case 1:
                    if(t.getGameStatus()=='R') t.waitForUpdates();
                    break;
                case 2:
                    if(t.getGameStatus()=='R') t.waitForUpdates();
                    if(t.getGameStatus()=='R') t.waitForUpdates();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown receiver operation!");
            }
        } catch (IOException ex) {
        }
    }
    
}

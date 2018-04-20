package gui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ClientGUIListener;
import model.TicTacToeClient;

/**
 * JTicTacToe - ClientWaitingThread : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class ClientWaitingThread extends Thread {
      public final TicTacToeClient t;
      public final ClientGUIListener l;

    public ClientWaitingThread(TicTacToeClient t,ClientGUIListener l) {
        this.t = t;
        this.l=l;
    }
    
    @Override
    public void run() {
          try {
              if(t.waitForOpponent())l.opponentFound();
              else l.errorInFindingOpponent();
          } catch (IOException ex) {
              Logger.getLogger(ClientWaitingThread.class.getName()).log(Level.SEVERE, null, ex);
          }
    }
    
}

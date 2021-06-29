package connection;

import model.TicTacToeServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * JTicTacToe - ServerReceiverThread : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class ServerReceiverThread extends Thread {
    public final TicTacToeServer t;
    private final ServerSocket serverSocket;

    public ServerReceiverThread(TicTacToeServer t, int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.t = t;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket s = serverSocket.accept();
                ConnectionThread connectionThread = new ConnectionThread(s, t);
                connectionThread.start();
                t.newConnection(connectionThread);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}

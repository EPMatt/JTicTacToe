package model;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import message.BoardUpdateMessage;
import message.ConnectionMessage;
import message.DisconnectionMessage;
import message.Message;
import message.TickCellMessage;

/**
 * JTicTacToe - TicTacToeClient : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class TicTacToeClient {

    public final int serverPort;
    public final InetAddress serverIp;
    private DatagramSocket clientSocket;
    private byte[] board;
    private char turn;
    private char status;
    private Client me;
    private ClientListener listener;

    public TicTacToeClient(int port, InetAddress ip) throws Exception {
        this.serverPort = port;
        this.serverIp = ip;
        this.board = new byte[9];
        for (byte b : board) {
            b = 0;
        }
        for (int i = 0; i < 65536; i++) {
            try {
                clientSocket = new DatagramSocket(i);
                System.out.println("Porta libera" + i);
                break;
            } catch (BindException e) {
                System.out.println("porta già in uso");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        clientSocket.connect(ip, serverPort);
        me = new Client(clientSocket.getLocalAddress(), clientSocket.getLocalPort());
        System.out.println("Client port =" + me.port);
        System.out.println("Server port= " + serverPort);
        System.out.println("Server IP= " + serverIp);
        turn = 'X';
        status = 'R';
    }

    public boolean connect() throws IOException {
        ConnectionMessage outMsg = new ConnectionMessage(Message.REQUEST);
        send(outMsg);
        System.out.println("sending conn request to " + serverIp + ":" + serverPort);
        System.out.println("clientSocket: " + clientSocket);
        System.out.println("waiting for server response...");
        Message inMsg = receiveConnectionMessageFromServer();
        System.out.println("received from server!");
        if (inMsg.isSuccessful()) {
            return true;
        }
        return false;
    }

    public boolean waitForOpponent() throws IOException {
        ConnectionMessage inMsg = receiveConnectionMessageFromServer();
        System.out.println("waiting for opponent...received from server!");
        if (inMsg.purpose == Message.CONNECTION_APPROVAL_RESPONSE) {
            me.setGameId(inMsg.getGameId());
            me.setSymbol(inMsg.getSymbol());
            return true;
        }
        return false;
    }

    public void getBoardUpdate() throws IOException {
        BoardUpdateMessage outMsg = new BoardUpdateMessage(Message.REQUEST);
        send(outMsg);
        BoardUpdateMessage inMsg = new BoardUpdateMessage(receiveBoardUpdateMessageFromServer().getBuf());
        if (inMsg.getStatus() == 'D') {
            System.out.println("opponent quitted, bye!");
            listener.close();
        } else {
            updateTableFromBuffer(inMsg);
        }
    }

    public void waitForUpdates() throws IOException {
        System.out.println("it's not my turn, i'm waiting for updates...");
        BoardUpdateMessage inMsg = new BoardUpdateMessage(receiveBoardUpdateMessageFromServer().getBuf());
        System.out.println("Hey! i received an update from the server!");
        if (inMsg.getStatus() == 'D') {
            System.out.println("opponent quitted, bye!");
            listener.close();
        } else {
            nextTurn();
            updateTableFromBuffer(inMsg);

        }
    }

    private void updateTableFromBuffer(BoardUpdateMessage msg) {
        board = msg.getBoard();
        System.out.println("the board i received is " + msg.getBoard());
        listener.boardUpdated();
        status = (char) msg.getStatus();
        if (status == me.getSymbol()) {
            System.out.println("I WON");
            listener.gameWon();
        } else if (status == 'X' && me.getSymbol() == 'O' || status == 'O' && me.getSymbol() == 'X') {
            System.out.println("I LOST");
            listener.gameLost();
        } else if (status == 'S') {
            System.out.println("STALE");
            listener.gameStale();
        } else if (status == 'R') {
            System.out.println("WERE STILL RUNNING");
        } else {
            System.out.println("ERROR IN GAME STATUS");
        }
    }

    public void setClientListener(ClientListener l) {
        this.listener = l;
    }

    public boolean isMyTurn() {
        return me.getSymbol() == turn;
    }

    public void tick(int cell) throws IOException {
        System.out.println("TrisClient: ticking cell " + cell);
        TickCellMessage outMsg = new TickCellMessage(Message.REQUEST);
        outMsg.setCell((byte) cell);
        send(outMsg);
        TickCellMessage inMsg = receiveTickCellMessageFromServer();
        System.out.println("after ticking received: " + Arrays.toString(inMsg.getBuf()));
        if (inMsg.isSuccessful()) {
            System.out.println("tick successfull!");
        } else {
            System.out.println("tick Error!");
        }
    }

    public char getBoardAt(int index) {
        return (char) board[index];
    }

    private void nextTurn() {
        turn = (turn == 'X') ? 'O' : 'X';
    }

    public Client getMe() {
        return me;
    }

    public char getTurn() {
        return turn;
    }

    public char getGameStatus() {
        return status;
    }

    private void send(Message m) throws IOException {
        clientSocket.send(new DatagramPacket(m.getBuf(), m.getBuf().length, serverIp, serverPort));
    }

    private Message receiveFromServer() throws IOException {
        DatagramPacket d = new DatagramPacket(new byte[Message.MSG_SIZE], Message.MSG_SIZE);
        clientSocket.receive(d);
        return new Message(d.getData());
    }

    private BoardUpdateMessage receiveBoardUpdateMessageFromServer() throws IOException {
        Message t = receiveFromServer();
        if (t.type != Message.BOARD_UPDATE) {
            throw new IOException();
        }
        return new BoardUpdateMessage(t.getBuf());
    }

    private ConnectionMessage receiveConnectionMessageFromServer() throws IOException {
        Message t = receiveFromServer();
        if (t.type != Message.CONNECTION) {
            throw new IOException();
        }
        return new ConnectionMessage(t.getBuf());
    }

    private TickCellMessage receiveTickCellMessageFromServer() throws IOException {
        Message t = receiveFromServer();
        if (t.type != Message.TICK) {
            throw new IOException();
        }
        return new TickCellMessage(t.getBuf());
    }

    public boolean disconnectFromServer() throws IOException {
        DisconnectionMessage d = new DisconnectionMessage(Message.REQUEST);
        send(d);
        DisconnectionMessage resp = new DisconnectionMessage(receiveFromServer().getBuf());
        return resp.isSuccessful();

    }
}

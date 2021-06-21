package model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
                break;
            } catch (Exception e) {
            }
        }
        clientSocket.connect(ip, serverPort);
        me = new Client(clientSocket.getLocalAddress(), clientSocket.getLocalPort());
        turn = 'X';
        status = 'R';
    }

    public boolean connect() throws IOException {
        ConnectionMessage outMsg = new ConnectionMessage(Message.REQUEST);
        send(outMsg);
        Message inMsg = receiveConnectionMessageFromServer();
        if (inMsg.isSuccessful()) {
            return true;
        }
        return false;
    }

    public boolean waitForOpponent() throws IOException {
        ConnectionMessage inMsg = receiveConnectionMessageFromServer();
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
            listener.close();
        } else {
            updateTableFromBuffer(inMsg);
        }
    }

    public void waitForUpdates() throws IOException {
        BoardUpdateMessage inMsg = new BoardUpdateMessage(receiveBoardUpdateMessageFromServer().getBuf());
        if (inMsg.getStatus() == 'D') {
            listener.close();
        } else {
            nextTurn();
            updateTableFromBuffer(inMsg);

        }
    }

    private void updateTableFromBuffer(BoardUpdateMessage msg) {
        board = msg.getBoard();
        listener.boardUpdated();
        status = (char) msg.getStatus();
        if (status == me.getSymbol()) {
            listener.gameWon();
        } else if (status == 'X' && me.getSymbol() == 'O' || status == 'O' && me.getSymbol() == 'X') {
            listener.gameLost();
        } else if (status == 'S') {
            listener.gameStale();
        }
    }

    public void setClientListener(ClientListener l) {
        this.listener = l;
    }

    public boolean isMyTurn() {
        return me.getSymbol() == turn;
    }

    public void tick(int cell) throws IOException {
        TickCellMessage outMsg = new TickCellMessage(Message.REQUEST);
        outMsg.setCell((byte) cell);
        send(outMsg);
        TickCellMessage inMsg = receiveTickCellMessageFromServer();
        if (!inMsg.isSuccessful()) {
            throw new IllegalStateException("Tick error!");
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
        checkMessageType(t, Message.Type.BOARD_UPDATE);
        return new BoardUpdateMessage(t.getBuf());
    }

    private ConnectionMessage receiveConnectionMessageFromServer() throws IOException {
        Message t = receiveFromServer();
        checkMessageType(t, Message.Type.CONNECTION);
        return new ConnectionMessage(t.getBuf());
    }

    private TickCellMessage receiveTickCellMessageFromServer() throws IOException {
        Message t = receiveFromServer();
        checkMessageType(t, Message.Type.TICK);
        return new TickCellMessage(t.getBuf());
    }

    public boolean disconnectFromServer() throws IOException {
        DisconnectionMessage d = new DisconnectionMessage(Message.REQUEST);
        send(d);
        DisconnectionMessage resp = new DisconnectionMessage(receiveFromServer().getBuf());
        return resp.isSuccessful();

    }


    private void checkMessageType(Message m, Message.Type t) throws IOException{
        if(Message.Type.fromCode(m.type)!=t) throw new IOException();
    }
}

package model;

import java.io.IOException;
import java.net.InetAddress;

import connection.ConnectionThread;
import connection.MessageHandler;
import message.*;

/**
 * JTicTacToe - TicTacToeClient : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class TicTacToeClient implements MessageHandler {

    public final int serverPort;
    public final InetAddress serverIp;
    private Player me;
    private ClientListener listener;
    private ConnectionThread connectionThread;

    public TicTacToeClient(int port, InetAddress ip, String name, ClientListener listener) throws Exception {
        this.serverPort = port;
        this.serverIp = ip;
        this.connectionThread = new ConnectionThread(ip, port, this);
        this.me = new Player(name);
        this.listener = listener;
        connectionThread.start();
        connect();
    }

    public void connect() throws IOException {
        ConnectionMessage outMsg = new ConnectionMessage(me.getName());
        connectionThread.send(outMsg);
    }

    private void updateTableFromBuffer(GameUpdateMessage msg) {
        Game game = me.getGame();
        byte[] b = msg.getBoard();
        char[] board = new char[b.length];
        for (int i = 0; i < board.length; i++) {
            board[i] = (char) b[i];
        }
        game.setBoard(board);
        if (game.getTurn() != msg.getTurn()) game.nextTurn();
        listener.boardUpdated();
        Game.Status status = msg.getStatus();
        switch (status) {
            case WINNER_X -> {
                if (game.getPlayerX().equals(me))
                    listener.gameWon();
                else
                    listener.gameLost();
            }
            case WINNER_O -> {
                if (game.getPlayerO().equals(me))
                    listener.gameWon();
                else
                    listener.gameLost();
            }
            case STALE -> {
                listener.gameStale();
            }
            case DRAW -> {
                listener.close();
            }
            default -> {
                // game is running
            }
        }
    }

    public void setClientListener(ClientListener l) {
        this.listener = l;
    }

    public boolean isMyTurn() {
        return me.getGame().getPlayerX().equals(me) && me.getGame().getTurn() == Game.X_SYMBOL || me.getGame().getPlayerO().equals(me) && me.getGame().getTurn() == Game.O_SYMBOL;
    }

    public void tick(int cell) throws IOException {
        // update local game
        me.getGame().tick(cell);
        // send message to server
        TickCellMessage outMsg = new TickCellMessage(Message.Purpose.REQUEST, cell);
        connectionThread.send(outMsg);
    }

    public char getBoardAt(int index) {
        return me.getGame().getAt(index);
    }

    public Player getMe() {
        return me;
    }

    public char getTurn() {
        return me.getGame().getTurn();
    }

    public Game.Status getGameStatus() {
        return me.getGame().getGameStatus();
    }

    public void disconnectFromServer() throws IOException {
        DisconnectionMessage d = new DisconnectionMessage(Message.Purpose.REQUEST);
        connectionThread.send(d);
        connectionThread.setRunning(false);
        try {
            connectionThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(Message m, ConnectionThread sender) {
        if (m instanceof GameUpdateMessage) {
            GameUpdateMessage inMsg = (GameUpdateMessage) m;
            updateTableFromBuffer(inMsg);
        } else if (m instanceof TickCellMessage) {
            TickCellMessage inMsg = (TickCellMessage) m;
            if (!inMsg.isSuccessful()) {
                throw new IllegalStateException("Tick error!");
            }
        } else if (m instanceof ConnectionMessage) {
            ConnectionMessage inMsg = (ConnectionMessage) m;
            if (inMsg.isSuccessful()) {
                listener.connected();
            }
        } else if (m instanceof GameJoinMessage) {
            GameJoinMessage inMsg = (GameJoinMessage) m;
            // join the game
            Player x;
            Player o;
            if (inMsg.getSymbol() == Game.X_SYMBOL) {
                x = me;
                o = inMsg.getOpponent();
            } else {
                x = inMsg.getOpponent();
                o = me;
            }
            Game g = new Game(x, o, inMsg.getGameId());
            x.setGame(g);
            o.setGame(g);
            listener.opponentFound();
            // TODO send confirmation to server
        }
    }


    public char getMySymbol() {
        if (me.getGame() == null) return ' ';
        if (me.getGame().getPlayerX().equals(me)) return Game.X_SYMBOL;
        return Game.O_SYMBOL;
    }
}

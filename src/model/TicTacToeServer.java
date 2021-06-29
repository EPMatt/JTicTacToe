package model;

import connection.ConnectionThread;
import connection.MessageHandler;
import gui.ServerGUI;

import java.io.IOException;
import java.net.*;
import java.util.*;

import connection.ServerReceiverThread;
import message.*;

/**
 * JTicTacToe - TicTacToeServer : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class TicTacToeServer implements MessageHandler {

    private List<Player> players = new ArrayList<>();
    public final int serverPort;
    private ServerGUI serverGui;
    private int id = 0;
    private List<Game> games = new ArrayList<>();
    public final int maxClients;
    private int activeGames = 0;
    private int activeClients = 0;
    private ServerReceiverThread serverThread;
    private HashMap<ConnectionThread, Player> connections = new HashMap<>();

    public ConnectionThread getConnectionThread(Player p) {
        for (ConnectionThread c : connections.keySet()) {
            if (connections.get(c).equals(p))
                return c;
        }
        return null;
    }

    public TicTacToeServer(int port, int maxClients) throws IOException {
        this.serverPort = port;
        this.maxClients = maxClients;
        serverThread = new ServerReceiverThread(this, port);
        serverThread.start();
    }

    public void setListener(ServerGUI s) {
        serverGui = s;
    }

    public void notifyListenersClientListChanged() {
        serverGui.clientListChanged();
    }

    public Collection<Player> getClients() {
        return connections.values();
    }

    private void checkGameStatus(Game g) {   //manca controllo disattivazione giocatori!!
        Game.Status status = g.getGameStatus();
        if (status == Game.Status.RUNNING) {
            consolePrint("\tGame(" + g.getId() + ") status: RUNNING\n");
        } else {
            if (status == Game.Status.STALE) {
                consolePrint("\tGame(" + g.getId() + ") status: STALE\n");
            } else if (status == Game.Status.WINNER_X) {
                consolePrint("\tGame(" + g.getId() + ") status: PLAYER X WON\n");
            } else if (status == Game.Status.WINNER_O) {
                consolePrint("\tGame(" + g.getId() + ") status: PLAYER O WON\n");
            }
        }
    }

    private void sendBoardUpdate(Player p) throws IOException {
        GameUpdateMessage outMsg = new GameUpdateMessage(p.getGame());
        ConnectionThread t = getConnectionThread(p);
        t.send(outMsg);
        consolePrint("(Server) - Sent a board update to " + p + " via " + t + " \n");
    }

    public int getGamesNum() {
        return games.size();
    }

    private void consolePrint(String string) {
        serverGui.consolePrint(string);
    }

    public int getActiveGames() {
        return activeGames;
    }

    public int getActiveClients() {
        return activeClients;
    }

    public synchronized void cleanLists() {
        //scan games and delete inactive ones
        games.removeIf(g -> !g.isDraw());
    }

    @Override
    public void handle(Message m, ConnectionThread sender) {
        try {
            if (m instanceof GameUpdateMessage) {
                GameUpdateMessage outMsg = new GameUpdateMessage(Message.Purpose.RESPONSE);
                consolePrint("[" + sender.getAddress() + ":" + sender.getPort() + "] - " + "(" + id + ")Game board request\n");
                Player c = connections.get(sender);
                Game game = c.getGame();
                if (game != null) {
                    sendBoardUpdate(c);
                } else {
                    outMsg.setAsError();
                    try {
                        sender.send(outMsg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (m instanceof TickCellMessage) {
                TickCellMessage outMsg = new TickCellMessage(Message.Purpose.RESPONSE), tickInMsg = (TickCellMessage) m;
                Player c = connections.get(sender);
                Game game = c.getGame();
                if (game == null) {
                    outMsg.setAsError();
                } else {
                    byte cell = tickInMsg.getRequiredCell();
                    char symbol = game.getPlayerX().equals(c) ? Game.X_SYMBOL : Game.O_SYMBOL;
                    consolePrint("[" + sender.getAddress() + ":" + sender.getPort() + "] - " + "(" + c.getGame() + ")Tick cell request: cell= " + cell + " symbol= " + symbol + "\n");
                    if (game.getTurn() == symbol) {
                        game.tick(cell);
                        consolePrint("(Server) - Cell ticked!\n");
                        outMsg.setSuccessful();
                    } else {
                        outMsg.setAsError();
                    }
                    try {
                        sender.send(outMsg);
                        sendBoardUpdate(game.getPlayerX());
                        sendBoardUpdate(game.getPlayerO());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    checkGameStatus(game);
                    if (game.getGameStatus() != Game.Status.RUNNING)
                        endGame(game);
                }
            } else if (m instanceof ConnectionMessage) {
                ConnectionMessage inMsg = (ConnectionMessage) m;
                ConnectionMessage outMsg = new ConnectionMessage(Message.Purpose.RESPONSE);
                consolePrint("[" + sender.getAddress() + ":" + sender.getPort() + "] - " + "connection request.\n");
                final Player player1 = new Player(inMsg.getName());
                if (activeClients < maxClients) {
                    connections.put(sender, player1);
                    notifyListenersClientListChanged();
                    outMsg.setSuccessful();
                    sender.send(outMsg);
                    consolePrint("(Server) - Connection Accepted, checking for opponent...\n");
                    //check if 2 clients are free, add to game and play
                    //find second client
                    Player player2 = null;
                    for (Player p : connections.values())
                        if (p.getGame() == null && !p.equals(player1))
                            player2 = p;
                    if (player2 != null) {
                        consolePrint("(Server) - Free opponent found!\n");
                        //if both searches are x success create game
                        id++;
                        Game g = new Game(player1, player2, id);
                        player1.setGame(g);
                        player2.setGame(g);
                        games.add(g);
                        consolePrint("(Server) - Game started: id=" + id + " X player= [" + player1 + "] O player= [" + player2 + "]\n");
                        activeGames++;
                        activeClients += 2;
                        notifyListenersClientListChanged();
                        GameJoinMessage m1 = new GameJoinMessage(player2.getName(), g.getId(), Game.X_SYMBOL);
                        getConnectionThread(player1).send(m1);
                        GameJoinMessage m2 = new GameJoinMessage(player1.getName(), g.getId(), Game.O_SYMBOL);
                        getConnectionThread(player2).send(m2);
                    }
                } else {        //reached maximum number of clients: connection refused
                    consolePrint("(Server) - Maximum number of clients reached, connection refused");
                    outMsg.setAsError();
                    sender.send(outMsg);
                }
            } else if (m instanceof DisconnectionMessage) {
                Player p = connections.get(sender);
                Game game = p.getGame();
                consolePrint("[" + p + "] - " + "(" + game + ")Disconnection request\n");
                DisconnectionMessage outMsg = new DisconnectionMessage(Message.Purpose.RESPONSE);
                outMsg.setSuccessful();
                sender.send(outMsg);
                consolePrint("(Server) - Sent successful disconnection response to [" + sender.getAddress() + ":" + sender.getPort() + "]\n");
                Player toSendDisconnectionUpdate = p.equals(p.getGame().getPlayerX()) ? p.getGame().getPlayerO() : p.getGame().getPlayerX();
                sendBoardUpdate(toSendDisconnectionUpdate);
                game.draw();
                endGame(game);
            }
            serverGui.packetReceived();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void endGame(Game game) {
        activeGames--;
        game.getPlayerX().setInactive();
        game.getPlayerO().setInactive();
        activeClients -= 2;
        cleanLists();
    }

    public synchronized void newConnection(ConnectionThread t) {
        connections.put(t, null);
    }

    public InetAddress getAddress(Player c) {
        return getConnectionThread(c).getAddress();
    }

    public int getPort(Player c) {
        return getConnectionThread(c).getPort();
    }

    public Collection<Game> getGames() {
        return games;
    }
}

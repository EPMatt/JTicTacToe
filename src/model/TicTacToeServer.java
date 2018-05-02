package model;

import gui.ServerGUI;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Vector;
import message.BoardUpdateMessage;
import message.ConnectionMessage;
import message.DisconnectionMessage;
import message.Message;
import message.TickCellMessage;

/**
 * JTicTacToe - TicTacToeServer : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class TicTacToeServer {

    private Vector<Client> clients;
    public final int serverPort;
    private DatagramSocket serverSocket;
    private ServerGUI serverGui;
    private int id;
    private Vector<Game> games;
    public final int maxClients;
    public final boolean deleteOldClients;
    public final boolean deleteOldGames;
    private int activeGames;
    private int activeClients;

    public TicTacToeServer(int port, int maxClients, boolean deleteOldClients, boolean deleteOldGames) throws SocketException {
        this.clients = new Vector<Client>();
        this.serverPort = port;
        this.serverSocket = new DatagramSocket(port);
        this.id = 0;
        this.games = new Vector<Game>();
        this.maxClients = maxClients;
        this.deleteOldClients = deleteOldClients;
        this.deleteOldGames = deleteOldGames;
        this.activeClients = 0;
        this.activeGames = 0;
    }

    public void setListener(ServerGUI s) {
        serverGui = s;
    }

    public void addClient(Client c) {
        clients.add(c);
        notifyListenersClientListChanged();
    }

    public DatagramSocket getSocket() {
        return serverSocket;
    }

    public void receive() throws IOException, InterruptedException {
        DatagramPacket p = new DatagramPacket(new byte[Message.MSG_SIZE], Message.MSG_SIZE);
        consolePrint("(Server) - Waiting for packets...\n");
        serverSocket.receive(p);
        Message inMsg = new Message(p.getData());
        if (inMsg.type == Message.CONNECTION) {  //connection request
            ConnectionMessage outMsg = new ConnectionMessage(Message.RESPONSE);
            consolePrint("[" + p.getAddress() + ":" + p.getPort() + "] - " + "connection request.\n");
            Client c = new Client(p.getAddress(), p.getPort());
            if (activeClients < maxClients) {
                outMsg.setSuccessful();
                consolePrint("(Server) - Connection Accepted, checking for opponent...\n");
                send(p.getAddress(), p.getPort(), outMsg);
                this.addClient(c);
                activeClients++;
                //check if 2 clients are free, add to game and play
                //find first client
                Client player1 = null;
                if ((player1 = getFreeClient()) != null) {
                    player1.setGameId(-1);          //let the client seem occupied
                    Client player2 = null;
                    //find second client
                    if ((player2 = getFreeClient()) != null) {
                        consolePrint("(Server) - Free opponent found!\n");
                        //if both searches are x success create game
                        id++;
                        player1.setGameId(id);
                        player2.setGameId(id);
                        consolePrint("(Server) - Game started: id=" + id + " X player= [" + player1.ip + ":" + player1.port + "] O player= [" + player2.ip + ":" + player2.port + "]\n");
                        addGame(new Game(player1, player2, id));
                        activeGames++;
                        notifyListenersClientListChanged();
                        outMsg = new ConnectionMessage(Message.CONNECTION_APPROVAL_RESPONSE);
                        outMsg.setGameId(id);
                        outMsg.setSymbol('X');
                        send(player1.ip, player1.port, outMsg);
                        outMsg.setSymbol('O');
                        send(player2.ip, player2.port, outMsg);
                    } else {    //no second player found
                        player1.setGameId(0);       //free first player
                    }
                }
            } else {        //reached maximum number of clients: connection refused
                consolePrint("(Server) - Maximum number of clients reached, connection refused");
                outMsg.setAsError();
                send(p.getAddress(), p.getPort(), outMsg);
            }
        } else if (inMsg.type == Message.TICK) {    //tick cell request
            TickCellMessage outMsg = new TickCellMessage(Message.RESPONSE), tickInMsg = new TickCellMessage(inMsg.getBuf());
            int requestId =getClientByAddress(p.getAddress(), p.getPort()).getGameId();
            if (requestId == -1) {
                outMsg.setAsError();
            } else {
                Game requested = getGameById(requestId);
                byte cell = tickInMsg.getRequiredCell();
                char symbol = getClientByAddress(p.getAddress(), p.getPort()).getSymbol();
                consolePrint("[" + p.getAddress() + ":" + p.getPort() + "] - " + "(" + requestId + ")Tick cell request: cell= " + cell + " symbol= " + symbol + "\n");
                if (requested != null && requested.getTurn() == symbol) {
                    requested.tick(cell);
                    consolePrint("(Server) - Cell ticked!\n");
                    outMsg.setSuccessful();
                } else {
                    outMsg.setAsError();
                }
                send(p.getAddress(), p.getPort(), outMsg);
                checkGameStatus(requested);
                sendBoardUpdate(requested, 'X');
                sendBoardUpdate(requested, 'O');
                if (requested.getGameStatus() != 'R') {
                    requested.x.setInactive();
                    requested.o.setInactive();
                    activeClients -= 2;
                    requested.setInactive();
                    activeGames--;
                    cleanLists();
                }
            }
        } else if (inMsg.type == Message.BOARD_UPDATE) {    //game board request
            BoardUpdateMessage outMsg = new BoardUpdateMessage(Message.RESPONSE);
            consolePrint("[" + p.getAddress() + ":" + p.getPort() + "] - " + "(" + id + ")Game board request\n");
            int requestId = getClientByAddress(p.getAddress(), p.getPort()).getGameId();
            Game requested = getGameById(requestId);
            if (requested != null) {
                char toSendUpdate = (requested.x.ip.equals( p.getAddress()) && requested.x.port == p.getPort()) ? 'X' : 'O';
                sendBoardUpdate(requested, toSendUpdate);
            } else {
                outMsg.setAsError();
                send(p.getAddress(), p.getPort(), outMsg);
            }
        } else if (inMsg.type == Message.DISCONNECTION) {   
            int requestId = getClientByAddress(p.getAddress(), p.getPort()).getGameId();
            consolePrint("[" + p.getAddress() + ":" + p.getPort() + "] - " + "(" + id + ")Disconnection request\n");
            Game requested = getGameById(requestId);
            requested.setInactive();
            DisconnectionMessage outMsg = new DisconnectionMessage(Message.RESPONSE);
            outMsg.setSuccessful();
            send(p.getAddress(), p.getPort(), outMsg);
            consolePrint("(Server) - Sent successful disconnection response to [" + p.getAddress() + ":" + p.getPort() + "]\n");
            char toSendDisconnectionUpdate = (requested.x.ip.equals( p.getAddress()) && requested.x.port == p.getPort()) ? 'O' : 'X';
            sendBoardUpdate(requested, toSendDisconnectionUpdate);
            requested.x.setInactive();
            requested.o.setInactive();
            activeClients -= 2;
            requested.setInactive();
            activeGames--;
            cleanLists();
        }
        serverGui.packetReceived();
    }

    public void notifyListenersClientListChanged() {
        serverGui.clientListChanged();
    }

    public Vector<Client> getClients() {
        return clients;
    }

    private void addGame(Game g) {
        games.add(g);
    }

    private void checkGameStatus(Game g) {   //manca controllo disattivazione giocatori!!
        char status = g.getGameStatus();
        if (status == 'R') {
            consolePrint("\tGame(" + g.id + ") status: RUNNING\n");
        } else {
            if (status == 'S') {
                consolePrint("\tGame(" + g.id + ") status: STALE\n");
            } else if (status == 'X') {
                consolePrint("\tGame(" + g.id + ") status: PLAYER X WON\n");
            } else if (status == 'O') {
                consolePrint("\tGame(" + g.id + ") status: PLAYER O WON\n");
            }
        }
    }

    private void sendBoardUpdate(Game g, char c) throws IOException {
        BoardUpdateMessage outMsg = new BoardUpdateMessage(Message.RESPONSE);
        outMsg.setBoard(g.getBoard());
        outMsg.setStatus(g.getGameStatus());
        Client toSend = (c == 'X') ? g.x : g.o;
        send(toSend.ip, toSend.port, outMsg);
        consolePrint("(Server) - Sent a board update to " + c + " \n");
    }

    public int indexOfGame(int id) {
        int in = -1;
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).id == id) {
                in = i;
                break;
            }
        }
        return in;
    }

    public Game getGameAt(int indexOfGame) {
        return games.get(indexOfGame);
    }

    public int getGamesNum() {
        return games.size();
    }

    private void consolePrint(String string) {
        serverGui.consolePrint(string);
    }

    private void send(InetAddress ip, int port, Message m) throws IOException {
        serverSocket.send(new DatagramPacket(m.getBuf(), Message.MSG_SIZE, ip, port));
    }

    private Client getFreeClient() {
        Client c = null;
        for (Client i : clients) {
            if (i.getGameId() == 0) {  //player is free
                c = i;
                break;
            }
        }
        return c;
    }

    private Game getGameById(int id) {
        Game g = null;
        for (Game i : games) {
            if (i.id == id) {  //found game
                g = i;
                break;
            }
        }
        return g;
    }

    private Client getClientByAddress(InetAddress address, int port) {
        Client c = null;
        for (Client i : clients) {
            if (i.ip.equals(address) && i.port == port) {  //found player
                c = i;
                break;
            }
        }
        return c;
    }

    public int getActiveGames() {
        return activeGames;
    }

    public int getActiveClients() {
        return activeClients;
    }

    public void cleanLists() {
        //scan games and clients list and delete inactive if required
        if (deleteOldClients) {
            Iterator<Client> clientIterator = clients.iterator();
            while (clientIterator.hasNext()) {
                Client c = clientIterator.next();
                if (!c.isActive()) {
                    clientIterator.remove();
                }
            }
        }
        if (deleteOldGames) {
            Iterator<Game> gameIterator = games.iterator();
            while (gameIterator.hasNext()) {
                Game g = gameIterator.next();
                if (!g.isActive()) {
                    gameIterator.remove();
                }
            }
        }
    }
}

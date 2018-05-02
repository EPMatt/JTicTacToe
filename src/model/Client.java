package model;

import java.net.InetAddress;

/**
 * JTicTacToe - Client : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class Client {
    public final InetAddress ip;
    public final int port;
    private int gameId;
    private char symbol;
    private boolean active;
    public Client(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        this.active=true;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }
    public char getSymbol(){
        return symbol;
    }
    public void setSymbol(char s){
        this.symbol=s;
    }

    @Override
    public String toString() {
        return "["+ip+":"+port+"]";
    }
    
    void setInactive() {
        active=false;
    }

    boolean isActive() {
        return active;
    }
    
}

package model;

/**
 * JTicTacToe - Client : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class Player {
    private Game game;
    private final String name;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isActive() {
        return game != null;
    }

    public void setInactive() {
        game = null;
    }
}

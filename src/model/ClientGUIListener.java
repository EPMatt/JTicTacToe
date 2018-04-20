package model;

/**
 * JTicTacToe - ClientGUIListener : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public interface ClientGUIListener {
    public void opponentFound();
    public void errorInFindingOpponent();
}

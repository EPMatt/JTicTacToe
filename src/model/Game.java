package model;

import java.util.Arrays;

/**
 * JTicTacToe - Game : Assignment for Java course. This application can work as an UDP server which can handle multiple TicTacToe games simultaneously,
 * or as an UDP client which can be used to play a TicTacToe game over a network.
 *
 * @author Matteo Agnoletto <epmatt>
 * @version 1.0.0
 */
public class Game {

    public enum Status {
        WINNER_X('X'),
        WINNER_O('O'),
        STALE('S'),
        DRAW('D'),
        RUNNING('R');

        private char code;

        Status(char code) {
            this.code = code;
        }

        public static Status fromCode(char code) {
            for (Status e : Status.values()) {
                if (code == e.code) return e;
            }
            throw new IllegalArgumentException("No Game status for code (" + code + ") found");
        }

        public char getCode(){
            return code;
        }
    }


    public final int id;
    public final Client x;
    public final Client o;
    private char[] board;
    private char turn;
    public static final char X_SYMBOL = 'X';
    public static final char O_SYMBOL = 'O';
    private boolean active;

    public Game(Client a, Client b, int id) {
        this.x = a;
        this.o = b;
        x.setSymbol(X_SYMBOL);
        o.setSymbol(O_SYMBOL);
        this.board = new char[9];
        this.id = id;
        this.turn = X_SYMBOL;
        this.active = true;
    }

    public void tick(byte cell) {
        if (cell < 0 || cell > 9) throw new IndexOutOfBoundsException();
        board[cell] = turn;
        nextTurn();
    }

    public char getTurn() {
        return turn;
    }

    public void nextTurn() {
        turn = turn == X_SYMBOL ? O_SYMBOL : X_SYMBOL;
    }

    public char getAt(int cell) {
        return board[cell];
    }

    public Status getGameStatus() {
        Status status = Status.RUNNING;
        byte ticked = 0;
        for (char c : board) {
            if (c != 0) ticked++;
        }
        if (isWinner(X_SYMBOL)) status = Status.WINNER_X;    //X: winner x
        else if (isWinner(O_SYMBOL)) status = Status.WINNER_O;  //O: winner o
        else if (ticked == 9) status = Status.STALE;  //S: stale
        else if (!active) status = Status.DRAW;
        //else R: still running
        return status;
    }

    private boolean isWinner(char player) {
        if (board[0] == board[1] && board[1] == board[2] && board[2] == player) return true;
        if (board[3] == board[4] && board[4] == board[5] && board[5] == player) return true;
        if (board[6] == board[7] && board[7] == board[8] && board[8] == player) return true;
        if (board[0] == board[3] && board[3] == board[6] && board[6] == player) return true;
        if (board[1] == board[4] && board[4] == board[7] && board[7] == player) return true;
        if (board[2] == board[5] && board[5] == board[8] && board[8] == player) return true;
        if (board[0] == board[4] && board[4] == board[8] && board[8] == player) return true;
        if (board[2] == board[4] && board[4] == board[6] && board[6] == player) return true;
        return false;
    }

    public char[] getBoard() {
        return Arrays.copyOf(board, 9);
    }

    public void setInactive() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }
}

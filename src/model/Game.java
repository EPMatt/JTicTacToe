package model;

import org.jetbrains.annotations.NotNull;

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

        private final char code;

        Status(char code) {
            this.code = code;
        }

        public static Status fromCode(char code) {
            for (Status e : Status.values()) {
                if (code == e.code) return e;
            }
            throw new IllegalArgumentException("No Game status for code (" + code + ") found");
        }

        public char getCode() {
            return code;
        }
    }


    private final int id;
    private final Player x;
    private final Player o;
    private final char[] board = new char[9];
    private char turn = X_SYMBOL;
    public static final char X_SYMBOL = 'X';
    public static final char O_SYMBOL = 'O';
    private boolean draw = false;

    public Game(@NotNull Player x, @NotNull Player o, int id) {
        this.x = x;
        this.o = o;
        this.id = id;
    }

    public void tick(int cell) {
        if (cell < 0 || cell > 9) throw new IndexOutOfBoundsException("Provided an invalid cell index");
        if (board[cell] != 0) throw new IllegalArgumentException("Cell is already ticked");
        board[cell] = turn;
        nextTurn();
    }

    public int getId() {
        return id;
    }

    public Player getPlayerX() {
        return x;
    }

    public Player getPlayerO() {
        return o;
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

    @NotNull
    public Status getGameStatus() {
        Status status = Status.RUNNING;
        byte ticked = 0;
        for (char c : board) {
            if (c != 0) ticked++;
        }
        if (isWinner(X_SYMBOL)) status = Status.WINNER_X;    //X: winner x
        else if (isWinner(O_SYMBOL)) status = Status.WINNER_O;  //O: winner o
        else if (ticked == 9) status = Status.STALE;  //S: stale
        else if (draw) status = Status.DRAW;
        //else R: still running
        return status;
    }

    private boolean isWinner(char player) {
        boolean win = false, windiag1 = true, windiag2 = true;
        for (int i = 0; i < 3 && !win; i++) {
            boolean winrow = true;
            for (int j = 0; j < 3 && winrow; j++) {
                winrow = board[i * 3 + j] == player;

            }
            boolean wincol = true;
            for (int j = 0; j < 3 && wincol; j++) {
                wincol = board[j * 3 + i] == player;
            }
            win = winrow || wincol;
            if (board[i * 3 + i] != player) windiag1 = false;
            if (board[8 - (i * 3 + i)] != player) windiag2 = false;
        }
        return win || windiag1 || windiag2;
    }

    public char[] getBoard() {
        return Arrays.copyOf(board, 9);
    }

    public void draw() {
        this.draw = true;
    }

    public void setBoard(char[] board) {
        System.arraycopy(board, 0, this.board, 0, 9);
    }

    public boolean isDraw() {
        return draw;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                '}';
    }
}

package message;

import common.Utils;
import model.Player;

public class GameJoinMessage extends Message {
    private static final int INDEX_ID = INDEX_DATA;
    private static final int INDEX_SYMBOL = INDEX_DATA + 4;
    private static final int INDEX_OPPONENT_LEN = INDEX_DATA + 5;
    private static final int INDEX_OPPONENT = INDEX_DATA + 6;

    public GameJoinMessage(byte[] buf) throws WrongMessageTypeException {
        super(buf, Type.JOIN_GAME);
    }

    public GameJoinMessage(String opponent, int gameId, char symbol) {
        super(Type.JOIN_GAME, Purpose.RESPONSE, 6 + opponent.length());
        setGameId(gameId);
        setSymbol(symbol);
        setOpponent(new Player(opponent));
    }

    private void setGameId(int id) {
        byte[] arrayId = Utils.toByteArray(id);
        for (int i = 0; i < 4; i++)
            setAt(INDEX_ID + i, arrayId[i]);
    }

    public int getGameId() {
        byte[] arrayId = new byte[4];
        for (int i = 0; i < 4; i++)
            arrayId[i] = getAt(INDEX_ID + i);
        return Utils.byteArrayToInt(arrayId);
    }

    private void setSymbol(char symbol) {
        setAt(INDEX_SYMBOL, (byte) symbol);
    }

    public char getSymbol() {
        return (char) getAt(INDEX_SYMBOL);
    }

    private void setOpponent(Player opponent) {
        String name = opponent.getName();
        for (int i = 0; i < name.length(); i++)
            setAt(INDEX_OPPONENT + i, (byte) name.charAt(i));
    }

    public Player getOpponent() {
        int opponentLen = getAt(INDEX_OPPONENT_LEN);
        char[] namebuf = new char[opponentLen];
        for (int i = 0; i < opponentLen; i++)
            namebuf[i] = (char) getAt(INDEX_OPPONENT + i);
        return new Player(new String(namebuf));
    }

}

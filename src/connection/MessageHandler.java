package connection;

import message.Message;

public interface MessageHandler {
    /**
     * Handle the provided Message
     * @param m
     */
    public void handle(Message m, ConnectionThread sender);
}

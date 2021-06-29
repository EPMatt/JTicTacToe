package connection;

import message.Message;
import message.WrongMessageTypeException;
import model.TicTacToeClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionThread extends Thread {
    private final Socket socket;
    private final InputStream is;
    private final OutputStream os;
    private final MessageHandler handler;
    private boolean running = false;


    public ConnectionThread(@NotNull InetAddress ip, int port, MessageHandler handler) throws IOException {
        this(new Socket(ip, port), handler);
    }

    public ConnectionThread(@NotNull Socket s, MessageHandler handler) throws IOException {
        this.socket = s;
        is = socket.getInputStream();
        os = socket.getOutputStream();
        this.handler = handler;
    }

    @Override
    public String toString() {
        return "ConnectionThread{" + getAddress() + ":" + getPort() + "}";
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        setRunning(true);
        byte[] preamblebuf = new byte[Message.PREAMBLE_SIZE];
        byte[] databuf;
        while (isRunning()) {
            try {
                System.out.println(this + ": receiving preamble");
                is.readNBytes(preamblebuf, 0, Message.PREAMBLE_SIZE);
                int len = preamblebuf[Message.INDEX_DATA_LENGTH];
                databuf = new byte[len];
                is.readNBytes(databuf, 0, len);
                // build message
                byte[] buf = new byte[preamblebuf.length + databuf.length];
                System.arraycopy(preamblebuf, 0, buf, 0, preamblebuf.length);
                System.arraycopy(databuf, 0, buf, Message.INDEX_DATA, databuf.length);
                Message m = Message.fromBuffer(buf);
                System.out.println(this + ": received " + m);
                // hand over to the linked message handler
                // the operation will be executed by this thread
                handler.handle(m, this);
            } catch (IOException e) {
                setRunning(false);
                e.printStackTrace();
            } catch (WrongMessageTypeException e) {
                // received an invalid message
                // TODO notify that an invalid message was received
                e.printStackTrace();
            }
        }
    }

    /**
     * Send the provided message via the socket's OutputStream
     *
     * @param m
     */
    public void send(Message m) throws IOException {
        System.out.println(this + ": sending " + m);
        os.write(m.getBuf());
    }

    public InetAddress getLocalAddress() {
        return socket.getLocalAddress();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public InetAddress getAddress() {
        return socket.getInetAddress();
    }

    public int getPort() {
        return socket.getPort();
    }
}

package lesson6;

import Network.ServerSocketThread;
import Network.ServerSocketThreadListener;
import Network.SocketThread;
import Network.SocketThreadListener;
import Common.Messages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {
    private final int SERVER_SOCKET_TIMEOUT = 2000;
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss: ");
    private Vector<SocketThread> clients = new Vector<>();
    private static final Logger log = LogManager.getLogger();

    int counter = 0;
    ServerSocketThread server;
    ChatServerListener listener;

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
    }

    public void start(int port) {
        if (server != null && server.isAlive()) {
            putLog("Server already started");
            log.info("Server already started");
        } else {
            server = new ServerSocketThread(this, "Chat server " + counter++, port, SERVER_SOCKET_TIMEOUT);
        }
    }

    public void stop() {
        if (server == null || !server.isAlive()) {
            putLog("Server is not running");
            log.info("Server is not running");
        } else {
            server.interrupt();
        }
    }

    private void putLog(String msg) {
        msg = DATE_FORMAT.format(System.currentTimeMillis()) +
                Thread.currentThread().getName() +
                ": " + msg;
        listener.onChatServerMessage(msg);
    }

    /**
     * Server socket thread methods
     * */

    @Override
    public void onServerStart(ServerSocketThread thread) {
        putLog("Server thread started");
        log.info("Server thread started");
        SqlClient.connect();
    }

    @Override
    public void onServerStop(ServerSocketThread thread) {
        putLog("Server thread stopped");
        log.info("Server thread stopped");
        SqlClient.disconnect();
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).close();
        }
    }

    @Override
    public void onServerSocketCreated(ServerSocketThread t, ServerSocket s) {
        putLog("Server socket created");
        log.info("Server socket created");
    }

    @Override
    public void onServerSoTimeout(ServerSocketThread t, ServerSocket s) {
        //
    }

    @Override
    public void onSocketAccepted(ServerSocketThread t, ServerSocket s, Socket client) {
        putLog("client connected");
        log.info("client connected");
        String name = "SocketThread" + client.getInetAddress() + ": " + client.getPort();
        new ClientThread(this, name, client);
    }

    @Override
    public void onServerException(ServerSocketThread t, Throwable e) {
        e.printStackTrace();
        log.debug("ServerException");
    }

    /**
     * Socket Thread listening
     * */

    @Override
    public synchronized void onSocketStart(SocketThread t, Socket s) {
        putLog("Client connected");
        log.info("client connected");
    }

    @Override
    public synchronized void onSocketStop(SocketThread t) {
        ClientThread client = (ClientThread) t;
        clients.remove(client);
        if (client.isAuthorized() && !client.isReconnecting()) {
            sendToAllAuthorized(Messages.getTypeBroadcast("Server", client.getNickname() + " disconnected"));
        }
        sendToAllAuthorized(Messages.getUserList(getUsers()));
    }

    @Override
    public synchronized void onSocketReady(SocketThread t, Socket socket) {
        putLog("client is ready");
        log.info("client is ready");
        clients.add(t);
    }

    @Override
    public synchronized void onReceiveString(SocketThread t, Socket s, String msg) {
        ClientThread client = (ClientThread) t;
        if (client.isAuthorized()) {
            handleAuthMsg(client, msg);
        } else {
            handleNonAuthMsg(client, msg);
        }
    }

    private void handleAuthMsg(ClientThread client, String msg) {
        String[] arr = msg.split(Messages.DELIMITER);
        String msgType = arr[0];
        switch (msgType) {
            case Messages.USER_BROADCAST:
                sendToAllAuthorized(Messages.getTypeBroadcast(client.getNickname(), arr[1]));
                break;
            default:
                client.msgFormatError(msg);
        }
    }

    private void sendUserAuthorized(String nickname, String msg) {
        findClientByNickname(nickname).sendMessage(msg);
    }

    private void sendToAllAuthorized(String msg) {
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            client.sendMessage(msg);
        }
    }

    private void registrationMsg (ClientThread client, String msg) {
        String[] arrReg = msg.split(Messages.DELIMITER);
        if (arrReg.length != 4 || !arrReg[0].equals(Messages.USER_REGISTRATION)) {
            client.msgFormatError(msg);
            return;
        }
        String login = arrReg[1];
        String password = arrReg[2];
        String nickname = arrReg[3];
        SqlClient.registration(arrReg[1], arrReg[2], arrReg[3]);
        putLog("Registered as " + login);
    }

    private void handleNonAuthMsg(ClientThread client, String msg) {
        String[] arr = msg.split(Messages.DELIMITER);
        if (arr.length != 3 || !arr[0].equals(Messages.AUTH_REQUEST)) {
            client.msgFormatError(msg);
            return;
        }
        String login = arr[1];
        String password = arr[2];
        String nickname = SqlClient.getNick(login, password);
        if (nickname == null) {
            putLog("Invalid login attempt " + login);
            client.authFail();
            return;
        } else {
            ClientThread oldClient = findClientByNickname(nickname);
            client.authAccept(nickname);
            if (oldClient == null){
                sendToAllAuthorized(Messages.getTypeBroadcast("Server", nickname + " connected."));
            } else {
                oldClient.reconnect();
                clients.remove(oldClient);
            }
        }
        sendToAllAuthorized(Messages.getUserList(getUsers()));
    }

    @Override
    public synchronized void onSocketException(SocketThread t, Throwable e) {
        e.printStackTrace();
        log.debug("SocketException");
    }

    private String getUsers() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            sb.append(client.getNickname()).append(Messages.DELIMITER);
        }
        return sb.toString();
    }

    private synchronized ClientThread findClientByNickname(String nickname) {
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            if (client.getNickname().equals(nickname))
                return client;
        }
        return null;
    }
}

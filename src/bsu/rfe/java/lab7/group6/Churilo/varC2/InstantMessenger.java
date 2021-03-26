package bsu.rfe.java.lab7.group6.Churilo.varC2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.LinkedList;

public class InstantMessenger {
    private User owner;
    private int serverPort;
    private LinkedList<MessageListener> listeners = new LinkedList<MessageListener>();
    private LinkedList<User> users = new LinkedList<User>();
    private LinkedList<UserListener> userListeners = new LinkedList<UserListener>();

    public InstantMessenger(User owner){
        serverPort = owner.getAddress().getPort();
        this.owner = owner;
        startServer();
    }

    private void startServer(){
        new Thread(new Runnable() {
            public void run() {
                try{
                    ServerSocket serverSocket = new ServerSocket(serverPort);

                    while (!Thread.interrupted()){
                        Socket socket = serverSocket.accept();
                        DataInputStream in = new DataInputStream(socket.getInputStream());

                        String messageCode = in.readUTF();
                        System.out.println(messageCode);
                        if(messageCode.equals("0001")) {
                            addNewFriend(in);
                        }
                        else if(messageCode.equals("0002")){
                            String IP = in.readUTF();
                            String port = in.readUTF();
                            String name = in.readUTF();

                            User newUser = new User(name, new InetSocketAddress(IP, Integer.parseInt(port)));
                            synchronized (users) {
                                if(findUser(newUser) == null) {
                                    users.add(newUser);
                                    notifyAddUserListeners(newUser);
                                }
                            }
                        }
                        else if(messageCode.equals("0042")){
                            String IP = in.readUTF();
                            String port = in.readUTF();
                            String name = in.readUTF();
                            String message = in.readUTF();

                            User sender;
                            synchronized (users) {
                                sender = findUser(new User(name, new InetSocketAddress(IP, Integer.parseInt(port))));
                                if (sender == null) {
                                    sender = new User(name, new InetSocketAddress(IP, Integer.parseInt(port)));
                                    users.add(sender);
                                    notifyAddUserListeners(sender);
                                }
                            }

                            message = name + ": " + message + "\n";
                            sender.addMessageToHistory(message);
                            notifyMessageListeners(sender, message);
                        }
                        socket.close();
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Не удалось подключиться к порту");
                }
            }
        }).start();
    }

    public void sendFriendRequest(String port) throws UnknownHostException, IOException, NumberFormatException{
        int portInt = Integer.parseInt(port);
        if(portInt == serverPort) return;

        Socket socket = new Socket("127.0.0.1", portInt);

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("0001");
        out.writeUTF(owner.getAddress().getAddress().getHostAddress());
        out.writeUTF(String.valueOf(serverPort));
        out.writeUTF(owner.getName());

        socket.close();
    }

    private synchronized void addNewFriend(DataInputStream in) throws IOException{
        String IP = in.readUTF();
        String port = in.readUTF();
        String name = in.readUTF();

        User newUser = new User(name, new InetSocketAddress(IP, Integer.parseInt(port)));
        try{
            Socket socket = new Socket(IP, Integer.parseInt(port));

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("0002");
            out.writeUTF(owner.getAddress().getAddress().getHostAddress());
            out.writeUTF(String.valueOf(serverPort));
            out.writeUTF(owner.getName());

            socket.close();

            synchronized (users) {
                if(findUser(newUser) == null) {
                    users.add(newUser);
                    notifyAddUserListeners(newUser);
                }
            }
        }
        catch(UnknownHostException ex){
            System.out.println("connectError");
        }
        catch (IOException ex){
            System.out.println("IOError");
        }
    }

    public synchronized void sendMessage(User recipient, String message) throws UnknownHostException, IOException {
        Socket socket = new Socket(recipient.getAddress().getAddress().getHostAddress(),recipient.getAddress().getPort());

        recipient.addMessageToHistory("Я: " + message + "\n");
        notifyMessageListeners(recipient, "Я: " + message + "\n");

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("0042");
        out.writeUTF(owner.getAddress().getAddress().getHostAddress());
        out.writeUTF(String.valueOf(serverPort));
        out.writeUTF(owner.getName());
        out.writeUTF(message);

        socket.close();
    }

    private synchronized User findUser(User userC) {
        for (User user : users) {
            if (userC.equals(user))
                return user;
        }
        return null;
    }


    public void addMessageListener(MessageListener listener){
        synchronized (listeners){
            listeners.add(listener);
        }
    }

    public void removeMessageListener(MessageListener listener){
        synchronized (listeners){
             listeners.remove(listener);
        }
    }

    private void notifyMessageListeners(User sender, String message){
        synchronized (listeners){
            for (MessageListener listener : listeners)
                listener.messageReceived(sender,message);
        }
    }

    public void addUserListener(UserListener listener){
        synchronized (userListeners){
            userListeners.add(listener);
        }
    }

    public void removeUserListener(UserListener listener){
        synchronized (userListeners){
            userListeners.remove(listener);
        }
    }

    private void notifyAddUserListeners(User newUser){
        synchronized (userListeners){
            for (UserListener listener : userListeners)
                listener.addedNewUser(newUser);
        }
    }
}

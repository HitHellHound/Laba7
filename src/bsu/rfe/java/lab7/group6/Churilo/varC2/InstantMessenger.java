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

    public void sendMessage(String senderName, String destinationAddress, String message) throws UnknownHostException, IOException {
        Socket socket = new Socket("127.0.0.1", Integer.parseInt(destinationAddress));

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(senderName);
        out.writeUTF("127.0.0.1" + String.valueOf(serverPort));
        out.writeUTF(message);

        socket.close();
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

                            User newUser = new User(name,new InetSocketAddress(IP, Integer.parseInt(port)));
                            synchronized (users) {
                                if(!isUserInList(newUser)) {
                                    users.add(newUser);
                                    notifyAddUserListeners(newUser);
                                }
                            }
                        }
                        /*String senderName = in.readUTF();
                        String senderPort = in.readUTF();
                        String message = in.readUTF();


                        String address = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress();
                        notifyMessageListeners(senderName + " (" + address + ":" + senderPort + ")", message);
                        */
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
        Socket socket = new Socket("127.0.0.1", Integer.parseInt(port));

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("0001");
        out.writeUTF("127.0.0.1");
        out.writeUTF(String.valueOf(serverPort));
        out.writeUTF(owner.getName());

        socket.close();
    }

    private synchronized void addNewFriend(DataInputStream in) throws IOException{
        String IP = in.readUTF();
        String port = in.readUTF();
        String name = in.readUTF();

        User newUser = new User(name,new InetSocketAddress(IP, Integer.parseInt(port)));
        try{
            Socket socket = new Socket(IP, Integer.parseInt(port));

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("0002");
            out.writeUTF("127.0.0.1");
            out.writeUTF(String.valueOf(serverPort));
            out.writeUTF(owner.getName());

            socket.close();
            synchronized (users) {
                if(!isUserInList(newUser)) {
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

    private boolean isUserInList(User userC) {
        for (User user : users) {
            if (userC.equals(user))
                return true;
        }
        return false;
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

    private void notifyMessageListeners(String sender, String message){
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

package bsu.rfe.java.lab7.group6.Churilo.varC2;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

public class InstantMessenger {
    private int serverPort;
    private LinkedList<MessageListener> listeners = new LinkedList<MessageListener>();

    public InstantMessenger(int port){
        serverPort = port;
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

                        String senderName = in.readUTF();
                        String senderPort = in.readUTF();
                        String message = in.readUTF();

                        socket.close();

                        String address = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress();
                        notifyListeners(senderName + " (" + address + ":" + senderPort + ")", message);
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Не удалось подключиться к порту");
                }
            }
        }).start();
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

    private void notifyListeners(String sender, String message){
        synchronized (listeners){
            for (MessageListener listener : listeners)
                listener.messageReceived(sender,message);
        }
    }
}

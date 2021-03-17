package bsu.rfe.java.lab7.group6.Churilo.varC2;

import java.awt.*;
import java.io.IOException;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

public class InstantMessenger {
    private int serverPort = 0;
    private LinkedList<MessageListener> listeners = new LinkedList<MessageListener>();

    public InstantMessenger(){
        serverPort = 0;
    }

    public void sendMessage(String senderName, String destinationAddress, String message) throws UnknownHostException, IOException {

    }

    public int startServer(int port){
        if (serverPort != 0)
            return -1;
        else
            return 1;
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

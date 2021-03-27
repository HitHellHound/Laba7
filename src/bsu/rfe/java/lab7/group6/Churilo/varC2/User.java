package bsu.rfe.java.lab7.group6.Churilo.varC2;

import java.net.InetSocketAddress;

public class User {
    private String name;
    private boolean onlineStatus;
    private InetSocketAddress address;
    private StringBuffer messageHistory = new StringBuffer();

    public User(String name, InetSocketAddress address){
        this.name = name;
        this.address = address;
        onlineStatus = true;
    }

    public String getName(){
        return name;
    }

    public InetSocketAddress getAddress(){
        return address;
    }

    private void setNewName(String name){
        this.name = name;
    }

    public boolean isOnline(){
        return onlineStatus;
    }

    public synchronized void setOnlineStatus(boolean status){
        this.onlineStatus = status;
    }

    public StringBuffer getMessageHistory(){
        return messageHistory;
    }

    public void addMessageToHistory(String message){
        messageHistory.append(message);
    }

    public boolean equals(User user){
        if(this.address.getAddress().getHostAddress().equals(user.address.getAddress().getHostAddress()) && this.address.getPort() == user.address.getPort())
            return true;
        return false;
    }
}

package bsu.rfe.java.lab7.group6.Churilo.varC2;

import java.net.InetSocketAddress;

public class Peer {
    String name;
    InetSocketAddress address;

    public Peer(String name, InetSocketAddress address){
        this.name = name;
        this.address = address;
    }

    public String getName(){
        return name;
    }

    public InetSocketAddress getAddress(){
        return address;
    }

    public void setName(String name){
        this.name = name;
    }
}

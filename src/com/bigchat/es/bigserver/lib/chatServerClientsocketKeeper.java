/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigserver.lib;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * The class chatServerClientsocketKeeper keeps a reference of each one of the
 * Socket objects that has been associated with a connected client after the
 * ServerSocket calls the accept() method. The reference is stored in a HashMap
 * object. The class has some synchronized methods to make sure that only one
 * Thread may access the object at a time in order to avoid concurrency problems.
 * 
 * @author aorozco bigfito@gmail.com
 * @version 1.0
 */
public class chatServerClientsocketKeeper {
    
    //MEMBER ATTRIBUTES DEFINITION
    
    /**
     * HashMap object for storing a reference of the chat client's Socket.
     */
    private Map<Integer,Socket> clientSocketVault;
    
    /**
     * Default constructor for the chatServerClientsocketKeeper class.
     */
    public chatServerClientsocketKeeper(){
        this.clientSocketVault = new HashMap<>();
    }
    
    /**
     * The addClientsocketToVault method add a connected client's Socket reference
     * to the HashMap.
     * @param key An integer representing the value of the key from the key-value
     * pair to insert into the HashMap.
     * @param socket A socket representing the connected client's socket.
     */
    public synchronized void addClientsocketToVault(int key, Socket socket){
        this.clientSocketVault.put(key, socket);
    }
    
    /**
     * The removeClientsocketFromVault method removes a connected client's Socket
     * reference from the HashMap.
     * @param key An integer representing the value of the key from the key-value
     * pair to remove from the HashMap.
     * @throws IOException
     */
    public synchronized void removeClientsocketFromVault(int key) throws IOException{
        Socket s;
        s = getClientsocketFromVault(key);
        if ( s != null && s.isConnected() ) s.close();
        this.clientSocketVault.remove(key);
    }
    
    /**
     * The getClientsocketFromVault method returns a reference to a connnected
     * client's Socket stored in the HashMap.
     * @param key An integer representing the value of the key from the key-value
     * pair to retrieve from the HashMap.
     * @return A Socket object representing the reference to the connected client's
     * Socket.
     */
    public synchronized Socket getClientsocketFromVault(int key){
        return this.clientSocketVault.get(key);
    }
    
    /**
     * The closeClientsocketsFromVault method closes all sockets stored in the HashMap.
     * @throws IOException If a problem occurs when closing the socket.
     */
    public synchronized void closeClientsocketsFromVault() throws IOException{
        Socket s;        
        
        for ( Map.Entry me : this.clientSocketVault.entrySet() ){
            s = this.getClientsocketFromVault( new Integer( me.getKey().toString() ) );
            if ( s != null && s.isConnected() ) s.close();
        }
    }
    
    /**
     * The clearClientSocketVault method clears the content of the HashMap object.
     */
    public synchronized void clearClientSocketVault(){
        this.clientSocketVault.clear();
    }
    
}
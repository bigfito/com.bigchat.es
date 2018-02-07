/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigserver.lib;

import java.io.*;
import java.util.*;

import com.bigchat.es.lib.chatEnvelope;

/**
 * The class chatServerBroadcaster keeps a reference of each one of the
 * OutputStream objects that has been associated with a connected client after the
 * Socket getOutpuStream method call. The reference is stored in a HashMap
 * object. The class has some synchronized methods to make sure that only one
 * Thread may access the object at a time in order to avoid concurrency problems.
 * 
 * @author aorozco bigfito@gmail.com
 * @version 1.0
 */
public class chatServerBroadcaster {
    
    //MEMBER ATTRIBUTES DEFINITION
    
    /**
     * HashMap object for storing a reference of the chat client's OutputStream.
     */
    private Map<Integer,ObjectOutputStream> broadcastList;
    
    /**
     * Default constructor for chatServerBroadcaster class.
     */
    public chatServerBroadcaster(){
        this.broadcastList = new HashMap<>();
    }

    /**
     * The setBroadcastList method sets a value reference for an instance of a
     * chatServerBroadcaster class.
     * @param broadcastList A {@link chatServerBroadcaster} type of object.
     */
    public void setBroadcastList(Map<Integer, ObjectOutputStream> broadcastList) {
        this.broadcastList = broadcastList;
    }

    /**
     * The getBroadcastList gets a value reference for an instance of a
     * chatServerBroadcaster class.
     * @return  A {@link chatServerBroadcaster} type of object.
     */
    public Map<Integer, ObjectOutputStream> getBroadcastList() {
        return this.broadcastList;
    }
    
    /**
     * The getBroadcastListSize method returns the size of the HashMap object
     * containing the references to the ObjectOutputStreams from all connected
     * clients.
     * @return An integer representing the number of references stored in the HashMap.
     */
    public synchronized int getBroadcastListSize() {
        return this.broadcastList.size();
    }    
    
    /**
     * The addObjectOutputStreamToBroadcastList method adds a reference to an 
     * ObjectOutputStream from a connected chat client's original ObjectOutputStream
     * to the HashMap.
     * @param id An integer representing the value of the key from the key-value pair
     * to add to the HashMap.
     * @param oos An ObjectOutputStream reference to the connected client's original
     * ObjectOutputStream.
     */
    public synchronized void addObjectOutputStreamToBroadcastList(int id, ObjectOutputStream oos){
        this.broadcastList.put(id, oos);
    }

    /**
     * The removeObjectOutputStreamFromBroadcastList method removes a reference to
     * an OjectOutputStream object stored in the HashMap.
     * @param id An integer representing the value of the key from the key-value pair
     * to remove from the HashMap.
     * @throws IOException If a problem occurs when closing the ObjectOutputStream.
     */
    public synchronized void removeObjectOutputStreamFromBroadcastList(int id) throws IOException {
        ObjectOutputStream oos;                
        oos = getObjectOutputStreamFromBroadcastList(id);
        if ( oos != null ) oos.close();        
        this.broadcastList.remove(id);
    }
    
    /**
     * The getObjectOutputStreamFromBroadcastList method returns a reference to an
     * ObjectOutputStream from a connected chat client which is stored in the HashMap.
     * @param id An integer representing the value of the key from the key-value pair
     * to retrieve from the HashMap.
     * @return A reference to an ObjectOutputStream from the HashMap.
     */
    public synchronized ObjectOutputStream getObjectOutputStreamFromBroadcastList(int id){
        return this.broadcastList.get(id);
    }    
    
    /**
     * The broadcastMessage method sends out a message through each one of the 
     * ObjectOutputStream references in the HashMap set.
     * @param ce A {@link chatEnvelope} type of message to broadcast through all
     * ObjectOutputStreams in the HashMap set.
     * @throws IOException If a problem occurs when writing to the ObjectOutputStream
     * object.
     */
    public synchronized void broadcastMessage(chatEnvelope ce) throws IOException{
        
        ObjectOutputStream oos;        
        for ( Map.Entry me : this.broadcastList.entrySet() ) {            
            oos = this.getObjectOutputStreamFromBroadcastList( new Integer( me.getKey().toString() ) );           
            oos.writeObject(ce);
            oos.flush();
            oos.reset();            
        }        
        
    }
    
    /**
     * The closeOutputStreamsFromPooler method closes all ObjectOutputStream references
     * stored in the HashMap.
     * @throws IOException If a problem occurs when closing the ObjectOutputStream.
     */
    public synchronized void closeOutputStreamsFromPooler() throws IOException{
        
        ObjectOutputStream oos;        
        for ( Map.Entry me : this.broadcastList.entrySet() ) {            
            oos = this.getObjectOutputStreamFromBroadcastList( new Integer( me.getKey().toString() ) );                       
            oos.close();
        }        
        
    }    
    
    /**
     * The clearBroadcastPooler method clears the content of the HashMap object.
     */
    public synchronized void clearBroadcastPooler(){
        this.broadcastList.clear();
    }
    
}

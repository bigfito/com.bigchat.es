/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.lib;

/**
 * The class chatEnvelope implements a data structure representing a message
 * being exchanged between server and clients of the chat application and the
 * other way around.  The class is Serializable so an instance of it can be sent 
 * over stream channels to ease reading/writing operations.
 * 
 * @author aorozco bigfito@gmail.com
 * @version 1.0
 */
public class chatEnvelope implements java.io.Serializable{
    
    // CONTROL HEADER DEFINITIONS

    /**
     * A header of this type means it is an invalid control message.
     */    
    public static final int INVALID_MSG = 0;
    
    /**
     * A header of this type means it is a connecting message coming from a chat
     * client.
     */    
    public static final int CONNECT_MSG = 1;    
    
    /**
     * A header of this type means the chat server will acknowledge the connetion
     * attempt from the chat client.
     */        
    public static final int CONNECT_MSG_ACK = 10;

    /**
     * A header of this type means the username is already being used by another
     * chat client user.
     */      
    public static final int CONNECT_MSG_NACK = 100;

    /**
     * A header of this type means a chat client user is willing to disconnect from
     * the chat server.
     */      
    public static final int DISCONNECT_MSG = 2;

    /**
     * A header of this type means the chat server will acknowledge disconnection
     * message from the chat client user.
     */      
    public static final int DISCONNECT_MSG_ACK = 20;
    
    /**
     * A header of this type means it is a regular chat message from a chat client
     * and it must be sent to all connected users.
     */      
    public static final int CHAT_MSG = 3;

    /**
     * A header of this type means the chat server is sending a message to all connected
     * chat client users.
     */      
    public static final int SERVERBROADCAST_MSG = 4;

    /**
     * A header of this type means the chat client user is the first one to connect
     * to the chat server.
     */  
    public static final int HOMEALONE_MSG = 5;

    /**
     * A header of this type means that a request for updating the list of users
     * has been sent.
     */  
    public static final int UPDTLISTOFUSERS_MSG = 6;

    /**
     * A header of this type means a new client user has joined the chat room.
     */      
    public static final int JOINING_MSG = 7;

    /**
     * A header of this type means the chat server is going down.
     */  
    public static final int SERVERGOINGDOWN_MSG = 8;

    /**
     * A header of this type means the chat client acknowledges the server going
     * down message.
     */  
    public static final int SERVERGOINGDOWN_MSG_ACK = 80;

    /**
     * A header of this type means a chat client user has abandoned the chat room.
     */  
    public static final int ABANDON_MSG = 9;
    
    //MEMBER ATTRIBUTES

    /**
     * An integer representing the control header of the chat envelope.
     */
    private int envHeader;

    /**
     * A string representing the sender of the chat envelope.
     */
    private String envFrom;

    /**
     * A string representing the body of the chat envelope.
     */
    private String envBody;

    /**
     * A {@link chatUsersList} type of object having a list of users.
     */
    private chatUsersList envList;
    
    /**
     * Default constructor method for class chatEnvelope.
     * @param eHeader A control header for the envelope.
     * @param eFrom An envelope sender.
     * @param eBody The body of the envelope message.
     * @param eList A list of users.
     */
    public chatEnvelope(int eHeader, String eFrom, String eBody, chatUsersList eList){
        this.envHeader = eHeader;
        this.envFrom = eFrom;
        this.envBody = eBody;
        this.envList = eList;
    }
    
    /**
     * Another constructor for the chatEnvelope class.  It takes no parameters and
     * sets all its member attributes to its default values.
     */
    public chatEnvelope(){
        this.envHeader = 0;
        this.envFrom = "";
        this.envBody = "";
        this.envList = null;
    }    

    /**
     * The setEnvHeader method sets the value of the control header member attribute
     * for the chat envelope.
     * @param envHeader An integer representing a control message header for the
     * chat envelope.
     */
    public void setEnvHeader(int envHeader) {
        this.envHeader = envHeader;
    }

    /**
     * The setEnvFrom method sets the value of the sender member attribute of 
     * the chat envelope.
     * @param envFrom A string representing the sender for the chat envelope.
     */
    public void setEnvFrom(String envFrom) {
        this.envFrom = envFrom;
    }    

    /**
     * The setEnvBody method sets the value of the body member attribute of the 
     * chat envelope.
     * @param envBody A string representing the body for the chat envelope.
     */
    public void setEnvBody(String envBody) {
        this.envBody = envBody;
    }
    
    /**
     * The setEnvList method sets the value of the list of users member attribute 
     * of the chat envelope.
     * @param envList A {@link chatUsersList} object representing a list of users
     * for the chat envelope.
     */
    public synchronized void setEnvList(chatUsersList envList){
        this.envList = envList;
    }

    /**
     * The getEnvHeader method returns the value of the control message header
     * of the chat envelope.
     * @return An integer representing a control header message of the chat
     * envelope.
     */
    public int getEnvHeader() {
        return envHeader;
    }

    /**
     * The getEnvFrom method returns the value of the sender of the chat envelope.
     * @return A string representing the sender of the chat envelope.
     */
    public String getEnvFrom() {
        return envFrom;
    }

    /**
     * The getEnvBody method returns the value of the body of the chat envelope.
     * @return A string representing the body of the chat envelope.
     */
    public String getEnvBody() {
        return envBody;
    }
    
    /**
     * The getEnvUsersList return the list of users set in the chat envelope.
     * @return A {@link chatUsersList} object representing a list of users of the
     * chat envelope.
     */
    public synchronized chatUsersList getEnvUsersList(){
        return envList;
    }
    
    /**
     * The resetEnvelope method resets the value of the member attributes of the
     * chat envelope to its default values.
     */
    public void resetEnvelope(){
        this.setEnvHeader(0);
        this.setEnvFrom("");
        this.setEnvBody("");
        this.envList = null;
    }
    
}

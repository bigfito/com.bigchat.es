/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigserver.lib;

import java.io.*;
import java.net.*;
import javax.swing.*;

import com.bigchat.es.lib.*;

/**
 * The class chatServerClientThread handles all the communication between the
 * chat server and the connected client. This class is instantiated for each
 * connected client by the {@link chatServerOrchestator}. The class extends from
 * Thread in order to implement concurrency.
 * 
 * @author aorozco bigfito@gmail.com
 * @version 1.0
 */
public class chatServerClientThread extends Thread {
    
    //MEMBER ATTRIBUTES DEFINITION
    
    /**
     * A boolean switch used for the infinite loop.
     */
    private boolean looping;

    /**
     * A socket to reference the connected client's socket connection.
     */
    private Socket clientThreadSocket;

    /**
     * An ObjectInputStream to reference the original from the connected client's
     * socket.
     */
    private ObjectInputStream serverInput;
    
    /**
     * An ObjectOutputStream to reference the original from the connected client's
     * socket.
     */
    private ObjectOutputStream serverOutput;
    
    /**
     * A JTextArea object to append messages to the chat server's notification
     * area in the GUI.
     */
    private JTextArea txtNotifArea;
    
    /**
     * A JLabel object to update the number of connections from the chat server's
     * GUI.
     */
    private JLabel lblnumberOfConnections;
    
    /**
     * A {@link chatEnvelope} type of message to exchange between the server and
     * the connected client (vice-versa).
     */
    private chatEnvelope chatMessageS;
    
    /**
     * A {@link chatUsersList} reference to the original object from
     * the chat server instance.
     */
    private chatUsersList chatUsersListServerThread;
    
    /**
     * A {@link chatServerBroadcaster} reference to the original object from
     * the chat server instance.
     */
    private chatServerBroadcaster cserverBroadcasterThread;
    
    /**
     * A {@link chatServerClientsocketKeeper} reference to the original object from
     * the chat server instance.
     */
    private chatServerClientsocketKeeper csClientsocketKeeperThread;        
    
    /**
     * Default constructor for chatServerClientThread class.
     * @param clientThreadSocket A reference to the connected client's socket.
     * @param chatUsersListServerThread A {@link chatUsersList} object reference.
     * @param cserverBroadcaster A {@link chatServerBroadcaster} object reference.
     * @param csClientsocketKeeperThread A {@link chatServerClientsocketKeeper} object reference.
     * @param txtNotifArea A JTextArea object reference.
     * @param lblnumberOfConnections A JLabel object reference.
     */
    public chatServerClientThread(Socket clientThreadSocket,
                                  chatUsersList chatUsersListServerThread,
                                  chatServerBroadcaster cserverBroadcaster,
                                  chatServerClientsocketKeeper csClientsocketKeeperThread,
                                  JTextArea txtNotifArea,
                                  JLabel lblnumberOfConnections){
        
        this.looping = true;
        
        this.clientThreadSocket = clientThreadSocket;
        
        this.chatUsersListServerThread = chatUsersListServerThread;
        this.cserverBroadcasterThread = cserverBroadcaster;
        this.csClientsocketKeeperThread = csClientsocketKeeperThread;
        
        this.lblnumberOfConnections = lblnumberOfConnections;
        this.txtNotifArea = txtNotifArea;        
        
    }
    
    /**
     * The initiateStreams() method initializes the input/output streams linked to the
     * connected client's socket.  It also adds a reference of the ObjectOutputStream
     * into the {@link chatServerBroadcaster} object.  Finally, it adds a reference of
     * the connected client's socket into the {@link chatServerClientsocketKeeper} object.
     * @throws IOException If a problem occurs when retrieving the Input/Output streams
     * from the client's socket reference.
     */
    public void initiateStreams() throws IOException{
        
        this.serverOutput = new ObjectOutputStream( clientThreadSocket.getOutputStream() );
        this.serverInput = new ObjectInputStream( clientThreadSocket.getInputStream() );
        
        //ADD A REFERENCE OF THE OUTPUT STREAM IN THE BROADCAST POOLER
        cserverBroadcasterThread.addObjectOutputStreamToBroadcastList( (int)this.getId(),
                                                                        serverOutput );
                    
        //ADD A REFERENCE OF THE INCOMING CLIENT SOCKET IN THE VAULT
        csClientsocketKeeperThread.addClientsocketToVault( (int)this.getId() , 
                                                                    clientThreadSocket);        
    }

    /** 
     * The overloaded run() method implements the logic that the server chat
     * client thread handler will be doing durint its execution on an infinite loop.
     */    
    public void run(){
        
        String envelopeSender = "";
        
        try{
            
            //INITIATE STREAMS BEFORE STARTING READING MESSAGES FROM CLIENT
            initiateStreams();
            
            while ( looping ){

                //START READING MESSAGES FROM INCOMING CLIENT
                chatMessageS = new chatEnvelope();
                chatMessageS = (chatEnvelope)serverInput.readObject();
                envelopeSender = chatMessageS.getEnvFrom();
                
                switch ( chatMessageS.getEnvHeader() ){                    
                    
                    case chatEnvelope.CONNECT_MSG:
                        
                        //CONNECTING MESSAGE
                        writeToNotificationArea("USER " + chatMessageS.getEnvFrom() + " WANTS TO CONNECT.\n");                        
                        
                        //CHECK IF USERNAME IS NOT ALREADY IN USE
                        if ( !chatUsersListServerThread.usernameInUserslist(envelopeSender) ){
                        
                            //ACK TO CLIENT
                            chatMessageS.setEnvHeader(10);
                            chatMessageS.setEnvFrom("SERVER");
                            chatMessageS.setEnvBody("ACK");
                            serverOutput.writeObject(chatMessageS);
                            serverOutput.flush();
                            serverOutput.reset();

                            //ADD CLIENT TO LIST OF CONNECTED USERS
                            chatUsersListServerThread.addUserToListOfUsers((int)this.getId(), envelopeSender);
                            
                            //UPDATE NUMBER OF CONNECTED CLIENTS IN SERVER GUI
                            updateLabelConnections();

                            //BROADCAST UPDATED LIST OF USERS TO ALL CONNECTED CLIENTS
                            chatMessageS.setEnvHeader(6);
                            chatMessageS.setEnvFrom("SERVER");
                            chatMessageS.setEnvBody("USERSLIST");                        
                            chatMessageS.setEnvList(chatUsersListServerThread);
                            cserverBroadcasterThread.broadcastMessage(chatMessageS);

                            //IF ONLY ONE USER CONNECTED SEND WAIT MESSAGE
                            if ( chatUsersListServerThread.getListOfUsersSize() == 1 ){                                

                                chatMessageS.setEnvHeader(5);
                                chatMessageS.setEnvFrom("SERVER");
                                chatMessageS.setEnvBody("WAIT");
                                serverOutput.writeObject(chatMessageS);
                                serverOutput.flush();
                                serverOutput.reset();

                            }
                            
                            writeToNotificationArea("USER " + envelopeSender + " IS READY TO CHAT.\n");

                            //SEND BROADCAST ANNOUNCING CLIENT HAS JOINED THE CHAT
                            chatMessageS.setEnvHeader(7);
                            chatMessageS.setEnvFrom("SERVER");
                            chatMessageS.setEnvBody(envelopeSender);
                            cserverBroadcasterThread.broadcastMessage(chatMessageS);
                        
                        }else{                            
                                
                            //NACK TO CLIENT USERNAME IN USE
                            chatMessageS.setEnvHeader(100);
                            chatMessageS.setEnvFrom("SERVER");
                            chatMessageS.setEnvBody("NACK");
                            serverOutput.writeObject(chatMessageS);
                            serverOutput.flush();
                            serverOutput.reset();                            
                                
                        }
                        
                        break;
                        
                    case chatEnvelope.DISCONNECT_MSG:
                        
                        //DISCONNECTING MESSAGE
                        writeToNotificationArea("USER " + envelopeSender + " WANTS TO DISCONNECT.\n");                        
                        
                        //SEND DISCONNECT ACK BACK TO CLIENT
                        chatMessageS.setEnvHeader(20);
                        chatMessageS.setEnvFrom("SERVER");
                        chatMessageS.setEnvBody("ACK");
                        serverOutput.writeObject(chatMessageS);
                        serverOutput.flush();
                        serverOutput.reset();                        
                        
                        //REMOVE USER FROM LIST OF USERS
                        chatUsersListServerThread.removeUserFromListOfUsers( (int)this.getId() );

                        //REMOVE REFERENCE TO THREAD OUTPUTSTREAM FROM BROADCAST LIST
                        cserverBroadcasterThread.removeObjectOutputStreamFromBroadcastList( (int)this.getId() );                       
                        
                        //BROADCAST UPDATED LIST OF USERS TO ALL CONNECTED CLIENTS (IF ANY)
                        chatMessageS.setEnvHeader(6);
                        chatMessageS.setEnvFrom("SERVER");
                        chatMessageS.setEnvBody("USERSLIST");                        
                        chatMessageS.setEnvList(chatUsersListServerThread);
                        cserverBroadcasterThread.broadcastMessage(chatMessageS);                                              

                        //SEND BROADCAST ANNOUNCING CLIENT HAS ABANDONED THE CHAT
                        //BROADCAST UPDATED LIST OF USERS TO REMAINING CLIENTS (IF ANY)
                        chatMessageS.setEnvHeader(9);
                        chatMessageS.setEnvFrom("SERVER");
                        chatMessageS.setEnvBody(envelopeSender);
                        cserverBroadcasterThread.broadcastMessage(chatMessageS); 
                        
                        //UPDATE NUMBER OF CONNECTED CLIENTS IN SERVER GUI
                        updateLabelConnections();                        
                        
                        //CLOSE CLIENT THREAD COMMUNICATION CHANNELS
                        finalizeThreadCommunication();
                        
                        //REMOVE THE REFERENCE OF THE INCOMING CLIENT SOCKET FROM THE VAULT
                        csClientsocketKeeperThread.removeClientsocketFromVault( (int)this.getId() );                        
                        
                        //UPDATAE MESSAGE ON SERVER NOTIFICATION AREA
                        writeToNotificationArea("USER " + envelopeSender + " DISCONNECTED WITH SUCCESS.\n");

                        //STOP LOOPING
                        looping = false;                        
                        break;
                        
                    case chatEnvelope.CHAT_MSG:
                        
                        //CHAT MESSAGE FROM CLIENT USER (SEND TO ALL CONNECTED USERS )                        
                        chatMessageS.setEnvHeader(3);
                        chatMessageS.setEnvFrom(envelopeSender);
                        cserverBroadcasterThread.broadcastMessage(chatMessageS);                        
                        break;
                    
                    case chatEnvelope.SERVERGOINGDOWN_MSG_ACK:
                        
                        //CLOSE CLIENT THREAD COMMUNICATION CHANNELS
                        finalizeThreadCommunication();                        

                        //STOP LOOPING
                        looping = false;                        
                        break;
                        
                    default :
                        break;
                }
                
                //RESET CHAT MESSAGE ENVELOPE
                chatMessageS.resetEnvelope();
                
            }
            
        }catch( ClassNotFoundException cnf ){
            writeToNotificationArea ( cnf.toString() + "\n");
        }catch( IOException ioe ){
            writeToNotificationArea ( ioe.toString() + "\n");
        }
    }
    
    /**
     * The updateLabelConnections method updates the number of connections label
     * on the chat server's GUI.
     */
    public synchronized void updateLabelConnections(){
        int listSize = chatUsersListServerThread.getListOfUsersSize();
        lblnumberOfConnections.setText( Integer.toString( listSize ) );
    }

    /**
     * The writeToNotificationArea method appends a message to the notification
     * area of the chat server's GUI.
     * @param message A string representing the message to write to the notificaation
     * area.
     */
    public void writeToNotificationArea(String message){
        this.txtNotifArea.append(message);
    }          
    
    /**
     * The finalizeThreadCommunication method closes the input/output object streams
     * references attached to the connected client's socket.
     * @throws IOException If a problem occurs when closing the input/output streams.
     */
    public void finalizeThreadCommunication() throws IOException{
        
        if ( this.serverOutput != null ) this.serverOutput.close();
        if ( this.serverInput  != null )  this.serverInput.close();
        
    }
    
}
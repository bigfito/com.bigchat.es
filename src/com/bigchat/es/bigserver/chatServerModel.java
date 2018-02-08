/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigserver;

import java.io.*;
import java.net.*;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.bigchat.es.lib.*;
import com.bigchat.es.bigserver.lib.*;

/**
 * The class chatServerModel implements the logic of the chat server application. 
 * It also implements an inner class named chatServerOrchestator which will be 
 * listening for incoming client connections and creating new Threads to handle
 * the communication between the different chat clients and the chat server.
 * @author aorozco bigfito@gmail.com
 * @version 1.0
 */
public class chatServerModel{
    
    //MEMBER ATTRIBUTES SECTION
          
    /** 
     * A String for storing the server IP address.
     */
    private String cServerAddress;

    /** 
     * A String for storing the listening port on the server.
     */
    private String cServerPort;

    /** 
     * A ServerSocket object to allow the server to listen on the desired port.
     */
    private ServerSocket serverSocket;
    
    /** 
     * A JLabel object reference to update the number of clients on the GUI.
     */
    private JLabel lblNumberOfClients;
    
    /** 
     * A JTextArea object reference to add text to the notification area on the GUI.
     */    
    private JTextArea txtNotificationArea;

    /** 
     * An integer to store the number of incoming client connections.
     */    
    private int numberOfClientConnections;
    
    /** 
     * A boolean switch used for the while loop on the orchestator.
     */    
    private boolean handlerSwitch;    

    /** 
     * A {@link chatEnvelope} object for exchanging messages between server and
     * clients (vice-versa).
     */
    private chatEnvelope msgEnvelope;    

    /** 
     * A {@link chatUsersList} object for keeping a list of the connnected users.
     */
    private chatUsersList chatUsersListServer;

    /** 
     * A {@link chatServerBroadcaster} object for communicating with the connected
     * clients through their ObjectOutputStream channel.
     */
    private chatServerBroadcaster csBroadcaster;

    /** 
     * A {@link chatServerClientsocketKeeper} object used to keep a reference of the
     * Socket where a client is connected to.
     */
    private chatServerClientsocketKeeper csClientsocketKeeper;

    /** 
     * A {@link chatServerOrchestator} object to monitor and handling all incoming
     * client connections.
     */
    private chatServerOrchestator csOrchestator;
    
    /**
     * Default constructor for the class chatServerModel.
     * @param lblNumberOfClients A reference to the JLabel object on the GUI linked to the
     * number of connected clients.
     * @param txtNotificationArea A reference to the JTextArea object on the GUI linked
     * to the server's notification area.
     */
    public chatServerModel( javax.swing.JLabel lblNumberOfClients, 
                            javax.swing.JTextArea txtNotificationArea){
        
        this.serverSocket = null;
        this.lblNumberOfClients = lblNumberOfClients;
        this.txtNotificationArea = txtNotificationArea;        
        this.chatUsersListServer = new chatUsersList();
        this.csClientsocketKeeper = new chatServerClientsocketKeeper();
        this.csBroadcaster = new chatServerBroadcaster();
        this.msgEnvelope = new chatEnvelope();
        this.numberOfClientConnections = 0;
        this.handlerSwitch = false;
        this.csOrchestator = null;        
    }    
    
    /**
     * The startChatServer method starts the chat server application by opening
     * a ServerSocket listening on a determined TCP port and creating a new single
     * instance of an orchestator object.  It takes not parameters.
     */
    public void startChatServer(){
        
        try{            
            //OPEN SERVER CONNECTION
            InetAddress serverIP = InetAddress.getByName( cServerAddress );
            serverSocket = new ServerSocket( Integer.parseInt(cServerPort), 50, serverIP );                           

            //UPDATE MESSAGE ON CHAT SERVER NOTIFICATION AREA
            writeToNotificationArea("SERVER STARTED IN LOCAL NETWORK INTERFACE " + cServerAddress + ".\n");
            writeToNotificationArea("SERVER IS LISTENING ON PORT " + cServerPort + ".\n");
            
            //SET SWITCH FOR ORCHESTATOR
            handlerSwitch = true;

            //CREATE A NEW INSTANCE OF THE ORCHESTATOR            
            csOrchestator = new chatServerOrchestator( serverSocket );
            csOrchestator.start();
            
        }catch(UnknownHostException uhe){
            writeToNotificationArea("[ERROR]: ERROR WHILE RETRIEVING LOCAL IP ADDRESS.\n");
            writeToNotificationArea( uhe.toString() + "\n" );
        }catch(IOException ioe){
            writeToNotificationArea("[ERROR]: ERROR WHILE OPENING LISTENING SERVER SOCKET.\n");
            writeToNotificationArea( ioe.toString() + "\n" );
        }
    }
    
    /**
     * The serverNetworkParametersChecked method makes sure that the desired TCP port
     * is avaliable and not in use by another process in the server machine.
     * @param IPaddress IPv4 address of the server.
     * @param port TCP port of the server.
     * @return true If the listening port is available, otherwise returns false.
     */
    public boolean serverNetworkParametersChecked(String IPaddress, String port){        
        
        cServerAddress = IPaddress;
        cServerPort = port;        
        boolean result = true;

        ServerSocket localSS;        
        try{            
            InetAddress serverIP = InetAddress.getByName( cServerAddress );
            localSS = new ServerSocket( Integer.parseInt(cServerPort), 50, serverIP );
            localSS.close();
        }catch( IOException e ){
            result = false;
            writeToNotificationArea("[ERROR]: SERVER PORT NOT AVAILABLE.\n");
            writeToNotificationArea( e.toString() + "\n");
        }        
        return result;
    }
    
    /**
     * The broadcastMessage method sends out a chat message from the server to all 
     * connected clients.
     * @param type A {@link chatEnvelope} header type of message.
     * @param msgToBroadcast A {@link chatEnvelope} body type of message.
     */
    public void broadcastMessage(int type, String msgToBroadcast){
                
        try{            
            msgEnvelope.setEnvHeader(type);
            msgEnvelope.setEnvFrom("SERVER");
            msgEnvelope.setEnvBody(msgToBroadcast);
            csBroadcaster.broadcastMessage(msgEnvelope);                     
        }catch(IOException ioe){
            writeToNotificationArea("[ERROR]: ERROR SENDING BROADCAST MESSAGE.\n");
            writeToNotificationArea( ioe.toString() + "\n");
        }
    }

    /**
     * The writeToNotificationArea appends a message to the chat server notification
     * area.
     * @param message A String having the message to append.
     */    
    public void writeToNotificationArea(String message){
        this.txtNotificationArea.append(message);
    }
    
    /**
     * The getNumberOfConnectedClients method returns the number of connected clients.
     * @return An integer with the number of connected clients.
     */
    public int getNumberOfConnectedClients(){
        return this.chatUsersListServer.getListOfUsersSize();
    }
    
    /**
     * The stopChatServer method stops the chat server execution.
     */
    public void stopChatServer(){        

        Socket fakeSocket;
        numberOfClientConnections = 0;
        handlerSwitch = false;
        
        try{            

            //CONNECT TO MYSELF TO FORCE server.accept() TO RESUME
            fakeSocket = new Socket( cServerAddress, Integer.valueOf( cServerPort) );
            fakeSocket.close();                        

            //UPDATE LABEL OF CONNECTED CLIENTS
            lblNumberOfClients.setText( Integer.toString( numberOfClientConnections ) );

            //RESET USERS LIST
            chatUsersListServer.clearUsersList();

            //RESET POOLER
            csBroadcaster.closeOutputStreamsFromPooler();
            csBroadcaster.clearBroadcastPooler();

            //RESET CLIENT SOCKET KEEPER
            csClientsocketKeeper.closeClientsocketsFromVault();
            csClientsocketKeeper.clearClientSocketVault();            

            //CLOSE SERVER SOCKET
            serverSocket.close();
            
            //WAIT 2 SECS TO OS TO PROPERLY CLOSE THE SOCKET            
            Thread.sleep(2000);            

        }catch(UnknownHostException uhe){
            writeToNotificationArea("[ERROR]: ERROR CREATING FAKE SOCKET FOR SERVER SHUTDOWN.\n");
            writeToNotificationArea( uhe.toString() + "\n");                            
        }catch(IOException ioe){
            writeToNotificationArea("[ERROR]: ERROR DURING SERVER SOCKET SHUTDOWN.\n");
            writeToNotificationArea( ioe.toString() + "\n");            
        }catch(InterruptedException ie){
            writeToNotificationArea("[ERROR]: ERROR DURING WAITING PERIOD FOR SERVER SHUTDOWN.\n");
            writeToNotificationArea( ie.toString() + "\n");                        
        }
        
    }

    /**
     * The inner class chatServerOrchestator implements the logic of a incoming 
     * connection orchestator. It runs on an infinite loop accepting incoming
     * client conncetions by the accept() method from the ServerSocket class. It
     * then trigger the creation of a {@link chatServerClientThread} object that
     * will keep the communication with the connected client creating a multi-threaded
     * environment.
     * @author aorozco bigfito@gmail.com
     * @version 1.0
     */
    class chatServerOrchestator extends Thread{

        //MEMBER ATTRIBUTES SECTION

        /** 
         * A ServerSocket object reference to the parent ServerSocket.
         */
        private ServerSocket ss;

        /** 
         * A Socket object reference to the incoming client connection.
         */
        private Socket incomingClientSocket;

        /** 
         * Default consturctor for the inner class chatServerOrchestator.
         * @param ss A ServerSocket object having a reference to the parent ServerSocket
         * from the {@link chatServerModel} class.
         */
        public chatServerOrchestator( ServerSocket ss ){            
            this.ss = ss;
            this.incomingClientSocket = null;
        }
        
        /** 
         * The overloaded run() method implements the logic that the server chat
         * orchestator will be doing durint its execution on an infinite loop.
         */
        public void run(){
            
            while( handlerSwitch ){                
                
                try{
                    
                    //UPDATE MESSAGE TO CHAT SERVER NOTIFICATION AREA
                    writeToNotificationArea("WAITING FOR INCOMING CLIENTS TO CONNECT....\n\n");
                                    
                    //WAITING FOR INCOMING CLIENTS TO CONNECT
                    incomingClientSocket = ss.accept();
                    
                    if ( !handlerSwitch ) break;

                    //UPDATE NUMBER OF CONNECTIONS on SERVER GUI
                    lblNumberOfClients.setText( Integer.toString( chatUsersListServer.getListOfUsersSize() ) );                    
                    
                    //UPDATE MESSAGE TO CHAT SERVER NOTIFICATION AREA
                    writeToNotificationArea("CONNECTION ATTEMPT FROM " 
                                            + incomingClientSocket.getRemoteSocketAddress().toString()
                                            + ".\n");                   
                    
                    //CREATE A NEW INSTANCE OF THE CHAT SERVER CLIENT THREAD HANDLER 
                    new chatServerClientThread( incomingClientSocket,
                                                chatUsersListServer,
                                                csBroadcaster,
                                                csClientsocketKeeper,
                                                txtNotificationArea,
                                                lblNumberOfClients).start();
                    
                    //UPDATE NUMBER OF CONNECTIONS
                    numberOfClientConnections = chatUsersListServer.getListOfUsersSize();
                    
                }catch(SocketException se){
                    writeToNotificationArea("[ERROR]: SOCKET EXCEPTION.\n");
                    writeToNotificationArea( se.toString() + "\n");                    
                }catch(IOException ioe){
                    writeToNotificationArea("[ERROR]: ERROR WHILE ACCEPTING CLIENT SOCKET.\n");
                    writeToNotificationArea( ioe.toString() + "\n");
                }                                
            }
            
            //CLOSE TEMPORARY ORCHESTATOR SOCKET
            try{
                if ( incomingClientSocket != null && !incomingClientSocket.isClosed() ) 
                    incomingClientSocket.close();
                if ( ss != null && !ss.isClosed() ) 
                    ss.close();
            }catch(IOException ioe){
                writeToNotificationArea("[ERROR]: ERROR CLOSING ORCHESTATOR TEMPORARY SOCKETS.\n");
            }
        }        
    
    }
    
}

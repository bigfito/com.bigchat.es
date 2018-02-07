/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigclient;

import com.bigchat.es.bigclient.lib.chatClientThread;
import java.io.*;
import java.net.*;

import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSpinner;

import com.bigchat.es.lib.ipAddressValidator;
import com.bigchat.es.lib.chatEnvelope;

/**
 *
 * @author aorozco
 */
public class chatClientModel{

    /* SERVER DEFAULT PARAMETERS */
    private String strServerHostanme = "localhost";
    private String strServerIPAddress = "127.0.0.1";
    private int strServerPort = 32565;
    
    /* CLIENT PARAMETERS */
    private String strUserName;
    private int sizeUserName;    
    private InetAddress hostMachine;
    private String strHostName;
    private String strIpAddress;
    
    /* WAS STARTED */
    private boolean clientIsConnected;
    
    /* CLIENT MESSAGE ENVELOPE */
    private chatEnvelope msgEnvelope;
    
    /* I/O CLIENT SOCKET STREAMS */
    private Socket clientSocket;
    private ObjectInputStream clientStreamInput;
    private ObjectOutputStream clientStreamOutput;    
    
    /* REFERENCE TO GUI OBJECTS */
    private JTextArea txtChatAreaRef;
    private JTextArea txtNotificationAreaRef;
    private JList txtUserListRef;
    private JButton btnConnRef, btnDisRef, btnSndRef;
    private JLabel lblStatRef;
    private JLabel lblClientHostname;
    private JLabel lblClientIPAddress;
    private JTextField txtChatMessageRef;
    private JTextField txtUsernameRef;
    private JTextField txtServerAddress;
    private JSpinner txtServerPort;
    
    /* REFERENCE TO MASTER MONITORING THREAD */
    private chatClientThread masterThread;
    
    /* IP ADDRESS VALIDATOR OBJECT */
    private ipAddressValidator ipav;

    public chatClientModel( JTextArea txtChatAreaReference, 
                       JTextArea txtNotificationAreaReference, 
                       JList txtUsersListReference,
                       JButton btnConnectReference,
                       JButton btnDisconnectReference,
                       JButton btnSendReference,
                       JLabel lblStatusReference,
                       JTextField txtChatMessageReference,
                       JTextField txtUsernameReference,
                       JTextField txtServerAddress,
                       JSpinner txtServerPort,
                       JLabel lblClientHostname,
                       JLabel lblClientIPAddress) {
        
        this.clientIsConnected = false;
        
        this.strUserName = "";
        this.sizeUserName = 0;
               
        this.txtChatAreaRef = txtChatAreaReference;
        this.txtNotificationAreaRef = txtNotificationAreaReference;
        this.txtUserListRef = txtUsersListReference;
        this.btnConnRef = btnConnectReference;
        this.btnDisRef = btnDisconnectReference;
        this.btnSndRef = btnSendReference;
        this.lblStatRef = lblStatusReference;
        this.txtChatMessageRef = txtChatMessageReference;
        this.txtUsernameRef = txtUsernameReference;
        this.txtServerAddress = txtServerAddress;
        this.txtServerPort = txtServerPort;
        this.lblClientHostname = lblClientHostname;
        this.lblClientIPAddress =lblClientIPAddress;
        
        this.clientStreamInput = null;
        this.clientStreamOutput = null;
        this.ipav = new ipAddressValidator();
    }

    public void clientConnected(boolean value){
        this.clientIsConnected = value;
    }
    
    public boolean isClientConnected(){
        return this.clientIsConnected;
    }
    
    public void setupNetworkFields() throws UnknownHostException{        

        this.hostMachine = InetAddress.getLocalHost();
        this.strIpAddress = hostMachine.getHostAddress();
        this.strHostName = hostMachine.getHostName();
                
        this.lblClientHostname.setText( strHostName );
        this.lblClientIPAddress.setText( strIpAddress );
    }

    public String getStrUserName() {
        return strUserName;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName.toUpperCase();
        this.sizeUserName = strUserName.length();
    }        

    public String getStrHostName() {
        return strHostName;
    }

    public String getStrIpAddress() {
        return strIpAddress;
    }
    
    public int getSizeUserName(){
        return sizeUserName;
    }
    
    public void setSizeUserName(){
        this.sizeUserName = this.strUserName.length();
    }
    
    public void addTxtToNotificationArea(String msg){
        this.txtNotificationAreaRef.append(msg);
    }
    
    public boolean isUsernameValid(){
        
        String pattern;
        pattern = "(\\w{4,15})";
        
        return ( this.strUserName.matches(pattern) ) ? true : false ;
        
    }
    
    public void prepareConnectionEnvelope(){
        
        this.msgEnvelope = new chatEnvelope(1, strUserName, "CONNECT", null);
        
    }
    
    public void prepareDisconnectionEnvelope(){
        
        this.msgEnvelope = new chatEnvelope(2, strUserName, "DISCONNECT", null);
        
    }
    
    public void prepareChatMessageEnvelope(String msgBody){
        
        this.msgEnvelope = new chatEnvelope(3, strUserName, msgBody, null);
        
    }
    
    public boolean serverIPAddressIsValid(String stripaddress){
        return ipav.validateIP(stripaddress);
    }
    
    public void connectToServer() throws SocketException, UnknownHostException, IOException{
            
        this.clientSocket = new Socket( strServerIPAddress, strServerPort );
        this.clientStreamOutput = new ObjectOutputStream( clientSocket.getOutputStream() );
        this.clientConnected(true);
        
    }    
    
    public void sendEnvelopeMessageToServer() throws IOException {
        
        this.clientStreamOutput.writeObject(msgEnvelope);
        this.clientStreamOutput.flush();
        this.clientStreamOutput.reset();
        
    }
    
    public void monitoringIncomingMessaging() throws IOException {
        
        this.masterThread = new chatClientThread("MASTER",
                                                 clientSocket, clientStreamInput,
                                                 clientStreamOutput, txtChatAreaRef, 
                                                 txtNotificationAreaRef, txtUserListRef,
                                                 btnConnRef, btnDisRef, btnSndRef, lblStatRef,
                                                 txtChatMessageRef, txtUsernameRef);
        this.masterThread.initiateInputStream();        
        this.masterThread.start();
        
    }    
    
}

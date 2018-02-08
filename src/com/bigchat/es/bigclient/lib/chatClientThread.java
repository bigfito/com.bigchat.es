/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigclient.lib;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import com.bigchat.es.lib.*;

/**
 * The class chatClientThread implements the message handling and processing 
 * between the chat client and the chat server (vice-versa). It extends from
 * Thread in order to create an instance of a thread object to keep reading and
 * writing to the stream channels on an infinite loop.
 * 
 * @author aorozco bigfito@gmail.com
 * @version 1.0
 */
public class chatClientThread extends Thread {
    
    //MEMBER ATTRIBUTES DEFINITION
    
    /**
     * A boolean switch for the infinite loop in the run method.
     */
    private boolean looping;

    /**
     * A socket object to reference the original chat client's socket from the
     * model.
     */    
    private Socket threadSocket;

    /**
     * An ObjectInputStream object to read from the chat client's socket input
     * stream.
     */
    private ObjectInputStream sInput;

    /**
     * An ObjectOutputStream object to write to the chat client's socket output
     * stream.
     */
    private ObjectOutputStream sOutput;
    
    /**
     * A JTextArea object to keep a reference to the chat client's chat area field 
     * from the GUI.
     */
    private JTextArea txtChatAreaRef;

    /**
     * A JTextArea object to keep a reference to the chat client's notification area field 
     * from the GUI.
     */
    private JTextArea txtNotificationAreaRef;    

    /**
     * A JList object to keep a reference to the chat client's list of users component 
     * from the GUI.
     */
    private JList txtUserListRef;

    /**
     * A JButton object to keep a reference to the chat client's connect button
     * from the GUI.
     */
    private JButton btnConn;

    /**
     * A JButton object to keep a reference to the chat client's disconnect button
     * from the GUI.
     */    
    private JButton btnDis;

    /**
     * A JButton object to keep a reference to the chat client's send button
     * from the GUI.
     */
    private JButton btnSnd;

    /**
     * A JLabel object to keep a reference to the chat client's status field 
     * from the GUI.
     */
    private JLabel lblStat;

    /**
     * A JTextField object to keep a reference to the chat client's chat message field 
     * from the GUI.
     */
    private JTextField txtChatInput;

    /**
     * A JTextField object to keep a reference to the chat client's username field 
     * from the GUI.
     */
    private JTextField txtUsernameInput;

    /**
     * A {@link chatEnvelope} type of message to exchange messages between the chat client and the
     * chat server.
     */    
    private chatEnvelope chatMessage;

    /**
     * A {@link chatUsersList} list of users to keep a reference to the original list of users from
     * the model.
     */
    private chatUsersList listOfUsers;
    
    /**
     * Default constructor for the chatClientThread class.
     * @param s A chat client's socket reference object.
     * @param i A chat client's ObjectInputStream reference object.
     * @param o A chat client's ObjectOutputStream reference object.
     * @param txtTextArea1 A chat client's JTextArea reference object.
     * @param txtTextArea2 A chat client's JTextArea reference object.
     * @param txtUsersList A chat client's JList reference object.
     * @param btnConn1 A chat client's JButton reference object.
     * @param btnDis1 A chat client's JButton reference object.
     * @param btnSnd1 A chat client's JButton reference object.
     * @param lblStat1 A chat client's JLabel reference object.
     * @param txtChatInput1 A chat client's JTextField reference object.
     * @param txtUsernameInput1 A chat client's JTextField reference object.
     */
    public chatClientThread(Socket s, ObjectInputStream i, ObjectOutputStream o, 
                            JTextArea txtTextArea1, JTextArea txtTextArea2, JList txtUsersList,
                            JButton btnConn1, JButton btnDis1, JButton btnSnd1, JLabel lblStat1,
                            JTextField txtChatInput1, JTextField txtUsernameInput1){
        
        this.looping = true;        
        this.threadSocket = s;
        this.sInput = i;
        this.sOutput = o;        
        this.txtChatAreaRef = txtTextArea1;
        this.txtNotificationAreaRef = txtTextArea2;
        this.txtUserListRef = txtUsersList;
        this.btnConn = btnConn1;
        this.btnDis = btnDis1;
        this.btnSnd = btnSnd1;
        this.lblStat = lblStat1;
        this.txtChatInput = txtChatInput1;
        this.txtUsernameInput = txtUsernameInput1;
    }
    
    /**
     * The initiateInputStream method initializes the input stream channel associateed
     * to the chat client's socket.
     * @throws IOException If a problem occurs when initializing the input stream.
     */
    public void initiateInputStream() throws IOException {
        sInput = new ObjectInputStream( threadSocket.getInputStream() );        
    }

    /**
     * The overrided run method executes the message handling and processing
     * running on a infinite loop.
     */
    public void run(){
                    
        try{
            
            //boolean looping = true;
            while( looping ){
                
                chatMessage = (chatEnvelope)sInput.readObject();                
                switch( chatMessage.getEnvHeader() ){
                    
                    case chatEnvelope.CONNECT_MSG_ACK:
                        
                        //LOGIN ACCEPTED
                        txtNotificationAreaRef.append("[INFO]: USERNAME ACCEPTED.\n");
                        break;
                        
                    case chatEnvelope.CONNECT_MSG_NACK:
                        
                        //LOGIN ALREADY IN USE
                        looping = false;
                        txtChatAreaRef.append("[ERROR]: USERNAME IS ALREADY IN USE.\n");                        
                        txtNotificationAreaRef.append("[ERROR]: USERNAME IS ALREADY IN USE.\n");                        
                        lblStat.setText("DESCONECTADO");
                        btnConn.setEnabled(true);
                        btnDis.setEnabled(false);
                        btnSnd.setEnabled(false);
                        txtChatInput.setText("");
                        txtUsernameInput.setEnabled(true);
                        txtUsernameInput.setEditable(true);
                        txtChatInput.setEnabled(false);
                        txtChatInput.setEditable(false);                        
                        close();
                        break;
                        
                    case chatEnvelope.DISCONNECT_MSG_ACK:
                        
                        //DISCONNECT ACCEPTED
                        looping = false;
                        clearListOfUsers();
                        txtNotificationAreaRef.append("[INFO]: SERVER ACCEPTED DISCONNECTION.\n");
                        close();
                        break;
                        
                    case chatEnvelope.CHAT_MSG:
                        
                        //CHAT MESSAGE
                        txtChatAreaRef.append("[" + chatMessage.getEnvFrom() + "]: " 
                                                  + chatMessage.getEnvBody() + ".\n");
                        break;
                        
                    case chatEnvelope.SERVERBROADCAST_MSG:
                        
                        //BROADCAST MESSAGE FROM SERVER
                        txtChatAreaRef.append("[SERVER]: " + chatMessage.getEnvBody() + ".\n");
                        break;
                        
                    case chatEnvelope.HOMEALONE_MSG:
                        
                        //CLIENT MUST WAIT BECAUSE IS THE ONLY ONE CONNECTED
                        txtChatAreaRef.append("YOU ARE THE FIRST PERSON IN THE CHAT ROOM. PLEASE WAIT FOR OTHERS.\n");
                        break;
                        
                    case chatEnvelope.UPDTLISTOFUSERS_MSG:
                        
                        //UPDATE LIST OF USERS
                        populateListOfUsers( chatMessage.getEnvUsersList() );
                        break;
                        
                    case chatEnvelope.JOINING_MSG:
                        
                        //JOIN MESSAGE
                        txtChatAreaRef.append("USER " + chatMessage.getEnvBody() + 
                                              " HAS JOINED THE CHATROOM.\n");
                        break;
                        
                    case chatEnvelope.SERVERGOINGDOWN_MSG:
                        
                        //SERVER IS GOING DOWN
                        looping = false;
                        
                        //SEND GOING DOWN ACK BACK TO SERVER
                        chatMessage.setEnvHeader(chatEnvelope.SERVERGOINGDOWN_MSG_ACK);
                        chatMessage.setEnvBody("ACK");
                        sOutput.writeObject(chatMessage);
                        sOutput.flush();
                        sOutput.reset();
                        
                        txtNotificationAreaRef.append("[INFO]: SERVER WENT DOWN.  ACK SENT BACK TO SERVER.\n");
                        
                        //CLEAR LIST OF USERS
                        clearListOfUsers();
                        
                        txtChatAreaRef.append("[INFO]: SERVER WENT DOWN.  YOU HAVE BEEN DISCONNECTED.\n");                        
                        txtNotificationAreaRef.append("[INFO]: SERVER WENT DOWN.  YOU HAVE BEEN DISCONNECTED.\n");
                        
                        //UPDATE GUI
                        lblStat.setText("DESCONECTADO");
                        btnConn.setEnabled(true);
                        btnDis.setEnabled(false);
                        btnSnd.setEnabled(false);
                        txtChatInput.setText("");
                        txtUsernameInput.setEnabled(true);
                        txtUsernameInput.setEditable(true);
                        txtChatInput.setEnabled(false);
                        txtChatInput.setEditable(false);                                                
                        
                        //CLOSE COMMUNICATION CHANNELS
                        close();                       
                        break;
                    
                    case chatEnvelope.ABANDON_MSG:
                        //USER ABANDONED CHAT
                        txtChatAreaRef.append("USER [" + chatMessage.getEnvBody() + "] ABANDONED THE CHATROOM.\n");                        
                        break;
                        
                    default:
                        break;
                }
                
                //RESETS CHAT MESSAGE
                chatMessage.resetEnvelope();
                
            }            
            
        }catch(IOException e1){
                
                txtNotificationAreaRef.append("ERROR: Problema al leer mensaje del servidor.\n");
                txtNotificationAreaRef.append( e1.toString() + "\n");
                
        }catch(ClassNotFoundException e2){
            
                txtNotificationAreaRef.append("ERROR: Problema al cerrar recursos de red.\n");
                txtNotificationAreaRef.append( e2.toString() + "\n");
            
        }
        
    }
    
    /**
     * The populateListOfUsers method populates the list of users on the chat client's
     * GUI based on the values received from the chat server.
     * @param listOfUsers A {@link chatUsersList} object representing the list of users.
     */
    public void populateListOfUsers(chatUsersList listOfUsers){
        
        Vector v = new Vector();        
        this.listOfUsers = listOfUsers;
        
        if ( listOfUsers != null ){
            for ( Map.Entry me : listOfUsers.getUsersListObject().entrySet() ) {
                v.add( listOfUsers.getUsernameFromListOfUsers( new Integer( me.getKey().toString() ) ) );
            }
            this.txtUserListRef.setListData(v);
        }else{
            v.add("");
            this.txtUserListRef.setListData(v);
        }                
        
    }
    
    /**
     * The clearListOfUsers method clears the list of users object from the
     * GUI.
     */
    public void clearListOfUsers(){
        Vector v;
        v = new Vector();
        v.add("");
        this.txtUserListRef.setListData(v);
    }
    
    /**
     * The close method closes the local input and output stream channels together
     * with the chat client's socket.
     * @throws IOException If a problem occurs when closing the streams.
     */
    public void close() throws IOException{
        
        if ( sOutput != null ) sOutput.close();
        if ( sInput != null ) sInput.close();
        if ( threadSocket != null ) threadSocket.close();
    }
    
}

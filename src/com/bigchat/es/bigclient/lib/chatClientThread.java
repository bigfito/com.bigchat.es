/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigclient.lib;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.bigchat.es.lib.chatUsersList;
import com.bigchat.es.lib.chatEnvelope;

/**
 *
 * @author aorozco
 */
public class chatClientThread extends Thread {
    
    private boolean looping;
    
    private Socket threadSocket;
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    
    private int intThreadId;
    
    /* REFERENCE TO GUI OBJECTS */
    private JTextArea txtChatAreaRef;
    private JTextArea txtNotificationAreaRef;    
    private JList txtUserListRef;
    private JButton btnConn, btnDis, btnSnd;
    private JLabel lblStat;
    private JTextField txtChatInput;
    private JTextField txtUsernameInput;
    
    private chatEnvelope chatMessage;
    private chatUsersList listOfUsers;
    
    public chatClientThread(String name, Socket s, ObjectInputStream i, ObjectOutputStream o, 
                            JTextArea txtTextArea1, JTextArea txtTextArea2, JList txtUsersList,
                            JButton btnConn1, JButton btnDis1, JButton btnSnd1, JLabel lblStat1,
                            JTextField txtChatInput1, JTextField txtUsernameInput1){
        
        this.looping = true;
        
        this.setName(name);
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
    
    public void initiateInputStream() throws IOException {
        sInput = new ObjectInputStream( threadSocket.getInputStream() );        
    }

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
    
    public void populateListOfUsers(chatUsersList listOfUsers){
        Vector v=new Vector();        
        this.listOfUsers = listOfUsers;
        
        if ( listOfUsers != null ){
            for ( Map.Entry me : listOfUsers.getUsersListObject().entrySet() ) {
                v.add(listOfUsers.getUsernameFromListOfUsers( new Integer(me.getKey().toString()) ));
            }
            this.txtUserListRef.setListData(v);
        }else{
            v.add(new String(""));
            this.txtUserListRef.setListData(v);
        }                
        
    }
    
    public void clearListOfUsers(){
        Vector v=new Vector();
        v.add(new String(""));
        this.txtUserListRef.setListData(v);
    }
    
    public void close() throws IOException{
        
        if ( sOutput != null ) sOutput.close();
        if ( sInput != null ) sInput.close();
        if ( threadSocket != null ) threadSocket.close();
    }
    
}

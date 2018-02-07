/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.bigchat.es.bigserver.gui.chatServerView;
import com.bigchat.es.bigserver.lib.chatServerNetworkInterfaces;

/**
 * The class chatServerController implements a controller application listening
 * for user actions to be triggered. It receives a reference of the view and model
 * clases.
 * @author aorozco bigfito@gmail.com
 * @version 1.0
 */
public class chatServerController {
    
    //MEMBER ATTRIBUTES SECTION

    /** 
     * A {@link chatServerModel} object reference.
     */
    private chatServerModel model;

    /** 
     * A {@link chatServerView} object reference.
     */
    private chatServerView view;
    
    /** 
     * An action listener for the START button.
     */
    private ActionListener actionListenerStart;

    /** 
     * An action listener for the STOP button.
     */
    private ActionListener actionListenerStop;

    /** 
     * An action listener for the SEND button.
     */
    private ActionListener actionListenerSend;
    
    /** 
     * A String for storing a message to broadcast.
     */
    String msgBroadcast;

    /** 
     * A {@link chatServerNetworkInterfaces} object reference.
     */
    private chatServerNetworkInterfaces nic;

    /**
     * Default constructor for chatServerController class.
     * @param model A {@link chatServerModel} object reference.
     * @param view A {@link chatServerView} object reference.
     */
    public chatServerController(chatServerModel model, chatServerView view) {
        this.model = model;
        this.view = view;
        this.nic = new chatServerNetworkInterfaces();
    }

    /**
     * The triggerButtonStart method is called when the chat server user starts
     * the server execution.
     */
    public void triggerButtonStart(){
                
        actionListenerStart = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                
                int indexFromList;
                String serverIP;
                String serverPort;                
                
                //UPDATE MESSAGE TO CHAT SERVER NOTIFICATION AREA
                view.getTxtNotificationAreaReference().append("STARTING SERVER...\n");
                
                /*VALIDATE IP ADDRESS AND PORT*/
                indexFromList = view.getListNetworkInterfacesReference().getSelectedIndex();
                
                if ( indexFromList > -1 ){
                    
                    nic = view.getChatServerNetworkInterfacesReference();
                    serverIP = nic.getNetworkInterfaceAddressElement( indexFromList );
                    serverPort = view.getTxtPortReference().getValue().toString();
                    
                    if ( model.serverNetworkParametersChecked(serverIP, serverPort) ){
                        
                        //DISABLE COMPONENTS FROM GUI
                        view.getButtonStartServerReference().setEnabled(false);
                        view.getButtonStopServerReference().setEnabled(true);
                        
                        //ENABLE BROADCAST MESSAGE SECTION
                        view.getTxtBroadcastMessageReference().setEditable(true);
                        view.getButtonSendMessageReference().setEnabled(true);
                        
                        //START CHAT SERVER
                        model.startChatServer();                       
                        
                    }
                }
                
            }
        };
        view.getButtonStartServerReference().addActionListener(actionListenerStart);
        
    }
    
    /**
     * The triggerButtonStop method is called when the chat server user stops
     * the server execution.
     */
    public void triggerButtonStop(){        
        
        actionListenerStop = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                
                //UPDATE MESSAGE TO CHAT SERVER NOTIFICATION AREA
                view.getTxtNotificationAreaReference().append("STOPPING SERVER...\n");
                
                //IF THERE ARE CONNECTED USERS THEN SEND DISCONNECT MESSAGE
                if ( model.getNumberOfConnectedClients() > 0 ){
                    msgBroadcast = "SERVER IS GOING DOWN. YOU HAVE BEEN DISCONNECTED.\n";
                    model.broadcastMessage(8, msgBroadcast);
                }
                model.stopChatServer();
                
                view.getTxtNotificationAreaReference().append("SERVER HAS BEEN STOPPED...\n");
                                                    
                //DISABLE COMPONENTS FROM GUI
                view.getButtonStartServerReference().setEnabled(true);
                view.getButtonStopServerReference().setEnabled(false);
                        
                //DISABLE BROADCAST & NOTIFICATION MESSAGE SECTION
                view.getTxtBroadcastMessageReference().setText("");
                view.getTxtBroadcastMessageReference().setEditable(false);
                view.getTxtNotificationAreaReference().setText("");
                view.getButtonSendMessageReference().setEnabled(false); 
                
                //RESET CONNECTED USERS LABEL
                view.getLblConnectedUsersReference().setText("0");
                
            }
        };
        view.getButtonStopServerReference().addActionListener(actionListenerStop);        
    }
    
    /**
     * The triggerButtonSend method is called when the chat server user sends out
     * a broadcast message to all connected client.
     */
    public void triggerButtonSend(){       
        
        actionListenerStop = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                
                //RETRIEVE MESSAGE TO BROADCAST
                msgBroadcast = view.getTxtBroadcastMessageReference().getText();
                
                if ( !msgBroadcast.equals("") ){
                    
                    model.broadcastMessage(4, msgBroadcast);
                    view.getTxtNotificationAreaReference().append("[INFO]: BROADCAST MESSAGE SENT.\n");
                    
                }else{
                    view.getTxtNotificationAreaReference().append("[ERROR]: TYPE A VALID BROADCAST MESSAGE.\n");
                }
            }
        };
        view.getButtonSendMessageReference().addActionListener(actionListenerStop);                
    }    
}

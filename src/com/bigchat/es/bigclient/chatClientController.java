/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;
import java.net.*;

import com.bigchat.es.bigclient.gui.chatClientView;

/**
 *
 * @author aorozco
 */
public class chatClientController {
    
    private final chatClientModel model;
    private final chatClientView view;
    
    private ActionListener actionListenerConnect;
    private ActionListener actionListenerDisconnect;
    private ActionListener actionListenerSend;
    
    private String strAnnounce;

    public chatClientController(chatClientModel model, chatClientView view) {
        this.model = model;
        this.view = view;
        this.view.setLblHostName( model.getStrHostName() );
        this.view.setLblIpAddress( model.getStrIpAddress() );
        this.view.addTextToNotificationArea("BIENVENIDO...");
        this.strAnnounce = "";
    }
    
    public void cleanStrAnnounce(){
        strAnnounce = "";
    }    
   
    public void triggerButtonConnect(){
        actionListenerConnect = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                
                /* WRITE CONNECTION ATTEMPT TO NOTIFICATION AREA */
                view.addTextToNotificationArea("[INFO]: INICIA INTENTO DE CONEXION...");
                
                /* VALIDATE IP ADDRESS */
                if ( model.serverIPAddressIsValid( view.getTxtServerAddress().getText() ) ){
                    
                    /* RETRIEVE USERNAME */
                    model.setStrUserName(view.getTxtUserNameReference().getText());
                    view.getTxtUserNameReference().setText( model.getStrUserName() );

                    if ( model.isUsernameValid() ){

                        view.getTxtUserNameReference().setEditable(false);

                        strAnnounce =  "[INFO]: USUARIO (" + model.getStrUserName() + ") ";
                        strAnnounce += "CONECTANDO DESDE LA MAQUINA (" + model.getStrHostName() + ") ";
                        strAnnounce += "CON IP (" + model.getStrIpAddress() + ").";
                        view.addTextToNotificationArea(strAnnounce);
                        cleanStrAnnounce();

                        /* DISABLE CONNECT BUTTON AND ENABLE DISCONNECT */
                        view.getButtonConnectReference().setEnabled(false);
                        view.getButtonDisconnectReference().setEnabled(true); 

                        /* CREATE CONNECTING MESSAGE ENVELOPE */
                        model.prepareConnectionEnvelope();

                        try{
                            /* CONNECT TO SERVER */
                            model.connectToServer();

                            /* SUBMIT LOGIN & REGISTER INTO SERVER */
                            model.sendEnvelopeMessageToServer();                        

                            /* HANDLE CONTROL TO MASTER THREAD */
                            model.monitoringIncomingMessaging();

                            /* IF LOGIN IS NOT ALREADY IN USE */
                            view.getLblUserStatus().setText("CONECTADO");
                            view.getTxtChatAreaReference().append("BIENVENIDO " + model.getStrUserName() + "\n");                        

                            /* ENABLE CHAT TEXT MESSAGE AND SEND BUTTON */
                            view.getTxtChatMessageReference().setEnabled(true);
                            view.getTxtChatMessageReference().setEditable(true);
                            view.getButtonSendReference().setEnabled(true);

                        }catch(SocketException e1){
                            view.addTextToNotificationArea("[ERROR] [CONNECT]: Problema al conectar con el servidor.\n");
                            view.addTextToNotificationArea(e1.toString());                                                
                        }catch(IOException e2){
                            view.addTextToNotificationArea("[ERROR] [CONNECT]: Problema al crear los flujos de entrada salida.\n");
                            view.addTextToNotificationArea(e2.toString());                                                
                        }

                    }else{
                        view.addTextToNotificationArea("[ERROR] [CONNECT]: USERNAME invalido.  Vuelva a intentar.\n");
                    }
                    
                }else{
                    view.addTextToNotificationArea("[ERROR] [CONNECT]: La direccion IP del servidor no es valida.  Vuelva a intentar.\n");
                }                                
            }
        };
        view.getButtonConnectReference().addActionListener(actionListenerConnect);
    }
    
    
    public void triggerButtonDisconnect(){        
        actionListenerDisconnect = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                
                view.addTextToNotificationArea("[INFO]: USTED HA PULSADO DISCONNECT...");
                
                /* STOP MONITORING INCOMING MESSAGES BY MASTER THREAD */
                try{
                    
                    /* SEND DISCONNECT MESSAGE TO SERVER */
                    model.prepareDisconnectionEnvelope();
                    model.sendEnvelopeMessageToServer();                    
                    
                    /* CLEAN CHAT AREA & USERS LIST */
                    view.getLblUserStatus().setText("DESCONECTADO");
                    view.getTxtChatAreaReference().setText("");                    
                    
                    /* DISABLE CHAT TEXT MESSAGE AND SEND BUTTON */
                    view.getTxtChatMessageReference().setEnabled(false);
                    view.getTxtChatMessageReference().setEditable(false);
                    view.getButtonSendReference().setEnabled(false);                    

                    /* DISABLE DISCONNECT BUTTON AND ENABLE CONNECT */
                    view.getTxtUserNameReference().setEditable(true);
                    view.getButtonConnectReference().setEnabled(true);
                    view.getButtonDisconnectReference().setEnabled(false);
                
                }catch(IOException e){
                    view.addTextToNotificationArea("[ERROR] [DISCONNECT]: Problema al desconectar del servidor.");
                    view.addTextToNotificationArea(e.toString());                                                
                }                                                
            }
        };
        view.getButtonDisconnectReference().addActionListener(actionListenerDisconnect);
    }
    
    public void triggerButtonSend(){        
        
        actionListenerSend = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {                           
                                               
                try{
                    
                    /* PREPARE CHAT MESSAGE */
                    model.prepareChatMessageEnvelope(view.getTextFromChatMessage());
                    model.sendEnvelopeMessageToServer();                                                           
                    
                    /* DISABLE CHAT TEXT MESSAGE AND SEND BUTTON */
                    view.getTxtChatMessageReference().setText("");
                
                }catch(IOException e){
                    view.addTextToNotificationArea("[ERROR] [GENERAL]: Problema al enviar mensaje " +
                                                   "de chat al servidor.");
                    view.addTextToNotificationArea(e.toString());                                                
                }                 
            }
        };
        view.getButtonSendReference().addActionListener(actionListenerSend);
    }
    
}

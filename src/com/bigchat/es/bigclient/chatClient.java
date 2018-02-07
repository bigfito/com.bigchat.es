/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigclient;

import java.net.UnknownHostException;
import com.bigchat.es.bigclient.gui.chatClientView;

/**
 *
 * @author aorozco
 */
public class chatClient{
     
    //member attributes implementing the MVC design pattern
    private final chatClientView view;
    private final chatClientModel model;
    private final chatClientController controller;

    public chatClient() {
        this.view = new chatClientView();
        this.model = new chatClientModel(view.getTxtChatAreaReference(), 
                                    view.getTxtNotificationAreaReference(),
                                    view.getTxtUsersListReference(),
                                    view.getButtonConnectReference(),
                                    view.getButtonDisconnectReference(),
                                    view.getButtonSendReference(),
                                    view.getLblUserStatus(),
                                    view.getTxtChatMessageReference(),
                                    view.getTxtUserNameReference(),
                                    view.getTxtServerAddress(),
                                    view.getTxtServerPort(),
                                    view.getLblClientHostname(),
                                    view.getLblClientIPAddress());
        this.controller = new chatClientController(model, view);
    }
    
    public void chatClientStart(){
        
        try{
            model.setupNetworkFields();
            view.setVisible(true);
            controller.triggerButtonConnect();
            controller.triggerButtonDisconnect();
            controller.triggerButtonSend();            
            
        }catch(UnknownHostException e){
            view.getTxtNotificationAreaReference().append("[ERROR]: Problema al leer los parametros de red "
                                                        + "del host.Revise su configuracion de red y vuelva "
                                                        + "a intentar.\n");
            view.getTxtNotificationAreaReference().append(e.toString());              
        }         
        
    }
    
    
}

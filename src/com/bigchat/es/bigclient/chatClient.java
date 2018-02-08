/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigclient;

import com.bigchat.es.bigclient.gui.chatClientView;

/**
 * The class chatClient implements a Model-View-Controller design pattern for
 * the "bigchat" application. The class creates an instance of the model, the
 * view and puts them all together from within a controller instance.
 * 
 * @author aorozco bigfito@gmail.com
 * @version 1.0
 */
public class chatClient{
     
    //MEMBER ATTRIBUTES SECTION
    
    /**
     * A {@link chatClientView} type of object.
     */
    private chatClientView view;

    /**
     * A {@link chatClientModel} type of object. 
     */
    private chatClientModel model;

    /**
    * A {@link chatClientController} type of object. 
     */
    private chatClientController controller;

    /**
     * Default constructor for chatClient class.
     */
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
                                    view.getTxtUserNameReference() );
        this.controller = new chatClientController(model, view);
    }
    
    /**
     * The chatClientStart method initializes all instances of the model, view
     * and controller in order to run the client chat application.
     */
    public void chatClientStart(){
        
        view.setupNetworkFields();
        view.setVisible(true);
        controller.triggerButtonConnect();
        controller.triggerButtonDisconnect();
        controller.triggerButtonSend();
    }    
}
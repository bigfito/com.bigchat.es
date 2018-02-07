/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigserver;

import java.net.SocketException;
import javax.swing.DefaultComboBoxModel;

import com.bigchat.es.bigserver.gui.chatServerView;

/**
 * The class chatServer implements a Model-View-Controller design pattern for
 * the "bigchat" application. The class creates an instance of the model, the
 * view and puts them all together from within a controller instance.
 * @author aorozco bigfito@gmail.com
 * @version 1.0
 */
public class chatServer{
    
    //MEMBER ATTRIBUTES SECTION
    
    /**
     * A {@link chatServerView} (view) instance variable.
     */
    private chatServerView view;
    
    /**
     * A {@link chatServerModel} (model) instance variable.
     */
    private chatServerModel model;
    
    /**
     * A {@link chatServerController} (controller) instance variable.
     */
    private chatServerController controller;    

    /**
     * Default constructor for class chatServer puts all MVC components to work. 
     * It takes no parameters.
     */
    public chatServer() {
        this.view = new chatServerView();
        this.model = new chatServerModel(view.getLblConnectedUsersReference(),
                                         view.getTxtNotificationAreaReference() );
        this.controller = new chatServerController(model, view);  
    }    

    /**
     * The chatServerStart method starts the chat server application.  It takes
     * no parameters.
     */
    public void chatServerStart(){
        
        view.setVisible(true);
        controller.triggerButtonStart();
        controller.triggerButtonStop();
        controller.triggerButtonSend();
    }
}

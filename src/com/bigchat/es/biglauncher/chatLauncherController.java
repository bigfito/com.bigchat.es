/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.biglauncher;

import com.bigchat.es.biglauncher.gui.bigChatLauncherView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.bigchat.es.bigserver.chatServer;
import com.bigchat.es.bigclient.chatClient;

/**
 *
 * @author aorozco
 */
public class chatLauncherController{
    
    private bigChatLauncherView gui;
    private ActionListener actionListenerLaunchServer;
    private ActionListener actionListenerLaunchClient;

    public chatLauncherController(bigChatLauncherView gui) {
        this.gui = gui;
    }

    public void triggerBtnLaunchServer(){
        actionListenerLaunchServer = new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                gui.getReferenceBtnLaunchServer().setEnabled(false);
                new chatServer().chatServerStart();
            }
        };
        gui.getReferenceBtnLaunchServer().addActionListener(actionListenerLaunchServer);
    }
    
    public void triggerBtnLaunchClient(){
        actionListenerLaunchClient = new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                new chatClient().chatClientStart();
            }
        };
        gui.getReferenceBtnLaunchClient().addActionListener(actionListenerLaunchClient);
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.biglauncher;

import com.bigchat.es.biglauncher.gui.bigChatLauncherView;

/**
 *
 * @author aorozco
 */
public class chatLauncher implements Runnable{
    
    //member attributes implementing the MVC design pattern
    private bigChatLauncherView view;
    private chatLauncherController controller;
    
    public chatLauncher(){
        view = new bigChatLauncherView();
        controller = new chatLauncherController(view);
    }
    
    @Override
    public void run(){
        view.setVisible(true);
        controller.triggerBtnLaunchServer();
        controller.triggerBtnLaunchClient();
    }
    
    public static void main(String[] args){
        new chatLauncher().run();
    }
}

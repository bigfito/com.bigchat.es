/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.bigserver.lib;

import java.net.*;
import java.util.*;

/**
 * The class chatServerNetworkInterfaces collects the information of the different
 * network interfaces configured in the machine running the chat server and then
 * stores them in separated array objects.
 * 
 * @author aorozco bigfito@gmail.com
 * @version 1.0
 */
public class chatServerNetworkInterfaces {
    
    //MEMBER ATTRIBUTES DEFINITION
    
    /**
     * A String array for storing the network interfaces names.
     */
    private ArrayList<String> networkInterfaces;

    /**
     * A String array for storing the network interfaces IPv4 addresses.
     */
    private ArrayList<String> networkAddresses;

    /**
     * A String array for storing a customized label "network interface - IPv4 address".
     */
    private ArrayList<String> nInterfacesAndAddresses;

    /**
     * Default constructor for class chatServerNetworkInterfaces.
     */
    public chatServerNetworkInterfaces() {
        this.networkInterfaces = new ArrayList<>();
        this.networkAddresses = new ArrayList<>();
        this.nInterfacesAndAddresses = new ArrayList<>();
    }
    
    /**
     * The getNumberOfNetworkIntefaces method returns the number of network interfaces
     * in the network interfaces string array object.
     * @return An integer representing the size of the network interfaces array object.
     */
    public int getNumberOfNetworkIntefaces(){
        return this.networkInterfaces.size();
    }
    
    /**
     * The getNetworkIntefacesArray method returns a String array having the names of
     * the different network interfaces from the machine running the chat server.
     * @return A String array with the names of the network interfaces.
     */
    public ArrayList<String> getNetworkIntefacesArray(){
        return this.networkInterfaces;
    }

    /**
     * The getNetworkIntefacesAddressesArray method returns a String array having
     * the IPv4 addresses of the different network interfaces from the machin running
     * the chat server.
     * @return A String array with the IPv4 addresses of the network interfaces.
     */
    public ArrayList<String> getNetworkIntefacesAddressesArray(){
        return this.networkAddresses;
    }
    
    /**
     * The getNetworkInterfacesAndAddressesArray method returns a String array
     * having the name of the network interface concatenated with the IPv4 address
     * for that interface.
     * @return A String array with the network inteface name and its IPv4 address
     * concatenated.
     */
    public ArrayList<String> getNetworkInterfacesAndAddressesArray(){
        return this.nInterfacesAndAddresses;
    }    

    /**
     * The getNetworkInterfaceNameElement method returns the name of a single network
     * inteface name at certain position which is stored in the network interface names array.
     * @param index An integer representing the index position in the array where the
     * name of the network interface was stored.
     * @return A String with the name of the network interface.
     */
    public String getNetworkInterfaceNameElement(int index){
        return this.networkInterfaces.get(index);
    }
    
    /**
     * The getNetworkInterfaceAddressElement method returns the IPv4 address of a
     * single network interface at certain position which is stored in the network
     * interfaces IPv4 addresses array.
     * @param index An integer representing the index position in the array where the
     * IPv4 address of the network interface was stored.
     * @return A String with the IPv4 address of the network interface.
     */
    public String getNetworkInterfaceAddressElement(int index){
        return this.networkAddresses.get(index);
    }
    
    /**
     * The getNetworkInterfacesAndAddressesArrayElement returns the name and IPv4
     * address of a single network interface at certain position which is stored in
     * the network interface name and IPv4 addressess array.
     * @param index An integer representing the index position in the array where the
     * name and IPv4 address of the network interface was stored in a concatenated form.
     * @return A String with the name and IPv4 address of the network interface in a concatenated
     * form.
     */
    public String getNetworkInterfacesAndAddressesArrayElement(int index){
        return this.nInterfacesAndAddresses.get(index);
    }
    
    /**
     * The retrieveNetworkInterfacesInfo collects the information of the different
     * IPv4 network interfaces from the machine running the chat server and then
     * stores the results on the different arrays.
     * @throws SocketException If there is a problem when retrieving the list of
     * network interfaces from the machine running the chat server.
     */
    public void retrieveNetworkInterfacesInfo() throws SocketException {
        
        int i = 0;
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while ( e.hasMoreElements() ) {

            NetworkInterface ni = e.nextElement();
            if ( ni.isVirtual() ){
                // discard virtual interfaces
                continue;
            }

            Enumeration<InetAddress> e2 = ni.getInetAddresses();
            while ( e2.hasMoreElements() ) {

                InetAddress ip = e2.nextElement();
                if ( ip instanceof Inet4Address ){
                        networkInterfaces.add( i, ni.getName() );
                        networkAddresses.add( i, ip.getHostAddress() );
                        nInterfacesAndAddresses.add(i, "INTERFACE: " + ni.getName() 
                                                     + " - " + ip.getHostAddress() );
                    i++;
                }
            }
        }
    }
    
}

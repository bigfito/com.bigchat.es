/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigchat.es.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class ipAddressValidator implements a validation pattern for an IP address
 * and validates a string representing an IP address against the pattern.
 * 
 * @author aorozco bigfito@gmail.com
 * @version 1.0
 */
public class ipAddressValidator{
    
    //IP ADDRESS constant pattern definition
    private static final String IPADDRESS_PATTERN =
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";    

    //MEMBER ATTRIBUTES

    /**
     * A pattern object to reference the defined pattern.
     */      
    private Pattern pattern;
    
    /**
     * A matcher object to validate the pattern.
     */  
    private Matcher matcher;

    /**
     * Default constructor method for class ipAddressValidator.  It setups the pattern
     * member attribute.
     */
    public ipAddressValidator(){
	  pattern = Pattern.compile( IPADDRESS_PATTERN );
    }

   /**
    * The validateIP method validates an IP address against the defined pattern.
    * @param ip A string representing the IP address to validate.
    * @return A boolean value of TRUE if the parameter ip matches the pattern.
    * Otherwise returns FALSE.
    */
    public boolean validateIP( String ip ){
	
        matcher = pattern.matcher(ip);
	return matcher.matches();
    }
}

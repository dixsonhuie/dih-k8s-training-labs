package com.mycompany.app;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;


/** 
 * UserFeederActivator class create a standalone proxy connection to the space using configurer.
 * The class then activates the UserFeeder to write all user into the space.
 * @author gsUniversity
 */

public class AccountFeeder {

	public static void main(String[] args) {
    	
		// Get a proxy to the space using a configurer
		
		String lookupGroups = System.getenv("GS_LOOKUP_GROUPS");
		lookupGroups="xap-16.2.0";
		SpaceProxyConfigurer spaceConfigurer = new SpaceProxyConfigurer("BillBuddy-space");
		spaceConfigurer.lookupGroups(lookupGroups);
	  	
	  	// Create a space proxy
	  	GigaSpace gigaSpace = new GigaSpaceConfigurer(spaceConfigurer).gigaSpace();
    	
    	try {
    		
    		// Write users into the space 
    		
    		UserFeeder.loadData(gigaSpace);
    		
    		// Write merchants into the space 
    		
    		MerchantFeeder.loadData(gigaSpace);
    	
    	} catch (Exception ex){
    		System.out.println(ex.getMessage());
    		System.out.println(ex.getStackTrace());
    	}
    	
	}

}

/**
 * Copyright (C) Carnegie Mellon University - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * This is proprietary and confidential.
 * Written by members of the ArticuLab, directed by Justine Cassell, 2014.
 * 
 * @author Yoichi Matsuyama <yoichim@cs.cmu.edu>
 * 
 */
package edu.usc.ict.vhmsg.main;

import javax.swing.JOptionPane;


import edu.usc.ict.vhmsg.*;

public class VhmsgSender
{
	private VHMsg vhmsg;
	private String VHMSG_SERVER;
	private String prefix = null;

	public VhmsgSender(String prefix)
	{
		
		this.VHMSG_SERVER = Config.VHMSG_SERVER_URL;
		this.prefix = prefix;
		System.setProperty("VHMSG_SERVER", VHMSG_SERVER);
		//System.out.println( "VHMSG_SERVER: " + System.getProperty( "VHMSG_SERVER" ) );
		//System.out.println( "VHMSG_SCOPE: " + System.getProperty( "VHMSG_SCOPE" ) );
		System.err.println(prefix + " connected. (" + VHMSG_SERVER + ")");
		vhmsg = new VHMsg();
		
		boolean ret = vhmsg.openConnection();
		if ( !ret )
		{
			System.out.println( prefix + " Connection error!" ); 
			return;
		}
	}
	
	public boolean connnect(){
		return vhmsg.openConnection();
	}
	
	
	public boolean disconnect(){
		return vhmsg.closeConnection();
	}

	public void sendMessage(String message){
		vhmsg.sendMessage(this.prefix + " " + message);
		//System.out.println(this.prefix + " " + message);
	}
}

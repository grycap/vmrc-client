/*******************************************************************************
 * Copyright 2012-2013, Grid and High Performance Computing group (http://www.grycap.upv.es)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.grycap.vmrc.client.cmd.admin;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.grycap.vmrc.client.api.FormattedVMRCServerAPI;
import org.grycap.vmrc.client.cmd.CLIArgsReader;
import org.grycap.vmrc.client.cmd.VMRCCLI;
import org.grycap.vmrc.client.exception.VMRCException;


public class VMRCAdminCLI extends VMRCCLI{
	
	public static void main(String[] args){
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("log4j.properties");
		PropertyConfigurator.configure(url);
		log = Logger.getLogger(VMRCAdminCLI.class);
		log.info("VMRC Client logging initialized");
				
		CLIArgsAdminReader uor = new CLIArgsAdminReader(args);			
		
		//VERBOSE
		if (uor.hasBooleanValueOption(CLIArgsReader.DEBUG)){
			log.setLevel(Level.DEBUG);	
		}
		
		FormattedVMRCServerAPI api;
		try {
			log.debug("About to configure client-side API");
			api = getConfiguredVMRCServerAPI(uor);
			log.debug("Successfully configured client-side API");
		} catch (VMRCException e1) {
			String msg = "Unable to configure client-side API. Reason: " + e1.getMessage();
			log.error(msg);
			return;
		}
		
		
		
		//ADD USER
		if (uor.hasStringValueOption(CLIArgsAdminReader.ADD_USER) && uor.hasStringValueOption(CLIArgsAdminReader.PASSWORD)){
			String newUserName = uor.getStringValue(CLIArgsAdminReader.ADD_USER);
			String newUserPassword = uor.getStringValue(CLIArgsAdminReader.PASSWORD);
			try{
				log.info("About to invoke ADDUSER operation with user name: " + newUserName);
				api.addUser(newUserName, newUserPassword);
				log.info("Successfully invoked ADDUSER operation.");
			}catch(Exception ex){
				String msg = "Problems while invoking ADDUSER. Reason: " + ex.getMessage();
				log.error(msg);
				return;
			}
		
		}else
		
			
		//LIST specific User
		if (uor.hasBooleanValueOption(CLIArgsAdminReader.LIST_USERS) && uor.hasStringValueOption(CLIArgsAdminReader.USER_NAME)){
			String userName = uor.getStringValue(CLIArgsAdminReader.USER_NAME);
			try {
				log.debug("About to invoke LIST_USER operation for " + userName);		
				String res = api.formattedListUser(userName);
				log.debug("Successfully invoked LIST_USER operation. Got result: \n " + res);
				System.out.println(res);
			} catch (Exception e) {
				String msg = "Problems while listing Users. Reason: " + e.getMessage();				
				log.error(msg);
				return;
			}						
		} else	
			
			
		//LIST Users
		if (uor.hasBooleanValueOption(CLIArgsAdminReader.LIST_USERS)){		
			try {
				log.debug("About to invoke LIST_USERS operation");		
				String res = api.formattedListUsers();
				log.debug("Successfully invoked LIST_USERS operation. Got result: \n " + res);
				System.out.println(res);
			} catch (Exception e) {
				String msg = "Problems while listing Users. Reason: " + e.getMessage();				
				log.error(msg);
				return;
			}						
		} else	
			
		//DELETE USER
		if (uor.hasStringValueOption(CLIArgsAdminReader.DELETE_USER)){
			String theUserName = uor.getStringValue(CLIArgsAdminReader.DELETE_USER);
			try {
				log.debug("About to invoke DELETE USER operation");				
				api.deleteUser(theUserName);
				log.debug("Successfully performed DELETE USER operation");
			} catch (Exception e) {
				String msg = "Problems while deleting User " + theUserName + ". Reason: " + e.getMessage();
				log.error(msg);
				return;
			}						
		} else
		
		//USER ACL
		if (uor.hasBooleanValueOption(CLIArgsAdminReader.USER_ACL)){		
			String[] remainingArgs = uor.getRemainingArgs();
			if (remainingArgs.length != 3) {
				log.debug("Wrong number of arguments");
				return;	
			}
			String theUserName = remainingArgs[0];
			String operation = remainingArgs[1];
			String perm = remainingArgs[2];
			try {
				log.debug("About to invoke change USER ACL operation");		
				api.changeUserAcl(theUserName, operation, perm);
				log.debug("Successfully invoked change USER ACL operation");
			} catch (Exception e) {
				String msg = "Problems while modifying ACL. Reason: " + e.getMessage();
				log.error(msg);
				return;
			}
		} else
		//VMI ACL
		if (uor.hasBooleanValueOption(CLIArgsAdminReader.VMI_ACL)){		
			String[] remainingArgs = uor.getRemainingArgs();
			if (remainingArgs.length != 3) {
				log.debug("Wrong number of arguments");
				return;	
			}
			String vmiName = remainingArgs[0];
			String operation = remainingArgs[1];
			String perm = remainingArgs[2];
			try {
				log.debug("About to invoke ACL operation");		
				api.changeVMIAcl(vmiName, operation, perm);
				log.debug("Successfully invoked ACL operation");
			} catch (Exception e) {
				String msg = "Problems while modifying ACL. Reason: " + e.getMessage();
				log.error(msg);
				return;
			}
									
		} 
		
		else {
			log.error("No operation performed: ");
			uor.printUsage();
		}

		
		
	}

}

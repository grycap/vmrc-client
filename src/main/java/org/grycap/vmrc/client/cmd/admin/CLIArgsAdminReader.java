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

import org.grycap.vmrc.client.cmd.CLIArgsReader;


public class CLIArgsAdminReader extends CLIArgsReader{
	/*Arguments that are followed by a String value */	
	 //public final static int URI = 0;  (Inherited from CLIArgsReader)
	 //public final static int AUTH_FILE = 1; (Inherited from CLIArgsReader)
	 public final static int ADD_USER = 2;	 
	 public final static int DELETE_USER = 3;
	 public final static int PASSWORD = 4;
	 public final static int USER_NAME = 5;

	 	 	 
	 /* Boolean arguments */
	 //public final static int VMI_ACL = 0; (Inherited from CLIArgsReader)
	 //public final static int XML = 1; (Inherited from CLIArgsReader)
	 //public final static int DEBUG = 2; (Inherited from CLIArgsReader)
	 
	 //This boolean parameters are not considered for the Admin CLI
	// public final static int LIST = 3; (Inherited from CLIArgsReader)
	 //public final static int OVF = 4; (Inherited from CLIArgsReader)
	 
	 public final static int LIST_USERS = 3;
	 public final static int USER_ACL = 4;
	  

	/**
	 * @param args
	 */
	public CLIArgsAdminReader(String[] args) {
		super(args);
	}
	

	protected void configureOptions(){
		stringOptionsNames = new String[]{"uri","authFile", "addUser","deleteUser","password","userName"};
		stringOptionsShortNames = new char[]{'u','z','a','d','p','n'};
		
		booleanOptionsNames  = new String[]{"vmiAcl","xml","debug","listUsers","userAcl"};
		booleanOptionsShortNames = new char[]{'v','x','b','l','c'};
		
	}
	
	protected void printUsage() {
		   String ls = System.getProperty("line.separator");
		        	    
		   String usage = "vmrc.sh";
		    
		   for (int i = 0 ; i < stringOptionsNames.length; i++) usage += " [-" + stringOptionsShortNames[i] + ",--" + stringOptionsNames[i] + "]";
		   for (int i = 0 ; i < booleanOptionsNames.length; i++) usage += " [-" + booleanOptionsShortNames[i] + ",--" + booleanOptionsNames[i] + "]";
		   usage += ls;
		   usage += "[-r, --uri vmrc_service_uri]" + ls;
		   usage += "[-z, --authFile vmrc_auth" + ls;		   
		   usage += "[-a, --addUser userName [-p, --password password]]" + ls;
		   usage += "[-d, --deleteUser userName]" + ls;
		   usage += "[-l, --listUsers]" + ls;
		   usage += "[-l, --listUsers] [-n, --userName userName]" + ls;
		   usage += "[-x, --xml]" + ls;
		   usage += "[-b, --debug]" + ls;
		   usage += "[-t, --userAcl userName operation perm], operation=[add|list|upload|search|delete] perm =[all|owner|none] " + ls;
		   
		   System.err.println(usage);
		  }

}

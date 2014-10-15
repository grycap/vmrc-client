/**
 * 
 * @author German Molto (gmolto@dsic.upv.es)
 */
package org.grycap.vmrc.client.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.grycap.vmrc.client.api.FormattedVMRCServerAPI;
import org.grycap.vmrc.client.exception.VMRCException;
import org.grycap.vmrc.client.utils.VMRCClientUtils;

/**
 * 
 */
public class VMRCCLI {
	protected static Logger log;
	
	
	protected static String loadProperties(String vmrcPropFile) throws Exception{
		Properties prop = new Properties();			
		prop.load(new FileInputStream(vmrcPropFile));
		return prop.getProperty("vmrc.uri");		
	}
	
	protected static void loadUserPasswordFromFile(String vmrcAuthFile, StringBuffer user, StringBuffer password){
		String userAuth ="anonymous", passwordAuth = "";
									
		if (!new File(vmrcAuthFile).exists()) {
			String msg = "There is no authorization file in " + vmrcAuthFile + ". Using 'anonymous' user.";
			log.warn(msg);			
		} else {		
		  	    FileReader authFileReader;
			    try {
				  authFileReader = new FileReader (vmrcAuthFile);
				  BufferedReader br = new BufferedReader(authFileReader);
				  String line = br.readLine();
				  int pos = line.indexOf(':');
				  userAuth = line.substring(0, pos);
				  passwordAuth = line.substring(pos+1, line.length());			
			    } catch (Exception e) {
			 	  String msg = "Error reading from : " + vmrcAuthFile + ". Review the file format [user:password].";
				  log.warn(msg);				 
			    }
			  }
			 
		user.append(userAuth);
		password.append(passwordAuth);
	}
	
	protected static FormattedVMRCServerAPI getConfiguredVMRCServerAPI(CLIArgsReader uor) throws VMRCException{
		FormattedVMRCServerAPI api = null;
		String vmrcServerURI = null;
		int format = FormattedVMRCServerAPI.FORMAT_TABBED;
		
		
		
		//PROPERTY FILE
		String vmrcConfDir = System.getProperty("user.home") + File.separator + ".vmrc";
		String vmrcPropFile = vmrcConfDir + File.separator + "vmrc.properties";
		try{
		 log.debug("Reading properties file at " + vmrcPropFile);
		  vmrcServerURI = loadProperties(vmrcPropFile);
		  log.debug("Read property vmrc.uri = " + vmrcServerURI);
		} catch (Exception e) {	
			log.warn("Unable to read VMRC property file at " + vmrcPropFile);
		}
		
		//USER,PASSWORD
		String authFile = null;
		if (uor.hasStringValueOption(CLIArgsReader.AUTH_FILE)){
			authFile = uor.getStringValue(CLIArgsReader.AUTH_FILE);
		} else authFile =  vmrcConfDir + File.separator + "vmrc_auth";
		StringBuffer userName = new StringBuffer(), password = new StringBuffer();
		
		log.debug("Reading user credentials from " + authFile);
		loadUserPasswordFromFile(authFile, userName, password);
		log.debug("Read user credentials " + userName + ":" + password);
		
		//URI
		if (uor.hasStringValueOption(CLIArgsReader.URI)){			
				vmrcServerURI = uor.getStringValue(CLIArgsReader.URI);					
		} 
		
		//FORMAT
		if (uor.hasBooleanValueOption(CLIArgsReader.XML)){
			format = FormattedVMRCServerAPI.FORMAT_XML;
		}
		
		try {
			api = new FormattedVMRCServerAPI(userName.toString(), password.toString(),vmrcServerURI,format);
		} catch (VMRCException e) {
			String msg = "Unable to connect to VMRC Server @ " + vmrcServerURI + ". Reason: " + e.getMessage();
			log.error(msg);
			throw e;
			
		}
				
		return api;
	}
	
	
	public static void main(String[] args){	
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("log4j.properties");
		PropertyConfigurator.configure(url);
		log = Logger.getLogger(VMRCCLI.class);
		
		//log.info("VMRC Client logging initialized");
				
		CLIArgsReader uor = new CLIArgsReader(args);	
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
		
		
		//ADD VMI
		if (uor.hasStringValueOption(CLIArgsReader.ADD)){
			String filePath = uor.getStringValue(CLIArgsReader.ADD);
			String vmiD;			
			try {				
				vmiD = VMRCClientUtils.textFileToString(filePath);
				if(uor.hasStringValueOption(CLIArgsReader.VMI_NAME)){
					String vmiName = uor.getStringValue(CLIArgsReader.VMI_NAME);
					log.debug("Overriding VMI name with user-specified value");
					vmiD = VMRCClientUtils.replaceVMIName(vmiD, vmiName);					
				}
				log.debug("About to invoke ADD operation with VMI descriptor in " + filePath);
				String res = api.addVMI(vmiD);				
				log.debug("Successfully invoked ADD operation. Got result: \n" + res);
				System.out.println(res);
			} catch (Exception e) {
				String msg = "Problems while adding VMI with descriptor in file" + filePath + ". Reason: " + e.getMessage();
				log.error(msg);
				return;
			}						
		} else 		
			
		//SEARCH
		if (uor.hasStringValueOption(CLIArgsReader.SEARCH)){
			String filePath = uor.getStringValue(CLIArgsReader.SEARCH);
			String vmiD;
			try {
				log.debug("About to invoke SEARCH operation");
				vmiD = VMRCClientUtils.textFileToString(filePath);
				String res = api.formattedSearchVMIs(vmiD);						
				System.out.println(res);			
				log.debug("Successfully invoked SEARCH operation. Got result: \n" + res);
			} catch (Exception e) {
				String msg = "Problems while searching VMIs with descriptor in " + filePath + ". Reason: " + e.getMessage();
				log.error(msg);
				return;
			}						
		} else
		
		//DELETE VMI
		if (uor.hasStringValueOption(CLIArgsReader.DELETE)){
			String vmiName = uor.getStringValue(CLIArgsReader.DELETE);
			try {
				log.debug("About to invoke DELETE operation");				
				api.deleteVMI(vmiName);
				log.debug("Successfully performed DELETE operation");
				System.out.println("Deleted VMI " + vmiName);
			} catch (Exception e) {
				String msg = "Problems while deleting VMI " + vmiName + ". Reason: " + e.getMessage();
				log.error(msg);
				return;
			}						
		} else
			
		//LIST specific VMI for OVF
		if (uor.hasBooleanValueOption(CLIArgsReader.LIST) && 
				uor.hasStringValueOption(CLIArgsReader.VMI_NAME) &&
				  uor.hasBooleanValueOption(CLIArgsReader.OVF)){
			String vmiName = uor.getStringValue(CLIArgsReader.VMI_NAME);
			try {
				log.debug("About to invoke get OVF for a specific VMI");				
				String res = api.getOVFByVMI(vmiName);				
				System.out.println(res);			
				log.debug("Successfully got OVF operation. Got result: \n" + res);
			} catch (Exception e) {
				String msg = "Problems while getting OVF for VMI " + vmiName + ". Reason: " + e.getMessage();
				log.error(msg);
				return;
			}			
		} else		
				
		//LIST specific VMI
		if (uor.hasBooleanValueOption(CLIArgsReader.LIST) && uor.hasStringValueOption(CLIArgsReader.VMI_NAME)){
			String vmiName = uor.getStringValue(CLIArgsReader.VMI_NAME);
			String vmiDescr = "system.name = " + vmiName;
			try {
				log.debug("About to invoke SEARCH operation for a specific VMI");				
				String res = api.formattedSearchVMIs(vmiDescr);						
				System.out.println(res);			
				log.debug("Successfully invoked SEARCH operation. Got result: \n" + res);
			} catch (Exception e) {
				String msg = "Problems while searching for VMI " + vmiName + ". Reason: " + e.getMessage();
				log.error(msg);
				return;
			}		
			
		} else
			
		//LIST VMIs
		if (uor.hasBooleanValueOption(CLIArgsReader.LIST)){		
			try {
				log.debug("About to invoke LIST operation");		
				String res = api.formattedListVMIs();				
				log.debug("Successfully invoked LIST operation. Got result: \n" + res);
				System.out.println(res);
			} catch (Exception e) {
				String msg = "Problems while listing VMIs. Reason: " + e.getMessage();
				e.printStackTrace();
				log.error(msg);
				return;
			}						
		} else
		
		//UPLOAD VMI
		if (uor.hasStringValueOption(CLIArgsReader.UPLOAD) && uor.hasStringValueOption(CLIArgsReader.VMI_NAME)){
			String filePath = uor.getStringValue(CLIArgsReader.UPLOAD);
			String vmiName = uor.getStringValue(CLIArgsReader.VMI_NAME);
			try {
				log.debug("About to invoke UPLOAD operation for VMI " + vmiName + " with local file " + filePath);		
				api.uploadVMI(vmiName, filePath);
				log.info("Successfully UPLOADed " + filePath + " into " + vmiName);
			} catch (Exception e) {
				String msg = "Problems while UPLOADING VMIs. Reason: " + e.getMessage();				
				log.error(msg);
				return;
			}
						
		} else 
			
		//DOWNLOAD VMI
		if (uor.hasStringValueOption(CLIArgsReader.DOWNLOAD) && uor.hasStringValueOption(CLIArgsReader.VMI_NAME)){
			String filePath = uor.getStringValue(CLIArgsReader.DOWNLOAD);
			String vmiName = uor.getStringValue(CLIArgsReader.VMI_NAME);
			try {
				log.debug("About to invoke DOWNLOAD operation for VMI " + vmiName + " into local file " + filePath);		
				api.downloadVMI(vmiName, filePath);
				log.debug("Successfully DOWNLOADed VMI " + vmiName + " into " + filePath);
			} catch (Exception e) {
				String msg = "Problems while DOWNLOADING VMIs. Reason: " + e.getMessage();
				e.printStackTrace();
				log.error(msg);
				return;
			}			
		} else 
		
		//VMI ACL
		if (uor.hasBooleanValueOption(CLIArgsReader.VMI_ACL)){		
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

/**
 * 
 * @author German Molto (gmolto@dsic.upv.es)
 * 
 */
package org.grycap.vmrc.client.api;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;
import org.grycap.vmrc.client.exception.VMRCException;
import org.grycap.vmrc.client.transfer.VMRCFTPClient;
import org.grycap.vmrc.client.utils.InstallCert;
import org.grycap.vmrc.client.utils.VMRCClientUtils;
import org.grycap.vmrc.client.utils.XMLOutputter;
import org.grycap.vmrc.client.ws.Application;
import org.grycap.vmrc.client.ws.FtpTransferParams;
import org.grycap.vmrc.client.ws.User;
import org.grycap.vmrc.client.ws.VMRCImpl;
import org.grycap.vmrc.client.ws.VMRCImplService;
import org.grycap.vmrc.client.ws.Vmi;

/**
 * VMRCServerAPI provides a Java API to query the VMRC Server (user-level operations)
 */
public class VMRCServerAPI {
	private String vmrcServiceURI = null;  	
	private String userAuth = null;			
	private String passwordAuth = null;		
	private Logger log; 
	

	  static {
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier(){
	    public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
		  return true;
	    }
	    });
	  }
	  
	  /**
	   * Create a new VMRCServerAPI
	   * @param user The VMRC user name
	   * @param password The password
	   * @param serviceURI The URI to the VMRC Server (i.e. http://localhost:8080/vmrc/vmrc)
	   * @throws VMRCException
	   */
	  public VMRCServerAPI(String user, String password, String serviceURI) throws VMRCException{
		  this.log = Logger.getLogger(getClass());
		  this.userAuth = user;
		  this.passwordAuth = password;
		  this.vmrcServiceURI = serviceURI;
		  log.debug("Using VMRCServerAPI with credentials " + user +  ":" + password + " for " + serviceURI);
		  connect(serviceURI);
	  }
	  
	  /**
	   * Create a new VMRCServerAPI. The "anonymous" user will be employed (restricted privileges apply)
	   * @param serviceURI The URI to the VMRC Server (i.e. http://localhost:8080/vmrc/vmrc)
	   * @throws VMRCException
	   */
	  public VMRCServerAPI(String serviceURI) throws VMRCException{
		  this("anonymous","", serviceURI);
	  }
	  	  
	  /**
	   * Set the URI address to the VMRC service and check if a valid certificate exists. 
	   * @param serviceURI the address to the VMRC service.
	   * For example: https://localhost:8443/VMRCServer/vmrc
	   */
	  private void connect(String serviceURI) throws VMRCException {
		URL url;
		try {		
		  url = new URL(serviceURI);
		} catch (MalformedURLException e) {
	 	  String msg = "Error connecting with the VMRC service. Malformed URL: " + serviceURI;
		  log.error(msg);		 
		  throw new VMRCException(msg,e);
		}
		
		String host = url.getHost();
		int port = url.getPort();
		String protocol = url.getProtocol();		
				
		String certPath = (new File(System.getProperty("user.home") + File.separator + ".vmrc")).getAbsolutePath();
		// check if exist a valid certificate for htpps protocol.
		if (protocol.equals("https")) {
		  String certFilePath = certPath + File.separator + "vmrcert.keystore";
		  if (!new File(certFilePath).exists()) {
		    try {
	          String result = InstallCert.getServerCertificate(host, port, certFilePath);
	         if (result.startsWith("Error")) throw new VMRCException("Problems while installing certificate");
		    } catch (Exception e) {
			  String msg = "Error getting the vmrc service certificate.";
			  log.error(msg);
			  throw new VMRCException(msg,e);			  
		    }
		  }
		  // Use the new keystore created.
	      Properties systemProps = System.getProperties();
		  systemProps.put( "javax.net.ssl.trustStore", certFilePath);
		  System.setProperties(systemProps);
		}

		// Set URL service.
		vmrcServiceURI = serviceURI + "?wsdl";
	  }
	  
	  
	  /**
	   * Get a configured VMRCProxy
	   * @return
	   * @throws VMRCException
	   */
	  protected VMRCImpl getVMRCProxy() throws VMRCException{
		    VMRCImpl vmrcProxy = null;
		    try {
		    	QName q = new QName("http://ws.vmrc.grycap.org/", "VMRCImplService");		    	
		    	VMRCImplService VMRCService = new VMRCImplService(new URL(vmrcServiceURI), q);
		    	vmrcProxy = VMRCService.getVMRCImplPort();
		    } catch (Exception e) {
		      String msg = "Error connecting to the VMRC Server at " + vmrcServiceURI ;
		      log.error(msg);
		      throw new VMRCException(msg,e);
		    }

		    // Authenticator header.
		    Map<String, Object> req_ctx = ((BindingProvider)vmrcProxy).getRequestContext();
		    req_ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, vmrcServiceURI);
		   
		    Map<String, List<String>> headers = new HashMap<String, List<String>>();
		    headers.put("Username", Collections.singletonList(userAuth));
		    headers.put("Password", Collections.singletonList(passwordAuth));
		    req_ctx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);

		  return vmrcProxy;
	  }
	  
	  /**
	   * Adds a VMI into VMRC Server
	   * @param vmid The VMI descriptor, according to the VMRC language
	   * @return
	   * @throws Exception
	   */
	  public String addVMI(String vmid) throws Exception{
		  VMRCImpl vmrcProxy = getVMRCProxy();			  
		  return XMLOutputter.toXMLVMI(vmrcProxy.addVMI(vmid));			   		  		    
	  }
	  	  
	  public List<Vmi> listVMIs() throws Exception{
		  VMRCImpl vmrcProxy = getVMRCProxy();
		  return  vmrcProxy.list();			   	    
      }
	  
	  public List<Application> listApplications() throws Exception{
		  VMRCImpl vmrcProxy = getVMRCProxy();
		  return vmrcProxy.listApplications();
	  }
	  
	  public List<User> listUsers() throws Exception{
		  VMRCImpl vmrcProxy = getVMRCProxy();
		  return  vmrcProxy.listUsers();			   	    
      }
	  
	  public User listUser(String userName) throws Exception{
		  VMRCImpl vmrcProxy = getVMRCProxy();
		  return vmrcProxy.listUser(userName);
	  }
	  
	 	  
	  public List<Vmi> search(String vmiDefinition) throws Exception{					
		  VMRCImpl vmrcProxy = getVMRCProxy();		   		    		    		   
		  return vmrcProxy.search(vmiDefinition);		   
	  }
	  
	  public User addUser(String userName, String userPassword) throws Exception{
		  VMRCImpl vmrcProxy = getVMRCProxy();		   		    		    		  
		  return vmrcProxy.addUser(userName, userPassword);			   		  		    
	  }
	  
	  public void deleteUser(String userName) throws Exception{
		  VMRCImpl vmrcProxy = getVMRCProxy();		   		    		    		  
		  vmrcProxy.deleteUser(userName);			   		  		    
	  }
	  
	  public void deleteVMI(String vmiName) throws Exception{					 
		  VMRCImpl vmrcProxy = getVMRCProxy();		   		    		    		   		  
		  vmrcProxy.delete(vmiName);					
		  
    }
	  
	  public String getOVFByVMI(String vmiName) throws Exception{					 
		  VMRCImpl vmrcProxy = getVMRCProxy();		   		    		    		   		   
		 return vmrcProxy.getOVFByVMI(vmiName);					
		   		   
    }
	  
	  public void changeVMIAcl(String vmiName, String operation, String perm) throws Exception{					 
		  VMRCImpl vmrcProxy = getVMRCProxy();		   		    		    		   		   
		  vmrcProxy.changeVMIAcl(vmiName, operation, perm);							   		   
	  }
	  
	  public void changeUserAcl(String userName, String operation, String perm) throws Exception{					 
		  VMRCImpl vmrcProxy = getVMRCProxy();		   		    		    		   		   
		  vmrcProxy.changeUserAcl(userName, operation, perm);					
		   		   
	  }
	  
	  private FtpTransferParams requestToUploadVMI(String vmiName, String vmiFileName) throws Exception{
		  VMRCImpl vmrcProxy = getVMRCProxy();		  
		  return vmrcProxy.requestToUploadVMI(vmiName,vmiFileName);		  
	  }
	  
	  public void uploadVMI(String vmiName, String pathToVMI) throws Exception{	
		  log.debug("Uploading " + pathToVMI + " for VMI " + vmiName);
		  String fileName = new File(pathToVMI).getName();
		  FtpTransferParams ftp = requestToUploadVMI(vmiName, fileName);		
		  log.debug("Obtained FTP transfer params: " + VMRCClientUtils.toString(ftp));		  
		  VMRCFTPClient.uploadFile(ftp.getHost(),ftp.getPort(), ftp.getUser(), ftp.getPass(), pathToVMI, new File(ftp.getPath()).getName());		  
	  }
	  
	  	  
	  private FtpTransferParams requestToDownloadVMI(String vmiName) throws Exception{
		  VMRCImpl vmrcProxy = getVMRCProxy();
		  return vmrcProxy.requestToDownloadVMI(vmiName);		  
	  }
	  
	  public void downloadVMI(String vmiName, String pathToVMI) throws Exception{
		  log.debug("Downloading VMI " + vmiName + " into " + pathToVMI);
		  FtpTransferParams ftp = requestToDownloadVMI(vmiName);		
		  log.debug("Obtained FTP transfer params: " + VMRCClientUtils.toString(ftp));		  
		  VMRCFTPClient.downloadFile(ftp.getHost(),ftp.getPort(), ftp.getUser(), ftp.getPass(), pathToVMI, ftp.getPath());
	  }
	 
}

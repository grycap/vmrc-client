/**
 * 
 * @author German Molto (gmolto@dsic.upv.es)
 */
package org.grycap.vmrc.client.transfer;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.grycap.vmrc.client.exception.VMRCException;
import org.grycap.vmrc.client.utils.VMRCClientUtils;

/**
 * @author gmolto
 *
 */
public class VMRCFTPClient {
	private static int CODE_ERROR = -1;
	  private static int CODE_FTPFILE = -1;
	  private static int CODE_OK = 1;
	  private static Logger log = Logger.getLogger(VMRCFTPClient.class);

	  public static void uploadFile(String serverName, int serverPort, String userName, String password, String fileLocal, String fileDest) throws VMRCException{
		  log.debug("About to upload file to " + serverName);
		  if (sendFTPRequest(serverName, serverPort, userName, password, fileLocal, fileDest, 0) <0)
			  throw new VMRCException("Unable to upload file");
	  }

	  
	  public static void downloadFile(String serverName, int serverPort, String userName, String password, String fileLocal, String fileDest) throws VMRCException{
		  log.debug("About to upload file to " + serverName);
		  if (sendFTPRequest(serverName, serverPort, userName, password, fileLocal, fileDest, 1) <0)
			  throw new VMRCException("Unable to download file");
	  }

	  /**
	   * Execute a FTP operation.
	   * @param serverName the name of the ftp server.
	   * @param serverPort the number port where is listening the ftp server.
	   * @param userName the user name to access to the ftp server.
	   * @param password the password to access to the ftp server.
	   * @param fileLocal the path to the source file to transfer.
	   * @param fileDest the path to the destine file to transfer.
	   * @param operation the type of operation to realize with the ftp server.
	   * @return
	   */
	  private static int sendFTPRequest(String serverName, int serverPort, String userName, String password, String fileLocal, String fileDest, int operation) {
		log.debug("Sending FTP request to " + userName + ":" + password + "@" + serverName + ":" + serverPort + ", local = " + fileLocal + ", dest= " + fileDest);
	    FTPClient ftp = new FTPClient();
		int resultCode = CODE_ERROR;
		boolean binaryTransfer = true;
		try {
		  int reply;
		  ftp.connect(serverName, serverPort);
		  reply = ftp.getReplyCode();
		  if (!FTPReply.isPositiveCompletion(reply)) {
		    ftp.disconnect();
		    return CODE_FTPFILE ;
		  }
		} catch (IOException e) {
		  log.fatal("Caught IOException while uploading: " + e.getMessage());
		  e.printStackTrace();
		  if (ftp.isConnected()) {
		  	try {
		      ftp.disconnect();
		    } catch (IOException f) {
		      return CODE_FTPFILE ;
		    }
		  }
		  return CODE_FTPFILE;
		}
		try {
		  if (!ftp.login(userName, password)) {
			log.fatal("Unable to connect to FTP server with the specified credentials");
		    ftp.logout();
		    return CODE_FTPFILE;
		  }
	      if (binaryTransfer) ftp.setFileType(FTP.BINARY_FILE_TYPE);
	        ftp.enterLocalPassiveMode();
	        // Operation 0 -> store.
	        // Operation 1 -> retrieve.
	        // Operation 2 -> rename.
	        switch (operation) { 
	          case 0:	        	
		      // Store to the ftp server.
	            InputStream input = new FileInputStream(fileLocal);
	            if (ftp.storeFile(fileDest, input)) resultCode = CODE_OK;
	            else resultCode = CODE_FTPFILE;
	            int reply = ftp.getReplyCode();
		        input.close();
		        break;
		      case 1:
		      // Retrieve from the ftp server.
		        OutputStream output;
		        output = new FileOutputStream(fileLocal);
		        if (ftp.retrieveFile(fileDest, output)) resultCode = CODE_OK;
		        else resultCode = CODE_FTPFILE;
		        output.close();
		        break;
		      case 2:
		      // Rename a file in the ftp server.
	           	ftp.deleteFile(fileDest);
	           	if (ftp.rename(fileLocal, fileDest )) resultCode = CODE_OK;
	           	else resultCode = CODE_FTPFILE;
		       	break;
		    }
		    ftp.logout();
		    return resultCode;
		  } catch (FTPConnectionClosedException e) {
			log.warn(e.getMessage());
			e.printStackTrace();
		    return CODE_FTPFILE;
		  } catch (IOException e) {
			log.warn(e.getMessage());
		   	e.printStackTrace();
	       	return CODE_FTPFILE;
	      } finally {
		   	if (ftp.isConnected()) {
			try {
		 	  ftp.disconnect();
		    } catch (IOException f) {
		      log.warn(f.getMessage());
		      f.printStackTrace();
		      return CODE_FTPFILE;
		    }
		  }
	    }
	  }

	  /* (non-Javadoc)
	   * @see grycap.VMRC.Transfer.VMRTransferI#uploadFiles(java.lang.StringBuffer, java.lang.String)
	   */
	  public int uploadFiles(StringBuffer params, String imagesDirPath) {
		log.debug("Invoked FTPClient_::uploadFiles with " + params + " and imagesDirPath = " + imagesDirPath);
		int port = 0;
	    int p1 = params.toString().indexOf(";");
	    if (p1 < 0) return -1;
	    // If exist a valid session code then continue.
	    String idSession = params.toString().substring(0,p1);
	    params.delete(0, p1+1);

	    // Get username, password from  the connection chain.
	    String[] connectParams = params.toString().split(";");
	    if (connectParams.length != 4) return -1;
	    port = Integer.parseInt(connectParams[1]); 

	     
	    // Upload to the ftp server.
	    String[] lstFiles = VMRCClientUtils.listDirectory(imagesDirPath);
	    if (lstFiles == null) return -1; 
	    
	    for (int i=0; i < lstFiles.length; i++) {
	   	  String fileDest = lstFiles[i];
	      String fileSource = imagesDirPath + File.separator + fileDest;
	      if (sendFTPRequest(connectParams[0], port, connectParams[2], connectParams[3], fileSource, fileDest, 0) < 0) return -1;
	    }
	    params.delete(0,params.length());
	    params.append(idSession);
	    return 1;
	  }
	  

	  /* (non-Javadoc)
	   * @see grycap.VMRC.Transfer.VMRTransferI#downloadFiles(java.lang.StringBuffer, java.lang.String)
	   */
	  public int downloadFiles(StringBuffer params, String imagesDirPath) {
		int port = 0;
	    int p1 = params.toString().indexOf(";");
	    if (p1 < 0) return -1;
	    // If exist a valid session code then continue.
	    String idSession = params.toString().substring(0,p1);
	    params.delete(0, p1+1);

	    // Retrieve the list of files.
	    p1 = params.toString().indexOf(";");
	    if (p1 < 0) return -1;
	    String files_ = params.toString().substring(0,p1);
	    params.delete(0, p1+1);

	    // Get de username, password connection parameters.
	    String[] connectParams = params.toString().split(";");
	    if (connectParams.length != 4) return -1;
	    port = Integer.parseInt(connectParams[1]); 

	    String files[] = files_.split(",");
	    for (int i=0; i<files.length; i++) {
	      String fileDest = files[i];
	   	  String fileSource = imagesDirPath + File.separator + fileDest;
	   	  sendFTPRequest(connectParams[0], port, connectParams[2], connectParams[3], fileSource, fileDest, 1);
	    }
	    params.delete(0,params.length());
	    params.append(idSession);
	    return 1;    
	  }
}

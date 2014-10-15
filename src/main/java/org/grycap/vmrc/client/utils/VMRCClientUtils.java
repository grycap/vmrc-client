/**
 * 
 * @author German Molto (gmolto@dsic.upv.es)
 */
package org.grycap.vmrc.client.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.grycap.vmrc.client.ws.Application;
import org.grycap.vmrc.client.ws.FtpTransferParams;

/**
 * @author gmolto
 *
 */
public class VMRCClientUtils {
	
	/**
	   * Puts the content of a text-based file into a String variable.
	   * @param textFilePath The path to the text-based file
	   * @return A String with the context of the file.
	   * @throws IOException
	   */
	  public static String textFileToString(String textFilePath) throws IOException {
	    String result;

	    FileInputStream fis = new FileInputStream(textFilePath);
	    byte[] b = new byte[fis.available()];
	    fis.read(b);
	    fis.close();
	    result = new String(b);

	    return result;
	  }
	  
	  public static String[] listDirectory(String srcDirectory) {
		    File dir = new File(srcDirectory);
		    String[] files = null;
		    if (! dir.isDirectory() ) {
		    	return files;
		    } else {
		      files = dir.list();
		      return files;
		    }
		  }
	  
	  public static String toString(FtpTransferParams ftp){
		 return "ftp://" + ftp.getUser() + ":" + ftp.getPass() + "@" + ftp.getHost() + ":" + ftp.getPort() + "/" + ftp.getPath();
	  }
	  
	  public static String toString(List<Application> l){
		  String s = "";
		  for (Application app : l) s+= toString(app) + " ";
		  return s;
	  }
	  
	  public static String toString(Application app){
		  return "[" + app.getName() + "," + app.getVersion() + "]";
	  }
	  
	  
	  public static String replaceVMIName(String vmiDescriptor, String vmiName){
		  Scanner scanner = new Scanner(vmiDescriptor); 
		  scanner.findWithinHorizon("system.name", 100);
		  scanner.next();
		  String oldVmiName = scanner.next();
		  return vmiDescriptor.replace(oldVmiName, vmiName);
	  }

}

/**
 * 
 * @author German Molto (gmolto@dsic.upv.es)
 */
package org.grycap.vmrc.client.test;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.grycap.vmrc.client.api.VMRCServerAPI;
import org.grycap.vmrc.client.utils.VMRCClientUtils;
import org.grycap.vmrc.client.ws.FtpTransferParams;


/**
 * @author gmolto
 *
 */
public class TestVMRCServerHibClient {
	
	public static void main(String[] args) throws Exception{
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
		String url = "http://localhost:8080/vmrc/vmrc";
	//	String url = "http://dellblade.i3m.upv.es:8080/vmrc/vmrc";
		VMRCServerAPI vmrcApi = new VMRCServerAPI("admin","passwd1",url);
		//vmrcApi.connect();
		
//		String filePath = "/Users/gmolto/Documents/workspace/VMRCClientHib/src/test/vmis/kvm1.vmi";
//		String vmi = VMRCClientUtils.textFileToString(filePath);
		
//		String filePath2 = "/Users/gmolto/Documents/workspace/VMRCClientHib/src/test/vmis/sample1.vmiq";
//		String vmiq = VMRCClientUtils.textFileToString(filePath2);
		
//	System.out.println(vmi);
	//	String vmix = vmrcApi.addVMI(vmi);
		//vmrcApi.deleteVMI("MyImage4");
		//vmrcApi.uploadVMI("MyImage6", "/tmp/img.qcow2");
		
		//System.out.println("ftp://" + ftp.getUser() + ":" + ftp.getPass() + "@" + ftp.getHost() + "/" + ftp.getPath());
		
		//System.out.println("LIST AFTER ADD: " + vmrcApi.listVMIsAsXML());
		
	//	System.out.println("OVF: " + vmrcApi.getOVFByVMI("MyImage4"));
		
		
		
		//System.out.println("LIST AFTER DELETE" + vmrcApi.listVMIsAsXML());
		
		System.out.println(vmrcApi.listVMIs());
		//
		
		//System.out.println("Obtained list: "  + vmrcApi.queryAsXML(vmiq));
		
		//System.out.println(vmix);
		
		
		
	}

}

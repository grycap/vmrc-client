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
package org.grycap.vmrc.client.api;

import java.util.List;

import org.grycap.vmrc.client.exception.VMRCException;
import org.grycap.vmrc.client.utils.TabbedOutputter;
import org.grycap.vmrc.client.utils.XMLOutputter;
import org.grycap.vmrc.client.ws.User;
import org.grycap.vmrc.client.ws.Vmi;

import com.thoughtworks.xstream.XStream;

/**
 * @author gmolto
 *
 */
public class FormattedVMRCServerAPI extends VMRCServerAPI {
	private int format; 
	public final static int FORMAT_TABBED = 0;
	public final static int FORMAT_XML = 1;
	
	/**
	 * @param serviceURI
	 * @throws VMRCException
	 */
	public FormattedVMRCServerAPI(String serviceURI, int format) throws VMRCException {
		super(serviceURI);
		this.format = format;
	}
	
	 public FormattedVMRCServerAPI(String user, String password, String serviceURI, int format) throws VMRCException{
		 super(user,password, serviceURI);
		 this.format = format;
	 }
	 
	 public String formattedListVMIs() throws Exception{
		  switch(format){		  	
		  	case FORMAT_TABBED:return TabbedOutputter.toStringVMIs(listVMIs());
		  	case FORMAT_XML: 
		  	default: return XMLOutputter.toXMLVMIs(listVMIs());
		  }
	  }
	 
	 public String formattedSearchVMIs(String vmid) throws Exception{
		 switch(format){		  	
		  	case FORMAT_TABBED:return TabbedOutputter.toStringVMIs(search(vmid));
		  	case FORMAT_XML: 
		  	default: return XMLOutputter.toXMLVMIs(search(vmid));
		  }
	 }
	 
	 public String formattedListUser(String userName) throws Exception{
		 switch(format){		  	
		  	case FORMAT_TABBED:return TabbedOutputter.toStringUser(listUser(userName));
		  	case FORMAT_XML: 
		  	default: return XMLOutputter.toXMLUser(listUser(userName));
		  }		 
	 }
	 
	 
	 
	 
	 
	 public String formattedListUsers() throws Exception{
		 switch(format){		  	
		  	case FORMAT_TABBED:return TabbedOutputter.toStringUsers(listUsers());
		  	case FORMAT_XML: 
		  	default: return XMLOutputter.toXMLUsers(listUsers());
		  }
		 
	 }
	 
	
}

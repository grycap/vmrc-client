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
package org.grycap.vmrc.client.utils;

import java.util.List;

import org.grycap.vmrc.client.ws.User;
import org.grycap.vmrc.client.ws.Vmi;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * @author gmolto
 *
 */
public class XMLOutputter {

	private static XStream getConfiguredXStream(){
		XStream xstream = new XStream();
		xstream.alias("VMI", org.grycap.vmrc.client.ws.Vmi.class);
		xstream.alias("Application",org.grycap.vmrc.client.ws.Application.class);
		xstream.alias("User", org.grycap.vmrc.client.ws.User.class);	
		return xstream;
	}
			
	public static String toXMLVMIs(List<Vmi> l){				 
		return getConfiguredXStream().toXML(l);			
	}
	
	public static String toXMLVMI(Vmi v){			 
		return getConfiguredXStream().toXML(v);
	}
	
	
	 public static String toXMLUsers(List<User> l) throws Exception{				  
		  return getConfiguredXStream().toXML(l);		 
    }
	 
	 public static String toXMLUser(User u) throws Exception{				  
		  return getConfiguredXStream().toXML(u);		 
   }
	
}

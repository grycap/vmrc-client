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
import org.grycap.vmrc.client.ws.Os;
import org.grycap.vmrc.client.ws.User;
import org.grycap.vmrc.client.ws.Vmi;

public class TabbedOutputter {
	
	public  static String toStringVMIs(List<Vmi> l){		
		String frmt = "%1$15s %2$10s %3$9s %4$4s %5$9s %6$20s %7$30s\n";
		String res = String.format(frmt, "VMI", "OWNER", "HYPERVISOR", "ARCH","DISK", "OS", "APPS");
		res += "---------------------------------------------------------------------------\n";
		for (Vmi v: l)
			res += String.format(frmt, v.getName(),v.getOwner(), v.getHypervisor(), v.getArch(), v.getDiskSize(), toString(v.getOs()), VMRCClientUtils.toString(v.getApplications()));		
		return res;
	}
	
	public  static String toStringUsers(List<User> l){		
		String frmt = "%1$12s %2$12s\n";
		String res = String.format(frmt, "USER", "PASSWORD");		
		for (User u: l)
			res += toStringUser(u) ;		
		return res;
	}
	
	public static String toStringUser(User u){
		String frmt = "%1$12s %2$12s\n";
		return String.format(frmt, u.getUserName(), u.getPassword());
	}
	
	
	public static String toString(Os os){
		return "[" + os.getName() + "," + os.getFlavour() + "," + os.getVersion() +"]";
	}
	
	

}

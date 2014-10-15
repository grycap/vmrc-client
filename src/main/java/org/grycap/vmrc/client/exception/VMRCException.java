/**
 * 
 * @author German Molto (gmolto@dsic.upv.es)
 */
package org.grycap.vmrc.client.exception;

/**
 * @author gmolto
 *
 */
public class VMRCException extends Exception{
	public VMRCException(String msg){
		super(msg);
	}
	
	public VMRCException(String msg, Throwable t){
		super(msg,t);
	}

}

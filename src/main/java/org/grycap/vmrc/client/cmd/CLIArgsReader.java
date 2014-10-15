package org.grycap.vmrc.client.cmd;

import java.util.Hashtable;


public class CLIArgsReader {
	private Hashtable<String,String> hTable;
	private Hashtable<String, CmdLineParser.Option> htStringOptions; 
	  	
	//Arguments that are followed by a String value (The common args between admin/user CLI first)
	 public final static int URI = 0;
	 public final static int AUTH_FILE = 1;
	 
	 public final static int ADD = 2;	 
	 public final static int UPLOAD = 3;
	 public final static int SEARCH = 4;
	 public final static int DELETE = 5;	 
	 public final static int VMI_NAME = 6;
	 public final static int DOWNLOAD = 7;
	 
	 
	 //Boolean arguments  (The common args between admin/user CLI first)
	 public final static int VMI_ACL = 0;
	 public final static int XML = 1;
	 public final static int DEBUG = 2;	 
	 public final static int LIST = 3;
	 public final static int OVF = 4;
	 
	 
	 private final static String UNSPECIFIED_VALUE= "A" + 0x929288;
	
	 protected String[] stringOptionsNames;
	 protected char[] stringOptionsShortNames;
	 	 
	 protected String[] booleanOptionsNames;
	 protected char [] booleanOptionsShortNames;
	 
	 private static String[] remainingArgs;
	 
	public CLIArgsReader(String[] args){
		configureOptions();
		this.hTable = new Hashtable<String,String>();
		this.htStringOptions = new Hashtable<String, CmdLineParser.Option>();		
		parseCommandLine(args);
	}
	  	
	protected void configureOptions(){
		//Order matters
		stringOptionsNames = new String[]{"uri","authFile","add","upload", "search","delete","vmi","download"};
		stringOptionsShortNames = new char[]{'r','z','a','u','s','d','v','w'};
		
		booleanOptionsNames  = new String[]{"vmiAcl","xml","debug","list","ovf"};
		booleanOptionsShortNames = new char[]{'c','x','b','l','o'};
		
	}
  
 
	protected void printUsage() {
	   String ls = System.getProperty("line.separator");
	 		   
	   String usage = "vmrc.sh";
	    
	   for (int i = 0 ; i < stringOptionsNames.length; i++) usage += " [-" + stringOptionsShortNames[i] + ",--" + stringOptionsNames[i] + "]";
	   for (int i = 0 ; i < booleanOptionsNames.length; i++) usage += " [-" + booleanOptionsShortNames[i] + ",--" + booleanOptionsNames[i] + "]";
	   usage += ls;
	   usage += "[-a, --add file.vmi [-v, --vmi vmiName]]" + ls;
	   usage += "[-s, --search file.vmiq]" + ls;
	   usage += "[-d, --delete vmiName]" + ls;
	   usage += "[-r, --uri vmrc_service_uri]" + ls;
	   usage += "[-z, --authFile vmrc_auth" + ls;
	   usage += "[-u, --upload file.img [-v, --vmi vmiName]]" + ls;
	   usage += "[-w, --download dst_file.img [-v, --vmi vmiName]]" + ls;
	   usage += "[-l, --list] " + ls;
	   usage += "[-l, --list] [--vmi vmiName] " + ls;
	   usage += "[-l, --list] [--vmi vmiName] [--ovf]" + ls;
	   usage += "[-x, --xml]" + ls;
	   usage += "[-b, --debug]" + ls;
	   usage += "[-c, --vmiAcl vmiName operation perm], operation=[add|list|upload|search|delete] perm =[all|owner] " + ls;
	   
	  
	   System.err.println(usage);
	  }
	
	
	public void parseCommandLine(String[] args) {
		CmdLineParser parser;
		
		if ( args.length <=0 ){ 
			printUsage();
			System.exit(-1);
		}
		
	/*
        1. Configure the command-line parser.
     */
		parser = new CmdLineParser();
		
		//String options
		for (int i = 0; i< stringOptionsNames.length; i++){
			CmdLineParser.Option option = parser.addStringOption(stringOptionsShortNames[i], stringOptionsNames[i]);
			htStringOptions.put(stringOptionsNames[i], option);
		}
	    
		//Boolean options
		for (int i = 0; i< booleanOptionsNames.length; i++){
			CmdLineParser.Option option = parser.addBooleanOption(booleanOptionsShortNames[i], booleanOptionsNames[i]);
			htStringOptions.put(booleanOptionsNames[i], option);
		}
		
		
	    /*
	      2. Parse the command line.
	     */
	    try {
	      parser.parse(args);
	    }
	    catch (Exception ex) {
	      System.err.println(ex.getMessage());
	      printUsage();
	      System.exit( -1);
	    }

	    /*
	        3. Get the actual values from the command line.
	     */
	    
	    //String options
	    for (int i = 0; i < stringOptionsNames.length; i++){
	       Object value = parser.getOptionValue(htStringOptions.get(stringOptionsNames[i]));
	       if (value == null) value = UNSPECIFIED_VALUE;
	       hTable.put(stringOptionsNames[i], (String) value);
	    }
	    
	    //Boolean options
	    for (int i = 0; i < booleanOptionsNames.length; i++){
		       Object value = parser.getOptionValue(htStringOptions.get(booleanOptionsNames[i]), Boolean.FALSE);
		       if (value == Boolean.FALSE) value = "false";
		       else value = "true";
		       hTable.put(booleanOptionsNames[i], (String) value);
		    }
	    
	    this.remainingArgs = parser.getRemainingArgs();
	   
	}
	
	public String[] getRemainingArgs(){
	     return this.remainingArgs;
	}
	
	public boolean hasStringValueOption(int option){
		return !getStringValue(option).equals(UNSPECIFIED_VALUE);
	}
	
	public boolean hasBooleanValueOption(int option){
		return getBooleanValue(option) != false;
	}
	
	/**
	 * Get the command line value of the option. If the user did not specify such value, it 
	 * supplies a default one
	 * @param optionName
	 * @return
	 */
	public String getStringValue(int option){
		return (String) hTable.get(stringOptionsNames[option]);		
	}
	
	public boolean getBooleanValue(int option){
		return Boolean.parseBoolean(hTable.get(booleanOptionsNames[option]));		
	}
	
	/**
	 * Get the command line value as a primitive int
	 * @param option
	 * @return
	 */
	public int getIntValue(int option){
		return Integer.parseInt(getStringValue(option));
	}

}

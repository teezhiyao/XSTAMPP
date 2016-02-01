package xstpa.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;

import xstpa.ui.dialogs.EditWindow;

public class ACTSController {
	
	private final static String INPUT = Platform.getInstanceLocation().getURL().getPath()+".metadata"+File.separator+"input.txt";
	
	private final static String INPUT2 =  Platform.getInstanceLocation().getURL().getPath()+".metadata"+File.separator+"input2.txt";
	
	private final static String OUTPUT =  Platform.getInstanceLocation().getURL().getPath()+".metadata"+File.separator+"output.txt";
	
	private final static String OUTPUT2 =  Platform.getInstanceLocation().getURL().getPath()+".metadata"+File.separator+"output2.txt";

	/**
	 * Writing the later used input file for ACTS
	 */
	public static Boolean writeFile(Boolean defaultSettings,boolean isProvidedContext,List<ProcessModelVariables> items) {
		PrintWriter writer = null;
		String paramName = null;
		try {
			if (isProvidedContext) {
				writer = new PrintWriter(INPUT, "UTF-8");
			}
			else {
				writer = new PrintWriter(INPUT2, "UTF-8");
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
			return false;
		}
		writer.println("[System]");
		
		writer.println("Name : test");
		// Print the Parameters
		writer.println("");
		writer.println("[Parameter]");
		for (ProcessModelVariables entry : items) {
			List<String> values = entry.getValues();
			 if (entry.getName().contains(" ")) {
				 paramName = entry.getName().replace(" ", "_");
			 }
			 else {
				 paramName = entry.getName();
			 }
			writer.write(paramName+ " (enum)" + " : ");
			for (int i = 0, size = entry.getSizeOfValues();i<size; i++) {
				if (i < entry.getSizeOfValues()-1) {
					writer.write(values.get(i) + ", " );
					
				}
				else {
					writer.write(values.get(i));
				}
			}
			writer.println("");
			
		}
		// Print the Relations
		writer.println("");
		writer.println("[Relation]");
		if (!defaultSettings) {
			for (int entry = 0; entry<EditWindow.relations.size(); entry++) {
				String temp = "";
		    	List<String> tempList = EditWindow.relations.get(entry).getVariables();
		    	for (int i =0; i<tempList.size(); i++) {
		    		
		    		if (i == tempList.size()-1) {
		    			temp = temp.concat(tempList.get(i).replace(" ", "_"));
		    		}
		    		else {
		    			temp = temp.concat(tempList.get(i).replace(" ", "_").concat(", "));
		    		}
		    	}
				writer.println("R"+entry+ " : ("+temp+", "+EditWindow.relations.get(entry).getStrength()+")");
			}
		}

		// Print the Constraints
		writer.println("");
		writer.println("[Constraint]");
		if (!defaultSettings) {
			for (String entry : EditWindow.constraints) {
				writer.println(entry);
			}
		}

		writer.println("");
		
		writer.close();
		return true;
		
	}
	
	public static List<ProcessModelVariables> open(Boolean defaultSettings,int columns,boolean providedContext,ControlActionEntry context) {
		// Run ACTS in a separate system process
		Process proc;
		String modes;
		if (columns-2 <= 6) {
			modes = " -Ddoi=" + Integer.toString(columns-2) + " ";	
		}
		else {
			modes = " -Ddoi=6 " ;	
		}
		
		try {
			if (!defaultSettings) {
				for (int i=0; i<EditWindow.modes.size(); i++) {
					
						if (i==0) {
							modes = " -Dalgo="+EditWindow.modes.get(i)+" ";
						}
					
						else if (i==1) {
							if (EditWindow.relations.isEmpty()) {
								modes = modes.concat("-Ddoi="+EditWindow.modes.get(i)+" ");
							}
							else {
								modes = modes.concat("-Ddoi=-1 ");
							}
							
						}
						else if (i==2) {
							modes = modes.concat("-Dmode="+EditWindow.modes.get(i)+" ");
						}
						else if (i == 3) {
							modes = modes.concat("-Dchandler="+EditWindow.modes.get(i)+" ");
						}
				}
				// clear so that the default mode gets selected again
//				editWindow.modes.clear();
			}
			String location = xstampp.Activator.getDefault().getPreferenceStore().getString("ACTS_Path");
			//location = location.substring(1, location.length());
			if (providedContext) {
				proc = Runtime.getRuntime().exec("java"+modes+"-jar " +location+" cmd "+INPUT+ " " + OUTPUT);
			}
			else {
				proc = Runtime.getRuntime().exec("java"+modes+"-jar " +location+" cmd "+INPUT2+ " " + OUTPUT2);
			}
			
			
			// Then retreive the process output
			InputStream in = proc.getInputStream();
			proc.getErrorStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
	        System.out.println(out.toString());   //Prints the string content read from input stream
	        reader.close();

	        return getEntrysFromFile(reader,providedContext,context);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return new ArrayList<>();
		
	}
	
	
	private static List<ProcessModelVariables> getEntrysFromFile (BufferedReader reader,boolean providedContext,ControlActionEntry context) {
		List<ProcessModelVariables> contextEntries = new ArrayList<ProcessModelVariables>();
		try {
			
			if (providedContext) {			
				reader = new BufferedReader(new FileReader(OUTPUT));
			}
			else {
				reader = new BufferedReader(new FileReader(OUTPUT2));
	
			}
			// go to the fourth line in the file to read the number of Configurations
			reader.readLine();
			reader.readLine();
			String line = reader.readLine();
			
			char temp = line.charAt(line.length()-1);
			
			int paramCount = Character.getNumericValue(temp);
			line = reader.readLine();
			temp = line.charAt(line.length()-1);
			Character.getNumericValue(temp);
			//contextRightTable.setVisible(false);
			while ((line = reader.readLine()) != null) {
				if (line.contains("Configuration #")) {
					ProcessModelVariables entry = new ProcessModelVariables();
					entry.setLinkedControlActionName(context.getControlAction(), null);
					entry.setLinkedControlActionID(context.getId());
					temp = line.charAt(line.length()-2);
//					System.out.println(temp);
					Character.getNumericValue(temp);
					reader.readLine();
					List<String> values = new ArrayList<String>();
					List<String> variables = new ArrayList<String>();
					for (int i = 0; i<paramCount; i++) {
						line = reader.readLine();
						temp = line.charAt(0);
						Character.getNumericValue(temp);
						try {
							line = line.substring(line.indexOf("=")+2, line.length());
							variables.add(line.substring(0, line.indexOf("=")));
							//entry.setName(line.substring(line.indexOf("=")+2, line.length()));
							line = line.substring(line.indexOf("=")+1, line.length());
						}
						catch (StringIndexOutOfBoundsException siobe) {
							
						}
						entry.addValue(line);
						
						values.add(line);
									
					}
					entry.setPmValues(values);
					entry.setPmVariables(variables);
					contextEntries.add(entry);
				}
				
			}
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return contextEntries;
	}
}
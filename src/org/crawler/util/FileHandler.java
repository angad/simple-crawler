package org.crawler.util;

import java.io.BufferedWriter;
import java.io.FileWriter;


import java.util.logging.Level;
import java.util.logging.Logger;

public class FileHandler {

	private final static Logger LOGGER = Logger.getLogger(FileHandler.class.getName()); 
	private BufferedWriter out;
	
	public FileHandler() {
		try {
		    FileWriter fstream = new FileWriter("result.log", true);
		    out = new BufferedWriter(fstream);
		    out.write("--------------Beginning crawling-----------------");
		    out.close();
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
	
	
}

package org.crawler.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple file handler which writes to a result log file
 * @author angadsingh
 *
 */
public class FileHandler {

	Logger LOGGER = Logger.getLogger(FileHandler.class.getName());
 
	private BufferedWriter out;
	
	public FileHandler() {
		try {
		    FileWriter fstream = new FileWriter("result.log", true);
		    out = new BufferedWriter(fstream);
		    out.write("--------------Beginning crawling-----------------\n");
		    out.flush();
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public void write(String str) {
		if(out!=null) {
			try {
				out.write(str + "\n");
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

package org.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Main {

	private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws IOException {
		Crawler crawler = Crawler.getInstance();
		LOGGER.info("Starting crawler");
		
		System.out.println("Please enter the starting URL. No http://. Just the address");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = br.readLine();
		crawler.beginCrawling(line);
	}	
}
	
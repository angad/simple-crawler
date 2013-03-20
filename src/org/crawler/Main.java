package org.crawler;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {

	private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws IOException {
		Crawler crawler = Crawler.getInstance();
		LOGGER.info("Starting crawler");
		crawler.beginCrawling("nus.edu.sg");
	}	
}

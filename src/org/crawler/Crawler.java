package org.crawler;

import java.util.Collections;
import java.util.HashMap;
/**
 * 
 * @author angad
 *
 */
import java.util.Map;
import java.util.logging.Logger;

/**
 * 
The crawler should store the following in a database or text file and should display them. 
Base URL of the web application / server
Response time of servers [time from sending a request to receiving the reply]
The crawler should not make more than one request to the same server.
Try to keep the request rate of your crawler low by introducing some delay between requests. Sending request to same web server several time may result in misinterpreting
the crawler as a DoS attack. If needed, you may run the crawler for long time.
You should do the assignment by creating the proper HTTP messages as per the RFCs. You should not call/use any existing web crawler class or tool in your application.
You are allowed to use any free/open-source classes/tools/packages for conversion
(such as HTML to XML) and parsing.
Your codes should be well written and well commented. All exceptions must be handled.
 *
 */


public class Crawler {

	Logger LOGGER = Logger.getLogger(Crawler.class.getName());
	private static Map<String, Double> crawled = Collections.synchronizedMap(new HashMap<String, Double>());
	private static Map<String, Boolean> toCrawl = Collections.synchronizedMap(new HashMap<String, Boolean>());
	
	private CrawlerManager manager;
	private static Crawler instance;
	
	public static Crawler getInstance() {
		if(instance == null) {
			instance = new Crawler();
		}
		return instance;
	}
	
	private Crawler() {
		manager = CrawlerManager.getInstance();
	}
	
	public void beginCrawling(String seedURL) {
		manager.startCrawler(seedURL);
	}
	
	public void queue(String url) {
		
		
	}
}

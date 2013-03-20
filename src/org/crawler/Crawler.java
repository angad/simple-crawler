package org.crawler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.crawler.util.FileHandler;
/**
 * 
 * @author angad
 *
 */

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

	/*
	 * A hashmap of crawled URLs with their response time
	 */
	private static Map<String, Long> crawled = Collections.synchronizedMap(new HashMap<String, Long>());
	
	/*
	 * A Set of URLs that are yet to be crawled
	 */
	private static SortedSet<String> toCrawl = Collections.synchronizedSortedSet(new TreeSet<String>());
	
	/*
	 * A list of URLs that are currently being crawled
	 */
	private static List<String> crawling = Collections.synchronizedList(new LinkedList<String>());
	
	private CrawlerManager manager;
	
	private FileHandler fh;

	/*
	 * Singleton
	 */
	private static Crawler instance;
	public static Crawler getInstance() {
		if(instance == null) {
			instance = new Crawler();
		}
		return instance;
	}

	/*
	 * Private Constructor
	 */
	private Crawler() {
		manager = CrawlerManager.getInstance();		
		fh = new FileHandler();
	}

	/*
	 * Begin crawling from a seed URL
	 */
	public void beginCrawling(String seedURL) {
		try {
			crawling.add(seedURL);
			manager.startCrawler(seedURL);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Enqueue a set of urls that are returned by a Crawl
	 * @param urls
	 */
	public void queue(Set<String> urls) {
		Iterator<String> it = urls.iterator();
		while(it.hasNext()) {
			String url = it.next();
			queue(url);
		}
//		LOGGER.info("Crawler queue remaining " + toCrawl.size());
	}

	/**
	 * Called when a crawler has finished crawling a URL
	 * @param url
	 * @param response
	 */
	public void crawled(String url, long response) {
		if(!crawled.containsKey(url)) {
			crawled.put(url, response);
			crawling.remove(url);
//			LOGGER.info("Crawled " + crawled.size() + " URLs");
			fh.write(response + "\t" + url);
		} else {
		}
	}

	/**
	 * Enqueue's a URL
	 * @param url
	 */
	public void queue(String url) {
		//if currently crawling or has already been crawled. then ignore
		if(!(crawled.containsKey(url) || crawling.contains(url))) {
			toCrawl.add(url);
			try {
				//once added to the set, probe to start a new thread
				manager.probe();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Manager calls this to get the next pending
	 * @return
	 */
	public String getNext() {
//		LOGGER.info("Crawler queue remaining " + toCrawl.size());
		if(toCrawl.size() == 0) return null;
		String url = toCrawl.first();
		toCrawl.remove(url);
		crawling.add(url);
		return url;
	}
}

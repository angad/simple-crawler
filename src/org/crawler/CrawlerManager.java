package org.crawler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class CrawlerManager {

	
	Logger LOGGER = Logger.getLogger(CrawlerManager.class.getName());

	// list of started threads
	private List<CrawlerThread> crawlerThreadsList;
	
	private static CrawlerManager instance;
	
	public static CrawlerManager getInstance() {
		if(instance == null) {
			instance = new CrawlerManager();
		} 
		return instance;
	}
	
 	private CrawlerManager() {
 		crawlerThreadsList = Collections.synchronizedList(new ArrayList<CrawlerThread>());
	}	
 	
 	public void startCrawler(String src) {
 		CrawlerThread t = new CrawlerThread(src);
 		crawlerThreadsList.add(t);
 		t.run();
 	}
	
 	public void finishCrawler(CrawlerThread t) {
 		synchronized(crawlerThreadsList) {
 			crawlerThreadsList.remove(t);
 		}
 	}
	
}

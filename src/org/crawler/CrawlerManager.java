package org.crawler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class CrawlerManager {

	Logger LOGGER = Logger.getLogger(CrawlerManager.class.getName());
	
	private static CrawlerManager instance;
	private Crawler crawler;
	private ExecutorService executor;

	public static CrawlerManager getInstance() {
		if(instance == null) {
			instance = new CrawlerManager();
		} 
		return instance;
	}

	private CrawlerManager() {
		executor = Executors.newFixedThreadPool(Config.THREADS);
	}	

	public void startCrawler(String src) throws InterruptedException {
//		LOGGER.info("Received request to start new crawler for " + src);
		Runnable crawlerThread = new CrawlerThread(src);
		executor.execute(crawlerThread);
	}

	public void probe() throws InterruptedException {
		LOGGER.info(Thread.currentThread().getName() + " Probing to start new thread");
		if(crawler == null) crawler = Crawler.getInstance();
		Thread.sleep(Config.DELAY);
		startCrawler(crawler.getNext());
	}

	public void finishCrawler(CrawlerThread t) {
//		LOGGER.info("Thread finished.");
		if(crawler == null)	crawler = Crawler.getInstance();
		crawler.queue(t.getLinks());
		crawler.crawled(t.getURL(), t.getResponseTime());
	}
}

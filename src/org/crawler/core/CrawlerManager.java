package org.crawler.core;

import org.crawler.util.Util;

import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

public class CrawlerManager {

	private static Logger LOGGER = Logger.getLogger(CrawlerManager.class.getName());
	
	private static CrawlerManager instance;
	private Crawler crawler;
	private ThreadPoolExecutor executor;
    private CrawlerThreadFactory factory;

    public static CrawlerManager getInstance() {
        if(instance == null) {
            LOGGER.severe("CrawlerManager instance is null");
            return null;
        } else if(instance.factory == null) {
            LOGGER.severe("CrawlerManager cannot get instance because factory is null");
            return null;
        } else {
            return instance;
        }
    }


	public static CrawlerManager getInstance(CrawlerThreadFactory factory) {
		if(instance == null) {
			instance = new CrawlerManager(factory);
		} 
		return instance;
	}

	private CrawlerManager(CrawlerThreadFactory factory) {
        this.factory = factory;
        LOGGER.info("New CrawlerManager created!");
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(BaseConfig.THREADS);
	}

    public int getQueueSize() {
        if(executor!=null) {
            return executor.getQueue().size();
        }
        else return 0;
    }

    public boolean isShutdown() {
        if(executor == null) return true;
        else return executor.isShutdown();
    }

    public void startCrawler(URL url) throws InterruptedException {
        if(executor.isShutdown()) {
            //cant take in more requests
            return;
        }
        CrawlerThread crawlerThread = factory.getThread();
        crawlerThread.setSrc(url);
//        System.out.println("Starting crawler for " + url.toString());
		executor.execute(crawlerThread);
	}

    public void stopCrawler() {
        System.out.println("Shutting down crawler");
        executor.shutdown();
    }

    /**
     * TODO: Cleanup
     * @throws InterruptedException
     */
	public void probe() throws InterruptedException {
//		LOGGER.info(Thread.currentThread().getName() + " Probing to start new thread");

		if(crawler == null) crawler = Crawler.getInstance(factory);
//        if(crawler.doneCrawling()) {
//            Thread.sleep(BaseConfig.DELAY);
//        }
		Thread.sleep(BaseConfig.DELAY);
        URL crawlURL = Util.getURL(crawler.getNext());
//        System.out.println("probing!" + crawlURL.toString());

        if(crawlURL != null) {
            startCrawler(crawlURL);
        } else {
            LOGGER.severe("I AM SLEEPING!");
            Thread.sleep(BaseConfig.DELAY);
        }
    }

    public void queue(String link) {
        if(crawler == null)	crawler = Crawler.getInstance(factory);
        crawler.queue(link);
    }

	public void finishCrawler(CrawlerThread t) {
		if(crawler == null)	crawler = Crawler.getInstance(factory);
		crawler.crawled(t.getURL().toString(), t.getResponseTime());
	}
}

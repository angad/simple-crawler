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

    public long getCompletedCount() {
        if(executor!=null) {
            return executor.getCompletedTaskCount();
        }
        else return 0;
    }

    public int getCurrentSize() {
        if(executor!=null) {
            return executor.getActiveCount();
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
		executor.execute(crawlerThread);
	}

    public void stopCrawler() {
        System.out.println("Shutting down crawler");
        executor.shutdown();
    }

    /**
     * @throws InterruptedException
     */
	public void probe() throws InterruptedException {
//		LOGGER.info(Thread.currentThread().getName() + " Probing to start new thread");

		if(crawler == null) crawler = Crawler.getInstance(factory);

		Thread.sleep(BaseConfig.DELAY);
        URL crawlURL = Util.getURL(crawler.getNext());

        if(crawlURL != null) startCrawler(crawlURL);
        else Thread.sleep(BaseConfig.DELAY);
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

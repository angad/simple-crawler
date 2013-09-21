package org.crawler.core;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.NoSuchElementException;

/**
 * @author angad
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

	/*
	 * Singleton
	 */
	private static Crawler instance;
	public static Crawler getInstance(CrawlerThreadFactory factory) {
		if(instance == null) {
			instance = new Crawler(factory);
		}
		return instance;
	}

	/*
	 * Private Constructor
	 */
	private Crawler(CrawlerThreadFactory factory) {
        LOGGER.info("New Crawler Created");
        manager = CrawlerManager.getInstance(factory);
	}

    public class Probe implements Runnable {
        int tries = 0;
        int maxTries = BaseConfig.PENDING_TIMEOUT / BaseConfig.PENDING_CHECK_RETRY;

        @Override
        public void run() {
            while(true) {
                try {
                    if(toCrawl.size() == 0 && manager.getCurrentSize() == 0) {
                        tries++;
//                        System.out.print("Try " + tries + "\r");
                        if(tries == maxTries) {
                            manager.stopCrawler();
                            if(manager.getQueueSize() == 0) {
                                System.exit(0);
                            } else {
                                System.out.println("Waiting for all threads to finish");
                                Thread.sleep(BaseConfig.PENDING_CHECK_RETRY);
                            }
                        }
                        Thread.sleep(BaseConfig.PENDING_CHECK_RETRY);
                    } else {
                        tries = 0;
                    }

                    if(toCrawl.size() > 0) {
                        System.out.print(
                                "Completed: " + manager.getCompletedCount() + ", " +
                                "Active: " + manager.getCurrentSize() + ", " +
                                "Pending " + toCrawl.size() + ", " +
                                "Thread queue " + manager.getQueueSize() + "\r"
                        );
                        manager.probe();
                    } else {
                        Thread.sleep(BaseConfig.DELAY);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


	/*
	 * Begin crawling from a seed URL
	 */
	public void beginCrawling(URL seedURL) {
		try {
            new Thread(new Probe()).start();
			crawling.add(seedURL.toString());
			manager.startCrawler(seedURL);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		}
	}

	/**
	 * Manager calls this to get the next pending
	 * @return
	 */
	public String getNext() {
        String url = null;
		if(toCrawl.size() == 0) {
            return url;
        }

        try {
            url = toCrawl.first();
        } catch (NoSuchElementException e) {
                LOGGER.severe("NO SUCH ELEMENT FOUND");
        }
		toCrawl.remove(url);
		crawling.add(url);
		return url;
	}

    public boolean doneCrawling() {
        return toCrawl.size() == 0;
    }
}

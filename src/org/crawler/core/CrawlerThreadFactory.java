package org.crawler.core;

import org.crawler.core.CrawlerThread;

/**
 * Created with IntelliJ IDEA.
 * User: asingh
 * Date: 9/15/13
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CrawlerThreadFactory {

    public abstract CrawlerThread getThread();
}

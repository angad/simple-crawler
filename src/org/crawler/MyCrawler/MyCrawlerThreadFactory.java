package org.crawler.MyCrawler;

import org.crawler.core.CrawlerThreadFactory;
import org.crawler.util.Pair;
import org.crawler.core.CrawlerThread;

import java.util.List;
import java.util.Map;

public class MyCrawlerThreadFactory extends CrawlerThreadFactory {
    String domainName;
    Map<String, List<Pair<String, String>>> filesFound;
    public MyCrawlerThreadFactory(String domainName, Map<String, List<Pair<String, String>>>filesFound) {
        super();
        this.domainName = domainName;
        this.filesFound = filesFound;
    }

    @Override
    public CrawlerThread getThread() {
        return new MyCrawlerThread(domainName, filesFound);
    }
}
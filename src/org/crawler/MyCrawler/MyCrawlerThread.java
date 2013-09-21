package org.crawler.MyCrawler;

import org.crawler.Pair;
import org.crawler.core.CrawlerThread;
import org.crawler.util.Util;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class MyCrawlerThread extends CrawlerThread {
    private String domainName;
    Logger LOGGER = Logger.getLogger(CrawlerThread.class.getName());

    private static String[] FILE_TYPES = {"pdf"};
    Map<String, List<Pair<String, String>>> filesFound;
    public MyCrawlerThread(String domainName, Map<String, List<Pair<String, String>>> filesFound) {
        super();
        this.domainName = domainName;
        this.filesFound = filesFound;
    }

    @Override
    protected boolean shouldCrawl(URL base, String link, String html) {
        if(Util.isFileType(link, FILE_TYPES)) {
            LOGGER.info("FOUND! " + link);
            List<Pair<String, String>> linkPairs = filesFound.get(base);
            Pair<String, String> linkPair = new Pair<String, String>(link, html);
            if(linkPairs == null) {
                linkPairs = new ArrayList<Pair<String, String>>();
                filesFound.put(base.toString(), linkPairs);
            }
            linkPairs.add(linkPair);
            return false;
        } else {
            URL url = Util.getURL(link);
            if(url != null) {
                if(url.getHost().contains(domainName)) return true;
            }
        }
        return false;
    }


}
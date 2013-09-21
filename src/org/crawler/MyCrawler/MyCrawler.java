package org.crawler.MyCrawler;

import org.crawler.core.CrawlerThreadFactory;
import org.crawler.util.Pair;
import org.crawler.core.Crawler;
import org.crawler.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class MyCrawler {
    private final static Logger LOGGER = Logger.getLogger(MyCrawler.class.getName());
    /**
     */
    private static Map<String, List<Pair<String, String>>> filesFound =
            Collections.synchronizedMap(new HashMap<String, List<Pair<String, String>>>());


    public static void main(String[] args) throws IOException {
        System.out.println("Please enter the starting URL. http:// required");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        final String startingURL = br.readLine();
        URL url = Util.getURL(startingURL);
        final String domainName = url.getHost();

        CrawlerThreadFactory factory = new MyCrawlerThreadFactory(domainName, filesFound);
        Crawler crawler = Crawler.getInstance(factory);
        LOGGER.info("Starting crawler");
        crawler.beginCrawling(url);
//        printResults();
    }

    public static void printResults() {
        Iterator it = filesFound.entrySet().iterator();
        System.out.println("printResults " + filesFound.size());
        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println("KEY " + pairs.getKey());
            for(Pair pair: (List<Pair>)pairs.getValue()) {
                System.out.println(pair.toString());
            }
            System.out.println("------------------");
        }
    }

}

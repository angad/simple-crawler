package org.crawler.core;

import org.crawler.util.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

import org.crawler.util.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The CrawlerThread
 *
 * Abstract class for the crawler thread. Implements the core run function
 * which opens a socket to the host, sends the header
 *
 * TODO: Fix concurrency issues
 * TODO: USE HTTP library
 * @author angad
 *
 */
public abstract class CrawlerThread implements Runnable {

	Logger LOGGER = Logger.getLogger(CrawlerThread.class.getName());

	/*
	 * The crawl URL passed to this Thread
	 */
	private URL crawlURL;
	
	/*
	 * An instance of the crawler to which the thread reports
	 */
	private CrawlerManager crawlerManager;

	/*
	 * The response time
	 */
	private long responseTime;

	/*
	 * Constructor for this thread
	 */
	public CrawlerThread() {
    	crawlerManager = CrawlerManager.getInstance();
		responseTime = 0;
	}

    public void setSrc(URL url) {
        this.crawlURL = url;
    }

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Socket socket = null;
		try {
            if(crawlURL == null || crawlURL.toString().equals("")) {
                return;
            }
            String path = crawlURL.getPath().equals("") ? "/" : crawlURL.getPath();
            String host = crawlURL.getHost();

			//Connect to the server
			socket = new Socket(host, Constants.HTTP_PORT);
			socket.setSoTimeout(Constants.SOCKET_TIMEOUT);
			//Get an output stream
			BufferedWriter out = new BufferedWriter(new
					OutputStreamWriter(socket.getOutputStream()));
			
			//Send a request to get the root page of the server
			out.write("GET " + path + " HTTP/1.0\n\n");
            out.write("User-Agent: Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.17 Safari/537.36");
			out.flush();
			
			//we have sent it. get the sent time
			long sentTime = System.currentTimeMillis();
//			LOGGER.info("Connected to " + socket.getInetAddress().toString());

			//get the input stream reader
			BufferedReader in = new BufferedReader(new
					InputStreamReader(socket.getInputStream()));
			
			//using a StringBuilder to store the content of the page
			StringBuilder htmlBuilder = new StringBuilder();
			String line;
			String htmlDoc;
			boolean headerEnded = false;
			while((line = in.readLine())!=null) {
			
				//Separating the header from the content
				if(line.equals("") && !headerEnded) {
					headerEnded = true;
					continue;
				}
				
				//append the content
				if(headerEnded) htmlBuilder.append(line);
			}

			htmlDoc = htmlBuilder.toString();
			
			in.close();
			
			//we have completely received the page
			long receivedTime = System.currentTimeMillis();
			responseTime = receivedTime - sentTime;
            finish();

//			LOGGER.info("Response time: " + (receivedTime - sentTime) + "ms");
//			System.out.println(htmlDoc);
//			Parse the document using Jsoup
			Document doc = Jsoup.parse(htmlDoc);
			
			//Extract all the a links
			Elements links = doc.select("a");
			
			//go through all the links and extract the href
			for(Element link: links) {
                String l = link.attr("href");
                String html = link.html();
                if(shouldCrawl(crawlURL, l, html)) {
                    List<String> list = Util.getLinks(l);
                    if(list !=null) {
                        for(String i: list) {
                            crawlerManager.queue(i);
                        }
                    }

                }
            }

		} catch (UnknownHostException e) {
			LOGGER.severe("UnknownHostException: " + e.getMessage());
		} catch (IOException e) {
			LOGGER.severe("IOException: " + e.getMessage());
		}
		
	}
	
	public long getResponseTime() {
		return responseTime;
	}
	
	public URL getURL() {
		return crawlURL;
	}

	private void finish() {
		crawlerManager.finishCrawler(this);
	}

	protected abstract boolean shouldCrawl(URL base, String link, String html);

}

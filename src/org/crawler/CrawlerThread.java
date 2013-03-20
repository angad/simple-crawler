package org.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.crawler.util.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The CrawlerThread 
 * @author angad
 *
 */
public class CrawlerThread implements Runnable {

	Logger LOGGER = Logger.getLogger(CrawlerThread.class.getName());

	/*
	 * The crawl URL passed to this Thread
	 */
	private String crawlURL;
	
	/*
	 * An instance of the crawler to which the thread reports
	 */
	private CrawlerManager crawlerManager;

	/*
	 * A local set of links found by this thread
	 */
	private Set<String> localSet = new LinkedHashSet<String>();
	
	/*
	 * The response time
	 */
	private long responseTime;

	/*
	 * Constructor for this thread
	 */
	public CrawlerThread(String src) {
		this.crawlURL = src;
		crawlerManager = CrawlerManager.getInstance();
		responseTime = 0;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Socket socket = null;
		try {
			//Connect to the server
			socket = new Socket(crawlURL, Constants.HTTP_PORT);
			socket.setSoTimeout(Constants.SOCKET_TIMEOUT);
			//Get an output stream
			BufferedWriter out = new BufferedWriter(new
					OutputStreamWriter(socket.getOutputStream()));
			
			//Send a request to get the root page of the server
			out.write("GET / HTTP/1.0\n\n");
			out.flush();
			
			//we have sent it. get the sent time
			long sentTime = System.currentTimeMillis();
			LOGGER.info("Connected to " + socket.getInetAddress().toString());
//			LOGGER.info("Sent GET Request to " + crawlURL);
			
			//get the input stream reader
			BufferedReader in = new BufferedReader(new
					InputStreamReader(socket.getInputStream()));
			
			//using a StringBuilder to store the content of the page
			StringBuilder htmlBuilder = new StringBuilder();
			String line;
			String htmlDoc;
//			String type = "HEADER: ";
			boolean headerEnded = false;
			while((line = in.readLine())!=null) {
			
				//Separating the header from the content
				if(line.equals("") && !headerEnded) {
//					type = "CONTENT: ";
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
//			LOGGER.info("Response time: " + (receivedTime - sentTime) + "ms");
//			System.out.println(htmlDoc);
			//Parse the document using Jsoup
			Document doc = Jsoup.parse(htmlDoc);
			
			//Extract all the a links
			Elements links = doc.select("a");
			
			//go through all the links and extract the href
			for(Element link: links) 
				processLink(link.attr("href"));

		} catch (UnknownHostException e) {
			LOGGER.severe(" host error: " + crawlURL + " "  + e.getMessage() );
		} catch (IOException e) {
			LOGGER.severe(" host error: " + crawlURL + " "  + e.getMessage() );
		}
		
		finish();
	}
	
	
	public Set<String> getLinks() {
		return localSet;
	}
	
	public long getResponseTime() {
		return responseTime;
	}
	
	public String getURL() {
		return crawlURL;
	}
	
	/**
	 * Submit the links to the CrawlerManager
	 */
	private void finish() {
		crawlerManager.finishCrawler(this);
	}
	
	/**
	 * Process the Link and add it to the local set
	 * @param link
	 */
	private void processLink(String link) {
		String serverAddress = getServerAddress(link);
		if(!serverAddress.isEmpty()) {
			localSet.add(serverAddress);
		}
	}
	
	/**
	 * Get the server address from the link
	 * @param link
	 * @return
	 */
	private String getServerAddress(String link) {
		String serverAddress;
		String pattern = "^((http|https|ftp|file)://)|^#"; //checks for http, https, ftp, file
		serverAddress = link.replaceFirst(pattern, "").split("/")[0];
		if(!serverAddress.contains(".")) return "";
		if(serverAddress.endsWith("html")) return "";
		return serverAddress;	
	}
}

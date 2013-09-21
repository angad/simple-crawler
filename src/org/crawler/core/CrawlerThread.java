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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The CrawlerThread
 *
 * Abstract class for the crawler thread. Implements the core run function
 * which opens a socket to the host, sends the headers and receives a response.
 *
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

    private List<Header> headers;

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

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    private String request(String host, String path) {
        Socket socket = null;
        String htmlDoc = null;
        try {
            //Connect to the server
            socket = new Socket(host, BaseConfig.HTTP_PORT);
            socket.setSoTimeout(BaseConfig.SOCKET_TIMEOUT);

            //Get an output stream
            BufferedWriter out = new BufferedWriter(new
                    OutputStreamWriter(socket.getOutputStream()));

            //Send a request to get the root page of the server
            out.write("GET " + path + " HTTP/1.0\n\n");
            if(headers != null) {
                for(int i=0; i<headers.size(); i++) {
                    out.write(headers.get(i).toString());
                }
            } else {
                out.write("User-Agent: Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.17 Safari/537.36");
            }
            out.flush();

            //we have sent it. get the sent time
            long sentTime = System.currentTimeMillis();
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(socket.getInputStream()));

            //using a StringBuilder to store the content of the page
            StringBuilder htmlBuilder = new StringBuilder();
            String line;
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
            long responseTime = receivedTime - sentTime;
        } catch (UnknownHostException e) {
            LOGGER.severe("UnknownHostException: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.severe("IOException: " + e.getMessage());
        }
        return htmlDoc;
    }

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
        if(crawlURL == null || crawlURL.toString().equals("")) {
            return;
        }
        String path = crawlURL.getPath().equals("") ? "/" : crawlURL.getPath();
        String host = crawlURL.getHost();
        String htmlDoc = request(host, path);
        crawlerManager.finishCrawler(this);
        if(htmlDoc == null) {
            LOGGER.severe("Could not get URL " + crawlURL.toString());
            return;
        }

        Document doc = Jsoup.parse(htmlDoc);

        //Extract all the a links
        Elements links = doc.select("a");

        //go through all the links and extract the href
        for(Element link: links) {
            String l = link.attr("href");
            String html = link.html();

            //check from user implemented function if shouldCrawl the URL
            if(shouldCrawl(crawlURL, l, html)) {
                List<String> list = Util.getLinks(l);
                if(list !=null) {
                    for(String i: list) {
                        crawlerManager.queue(i);
                    }
                }

            }
        }

	}

	public long getResponseTime() {
		return responseTime;
	}

	public URL getURL() {
		return crawlURL;
	}

	protected abstract boolean shouldCrawl(URL base, String link, String html);

}

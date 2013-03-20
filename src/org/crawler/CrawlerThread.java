package org.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerThread implements Runnable {

	Logger LOGGER = Logger.getLogger(CrawlerThread.class.getName());

	private String crawlURL;
	private Crawler crawler;


	public CrawlerThread(String src) {
		this.crawlURL = src;
		crawler = Crawler.getInstance();
	}

	@Override
	public void run() {
		Socket socket = null;
		try {
			socket = new Socket(crawlURL, Constants.HTTP_PORT);
			BufferedWriter out = new BufferedWriter(new
					OutputStreamWriter(socket.getOutputStream()));
			out.write("GET / HTTP/1.0\n\n");
			out.flush();
			long sentTime = System.currentTimeMillis();
			LOGGER.info("Connected to " + socket.getInetAddress().toString());
			LOGGER.info("Sent GET Request to " + crawlURL);
			
			BufferedReader in = new BufferedReader(new
					InputStreamReader(socket.getInputStream()));	
			StringBuilder htmlBuilder = new StringBuilder();
			String line;
			String htmlDoc;
			String type = "HEADER: ";
			boolean headerEnded = false;
			while((line = in.readLine())!=null) {
				if(line.equals("") && !headerEnded) {
					type = "CONTENT: ";
					headerEnded = true;
					continue;
				}
				if(headerEnded) htmlBuilder.append(line);
			}

			htmlDoc = htmlBuilder.toString();
			
			in.close();
			long receivedTime = System.currentTimeMillis();
			LOGGER.info("Response time: " + (receivedTime - sentTime) + "ms");
//			System.out.println(htmlDoc);
			Document doc = Jsoup.parse(htmlDoc);
			Elements links = doc.select("a");
			for(Element link: links) {
				System.out.println(link.attr("href"));
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

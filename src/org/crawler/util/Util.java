package org.crawler.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class Util {
    private final static Logger LOGGER = Logger.getLogger(Util.class.getName());

    /**
     * Checks if the hyperlink points to the required file type
     * @param link
     * @param fileType
     * @return
     */
    public static boolean isFileType(String link, String[] fileType) {
        for(int i = 0; i < fileType.length; i ++) {
            if(link.contains("." + fileType[i])) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getLinks(String link) {
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            return null;
        }
        String path = url.getPath();
        StringTokenizer st = new StringTokenizer(path, "/");
        List<String> list = new ArrayList<String>();
        StringBuilder newLink = new StringBuilder();
        newLink.append(url.getHost());
        list.add(newLink.toString());
        while(st.hasMoreTokens()) {
            newLink.append("/" + st.nextToken());
            String item = newLink.toString();
            if(!(item.startsWith("http://") || item.startsWith("https://"))) {
                item = "http://" + item;
            }
            list.add(item);
        }

        return list;
    }

    public static URL getURL(String link) {
        if(link == null || link.equals("")) return null;

        URL url = null;
        try {
             url = new URL(link);
        } catch (MalformedURLException e) {
             if(!link.startsWith("http://") && !(link.startsWith("https://"))) {
                 link = "http://" + link;
             }
            try {
                url = new URL(link);
            } catch (MalformedURLException e1) {
//                LOGGER.severe("Could not get URL from " + link + " " + e.getMessage());
            }
        }
        return url;
    }

}

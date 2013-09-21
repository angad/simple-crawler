package org.crawler.core;

public class Header {
    String header;
    String value;

    public Header(String header, String value) {
        this.header = header;
        this.value = value;
    }

    @Override
    public String toString() {
        return header + ": " + value;
     }

}

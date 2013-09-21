javac -classpath libs/jsoup-1.7.2.jar -d bin \
src/org/crawler/core/BaseConfig.java src/org/crawler/core/Crawler.java src/org/crawler/core/CrawlerManager.java \
src/org/crawler/core/CrawlerThread.java  src/org/crawler/core/CrawlerThreadFactory.java \
src/org/crawler/core/Header.java \
src/org/crawler/MyCrawler/MyCrawler.java src/org/crawler/MyCrawler/MyCrawlerThread.java \
src/org/crawler/MyCrawler/MyCrawlerThreadFactory.java \
src/org/crawler/util/FileHandler.java src/org/crawler/util/Util.java src/org/crawler/util/Pair.java

cd bin/
java -classpath .:../libs/jsoup-1.7.2.jar org.crawler.MyCrawler.MyCrawler

package com.redditgold.reader;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.redditgold.model.GoldComment;

public class Reader {
	
	/**
	 * Max times to follow link to next page.
	 */
	private final static int LIMIT = 1;
	
	static HttpURLConnection conn;
	static BufferedReader rd;
	static URL url;
	static URL nextUrl;
	
	private static final Logger LOGGER = Logger.getLogger(Reader.class);

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		LOGGER.debug("***********************Reddit Gold Comment Finder Started***********************");
 		List<GoldComment> goldComments = new ArrayList<GoldComment>();
		url = new URL("http://www.reddit.com");
		
		int counter = 0;
		
		while (counter < LIMIT) {
			LOGGER.info("----------Checking " + url.toString() + "------------");
			goldComments.addAll(crawlForComments(url));
			crawlForNextLink(url);
			url = nextUrl;
			counter++;
		}
		
		LOGGER.info("Complete!");
		OutputStream outputStream = new FileOutputStream("RedditGoldComments.txt");
		Writer out = new OutputStreamWriter(outputStream);
		
		for (GoldComment gc : goldComments) {
			out.write(gc.toString());
		}
		
		out.close();

	}
	
	/**
	 * Crawl page to find links to comments about posts.
	 * @param urlToSearch url of page
	 * @throws IOException e
	 */
	private static List<GoldComment> crawlForComments(URL urlToSearch) throws IOException {
		
		conn = (HttpURLConnection) urlToSearch.openConnection();
		rd = new BufferedReader( new InputStreamReader(conn.getInputStream(), "UTF-8"));
		
		List<GoldComment> goldComments = new ArrayList<GoldComment>();
		
		String regex = ".*comments\" href=\"(.*?)\".*";
		
		Matcher m;
		
		Pattern p = Pattern.compile(regex);
		LOGGER.debug("Searchging for comments link...");
		String line = null;
		int counter = 0;
		while ((line = rd.readLine()) != null ) {
			String[] stringPeices = line.split("<div");
			
			for (String s : stringPeices) {
				m = p.matcher(s);
				
				while (m.find() && counter < 12) {
					++counter;
					
					CrawlComments page = new CrawlComments(new URL(m.group(1)));
					goldComments.addAll(page.readComments());
				} 
				
			}
		}
		
		return goldComments;
				
	}
	
	/**
	 * Crawl page to find link to next page.
	 * @param urlToSearch url of page
	 * @throws MalformedURLException e
	 * @throws IOException e
	 */
	public static void crawlForNextLink(URL urlToSearch) throws MalformedURLException, IOException {
		LOGGER.debug("Searching for 'next' link....");
		conn = (HttpURLConnection) urlToSearch.openConnection();
		rd = new BufferedReader( new InputStreamReader(conn.getInputStream(), "UTF-8"));
		
		String nextPageRegex = ".*<a href=\"(.*?)\" rel=\"nofollow next\" >.*";
		Pattern nextPattern = Pattern.compile(nextPageRegex);
		Matcher nextMatcher;
		
		String line = null;
		while ((line = rd.readLine()) != null) {
			
			nextMatcher = nextPattern.matcher(line);
			
			if (nextMatcher.find()) {
				String nextLink = nextMatcher.group(1);
				nextUrl = new URL(nextLink);
				LOGGER.debug("Found 'next' link: " + nextLink);
			}
			
		}
	}

}

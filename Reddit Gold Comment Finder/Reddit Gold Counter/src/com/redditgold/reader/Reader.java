package com.redditgold.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.redditgold.model.GoldComment;

public class Reader {
	
	/**
	 * Max times to follow next link.
	 */
	private final static int LIMIT = 1;
	private static List<GoldComment> goldComments = new ArrayList<GoldComment>();
	
	static HttpURLConnection conn;
	static BufferedReader rd;
	static URL url;
	static URL nextUrl;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		url = new URL("http://www.reddit.com");
		
		int counter = 0;
		
		while (counter < LIMIT) {
			System.out.println("------------ Checking " + url.toString() + "----------------");
			crawlForComments(url);
			crawlForNextLink(url);
			url = nextUrl;
			counter++;
		}
		
		System.out.println("Finished");
		for (GoldComment gc : goldComments) {
			System.out.println(gc.toString());
		}

	}
	
	private static void crawlForComments(URL urlToSearch) throws IOException {
		
		conn = (HttpURLConnection) urlToSearch.openConnection();
		rd = new BufferedReader( new InputStreamReader(conn.getInputStream(), "UTF-8"));
		
		String regex = ".*comments\" href=\"(.*?)\".*";
		
		Matcher m;
		
		Pattern p = Pattern.compile(regex);
		System.out.println("Searching for comments link...");
		String line = null;
		while ((line = rd.readLine()) != null) {
			
			String[] stringPeices = line.split("<div");
			
			for (String s : stringPeices) {
				m = p.matcher(s);
				
				while (m.find()) {
					
					CrawlComments page = new CrawlComments(new URL(m.group(1)));
					goldComments.addAll(page.readComments());
				} 
				
			}
		}
	}
	
	public static void crawlForNextLink(URL urlToSearch) throws MalformedURLException, IOException {
		System.out.println("Searching for next link...");
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
				System.out.println("Found next link: " + nextLink);
			}
			
		}
	}

}

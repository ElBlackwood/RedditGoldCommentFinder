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

public class CrawlComments {
	
	private URL commentsPage;
	private HttpURLConnection conn;
	private BufferedReader rd;
	
	private final String TITLE_REGEX = ".*<title>(.*?)</title>";
	private final String UPVOTE_REGEX = ".*?<span class=\"score likes\">(.*?)</span>.*";
	private final String DOWNVOTE_REGEX = ".*?<span class=\"score dislikes\">(.*?)</span>.*";
	private final String COMMENT_REGEX = ".*<p>(.*)</p>.*";
	private final String GOLD_REGEX = ".*title=\"a redditor has gifted reddit gold to (.*?) for this comment.*";
	
	private Pattern titlePattern = Pattern.compile(TITLE_REGEX);
	private Pattern upvotePattern = Pattern.compile(UPVOTE_REGEX);
	private Pattern downvotePattern = Pattern.compile(DOWNVOTE_REGEX);
	private Pattern commentPattern = Pattern.compile(COMMENT_REGEX);
	private Pattern goldPattern = Pattern.compile(GOLD_REGEX);
	
	public CrawlComments(URL commentsPage) throws IOException{
		this.commentsPage = commentsPage;
		this.conn = (HttpURLConnection) this.commentsPage.openConnection();
		this.rd = new BufferedReader( new InputStreamReader(conn.getInputStream()));
	}


	public List<GoldComment> readComments() throws MalformedURLException, IOException {
		System.out.println("Reading comments");
		boolean foundTitle = false;
		String title = null;
		int goldCommentCounter = 0;
		List<GoldComment> goldComments = new ArrayList<GoldComment>();
		
		String line = null;
		while ((line = rd.readLine()) != null) {
			
			if (foundTitle) {
				String goldUser = checkStringWithRegex(line, goldPattern);
				if (goldUser != null) {
					goldCommentCounter++;
					System.out.println("Found gold comment, gathering other details");
					String downVotes = checkStringWithRegex(line, downvotePattern);
					String upVotes = checkStringWithRegex(line, upvotePattern);
					String comment = checkStringWithRegex(line, commentPattern);
					
					GoldComment gc = new GoldComment();
					gc.setThreadTitle(title);
					gc.setUserName(goldUser);
					gc.setTheComment(comment);
					gc.setUpVotes(upVotes);
					gc.setDownVotes(downVotes);
					
					goldComments.add(gc);
					
				}				
			} else {
				title = checkStringWithRegex(line, titlePattern);
				if (title != null) {
					System.out.println("Title of thread: " + title);
					foundTitle = true;
				}
			}
			
			
		}
		
		System.out.println("Found " + goldCommentCounter + " gold comments in thread ");
		return goldComments;
		
	}
	
	private String checkStringWithRegex(String line, Pattern pattern) {
		Matcher m = pattern.matcher(line);
		
		if (m.find()) {
			return m.group(1);
		} else {
			return null;
		}
	}
	

}

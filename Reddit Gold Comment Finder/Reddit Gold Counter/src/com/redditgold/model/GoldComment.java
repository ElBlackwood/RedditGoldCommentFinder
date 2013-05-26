package com.redditgold.model;

public class GoldComment {

	private String userName;
	private String theComment;
	private String threadTitle;
	private String upVotes;
	private String downVotes;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTheComment() {
		return theComment;
	}
	public void setTheComment(String theComment) {
		this.theComment = theComment;
	}
	public String getThreadTitle() {
		return threadTitle;
	}
	public void setThreadTitle(String threadTitle) {
		this.threadTitle = threadTitle;
	}
	public String getUpVotes() {
		return upVotes;
	}
	public void setUpVotes(String upVotes) {
		this.upVotes = upVotes;
	}
	public String getDownVotes() {
		return downVotes;
	}
	public void setDownVotes(String downVotes) {
		this.downVotes = downVotes;
	}
	
	@Override
	public String toString() {
		StringBuffer object = new StringBuffer();
		object.append("\n");
		object.append("Title: ");
		object.append(threadTitle);
		object.append("\n");
		object.append("User: ");
		object.append(userName);
		object.append("\n");
		object.append("Comment: ");
		object.append(theComment);
		object.append("\n");
		object.append("Upvotes: ");
		object.append(upVotes);
		object.append("\n");
		object.append("Downvotes: ");
		object.append(downVotes);
		object.append("\n");
		object.append("***********************");
		
		return object.toString();
		
	}
	
	
	
}

package com.blitztiger.twitterBots2;

import winterwell.jtwitter.*;

import java.io.*;
import java.util.Scanner;

public abstract class TwitterBot {
	
	protected Twitter twitter;
	
	protected static long waitBetweenRequestTime = Long.valueOf("30000");//30,000 milliseconds = 30 seconds
	protected static long waitIfExceedRateLimitTime = Long.valueOf("120000");//120,000 milliseconds = 2 minutes
	
	public TwitterBot(String username){
		OAuthSignpostClient oauthClient = new OAuthSignpostClient(
				OAuthSignpostClient.JTWITTER_OAUTH_KEY, 
				OAuthSignpostClient.JTWITTER_OAUTH_SECRET, "oob");
		oauthClient.authorizeDesktop();
		String v = OAuthSignpostClient.askUser("Please enter the verification PIN from Twitter");
		oauthClient.setAuthorizationCode(v);
		String[] accessToken = oauthClient.getAccessToken();
		System.out.println(accessToken[0]);
		System.out.println(accessToken[1]);
		try {
			File toFile = new File("authenticationFile.txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(toFile));
			writer.write(accessToken[0] + "\n");
			writer.write(accessToken[1]);
			writer.close();
			System.out.println("Authentication key saved to " + toFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not save authentication key in a file...");
		}
		twitter = new Twitter(username, oauthClient);
	}
	
	public TwitterBot(String username, String filePath){
		try {
			Scanner reader = new Scanner(new File(filePath));
			OAuthSignpostClient oauthClient = new OAuthSignpostClient(
					OAuthSignpostClient.JTWITTER_OAUTH_KEY, 
					OAuthSignpostClient.JTWITTER_OAUTH_SECRET,
					reader.nextLine(),
					reader.nextLine());
			reader.close();
			System.out.println("Key loaded from file");
			twitter = new Twitter(username, oauthClient);
			return;
		} catch (FileNotFoundException e) {
			System.out.println("Could not load authentication key from file...");
			e.printStackTrace();
		}
		OAuthSignpostClient oauthClient = new OAuthSignpostClient(
				OAuthSignpostClient.JTWITTER_OAUTH_KEY, 
				OAuthSignpostClient.JTWITTER_OAUTH_SECRET, "oob");
		oauthClient.authorizeDesktop();
		String v = OAuthSignpostClient.askUser("Please enter the verification PIN from Twitter");
		oauthClient.setAuthorizationCode(v);
		String[] accessToken = oauthClient.getAccessToken();
		System.out.println(accessToken[0]);
		System.out.println(accessToken[1]);
		try {
			File toFile = new File("authenticationFile.txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(toFile));
			writer.write(accessToken[0] + "\n");
			writer.write(accessToken[1]);
			writer.close();
			System.out.println("Authentication key saved to " + toFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not save authentication key in a file...");
		}
		twitter = new Twitter(username, oauthClient);
	}
	
	public abstract void runBot(boolean publicTimeline, String userToGetTimelineOf) throws Exception;
}

package com.blitztiger.twitterBots2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import winterwell.jtwitter.Twitter.Status;

public class MarkovBot extends TwitterBot {
	
	private MarkovElement<String> head;
	private List<MarkovElement<String>> allElements;
	private List<String> savedStatuses;
	private final static MarkovElement<String> TERMINATOR = new MarkovElement<String>("");
	
	public MarkovBot(String username) {
		super(username);
	}

	public MarkovBot(String username, String filePath) {
		super(username, filePath);
	}

	@Override
	public void runBot(boolean publicTimeline, String userToGetTimelineOf)throws Exception {
		head = new MarkovElement<String>("");
		allElements =  new ArrayList<MarkovElement<String>>();
		savedStatuses = new ArrayList<String>();
		Random rand = new Random();
		int iterations = 0; 
		List<Status> timeline;
/*		System.out.println(twitter.getRateLimitStatus());
		if(twitter.getRateLimitStatus() < 5){
			System.out.println("Waiting because I went over the rate limit :(");
		}
		while(twitter.getRateLimitStatus() < 5){
			System.out.println("\tstill waiting: rate limit at " + twitter.getRateLimitStatus());
			Thread.sleep(waitIfExceedRateLimitTime);
		}
*/		while(true){
			if(publicTimeline){
				timeline = twitter.getPublicTimeline();
			} else if (userToGetTimelineOf != null){
				timeline = twitter.getUserTimeline(userToGetTimelineOf);
			} else {
				timeline = twitter.getFriendsTimeline();
			} 
			for(Status status : timeline){
				boolean alreadyAdded = false;
				for(String saved : savedStatuses){
					if(status.text.equals(saved)){
						alreadyAdded = true;
					}
				}
				if(!alreadyAdded){
					insertSentence(status.text);
					savedStatuses.add(status.text);
				}
			}
			if(iterations-- == 0){
				String sentence = buildSentence();
				if(sentence.length() > (140 - " #markov".length())){
					sentence = sentence.substring(0, 140 - "... #markov".length()) + "... #markov";
				} else {
					sentence = sentence + " #markov";
				}
				twitter.setStatus(sentence);
				System.out.println(sentence);
				//iterations = 0;
				iterations = rand.nextInt(120);
			}
			System.out.println("Waiting to avoid going over the rate limit");
			Thread.sleep(waitBetweenRequestTime);
		}
	}
	
	private String buildSentence(){
		String sentence = "";
		for(String word : head.getChain()){
			sentence = sentence + word + " ";
		}
		return sentence.trim();
	}
	
	private void insertSentence(String sentence){
		String[] words = sentence.split(" ");
		MarkovElement<String> ele = head;
		for(String word : words){
			if(!word.equals("") && word.charAt(0) != '@')
				ele = ele.insert(findElement(word));
		}
		ele.insert(TERMINATOR);
	}

	private MarkovElement<String> findElement(String word) {
		for(MarkovElement<String> element : allElements){
			if(element.getValue().toLowerCase().equals(word.toLowerCase())){
				return element;
			}
		}
		MarkovElement<String> element = new MarkovElement<String>(word);
		allElements.add(element);
		return element;
	}
	
	public static void main(String[] args){
		MarkovBot bot = new MarkovBot("markovtwain", "/Users/jeff/Documents/workspace/TwitterBots2/authenticationFile.txt");
		while(true){
			try{
				bot.runBot(true, null);
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("Whoops, there was a fail... let's try that again in a minute...");
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}

class MarkovElement<T>{
	private T value;
	private ArrayList<MarkovElement<T>> branches;
	private static Random rand = new Random();
	
	public MarkovElement(T value){
		this.value = value;
		branches = new ArrayList<MarkovElement<T>>();
	}
	
	public MarkovElement<T> insert(MarkovElement<T> newElement){
		branches.add(newElement);
		return newElement;
	}
	
	public T getValue(){
		return value;
	}
	
	public List<T> getChain(){
		if(branches.isEmpty()){
			List<T> list = new ArrayList<T>();
			list.add(value);
			return list;
		}
		List<T> list = getRandomBranch().getChain();
		list.add(0, value);
		return list;
	}
	
	public MarkovElement<T> getRandomBranch(){
		if(branches.isEmpty()){
			return null;
		}
		return branches.get(rand.nextInt(branches.size()));
	}
	
	public String toString(){
		String str;
		str = value.toString() + "\n Links: \n";
		for(MarkovElement<T> ele : branches){
			str += "\t" + ele.getValue() + "\n";
		}
		return str;
	}
}
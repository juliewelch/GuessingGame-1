package client;

import static common.Topics.GUESS;
import static common.Topics.HINT;
import static common.Topics.JOIN;

import common.Observer;

public class HaysGuessingGameClient extends GuessingGameClient {
	HaysGuessingGameClient c;
	Integer secretValue=3;
	int lastGuess=1;
	public static void main(String[] args) {
		try{
			new HaysGuessingGameClient();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public HaysGuessingGameClient(){
		String ip="localhost";
		int port=12345;
		String teamName="Hays";
		// subscribe observers
		c=this;
		// new game: figure out role
		c.subscribe(JOIN, new Observer(){
			@Override
			public void onUpdate(Object response) {
				boolean isHinting=(Boolean)response;
				if(isHinting){
					// set new secret value for this round
					secretValue=3;
				}else{
					// Guess 5 to follow binary search alogirthm
					lastGuess=5;
					try{
					c.publish(GUESS, lastGuess);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		});
		// receive guess: send updated hint
		c.subscribe(GUESS, new Observer(){
			@Override
			public void onUpdate(Object response) {
				try{
					System.out.println("Hint: "+secretValue.compareTo((Integer)response));
					c.publish(HINT, secretValue.compareTo((Integer)response));
				}catch(Exception e){
					
				}
			}
		});
		// receive hint: send updated guess
		c.subscribe(HINT, new Observer(){
			@Override
			public void onUpdate(Object response) {
				int hint=(Integer)response;
				//follow binary search algorithm for guessing
                if(hint == 0)
                    lastGuess= lastGuess - (int)(lastGuess/2);
                else
                    lastGuess = lastGuess + (int)(lastGuess/2);
				try{
					System.out.println("Guess: "+lastGuess);
					c.publish(GUESS, lastGuess);
				}catch(Exception e){
					
				}
			}
		});
		try{
			c.openConnection(ip,port,teamName);
		}catch(Exception e){
			
		}
	}
}

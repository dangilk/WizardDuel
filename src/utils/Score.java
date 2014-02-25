package utils;

import java.util.List;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class Score {

	private int consecutiveWins = 0;
	private String uid;
	ParseObject self;
	private Listener listener;
	public interface Listener{
		void scoreUpdated(int score);
	}
	
	public Score(Listener l){
		listener = l;
	}
	
	public void init(final String uid){
		this.uid = uid;
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");
		query.whereEqualTo("uid", uid);
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> existing, ParseException e) {
		        if (e == null) {
		        	if(existing.size() == 1){
		        		ParseObject ex = existing.get(0);
		        		consecutiveWins = ex.getInt("consecutiveWins");
		        		self = ex;
		        	}else if(existing.size() == 0){
		        		ParseObject score = new ParseObject("Score");
		        		score.put("uid",uid);
		        		score.put("consecutiveWins",0);
		        		score.saveInBackground();
		        		self = score;
		        	}
		        	Log.e("tag","got score: "+consecutiveWins);
		        	listener.scoreUpdated(consecutiveWins);
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		        }
		    }

		});
	}
	
	public void increment(){
		consecutiveWins++;
		if(self != null){
			self.increment("consecutiveWins");
			self.saveInBackground();
		}
		listener.scoreUpdated(consecutiveWins);
	}
	public void clear(){
		consecutiveWins = 0;
		if(self != null){
			self.put("consecutiveWins", 0);
			self.saveInBackground();
		}
		listener.scoreUpdated(consecutiveWins);
	}

	public int getConsecutiveWins() {
		return consecutiveWins;
	}

	public void setConsecutiveWins(int consecutiveWins) {
		this.consecutiveWins = consecutiveWins;
	}
}

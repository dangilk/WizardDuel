package com.dan.wizardduel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameCompleteActivity extends WDGameActivity{
	
	public Button returnMain;
	public TextView result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_complete);
		returnMain = (Button) findViewById(R.id.gameComplete_main);
		result = (TextView) findViewById(R.id.gameComplete_text);
		Intent intent = getIntent();
		Boolean playerWon = intent.getBooleanExtra("won", true);
		if(playerWon){
			result.setText("You Won!");
		}else{
			result.setText("You Lost...");
		}
	}
	
	public void onReturnMainClicked(View v){
		Intent i = new Intent(this,MainActivity.class);
		this.startActivity(i);
		finish();
	}

}

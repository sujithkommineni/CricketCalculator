package com.hydapps.cricketcalc.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hydapps.cricketcalc.R;

public class ScoreBoardActivity extends Activity implements View.OnClickListener {
    /** Called when the activity is first created. */
	
	private final String TAG = "CRICKET_CALC";
	
	//keys to save in bundle
	private static final String NO_OF_BALLS = "no_of_balls";//no of balls including same balls in the current over
	public static final String TOTAL_BALLS = "total_no_of_balls";//total balls in the match so far
	public static final String TOTAL_RUNS = "total_runs";
	public static final String TOTAL_WICKETS = "total_wickets";
	
	//holder to place the balls
	private LinearLayout holder;
	private TextView ball;
	private TextView score_view, overs_view;
	private HorizontalScrollView scrollBall;
	private Animation animation;
	private Button button_sameball;
	
	private int score = 0;
	private int wickets = 0;
	private int balls = 0;
	private boolean same_ball = false;
	
	public static final int WICKET = 10;
	public static final int NO_WICKET = 11;
	public static final int CORRECT_BALL = 9;
	public static final int WIDE_BALL = 8;
	public static final int NO_BALL = 7;
	
	public static final int RESULT_WHAT = 12;
	public static final int RESULT_RUNS = 13;
	public static final int RESULT_WICKET = 14;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Window win = getWindow();
        win.setBackgroundDrawableResource(R.drawable.grass_tile_repeat);
        button_sameball = (Button)findViewById(R.id.button_sameball);
        button_sameball.setOnClickListener(this);
        findViewById(R.id.button_undo).setOnClickListener(this);
        findViewById(R.id.button_dotball).setOnClickListener(this);
        findViewById(R.id.button_one).setOnClickListener(this);
        findViewById(R.id.button_two).setOnClickListener(this);
        findViewById(R.id.button_three).setOnClickListener(this);
        findViewById(R.id.button_four).setOnClickListener(this);
        findViewById(R.id.button_six).setOnClickListener(this);
        findViewById(R.id.button_wicket).setOnClickListener(this);
        findViewById(R.id.button_wide).setOnClickListener(this);
        findViewById(R.id.button_nobe).setOnClickListener(this);
        score_view = (TextView)findViewById(R.id.tv_score);
        overs_view = (TextView)findViewById(R.id.tv_overs);
        holder = (LinearLayout)findViewById(R.id.ll_ballholder);
        animation = AnimationUtils.makeInAnimation(this, false);
        scrollBall = (HorizontalScrollView)findViewById(R.id.horizontal_scroll);
        if(savedInstanceState != null)
        recoverPreviousState(savedInstanceState);
        updateScore();
    }
    
    
    private void recoverPreviousState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
    	score = savedInstanceState.getInt(TOTAL_RUNS, 0);
    	balls = savedInstanceState.getInt(TOTAL_BALLS, 0);
    	wickets = savedInstanceState.getInt(TOTAL_WICKETS, 0);
    	int no_ballViews = savedInstanceState.getInt(NO_OF_BALLS,-1);
    	for(int i=1; i<=no_ballViews && i!=-1; i++){
    		String s = savedInstanceState.getString("ball"+i);
    		addRecoveredViews(s);
    	}
    	adjustScroll();
	}


	private void addRecoveredViews(String s) {
		// TODO Auto-generated method stub
		ball = (TextView)View.inflate(this, R.layout.ball, null);
		ball.setTag(s);
		ball.setText(s);
		if(s.startsWith("+"))
			s = s.substring(1);
		else
			ball.setBackgroundResource(R.drawable.ball_transback);
		holder.addView(ball);
	}


	public void onClick(View v){
    	Log.i(TAG, "handle button click called with view");
    	
    	AudioManager audio_manager= (AudioManager)getSystemService(AUDIO_SERVICE);
    	audio_manager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
    	
    	if(v.getId() == R.id.button_undo){
    		final int no_balls = holder.getChildCount();
    		if(no_balls != 0){
    			String s = (String)holder.getChildAt(no_balls-1).getTag();
    			processAndUpdateTag(s);
    			holder.removeViewAt(no_balls-1);
    			adjustScroll();
    			updateScore();
    		}
    		return;
    	}
    	
    	if(v.getId() == R.id.button_sameball){
    		same_ball = same_ball? false: true;
    		button_sameball.setSelected(same_ball);
    		return;
    	}
    	
    	
    	switch (v.getId()) {
    	case R.id.button_dotball:
    		process(0, "0", same_ball);
    		break;
    	
		case R.id.button_one:
			process(1,"1",same_ball);
			score++;
			break;
			
		case R.id.button_two:
			process(2,"2",same_ball);
			score+=2;
			break;
			
		case R.id.button_three:
			process(3,"3",same_ball);
			score+=3;
			break;

		case R.id.button_four:
			process(4,"4",same_ball);
			score+=4;
			break;
			
		case R.id.button_six:
			process(6,"6",same_ball);
			score+=6;
			break;
			
		case R.id.button_wicket:
			process(WICKET,"WK",same_ball);
			wickets++;
			break;
			
		case R.id.button_wide:
			process(WIDE_BALL, "WD", same_ball);
			score++;
			break;
			
		case R.id.button_nobe:
			process(NO_BALL, "NB", same_ball);
			score++;
			break;
			
		default:
			break;
		}
    	
    	if(!same_ball && v.getId()!=R.id.button_nobe && v.getId()!=R.id.button_wide)
    		balls++;
    	same_ball = false;
    	button_sameball.setSelected(same_ball);
    	updateScore();
    }
    
	//Moves the Scroll to the Right End
	private void adjustScroll() {
		// TODO Auto-generated method stub
		scrollBall.post(new Runnable(){

 			@Override
 			public void run() {
 				// TODO Auto-generated method stub
 				scrollBall.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
 			}
 			
 		});
	}


	//understands the tag. if it starts with '+' that means that is the same ball, so it reduces the score but not the balls.
    private void processAndUpdateTag(String s) {
		// TODO Auto-generated method stub
    	Log.i(TAG, "tag: "+s);
    	boolean sameball = false;
    	if(s.startsWith("+")){
    		s = s.substring(1);
    		Log.i(TAG, "tag substring: "+s);
    		sameball = true;
    	}
    	int tag = Integer.parseInt(s);
    	if(tag==NO_BALL || tag==WIDE_BALL){
    		score--;
    		return;
    	}
    	if(tag == WICKET){
    		wickets--;
    		if(!sameball)
    			balls--;
    		return;
    	}
    	score-=tag;
		if(!sameball)
			balls--;
	}


	/*
     * process the button clicks. 
     * adds '+' to the string Tag if it is same ball. these tags are stored as bundle in onSaveInstanceState();
     * While reading those string if there is '+' in the string we wont set ball background to that view.
     */
    private void process(int result,String text, boolean sameball){
    	Log.i(TAG, "balls: "+balls);
    	if(balls % 6 == 0 && !sameball){
    		holder.removeAllViews();
    	}
    	ball = (TextView)View.inflate(this, R.layout.ball, null);
		if(sameball){
			text = "+"+text;
			ball.setTag("+"+result);
		}else{
			ball.setTag(""+result);
			ball.setBackgroundResource(R.drawable.ball_transback);
		}
		ball.setText(text);
		holder.addView(ball);
		ball.startAnimation(animation);
		adjustScroll();
    }
    
    //updates the score and overs
    private void updateScore(){
    	score_view.setText(""+score+"/"+wickets);
    	overs_view.setText(""+balls/6+"."+balls%6);
    }
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
    	Log.i(TAG, "savedinstacestare() called");
    	int no_of_balls = holder.getChildCount();
    	outState.putInt(TOTAL_RUNS, score);
    	outState.putInt(TOTAL_BALLS, balls);
    	outState.putInt(TOTAL_WICKETS, wickets);
    	outState.putInt(NO_OF_BALLS, no_of_balls);
    	for(int i=1; i<=no_of_balls; i++){
    		String s = (String)holder.getChildAt(i-1).getTag();
    		outState.putString("ball"+i, s);
    		Log.i(TAG, "saving ball "+ i+" as "+s);
    	}
		super.onSaveInstanceState(outState);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add("RESET");
		menu.add("EDIT");
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getTitle().equals("RESET")){
			holder.removeAllViews();
			score = 0;
			balls = 0;
			wickets = 0;
			updateScore();
			adjustScroll();
		}
		if(item.getTitle().equals("EDIT")){
			Intent editIntent = new Intent(this, EditGameActivity.class);
			editIntent.putExtra(TOTAL_BALLS, balls);
			editIntent.putExtra(TOTAL_RUNS, score);
			editIntent.putExtra(TOTAL_WICKETS, wickets);
			startActivityForResult(editIntent, 1);
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == RESULT_OK){
		int new_balls = data.getIntExtra(TOTAL_BALLS, balls);
		wickets = data.getIntExtra(TOTAL_WICKETS, wickets);
		score = data.getIntExtra(TOTAL_RUNS, score);
		if(new_balls != balls){
			holder.removeAllViews();
			for(int i=0; i<new_balls%6; i++){
				addRecoveredViews("0");
			}
			adjustScroll();
		}
		balls = new_balls;
		updateScore();
		}
	}


		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent homeIntent = new Intent();
			homeIntent.setAction(Intent.ACTION_MAIN);
			homeIntent.addCategory(Intent.CATEGORY_HOME);
			homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(homeIntent);
			return true;
		}
		else
			return super.onKeyDown(keyCode, event);
	}
	
	
}
package com.hydapps.cricketcalc.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.utils.Utils;

public class ScoreBoardActivity extends ActionBarActivity implements View.OnClickListener {
    private static final int MENU_NEW_GAME = 1;
    private static final int MENU_EDIT = 2;
    private static final int MENU_BATTING = 3;
    private static final int MENU_WIN_SIDE1 = 4;
    private static final int MENU_WIN_SIDE2 = 5;
    private static final int MENU_DRAW = 6;
    /** Called when the activity is first created. */
	
	private final String TAG = "CRICKET_CALC";
	
	//keys to save in bundle
	private static final String NO_OF_BALLS = "no_of_balls";//no of mBalls including same mBalls in the current over
	
	//mBallHolder to place the mBalls
	private LinearLayout mBallHolder;
	private TextView mBall;
	private TextView mScoreView, mOversView;
	private HorizontalScrollView mScrollbar;
	private Animation mAnimation;
	private Button mButtonSameBall;
	
	private boolean mSameBall = false;
    private GameDetails mGameDetails;

	public static final int WICKET = 10;
	public static final int NO_WICKET = 11;
	public static final int CORRECT_BALL = 9;
	public static final int WIDE_BALL = 8;
	public static final int NO_BALL = 7;
	
	public static final int RESULT_WHAT = 12;
	public static final int RESULT_RUNS = 13;
	public static final int RESULT_WICKET = 14;
    private int mScore;
    private int mBalls;
    private int mWickets;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Window win = getWindow();
        win.setBackgroundDrawableResource(R.drawable.grass_tile_repeat);
        mButtonSameBall = (Button)findViewById(R.id.button_sameball);
        mButtonSameBall.setOnClickListener(this);
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
        mScoreView = (TextView)findViewById(R.id.tv_score);
        mOversView = (TextView)findViewById(R.id.tv_overs);
        mBallHolder = (LinearLayout)findViewById(R.id.ll_ballholder);
        mAnimation = AnimationUtils.makeInAnimation(this, false);
        mScrollbar = (HorizontalScrollView)findViewById(R.id.horizontal_scroll);
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            mGameDetails = savedInstanceState.getParcelable(Utils.EXTRA_GAME_DETAILS);
        } else if (intent != null) {
            mGameDetails = intent.getParcelableExtra(Utils.EXTRA_GAME_DETAILS);
        }
        if (mGameDetails == null || (mGameDetails.getGameSate() != GameDetails.GameState.SIDE1_BATTING
                && mGameDetails.getGameSate() != GameDetails.GameState.SIDE2_BATTING)) {
            Log.e(TAG, "this activity handles games that are in progress!!");
            finish();
            return;
        }
        recoverPreviousState(savedInstanceState);
        updateScore();
    }
    
    
    private void recoverPreviousState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        GameDetails gameDetails = mGameDetails;
        if (gameDetails.getGameSate() == GameDetails.GameState.SIDE1_BATTING) {
            mScore = gameDetails.getScore1();
            mBalls = gameDetails.getBalls1();
            mWickets = gameDetails.getWickets1();
        } else if (gameDetails.getGameSate() == GameDetails.GameState.SIDE2_BATTING) {
            mScore = gameDetails.getScore2();
            mBalls = gameDetails.getBalls2();
            mWickets = gameDetails.getWickets2();
        } else {
            throw new IllegalStateException("no team is batting");
        }
        if (savedInstanceState != null) {
            int no_ballViews = savedInstanceState.getInt(NO_OF_BALLS,-1);
            for(int i=1; i<=no_ballViews && no_ballViews != -1; i++){
                String s = savedInstanceState.getString("mBall"+i);
                addRecoveredViews(s);
            }
            adjustScroll();
        }
	}


	private void addRecoveredViews(String s) {
		// TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        mBall = (TextView)inflater.inflate(R.layout.ball, mBallHolder, false);
		mBall.setTag(s);
		if(s.startsWith("+")) {
            mBall.setText(getTextForballType(Integer.parseInt(s.substring(1))));
        } else {
            mBall.setBackgroundResource(R.drawable.ball_transback);
            mBall.setText(getTextForballType(Integer.parseInt(s)));
        }
		mBallHolder.addView(mBall);
	}

    private String getTextForballType(int ball_type) {
        String ret = null;
        switch (ball_type) {
            case NO_BALL:
                ret = "NB";
                break;
            case WIDE_BALL:
                ret = "WD";
                break;
            case WICKET:
                ret = "WK";
                break;
            default:
                ret = String.valueOf(ball_type);
                break;
        }
        return ret;
    }


	public void onClick(View v){
    	Log.i(TAG, "handle button click called with view");
    	
    	AudioManager audio_manager= (AudioManager)getSystemService(AUDIO_SERVICE);
    	audio_manager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
    	
    	if(v.getId() == R.id.button_undo){
    		final int no_balls = mBallHolder.getChildCount();
    		if(no_balls != 0){
    			String s = (String) mBallHolder.getChildAt(no_balls-1).getTag();
    			processAndUpdateTag(s);
    			mBallHolder.removeViewAt(no_balls - 1);
    			adjustScroll();
    			updateScore();
    		}
    		return;
    	}
    	
    	if(v.getId() == R.id.button_sameball){
    		mSameBall = !mSameBall;
    		mButtonSameBall.setSelected(mSameBall);
    		return;
    	}
    	
    	
    	switch (v.getId()) {
    	case R.id.button_dotball:
    		process(0, "0", mSameBall);
    		break;
    	
		case R.id.button_one:
			process(1,"1", mSameBall);
			mScore++;
			break;
			
		case R.id.button_two:
			process(2,"2", mSameBall);
			mScore +=2;
			break;
			
		case R.id.button_three:
			process(3,"3", mSameBall);
			mScore +=3;
			break;

		case R.id.button_four:
			process(4,"4", mSameBall);
			mScore +=4;
			break;
			
		case R.id.button_six:
			process(6,"6", mSameBall);
			mScore +=6;
			break;
			
		case R.id.button_wicket:
			process(WICKET,getTextForballType(WICKET), mSameBall);
			mWickets++;
			break;
			
		case R.id.button_wide:
			process(WIDE_BALL, getTextForballType(WIDE_BALL), mSameBall);
			mScore++;
			break;
			
		case R.id.button_nobe:
			process(NO_BALL, getTextForballType(NO_BALL), mSameBall);
			mScore++;
			break;
			
		default:
			break;
		}
    	
    	if(!mSameBall && v.getId()!=R.id.button_nobe && v.getId()!=R.id.button_wide)
    		mBalls++;
    	mSameBall = false;
    	mButtonSameBall.setSelected(mSameBall);
    	updateScore();
    }
    
	//Moves the Scroll to the Right End
	private void adjustScroll() {
		// TODO Auto-generated method stub
		mScrollbar.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mScrollbar.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }

        });
	}


	//understands the tag. if it starts with '+' that means that is the same mBall, so it reduces the mScore but not the mBalls.
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
    		mScore--;
    		return;
    	}
    	if(tag == WICKET){
    		mWickets--;
    		if(!sameball)
    			mBalls--;
    		return;
    	}
    	mScore -=tag;
		if(!sameball)
			mBalls--;
	}


	/*
     * process the button clicks. 
     * adds '+' to the string Tag if it is same mBall. these tags are stored as bundle in onSaveInstanceState();
     * While reading those string if there is '+' in the string we wont set mBall background to that view.
     */
    private void process(int result,String text, boolean sameball){
    	Log.i(TAG, "mBalls: "+ mBalls);
    	if(mBalls % 6 == 0 && !sameball){
    		mBallHolder.removeAllViews();
    	}
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
    	mBall = (TextView)inflater.inflate(R.layout.ball, mBallHolder, false);
		if (sameball) {
			text = "+"+text;
			mBall.setTag("+"+result);
		} else {
			mBall.setTag(""+result);
			mBall.setBackgroundResource(R.drawable.ball_transback);
		}
		mBall.setText(text);
		mBallHolder.addView(mBall);
		mBall.startAnimation(mAnimation);
		adjustScroll();
    }
    
    //updates the mScore and overs
    private void updateScore(){
    	mScoreView.setText("" + mScore + "/" + mWickets);
    	mOversView.setText("" + mBalls / 6 + "." + mBalls % 6);
    }
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
    	Log.i(TAG, "savedinstacestare() called");
    	int no_of_balls = mBallHolder.getChildCount();
        writeToGameDetails();
        outState.putParcelable(Utils.EXTRA_GAME_DETAILS, mGameDetails);
    	outState.putInt(NO_OF_BALLS, no_of_balls);
    	for(int i=1; i<=no_of_balls; i++){
    		String s = (String) mBallHolder.getChildAt(i-1).getTag();
    		outState.putString("mBall"+i, s);
    		Log.i(TAG, "saving mBall "+ i+" as "+s);
    	}
		super.onSaveInstanceState(outState);
	}

    private void writeToGameDetails() {
        mGameDetails.updateBattingTeamScore(mScore);
        mGameDetails.updateBattingTeamBallsPlayed(mBalls);
        mGameDetails.updateBattingTeamWickets(mWickets);
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(1, MENU_NEW_GAME, 0, R.string.new_game);
		menu.add(1, MENU_EDIT, 0, R.string.str_edit);
        if (mGameDetails.getGameSate() == GameDetails.GameState.SIDE1_BATTING) {
            menu.add(1, MENU_BATTING, 0, getString(R.string.menu_batting, 2));
        }/* else if (mGameDetails.getGameSate() == GameDetails.GameState.SIDE2_BATTING) {
            menu.add(1, MENU_BATTING, 0, getString(R.string.menu_batting, 1));
        }*/
        menu.add(1, MENU_WIN_SIDE1, 0, getString(R.string.menu_declare_win, 1));
        menu.add(1, MENU_WIN_SIDE2, 0, getString(R.string.menu_declare_win, 2));
        menu.add(1, MENU_DRAW, 0, R.string.menu_declare_draw);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_EDIT:
                Intent editIntent = new Intent(this, EditGameActivity.class);
                writeToGameDetails();
                editIntent.putExtra(Utils.EXTRA_GAME_DETAILS, mGameDetails);
                startActivityForResult(editIntent, 1);
                return true;
            case MENU_NEW_GAME:
                Intent intent = new Intent(this, NewGameEditActivity.class);
                startActivity(intent);
                writeToGameDetails();
                return true;
            case MENU_BATTING:
                if (mGameDetails.getGameSate() == GameDetails.GameState.SIDE1_BATTING) {
                    mGameDetails.setGameSate(GameDetails.GameState.SIDE2_BATTING);
                }
                refreshUi();
                return true;
            case MENU_DRAW:
                mGameDetails.setGameSate(GameDetails.GameState.DRAW);
                refreshUi();
                return true;
            case MENU_WIN:
                if (mGameDetails.getGameSate() == GameDetails.GameState.SIDE1_BATTING) {
                    mGameDetails.setGameSate(GameDetails.GameState.WIN_SIDE1);
                } else if (mGameDetails.getGameSate() == GameDetails.GameState.SIDE2_BATTING) {
                    mGameDetails.setGameSate(GameDetails.GameState.WIN_SIDE2);
                }
                refreshUi();
                return true;
        }
		return false;
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        int new_balls;
        if (resultCode == RESULT_OK) {
            mGameDetails = data.getParcelableExtra(Utils.EXTRA_GAME_DETAILS);
            if (mGameDetails.getGameSate() == GameDetails.GameState.SIDE1_BATTING) {
                mScore = mGameDetails.getScore1();
                new_balls = mGameDetails.getBalls1();
                mWickets = mGameDetails.getWickets1();
            } else if (mGameDetails.getGameSate() == GameDetails.GameState.SIDE2_BATTING) {
                mScore = mGameDetails.getScore2();
                new_balls = mGameDetails.getBalls2();
                mWickets = mGameDetails.getWickets2();
            } else {
                throw new IllegalStateException("no team is batting");
            }

            if (new_balls != mBalls) {
                mBallHolder.removeAllViews();
                for (int i = 0; i < new_balls % 6; i++) {
                    addRecoveredViews("0");
                }
                adjustScroll();
            }
            mBalls = new_balls;
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


    @Override
    protected void onPause() {
        super.onPause();
        Utils.saveAsync(this, mGameDetails, null);
    }
}
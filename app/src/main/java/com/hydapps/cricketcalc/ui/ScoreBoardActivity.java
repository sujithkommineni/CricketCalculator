package com.hydapps.cricketcalc.ui;

/**

 MIT License

 Copyright (c) [2016] [www.hydapps.com]

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.

 */

import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
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

public class ScoreBoardActivity extends AppCompatActivity implements View.OnClickListener {
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
    private int mBallsTemp;

    private UiAsyncQueryHandler mAsyncQueryHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        updateTitle();
        mAsyncQueryHandler = UiAsyncQueryHandler.getInstance();
    }
    
    
    private void recoverPreviousState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
            mGameDetails.addRuns(1);
			break;
			
		case R.id.button_two:
			process(2,"2", mSameBall);
            mGameDetails.addRuns(2);
			break;
			
		case R.id.button_three:
			process(3,"3", mSameBall);
            mGameDetails.addRuns(3);
			break;

		case R.id.button_four:
			process(4,"4", mSameBall);
            mGameDetails.addRuns(4);
			break;
			
		case R.id.button_six:
			process(6,"6", mSameBall);
            mGameDetails.addRuns(6);
			break;
			
		case R.id.button_wicket:
			process(WICKET,getTextForballType(WICKET), mSameBall);
            mGameDetails.addWickets(1);
			break;
			
		case R.id.button_wide:
			process(WIDE_BALL, getTextForballType(WIDE_BALL), mSameBall);
            mGameDetails.addRuns(1);
			break;
			
		case R.id.button_nobe:
			process(NO_BALL, getTextForballType(NO_BALL), mSameBall);
            mGameDetails.addRuns(1);
			break;
			
		default:
			break;
		}
    	
    	if(!mSameBall && v.getId()!=R.id.button_nobe && v.getId()!=R.id.button_wide)
    		mGameDetails.addBalls(1);
    	mSameBall = false;
    	mButtonSameBall.setSelected(mSameBall);
    	updateScore();
        if (mGameDetails.getGameSate() == GameDetails.GameState.SIDE2_BATTING) {
            if (mGameDetails.getScore2() > mGameDetails.getScore1()) {
                mGameDetails.setGameSate(GameDetails.GameState.WIN_SIDE2);
                finishAndLaunchSummaryActivity();
            }
        }
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
            mGameDetails.addRuns(-1);
    		return;
    	}
    	if(tag == WICKET){
            mGameDetails.addWickets(-1);
    		if(!sameball)
    			mGameDetails.addBalls(-1);
    		return;
    	}
        mGameDetails.addRuns(-tag);
		if(!sameball)
            mGameDetails.addBalls(-1);
	}


	/*
     * process the button clicks. 
     * adds '+' to the string Tag if it is same mBall. these tags are stored as bundle in onSaveInstanceState();
     * While reading those string if there is '+' in the string we wont set mBall background to that view.
     */
    private void process(int result,String text, boolean sameball){
    	Log.i(TAG, "mBalls: "+ mGameDetails.getBalls());
    	if(mGameDetails.getBalls() % 6 == 0 && !sameball){
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
    	mScoreView.setText("" + mGameDetails.getScore() + "/" + mGameDetails.getWickets());
        mOversView.setText(Utils.convertToOvers(mGameDetails.getBalls()));
    }
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
    	Log.i(TAG, "savedinstacestare() called");
    	int no_of_balls = mBallHolder.getChildCount();
        outState.putParcelable(Utils.EXTRA_GAME_DETAILS, mGameDetails);
    	outState.putInt(NO_OF_BALLS, no_of_balls);
    	for(int i=1; i<=no_of_balls; i++){
    		String s = (String) mBallHolder.getChildAt(i-1).getTag();
    		outState.putString("mBall"+i, s);
    		Log.i(TAG, "saving mBall "+ i+" as "+s);
    	}
		super.onSaveInstanceState(outState);
	}


    /*private void readFromGameDetails(GameDetails gameDetails) {
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
    }*/

    private void updateTitle() {
        if (mGameDetails.getGameSate() == GameDetails.GameState.SIDE1_BATTING) {
            setTitle(mGameDetails.getSide1());
        } else if (mGameDetails.getGameSate() == GameDetails.GameState.SIDE2_BATTING) {
            setTitle(mGameDetails.getSide2());
            getSupportActionBar().setSubtitle(getString(R.string.str_target)
                    + " "+String.valueOf(mGameDetails.getScore1()));
        } else {
            Log.e(TAG, "updateTitle: wrong game state.");
        }
    }


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
        menu.clear();
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
                mBallsTemp = mGameDetails.getBalls();
                Intent editIntent = new Intent(this, EditGameActivity.class);
                editIntent.putExtra(Utils.EXTRA_GAME_DETAILS, mGameDetails);
                startActivityForResult(editIntent, 1);
                return true;
            case MENU_NEW_GAME:
                Intent intent = new Intent(this, NewGameEditActivity.class);
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case MENU_BATTING://change the batting team to side2
                if (mGameDetails.getGameSate() == GameDetails.GameState.SIDE1_BATTING) {
                    mGameDetails.setGameSate(GameDetails.GameState.SIDE2_BATTING);
                }
                invalidateOptionsMenu();
                refreshUi();
                clearBalls();
                return true;
            case MENU_DRAW:
                mGameDetails.setGameSate(GameDetails.GameState.DRAW);
                finishAndLaunchSummaryActivity();
                return true;
            case MENU_WIN_SIDE1:
                mGameDetails.setGameSate(GameDetails.GameState.WIN_SIDE1);
                finishAndLaunchSummaryActivity();
                return true;
            case MENU_WIN_SIDE2:
                mGameDetails.setGameSate(GameDetails.GameState.WIN_SIDE2);
                finishAndLaunchSummaryActivity();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
		return false;
	}

    private void refreshUi() {
        if (mGameDetails.getGameSate() != GameDetails.GameState.SIDE1_BATTING
                && mGameDetails.getGameSate() != GameDetails.GameState.SIDE2_BATTING) {
            Intent gameSummary = new Intent(this, GameSummaryActivity.class);
            startActivity(gameSummary);
            finish();
            return;
        }
        updateScore();
        updateTitle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        int new_balls;
        if (resultCode == RESULT_OK) {
            mGameDetails = data.getParcelableExtra(Utils.EXTRA_GAME_DETAILS);
            if (mGameDetails.getGameSate() != GameDetails.GameState.SIDE1_BATTING
                    && mGameDetails.getGameSate() != GameDetails.GameState.SIDE2_BATTING) {
                finishAndLaunchSummaryActivity();
                return;
            }
            new_balls = mGameDetails.getBalls();

            if (new_balls != mBallsTemp) {
                mBallHolder.removeAllViews();
                for (int i = 0; i < new_balls % 6; i++) {
                    addRecoveredViews("0");
                }
                adjustScroll();
            }
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
        mAsyncQueryHandler.saveAsync(this, mGameDetails);
    }

    private void clearBalls() {
        mBallHolder.removeAllViews();
    }

    private void finishAndLaunchSummaryActivity() {
        Intent summaryIntent = new Intent(this, GameSummaryActivity.class);
        summaryIntent.putExtra(Utils.EXTRA_GAME_DETAILS, mGameDetails);
        startActivity(summaryIntent);
        finish();
    }
}
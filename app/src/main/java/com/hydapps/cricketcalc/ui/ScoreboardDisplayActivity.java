package com.hydapps.cricketcalc.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.utils.Utils;

public class ScoreboardDisplayActivity extends ActionBarActivity {

    TextView mTextGameName;
    TextView mTextSide1;
    TextView mTextSide2;
    TextView mTextScore1;
    TextView mTextScore2;
    TextView mTextBalls1;
    TextView mTextBalls2;
    TextView mTextGameResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameDetails gameDetails = getIntent().getParcelableExtra(Utils.EXTRA_GAME_DETAILS);
        if (gameDetails == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_scoreboard_display);

        mTextGameName = (TextView) findViewById(R.id.tv_item_header);
        mTextSide1 = (TextView) findViewById(R.id.tv_item_side1);
        mTextSide2 = (TextView) findViewById(R.id.tv_item_side2);
        mTextScore1 = (TextView) findViewById(R.id.tv_item_score_side1);
        mTextScore2 = (TextView) findViewById(R.id.tv_item_score_side2);
        mTextBalls1 = (TextView) findViewById(R.id.tv_item_overs_side1);
        mTextBalls2 = (TextView) findViewById(R.id.tv_item_overs_side2);
        mTextGameResult = (TextView) findViewById(R.id.tv_item_result);
        bindData(gameDetails);
    }

    private void bindData(GameDetails details) {
        mTextGameName.setText(details.getGameName());
        mTextSide1.setText(details.getSide1());
        mTextSide2.setText(details.getSide2());
        mTextScore1.setText(details.getScore1() + "/" + details.getWickets1());
        mTextScore2.setText(details.getScore2() + "/" + details.getWickets2());
        String overs = details.getBalls1() / 6 + "." + details.getBalls1() % 6;
        mTextBalls1.setText(overs);
        overs = details.getBalls2() / 6 + "." + details.getBalls2() % 6;
        mTextBalls2.setText(overs);
        mTextGameResult.setText(Utils.getResultString(details, this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scoreboard_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

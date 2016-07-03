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
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.MobileAds;
import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.db.GamesDb;
import com.hydapps.cricketcalc.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NewMatchChooserActivity  extends AppCompatActivity {

    private GameDetails mLatestGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8413693216965428~1065316742");
        setContentView(R.layout.activity_new_match_chooser);
        mLatestGame = GamesDb.getLatestGame(this);
        View button_continue = findViewById(R.id.button_continue);
        if (!isResumeGameNeeded()) {
            button_continue.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.appLaunched(this);
        if (Utils.needToShowReviewDialog(this)) {
            new FeedbackDialogFragment().show(getSupportFragmentManager(), "feedback_dialog");
        }
    }

    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.button_newgame:
                Intent newGameIntent = new Intent(this,NewGameEditActivity.class);
                startActivity(newGameIntent);
                break;
            case R.id.button_continue:
                Intent scoreBoardIntent = new Intent(this, ScoreBoardActivity.class);
                scoreBoardIntent.putExtra(Utils.EXTRA_GAME_DETAILS, mLatestGame);
                startActivity(scoreBoardIntent);
                break;
            case R.id.button_savedgames:
                Intent savedGames = new Intent(this, GamesListActivity.class);
                startActivity(savedGames);
                break;
        }
    }

    private boolean isResumeGameNeeded() {
        if (mLatestGame != null) {
            Date date = Calendar.getInstance().getTime();
            long startTime = mLatestGame.getStartTime();
            long diff = date.getTime() - startTime;
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            if (hours < 12 && (mLatestGame.getGameSate() == GameDetails.GameState.SIDE1_BATTING || mLatestGame.getGameSate() == GameDetails.GameState.SIDE2_BATTING)) {
                return true;
            }
        }
        return false;
    }
}

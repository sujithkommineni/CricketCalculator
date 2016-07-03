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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.db.GamesDb;
import com.hydapps.cricketcalc.utils.DateUtils;
import com.hydapps.cricketcalc.utils.Utils;

import java.util.Date;

public class NewGameEditActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEditGameName;
    private EditText mEditSide1;
    private EditText mEditSide2;
    private Button mButtonStart;

    private String mGameNameString, mSide1Str, mSide2Str;

    private long mStartTime;

    private UiAsyncQueryHandler mAsyncHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.new_game_edit_layout);
        mEditGameName = (EditText) findViewById(R.id.edit_game_name);
        mEditSide1 = (EditText) findViewById(R.id.edit_side1);
        mEditSide2 = (EditText) findViewById(R.id.edit_side2);
        mStartTime = new Date().getTime();
        mGameNameString = getString(R.string.str_game_prefix) + "_" + DateUtils.getDateTimeString(mStartTime);
        mEditGameName.setText(mGameNameString);
        mEditSide1.setText(R.string.str_side1);
        mEditSide2.setText(R.string.str_side2);
        mButtonStart = (Button) findViewById(R.id.button_start);
        mButtonStart.setOnClickListener(this);
        mAsyncHandler = UiAsyncQueryHandler.getInstance();
    }


    @Override
    public void onClick(View v) {
        if (!TextUtils.isEmpty(mEditGameName.getText().toString())) {
            mGameNameString = mEditGameName.getText().toString();
        }

        mSide1Str = mEditSide1.getText().toString();
        if (TextUtils.isEmpty(mSide1Str)) {
            mSide1Str = getString(R.string.str_side1);
        }

        mSide2Str = mEditSide2.getText().toString();
        if (TextUtils.isEmpty(mSide2Str)) {
            mSide2Str = getString(R.string.str_side2);
        }

        mAsyncHandler.insertAsync(mGameNameString, mSide1Str, mSide2Str, mStartTime,  this,
                new UiAsyncQueryHandler.AsyncOperationDoneListener() {
                    @Override
                    public void onAsyncOperationDone(Object obj) {
                        if (obj != null) {
                            long rowId = (Long) obj;
                            GameDetails game = new GameDetails();
                            game.setGameName(mGameNameString);
                            game.setSide1(mSide1Str);
                            game.setSide2(mSide2Str);
                            game.setRowId(rowId);
                            launchScorerActivity(game);
                        }
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchScorerActivity(GameDetails game) {
        Intent intent = new Intent(this, ScoreBoardActivity.class);
        intent.putExtra(Utils.EXTRA_GAME_DETAILS, game);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}

package com.hydapps.cricketcalc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GamesDb;
import com.hydapps.cricketcalc.utils.DateUtils;

import java.util.Date;

/**
 * Created by HRGN76 on 12/19/2014.
 */
public class NewGameEditActivity extends Activity implements View.OnClickListener{

    private EditText mEditGameName;
    private EditText mEditSide1;
    private EditText mEditSide2;
    private Button mButtonStart;

    private String mGameNameString, mSide1Str, mSide2Str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game_edit_layout);
        mEditGameName = (EditText) findViewById(R.id.edit_game_name);
        mEditSide1 = (EditText) findViewById(R.id.edit_side1);
        mEditSide2 = (EditText) findViewById(R.id.edit_side2);
        mGameNameString = getString(R.string.str_game_prefix) + DateUtils.getDateTimeString(new Date().getTime());
        mEditGameName.setText(mGameNameString);
        mEditSide1.setText(R.string.str_side1);
        mEditSide2.setText(R.string.str_side2);
        mButtonStart = (Button) findViewById(R.id.button_start);
        mButtonStart.setOnClickListener(this);
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

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GamesDb.insertNewGame(NewGameEditActivity.this, mGameNameString, mSide1Str, mSide2Str);
            }
        });
        // TODO start scorer activity and finish this.
        Intent intent = new Intent(this, GamesListActivity.class);
        startActivity(intent);
    }
}

package com.hydapps.cricketcalc.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GamesDb;

import java.util.ArrayList;


/**
 * Created by HRGN76 on 12/22/2014.
 */
public class GamesListActivity extends Activity {

    private ArrayList<MatchDetails> mMatchDetailsList;
    private Handler mUpdateHandler;
    private static final int INVALIDATE_LIST = 1;

    private RecyclerView mRecyclerView;
    private GameListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMatchDetailsList = new ArrayList<>();
        setContentView(R.layout.games_list_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_gamelist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GameListAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);
        mUpdateHandler = new UpdateHandler();
        startLoading();
    }



    private void startLoading() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Cursor cursor;
                cursor = GamesDb.getAllGames(GamesListActivity.this);
                mMatchDetailsList.clear();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        MatchDetails match = new MatchDetails();
                        match.mGameName = cursor.getString(0);
                        match.mTime = cursor.getString(1);
                        match.mSide1 = cursor.getString(2);
                        match.mSide2 = cursor.getString(3);
                        match.mSide1Score = cursor.getInt(4);
                        match.mSide1Wkts = cursor.getInt(5);
                        match.mSide1BallsPlayed = cursor.getInt(6);
                        match.mSide2Score = cursor.getInt(7);
                        match.mSide2Wkts = cursor.getInt(8);
                        match.mSide2BallsPlayed = cursor.getInt(9);
                        match.mGameState = cursor.getInt(10);
                        match.mNote = cursor.getString(11);
                        mMatchDetailsList.add(match);
                    } while(cursor.moveToNext());
                }
                mUpdateHandler.sendEmptyMessage(INVALIDATE_LIST);
                if (cursor != null) {
                    cursor.close();
                }
            }
        });
    }

    private class UpdateHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INVALIDATE_LIST:
                    mAdapter.updateList(mMatchDetailsList);
            }
        }
    }

}

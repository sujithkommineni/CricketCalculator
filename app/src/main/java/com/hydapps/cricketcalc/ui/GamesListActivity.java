package com.hydapps.cricketcalc.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.db.GamesDb;
import com.hydapps.cricketcalc.utils.Utils;

import java.util.ArrayList;


/**
 * Created by HRGN76 on 12/22/2014.
 */
public class GamesListActivity extends Activity implements GameListAdapter.OnEditClickListener{

    private ArrayList<GameDetails> mMatchDetailsList;
    private Handler mUpdateHandler;
    private static final int INVALIDATE_LIST = 1;

    private RecyclerView mRecyclerView;
    private GameListAdapter mAdapter;
    private static final int REQ_EDIT_GAME = 1;


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
                        GameDetails match = new GameDetails();
                        match.setGameName(cursor.getString(0));
                        match.setStartTime(cursor.getLong(1));
                        match.setSide1(cursor.getString(2));
                        match.setSide2(cursor.getString(3));
                        match.setScore1(cursor.getInt(4));
                        match.setWickets1(cursor.getInt(5));
                        match.setBalls1(cursor.getInt(6));
                        match.setScore2(cursor.getInt(7));
                        match.setWickets2(cursor.getInt(8));
                        match.setBalls2(cursor.getInt(9));
                        match.setGameSate(cursor.getInt(10));
                        match.setNote(cursor.getString(11));
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

    @Override
    public void onEditClick(int position) {
        GameDetails game = mMatchDetailsList.get(position);
        Intent editIntent = new Intent(this, EditGameActivity.class);
        editIntent.putExtra(Utils.EXTRA_GAME_DETAILS, game);
        startActivityForResult(editIntent, REQ_EDIT_GAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_EDIT_GAME && resultCode == RESULT_OK) {

        }
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

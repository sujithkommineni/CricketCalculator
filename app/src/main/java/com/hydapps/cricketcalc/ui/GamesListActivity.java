package com.hydapps.cricketcalc.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.db.GamesDb;
import com.hydapps.cricketcalc.utils.Utils;

import java.util.ArrayList;
import static com.hydapps.cricketcalc.utils.Utils.DEBUG;


/**
 * Created by HRGN76 on 12/22/2014.
 */
public class GamesListActivity extends AppCompatActivity implements GameListAdapter.OnEditClickListener, PopupMenu.OnMenuItemClickListener {

    private ArrayList<GameDetails> mMatchDetailsList;
    private Handler mUpdateHandler;
    private static final int INVALIDATE_LIST = 1;

    private RecyclerView mRecyclerView;
    private GameListAdapter mAdapter;
    private static final int REQ_EDIT_GAME = 1;
    private int mPopupMenuPosition;
    private PopupMenu mPopupMenu;
    UiAsyncQueryHandler mUiAsyncQueryHandler;

    private static final String TAG = "GamesListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMatchDetailsList = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.games_list_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_gamelist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        mAdapter = new GameListAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnEditClickListener(this);
        mUpdateHandler = new UpdateHandler();
        mUiAsyncQueryHandler = UiAsyncQueryHandler.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLoading();
    }

    private void startLoading() {
                mUiAsyncQueryHandler.queryForAllGamesAsync(new UiAsyncQueryHandler.AsyncOperationDoneListener() {
            @Override
            public void onAsyncOperationDone(Object obj) {
                mMatchDetailsList.clear();
                Cursor cursor = (Cursor) obj;
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
                        match.setRowId(cursor.getLong(12));
                        mMatchDetailsList.add(match);
                    } while(cursor.moveToNext());
                }
                mUpdateHandler.sendEmptyMessage(INVALIDATE_LIST);
                if (DEBUG) Log.v(TAG, "list size: " + mMatchDetailsList.size());
                if (cursor != null) {
                    cursor.close();
                }
            }
        }, this);
    }

    @Override
    public void onEditClick(int position, View anchor) {
        mPopupMenuPosition = position;
        PopupMenu menu = new PopupMenu(this, anchor);
        menu.inflate(R.menu.games_list_item_menu);
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

    @Override
    public void onItemClick(int position) {
        Intent gameSummary = new Intent(this, GameSummaryActivity.class);
        gameSummary.putExtra(Utils.EXTRA_GAME_DETAILS, mMatchDetailsList.get(position));
        startActivity(gameSummary);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_EDIT_GAME && resultCode == RESULT_OK) {
            GameDetails game = data.getParcelableExtra(Utils.EXTRA_GAME_DETAILS);
            updateItem(game);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        //menuItem.getMenuInfo().
        switch (menuItem.getItemId()) {
            case R.id.menu_edit:
                GameDetails game = mMatchDetailsList.get(mPopupMenuPosition);
                if (DEBUG) Log.v(TAG, "game: " + game);
                Intent editIntent = new Intent(this, EditGameActivity.class);
                editIntent.putExtra(Utils.EXTRA_GAME_DETAILS, game);
                startActivityForResult(editIntent, REQ_EDIT_GAME);
                return true;
            case R.id.menu_share:
                return true;
            case R.id.menu_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return false;
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

    private void updateItem(GameDetails game) {
        mMatchDetailsList.set(mPopupMenuPosition, game);
        mAdapter.notifyItemChanged(mPopupMenuPosition);
    }

    private Dialog mDeleteConfDialog;
    private void showDeleteConfirmationDialog() {
        if (mDeleteConfDialog != null) {
            mDeleteConfDialog = new AlertDialog.Builder(this).setTitle(R.string.str_are_you_sure)
                    .setMessage(R.string.delete_conf).setPositiveButton(R.string.str_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GameDetails game = mMatchDetailsList.get(mPopupMenuPosition);
                            Utils.deleteAsync(getApplicationContext(), game);
                            mMatchDetailsList.remove(mPopupMenuPosition);
                        }
                    }).setNegativeButton(R.string.menu_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog.dismiss();
                        }
                    }).create();
        }
    }

    private static class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mVerticalGap;
        public VerticalSpaceItemDecoration(int verticalGap) {
            mVerticalGap = verticalGap;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = mVerticalGap;
        }
    }


}

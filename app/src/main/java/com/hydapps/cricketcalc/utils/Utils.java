package com.hydapps.cricketcalc.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.db.GamesDb;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by sujith on 1/1/15.
 */
public class Utils {
    public static final boolean DEBUG = true;
    public static final String EXTRA_GAME_DETAILS = "EXTRA_GAME_DETAILS";

    public static String getResultString(GameDetails game, Context context) {
        String ret;
        if (game.getGameSate() == GameDetails.STATE_WIN_SIDE1) {
            ret = context.getString(R.string.str_result_sideWon, game.getSide1());
        } else if (game.getGameSate() == GameDetails.STATE_WIN_SIDE2) {
            ret = context.getString(R.string.str_result_sideWon, game.getSide2());
        } else if (game.getGameSate() == GameDetails.STATE_DRAW) {
            ret = context.getString(R.string.str_result_draw);
        } else {
            ret = context.getString(R.string.str_result_inprogress);
        }
        return ret;
    }

    /**
     * Should be called from Main Thread. if callback is supplied, onAsyncOperationDone() is called
     * on the mainthread at the end of this operation.
     * @param appContext
     * @param game
     * @param callback
     */
    public static void saveAsync(final Context appContext, final GameDetails game, final OnAsyncOperationDoneCallBack callback) {
        final android.os.Handler handler = new android.os.Handler();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GamesDb.updateGameDetails(appContext, game);
                if (callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onAsyncOperationDone();
                        }
                    });
                }
            }
        });
    }

    public interface OnAsyncOperationDoneCallBack {
        void onAsyncOperationDone();
    }

}

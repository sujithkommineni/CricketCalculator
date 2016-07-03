package com.hydapps.cricketcalc.utils;

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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.db.GamesDb;



public class Utils {
    public static final boolean DEBUG = false;
    public static final String EXTRA_GAME_DETAILS = "EXTRA_GAME_DETAILS";
    public static final String SHARED_PREF = "sp_file.xml";
    public static final String PREF_APP_REVIEWED = "PREF_APP_REVIEWED";
    public static final String PREF_APP_LAUNCHED = "PREF_APP_LAUNCHED";

    public static boolean isAppReviewed(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return sp.getBoolean(PREF_APP_REVIEWED, false);
    }

    public static void setAppReviewed(Context context, boolean reviewed) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        sp.edit().putBoolean(PREF_APP_REVIEWED, reviewed).apply();
    }

    public static boolean needToShowReviewDialog(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        boolean reviewed = sp.getBoolean(PREF_APP_REVIEWED, false);
        if (reviewed) return false;
        int launches = sp.getInt(PREF_APP_LAUNCHED, 0);
        if (launches > 1 && launches < Integer.MAX_VALUE) {
            if (launches < 10) {
                return launches % 3 == 0;
            } else {
                return launches % 10 == 0;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void appLaunched(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        int launches = sp.getInt(PREF_APP_LAUNCHED, 0);
        if (launches >= Integer.MAX_VALUE - 1) {
            launches = Integer.MAX_VALUE;
        } else {
            launches++;
        }
        sp.edit().putInt(PREF_APP_LAUNCHED, launches).apply();
    }


    public static String getResultString(GameDetails game, Context context) {
        String ret;
        if (game.getGameSate() == GameDetails.GameState.WIN_SIDE1) {
            ret = context.getString(R.string.str_result_sideWon, game.getSide1());
        } else if (game.getGameSate() == GameDetails.GameState.WIN_SIDE2) {
            ret = context.getString(R.string.str_result_sideWon, game.getSide2());
        } else if (game.getGameSate() == GameDetails.GameState.DRAW) {
            ret = context.getString(R.string.str_result_draw);
        } else {
            ret = context.getString(R.string.str_result_inprogress);
        }
        return ret;
    }

/*    *//**
     * Should be called from Main Thread. if callback is supplied, onAsyncOperationDone() is called
     * on the mainthread at the end of this operation.
     * @param appContext
     * @param game
     * @param callback
     *//*
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
    }*/

    public static void deleteAsync(final Context context, final GameDetails game) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GamesDb.deleteGame(context, game);
            }
        });
    }

    public interface OnAsyncOperationDoneCallBack {
        void onAsyncOperationDone();
    }

    public static String convertToOvers(int balls) {
        StringBuilder sb = new StringBuilder();
        sb.append((int) (balls/6));
        sb.append('.');
        sb.append(balls%6);
        return sb.toString();
    }

}

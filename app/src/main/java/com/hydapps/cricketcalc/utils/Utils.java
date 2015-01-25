package com.hydapps.cricketcalc.utils;

import android.content.Context;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;

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
}

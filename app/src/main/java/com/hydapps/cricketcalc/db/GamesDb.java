package com.hydapps.cricketcalc.db;

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


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GamesDb {

    public static final String TABLE_NAME = "games";
    public static final String _ID = "_id";
    public static final String GAME_NAME = "game_name";
    public static final String START_TIME = "start_time";
    public static final String SIDE_1 = "side_1";
    public static final String SIDE_2 = "side_2";
    public static final String SIDE_1_SCORE = "side_1_score";
    public static final String SIDE_1_WKTS = "side_1_wkts";
    public static final String SIDE_1_BALLS_PLAYED = "side_1_balls_played";
    public static final String SIDE_2_SCORE = "side_2_score";
    public static final String SIDE_2_WKTS = "side_2_wkts";
    public static final String SIDE_2_BALLS_PLAYED = "side_2_balls_played";
    public static final String MATCH_STATE = "match_state";
    public static final String NOTE = "note";

    private static final String[] PROJECTION = {GAME_NAME, START_TIME, SIDE_1, SIDE_2, SIDE_1_SCORE,
            SIDE_1_WKTS, SIDE_1_BALLS_PLAYED, SIDE_2_SCORE, SIDE_2_WKTS, SIDE_2_BALLS_PLAYED, MATCH_STATE, NOTE, _ID};

    private static class DBHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "games.db";
        private static final int DB_VERSION = 2;
        private static final String CREATE_TABLE = "CREATE TABLE games(_id integer NOT NULL PRIMARY KEY, game_name text, start_time integer, side_1 text, side_2 text, side_1_score integer," +
                " side_1_wkts integer, side_1_balls_played integer, side_2_score integer, side_2_wkts integer, side_2_balls_played integer, match_state integer, note text)";

        DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE " + TABLE_NAME );
            onCreate(db);
        }
    }

    public static long insertNewGame(Context context, String gameName, String side1, String side2, long startTime) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(GAME_NAME, gameName);
        cv.put(SIDE_1, side1);
        cv.put(SIDE_2, side2);
        cv.put(START_TIME, startTime);
        long rowId = db.insert(TABLE_NAME, null, cv);
        db.close();
        return rowId;
    }

    public static void updateSide(Context context, int side, int rowId, String side_name,
                                     int score, int wkts, int ballsPlayed) {

    }

    public static Cursor getAllGames(Context context) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, PROJECTION, null, null, null, null, START_TIME + " DESC");
        return c;
    }

    public static GameDetails getLatestGame(Context context) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, PROJECTION, null, null, null, null, START_TIME + " DESC", String.valueOf(1));
        GameDetails game = null;
        if (c != null && c.moveToFirst()) {
            game = new GameDetails();
            game.setGameName(c.getString(0));
            game.setStartTime(c.getLong(1));
            game.setSide1(c.getString(2));
            game.setSide2(c.getString(3));
            game.setScore1(c.getInt(4));
            game.setWickets1(c.getInt(5));
            game.setBalls1(c.getInt(6));
            game.setScore2(c.getInt(7));
            game.setWickets2(c.getInt(8));
            game.setBalls2(c.getInt(9));
            game.setGameSate(c.getInt(10));
            game.setNote(c.getString(11));
            game.setRowId(c.getLong(12));
        }
        if (c != null) c.close();
        return game;
    }

    public static void updateGameDetails(Context context, GameDetails game) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(GAME_NAME, game.getGameName());
        cv.put(SIDE_1, game.getSide1());
        cv.put(SIDE_2, game.getSide2());
        cv.put(SIDE_1_SCORE, game.getScore1());
        cv.put(SIDE_1_BALLS_PLAYED, game.getBalls1());
        cv.put(SIDE_1_WKTS, game.getWickets1());
        cv.put(SIDE_2_SCORE, game.getScore2());
        cv.put(SIDE_2_BALLS_PLAYED, game.getBalls2());
        cv.put(SIDE_2_WKTS, game.getWickets2());
        cv.put(MATCH_STATE, game.getGameSate().ordinal());
        cv.put(NOTE, game.getNote());
        db.update(TABLE_NAME, cv, _ID + "=?", new String[]{String.valueOf(game.getRowId())});
        db.close();
    }

    public static void deleteGame(Context context, GameDetails game) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete(TABLE_NAME, _ID+"=?", new String[]{String.valueOf(game.getRowId())});
        db.close();
    }
}

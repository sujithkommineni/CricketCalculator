package com.hydapps.cricketcalc.db;


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
            SIDE_1_WKTS, SIDE_1_BALLS_PLAYED, SIDE_2_SCORE, SIDE_2_WKTS, SIDE_2_BALLS_PLAYED, MATCH_STATE, NOTE};

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

    public static void insertNewGame(Context context, String gameName, String side1, String side2) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(GAME_NAME, gameName);
        cv.put(SIDE_1, side1);
        cv.put(SIDE_2, side2);
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public static void updateSide(Context context, int side, int rowId, String side_name,
                                     int score, int wkts, int ballsPlayed) {

    }

    public static Cursor getAllGames(Context context) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, PROJECTION, null, null, null, null, START_TIME);
        return c;
    }
}
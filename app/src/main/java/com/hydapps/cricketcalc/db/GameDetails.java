package com.hydapps.cricketcalc.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sujith on 10/1/15.
 */
public class GameDetails implements Parcelable{
    private String mGameName;
    private String mSide1;
    private String mSide2;
    private int mScore1;
    private int mScore2;
    private int mBalls1;
    private int mBalls2;
    private int mWickets1;
    private int mWickets2;
    private long mStartTime;
    private String mNote;
    private GameState mGameSate = GameState.SIDE1_BATTING;
    private long mId;
    private int mMaxOvers;

    /*public static final int STATE_SIDE1_BATTING = 0;
    public static final int STATE_SIDE2_BATTING = 1;

    public static final int STATE_WIN_SIDE1 = 2;
    public static final int STATE_WIN_SIDE2 = 3;
    public static final int STATE_DRAW = 4;*/

    public enum GameState {SIDE1_BATTING, SIDE2_BATTING, WIN_SIDE1, WIN_SIDE2, DRAW};

    public GameDetails(String mGameName, String mSide1, String mSide2,
                       int mScore1, int mScore2, int mBalls1, int mBalls2,
                       int mWickets1, int mWickets2, long time, String note, GameState gameState, long rowId) {
        this.mGameName = mGameName;
        this.mSide1 = mSide1;
        this.mSide2 = mSide2;
        this.mScore1 = mScore1;
        this.mScore2 = mScore2;
        this.mBalls1 = mBalls1;
        this.mBalls2 = mBalls2;
        this.mWickets1 = mWickets1;
        this.mWickets2 = mWickets2;
        this.mStartTime = time;
        this.mNote = note;
        this.mGameSate = gameState;
        this.mId = rowId;
    }

    public GameDetails() {}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MatchName: ").append(mGameName)
                .append("side1: ").append(mSide1)
                .append("score: ").append(mScore1).append('/').append(mWickets1)
                .append("overs: ").append(mBalls1/6).append('.').append(mBalls1%6)
                .append("side2: ").append(mSide2)
                .append("score: ").append(mScore2).append('/').append(mWickets2)
                .append("overs: ").append(mBalls2/6).append('.').append(mBalls2%6)
                .append("Result: ").append(mGameSate);
        return sb.toString();
    }

    public String getGameName() {
        return mGameName;
    }

    public void setGameName(String mGameName) {
        this.mGameName = mGameName;
    }

    public String getSide1() {
        return mSide1;
    }

    public void setSide1(String mSide1) {
        this.mSide1 = mSide1;
    }

    public String getSide2() {
        return mSide2;
    }

    public void setSide2(String mSide2) {
        this.mSide2 = mSide2;
    }

    public int getScore1() {
        return mScore1;
    }

    public void setScore1(int mScore1) {
        this.mScore1 = mScore1;
    }

    public int getScore2() {
        return mScore2;
    }

    public void setScore2(int mScore2) {
        this.mScore2 = mScore2;
    }

    public int getBalls1() {
        return mBalls1;
    }

    public void setBalls1(int mBalls1) {
        this.mBalls1 = mBalls1;
    }

    public int getBalls2() {
        return mBalls2;
    }

    public void setBalls2(int mBalls2) {
        this.mBalls2 = mBalls2;
    }

    public int getWickets1() {
        return mWickets1;
    }

    public void setWickets1(int mWickets1) {
        this.mWickets1 = mWickets1;
    }

    public int getWickets2() {
        return mWickets2;
    }

    public void setWickets2(int mWickets2) {
        this.mWickets2 = mWickets2;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String mNote) {
        this.mNote = mNote;
    }

    public GameState getGameSate() {
        return mGameSate;
    }

    public void setGameSate(GameState mGameSate) {
        this.mGameSate = mGameSate;
    }

    public void setGameSate(int mGameSate) {
        this.mGameSate = GameState.values()[mGameSate];
    }

    public long getRowId() {
        return mId;
    }

    public void setRowId(long mId) {
        this.mId = mId;
    }

    public int getMaxOvers() {
        return mMaxOvers;
    }

    public void setMaxOvers(int maxOvers) {
        this.mMaxOvers = maxOvers;
    }

    public GameDetails(Parcel pl) {
        mGameName = pl.readString();
        mSide1 = pl.readString();
        mSide2 = pl.readString();
        mScore1 = pl.readInt();
        mScore2 = pl.readInt();
        mBalls1 = pl.readInt();
        mBalls2 = pl.readInt();
        mWickets1 = pl.readInt();
        mWickets2 = pl.readInt();
        mStartTime = pl.readLong();
        mNote = pl.readString();
        mGameSate = GameState.values()[pl.readInt()];
        mId = pl.readLong();
        mMaxOvers = pl.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mGameName);
        dest.writeString(mSide1);
        dest.writeString(mSide2);
        dest.writeInt(mScore1);
        dest.writeInt(mScore2);
        dest.writeInt(mBalls1);
        dest.writeInt(mBalls2);
        dest.writeInt(mWickets1);
        dest.writeInt(mWickets2);
        dest.writeLong(mStartTime);
        dest.writeString(mNote);
        dest.writeInt(mGameSate.ordinal());
        dest.writeLong(mId);
        dest.writeInt(mMaxOvers);
    }

    public static final Creator<GameDetails> CREATOR = new Creator<GameDetails>() {
        @Override
        public GameDetails createFromParcel(Parcel source) {
            return new GameDetails(source);
        }

        @Override
        public GameDetails[] newArray(int size) {
            return new GameDetails[size];
        }
    };

    public void updateBattingTeamScore(int score) {
        if (mGameSate == GameState.SIDE1_BATTING) {
            mScore1 = score;
        }else if (mGameSate == GameState.SIDE2_BATTING) {
            mScore2 = score;
        } else {
            throw new IllegalStateException("No team is batting");
        }
    }

    public void updateBattingTeamBallsPlayed(int ballsPlayed) {
        if (mGameSate == GameState.SIDE1_BATTING) {
            mBalls1 = ballsPlayed;
        }else if (mGameSate == GameState.SIDE2_BATTING) {
            mBalls2 = ballsPlayed;
        } else {
            throw new IllegalStateException("No team is batting");
        }
    }

    public void updateBattingTeamWickets(int wickets) {
        if (mGameSate == GameState.SIDE1_BATTING) {
            mWickets1 = wickets;
        }else if (mGameSate == GameState.SIDE2_BATTING) {
            mWickets2 = wickets;
        } else {
            throw new IllegalStateException("No team is batting");
        }
    }

    public void addRuns(int runsToAdd) {
        if (mGameSate == GameState.SIDE1_BATTING) {
            mScore1 += runsToAdd;
        }else if (mGameSate == GameState.SIDE2_BATTING) {
            mScore2 += runsToAdd;
        } else {
            throw new IllegalStateException("No team is batting");
        }
    }

    public void addBall() {
        if (mGameSate == GameState.SIDE1_BATTING) {
            mBalls1++;
        }else if (mGameSate == GameState.SIDE2_BATTING) {
            mBalls2++;
        } else {
            throw new IllegalStateException("No team is batting");
        }
    }

    public void addWicket() {
        if (mGameSate == GameState.SIDE1_BATTING) {
            mWickets1++;
        }else if (mGameSate == GameState.SIDE2_BATTING) {
            mWickets2++;
        } else {
            throw new IllegalStateException("No team is batting");
        }
    }
}

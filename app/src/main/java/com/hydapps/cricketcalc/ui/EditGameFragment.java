package com.hydapps.cricketcalc.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.utils.DateUtils;
import com.hydapps.cricketcalc.utils.Utils;

import java.util.Date;
import static com.hydapps.cricketcalc.utils.Utils.DEBUG;

/**
 * Created by hrgn76 on 12/29/2014.
 */
public class EditGameFragment extends Fragment {

    private EditText mEditGameName, mEditSide1, mEditScore1, mEditOvers1, mEditWickets1,
                          mEditSide2, mEditScore2, mEditOvers2, mEditWickets2;
    private Spinner mSpinnerBalls1, mSpinnerBalls2;

    private static final String LOG_TAG = "EditGameFragment";
    private GameDetails mGameDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            mGameDetails = b.getParcelable(Utils.EXTRA_GAME_DETAILS);
        }
        if (mGameDetails == null) {
            if (DEBUG) Log.e(LOG_TAG, "GameDetails argument is null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView()...");

        View v;
        v = inflater.inflate(R.layout.edit_game_layout, null);
        mEditGameName = (EditText) v.findViewById(R.id.edit_game_name);
        mEditSide1 = (EditText) v.findViewById(R.id.edit_side1);
        mEditScore1 = (EditText) v.findViewById(R.id.edit_score1);
        mEditOvers1 = (EditText) v.findViewById(R.id.edit_overs1);
        mEditWickets1 = (EditText) v.findViewById(R.id.edit_wkts1);
        mSpinnerBalls1 = (Spinner) v.findViewById(R.id.spinner_balls1);

        mEditSide2 = (EditText) v.findViewById(R.id.edit_side2);
        mEditScore2 = (EditText) v.findViewById(R.id.edit_score2);
        mEditOvers2 = (EditText) v.findViewById(R.id.edit_overs2);
        mEditWickets2 = (EditText) v.findViewById(R.id.edit_wkts2);
        mSpinnerBalls2 = (Spinner) v.findViewById(R.id.spinner_balls2);

        ArrayAdapter<CharSequence> arrayAdapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.balls_array, android.R.layout.simple_spinner_item);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> arrayAdapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.balls_array, android.R.layout.simple_spinner_item);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerBalls1.setAdapter(arrayAdapter1);
        mSpinnerBalls2.setAdapter(arrayAdapter2);

        GameDetails game = mGameDetails;

        mEditGameName.setText(game.getGameName());
        mEditSide1.setText(game.getSide1());
        mEditSide2.setText(game.getSide2());
        mEditScore1.setText(String.valueOf(game.getScore1()));
        mEditScore2.setText(String.valueOf(game.getScore2()));
        mEditWickets1.setText(String.valueOf(game.getWickets1()));
        mEditWickets2.setText(String.valueOf(game.getWickets2()));
        int balls = game.getBalls1();
        if (balls > 0) {
            mEditOvers1.setText(String.valueOf((int) balls/6));
            mSpinnerBalls1.setSelection((int) balls % 6);
        } else {
            mEditOvers1.setText(String.valueOf(0));
            mSpinnerBalls1.setSelection(0);
        }
        balls = game.getBalls2();
        if (balls > 0) {
            mEditOvers2.setText(String.valueOf((int) balls/6));
            mSpinnerBalls2.setSelection((int) balls % 6);
        } else {
            mEditOvers2.setText(String.valueOf(0));
            mSpinnerBalls2.setSelection(0);
        }
        return v;
    }

    public GameDetails getEditedGameDetails() {
        String gameName = null;
        if (TextUtils.isEmpty(gameName = mEditGameName.getText().toString())) {
            Date d = new Date();
            gameName = getString(R.string.str_game_prefix) + "_"
                    + DateUtils.getDateTimeString(d.getTime());
        }

        String side1 = mEditSide1.getText().toString();
        if (TextUtils.isEmpty(side1)) {
            side1 = getString(R.string.str_side1);
        }

        String side2 = mEditSide2.getText().toString();
        if (TextUtils.isEmpty(side2)) {
            side2 = getString(R.string.str_side2);
        }
        String temp = null;
        int score1 = 0;
        if (!TextUtils.isEmpty(temp = mEditScore1.getText().toString())) {
            score1 = Integer.valueOf(temp);
        }
        int score2 = 0, balls1 = 0, balls2 = 0, wickets1 = 0, wickets2 = 0;
        if (!TextUtils.isEmpty(temp = mEditScore2.getText().toString())) {
            score2 = Integer.valueOf(temp);
        }
        if (!TextUtils.isEmpty(temp = mEditWickets1.getText().toString())) {
            wickets1 = Integer.valueOf(temp);
        }
        if (!TextUtils.isEmpty(temp = mEditWickets2.getText().toString())) {
            wickets2 = Integer.valueOf(temp);
        }
        if (!TextUtils.isEmpty(temp = mEditOvers1.getText().toString())) {
            Log.v(LOG_TAG, "overs is not empty: parsging to integer " + temp);
            balls1 = Integer.parseInt(temp);
        }
        if (!TextUtils.isEmpty(temp = mEditOvers2.getText().toString())) {
            balls2 = Integer.parseInt(temp);
        }
        balls1 *= 6;
        balls2 *= 6;
        Log.v(LOG_TAG, "selected item of spinner: " + mSpinnerBalls1.getSelectedItem());
        balls1 += Integer.parseInt((String) mSpinnerBalls1.getSelectedItem());
        balls2 += Integer.parseInt((String) mSpinnerBalls2.getSelectedItem());


        GameDetails game = mGameDetails;
        game.setGameName(gameName);
        game.setSide1(side1);
        game.setSide2(side2);
        game.setBalls1(balls1);
        game.setBalls2(balls2);
        game.setWickets1(wickets1);
        game.setWickets2(wickets2);
        Log.v(LOG_TAG, "getEditedGameDetails: " + game);
        return game;
    }

    @Override
    public void onDestroyView() {
        mGameDetails = getEditedGameDetails();
        super.onDestroyView();
    }
}

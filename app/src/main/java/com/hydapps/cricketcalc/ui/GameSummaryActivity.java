package com.hydapps.cricketcalc.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sujith on 20/12/15.
 * This class is show Summary of the game either progress/end.
 *
 */
public class GameSummaryActivity extends ActionBarActivity implements View.OnClickListener {

    private TextView mTextGameName;
    private TextView mTextSide1;
    private TextView mTextSide2;
    private TextView mTextScore1;
    private TextView mTextScore2;
    private TextView mTextBalls1;
    private TextView mTextBalls2;
    private TextView mTextGameResult;
    private Button mButtonEdit;
    private Button mButtonShare;

    private View mGameSummaryLayout;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.game_summary_layout);

        GameDetails game = getIntent().getParcelableExtra(Utils.EXTRA_GAME_DETAILS);

        mTextGameName = (TextView) findViewById(R.id.tv_item_header);
        mTextSide1 = (TextView) findViewById(R.id.tv_item_side1);
        mTextSide2 = (TextView) findViewById(R.id.tv_item_side2);
        mTextScore1 = (TextView) findViewById(R.id.tv_item_score_side1);
        mTextScore2 = (TextView) findViewById(R.id.tv_item_score_side2);
        mTextBalls1 = (TextView) findViewById(R.id.tv_item_overs_side1);
        mTextBalls2 = (TextView) findViewById(R.id.tv_item_overs_side2);
        mTextGameResult = (TextView) findViewById(R.id.tv_item_result);

        // this button is to show pop up menu in list activity. Not needed here.
        ((ImageButton) findViewById(R.id.button_edit)).setVisibility(View.GONE);

        mButtonEdit = (Button) findViewById(R.id.button_edit);
        mButtonShare = (Button) findViewById(R.id.button_share);

        mButtonEdit.setOnClickListener(this);
        mButtonShare.setOnClickListener(this);

        mGameSummaryLayout = findViewById(R.id.game_summary);

    }

    private void updateUi(GameDetails game) {
        mTextGameName.setText(game.getGameName());
        mTextSide1.setText(game.getSide1());
        mTextSide2.setText(game.getSide2());
        mTextScore1.setText(game.getScore1());
        mTextScore2.setText(game.getScore2());
        mTextBalls1.setText(Utils.convertToOvers(game.getBalls1()));
        mTextBalls2.setText(Utils.convertToOvers(game.getBalls2()));
        mTextGameResult.setText(Utils.getResultString(game, this));
    }

    private void createGameSummaryBitmap(View v) {
        Bitmap bmp = v.getDrawingCache(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_edit:

                break;
            case R.id.button_share:
                try {
                    shareBitmap(mGameSummaryLayout.getDrawingCache(true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private static final String SHARE_FILE = "game_summary.png";
    private static final String SHARE_AUTHORITY = "com.hydapps.cricketcalc";
    private void shareBitmap(Bitmap bmp) throws IOException {
        File outFile = new File(getCacheDir(), SHARE_FILE);
        FileOutputStream fos = new FileOutputStream(outFile);
        bmp.compress(Bitmap.CompressFormat.PNG, 0, fos);
        fos.flush();

        Uri fileUri = FileProvider.getUriForFile(this, SHARE_AUTHORITY, outFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setData(fileUri);
        shareIntent.setType("image/png");
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }
}

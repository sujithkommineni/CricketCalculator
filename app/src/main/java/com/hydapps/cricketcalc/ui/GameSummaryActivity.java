package com.hydapps.cricketcalc.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.net.rtp.AudioGroup;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.content.FileProvider;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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
import static com.hydapps.cricketcalc.utils.Utils.DEBUG;
import static com.hydapps.cricketcalc.utils.Utils.deleteAsync;

/**
 * Created by sujith on 20/12/15.
 * This class is show Summary of the game either progress/end.
 *
 */
public class GameSummaryActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextGameName;
    private TextView mTextSide1;
    private TextView mTextSide2;
    private TextView mTextScore1;
    private TextView mTextScore2;
    private TextView mTextBalls1;
    private TextView mTextBalls2;
    private TextView mTextGameResult;
    private Button mButtonShare, mButtonEdit, mButtonContinue;

    private View mGameSummaryLayout;
    private static String LOG_TAG = "GameSummaryActivity";

    private GameDetails mGameDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.game_summary_layout);

        GameDetails game = getIntent().getParcelableExtra(Utils.EXTRA_GAME_DETAILS);
        if (DEBUG) Log.v(LOG_TAG, "onCreate: game" + game);

        setTitle("Summary");

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

        mButtonShare = (Button) findViewById(R.id.button_share);
        mButtonEdit = (Button) findViewById(R.id.button_edit2);
        mButtonContinue = (Button) findViewById(R.id.button_continue);

        mButtonShare.setOnClickListener(this);
        mButtonEdit.setOnClickListener(this);
        mButtonContinue.setOnClickListener(this);

        mGameSummaryLayout = findViewById(R.id.game_summary);
        updateUi(game);
        mGameDetails = game;
    }

    private void updateUi(GameDetails game) {
        mTextGameName.setText(game.getGameName());
        mTextSide1.setText(game.getSide1());
        mTextSide2.setText(game.getSide2());
        mTextScore1.setText(String.valueOf(game.getScore1()));
        mTextScore2.setText(String.valueOf(game.getScore2()));
        mTextBalls1.setText(Utils.convertToOvers(game.getBalls1()));
        mTextBalls2.setText(Utils.convertToOvers(game.getBalls2()));
        mTextGameResult.setText(Utils.getResultString(game, this));
        if (game.getGameSate() == GameDetails.GameState.SIDE1_BATTING ||
                game.getGameSate() == GameDetails.GameState.SIDE2_BATTING) {
            mButtonContinue.setVisibility(View.VISIBLE);
        } else {
            mButtonContinue.setVisibility(View.GONE);
        }
    }

    private Bitmap createGameSummaryBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        v.draw(c);
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_edit2:
                Intent editIntent = new Intent(this, EditGameActivity.class);
                editIntent.putExtra(Utils.EXTRA_GAME_DETAILS, mGameDetails);
                startActivityForResult(editIntent, 1);
                break;
            case R.id.button_share:
                try {
                    shareBitmap(createGameSummaryBitmap(mGameSummaryLayout));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_continue:
                Intent continueIntent = new Intent(this, ScoreBoardActivity.class);
                continueIntent.putExtra(Utils.EXTRA_GAME_DETAILS, mGameDetails);
                startActivity(continueIntent);
                finish();
                break;
            default:
                Log.e(LOG_TAG, "unhandled button press!!!");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent gamesIntent = new Intent(this, GamesListActivity.class);
            gamesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(gamesIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == RESULT_OK) {
            mGameDetails = data.getParcelableExtra(Utils.EXTRA_GAME_DETAILS);
            updateUi(mGameDetails);
            UiAsyncQueryHandler.getInstance().saveAsync(this, mGameDetails);
        }
    }

    private static final String SHARE_FILE = "game_summary.png";
    private static final String SHARE_AUTHORITY = "com.hydapps.cricketcalc";
    private static final String FOLDER = "images";
    private void shareBitmap(Bitmap temp) throws IOException {
        Bitmap bmp = Bitmap.createBitmap(temp.getWidth(), temp.getHeight(), Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.rgb(141, 249, 72));
        Canvas c = new Canvas(bmp);
        //c.drawColor(Color.GREEN);
        Paint p = new Paint();
        //PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.DST);
        //p.setColorFilter(filter);
        c.drawBitmap(temp, 0, 0, p);
        File folder = new File(getCacheDir(), FOLDER);
        //File folder = new File(Environment.getExternalStorageDirectory(), FOLDER);
        if (!folder.exists()) folder.mkdir();
        File outFile = new File(folder, SHARE_FILE);
        outFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(outFile);
        bmp.compress(Bitmap.CompressFormat.PNG, 0, fos);
        fos.flush();
        fos.close();

        Uri fileUri = FileProvider.getUriForFile(this, SHARE_AUTHORITY, outFile);
        //Uri fileUri = Uri.fromFile(outFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.setType("image/png");
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(shareIntent);
    }
}

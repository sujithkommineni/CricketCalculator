package com.hydapps.cricketcalc.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.utils.Utils;

import java.util.List;

/**
 * Created by hrgn76 on 12/26/2014.
 */
public class GameListAdapter extends RecyclerView.Adapter <GameListAdapter.DefaultViewHolder> implements View.OnClickListener{

    List<GameDetails> mList;
    Context mContext;
    private static final String LOG_TAG = "GameListAdapter";
    private OnEditClickListener mEditClickListener;

    public interface OnEditClickListener {
        public void onEditClick(int position);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        mEditClickListener = listener;
    }

    public GameListAdapter(List<GameDetails> list, Context context) {
        mList = list;
        mContext = context;
    }

    public void updateList(List<GameDetails> list) {
        Log.d(LOG_TAG, "updateList() " + ((list == null) ? "NULL" : list.size()));
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public DefaultViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = View.inflate(mContext, R.layout.game_list_item, null);
        return new DefaultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder viewHoler, int position) {
        GameDetails details = mList.get(position);
        viewHoler.mTextGameName.setText(details.getGameName());
        viewHoler.mTextSide1.setText(details.getSide1());
        viewHoler.mTextSide2.setText(details.getSide2());
        viewHoler.mTextScore1.setText(details.getScore1() + "/" + details.getWickets1());
        viewHoler.mTextScore2.setText(details.getScore2() + "/" + details.getWickets2());
        String overs = details.getBalls1() / 6 + "." + details.getBalls1() % 6;
        viewHoler.mTextBalls1.setText(overs);
        overs = details.getBalls2() / 6 + "." + details.getBalls2() % 6;
        viewHoler.mTextBalls2.setText(overs);
        viewHoler.mTextGameResult.setText(Utils.getResultString(details, mContext));
        viewHoler.mImageButton.setTag(position);
        viewHoler.mImageButton.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        mEditClickListener.onEditClick(position);
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {

        TextView mTextGameName;
        TextView mTextSide1;
        TextView mTextSide2;
        TextView mTextScore1;
        TextView mTextScore2;
        TextView mTextBalls1;
        TextView mTextBalls2;
        TextView mTextGameResult;
        ImageButton mImageButton;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            mTextGameName = (TextView) itemView.findViewById(R.id.tv_item_header);
            mTextSide1 = (TextView) itemView.findViewById(R.id.tv_item_side1);
            mTextSide2 = (TextView) itemView.findViewById(R.id.tv_item_side2);
            mTextScore1 = (TextView) itemView.findViewById(R.id.tv_item_score_side1);
            mTextScore2 = (TextView) itemView.findViewById(R.id.tv_item_score_side2);
            mTextBalls1 = (TextView) itemView.findViewById(R.id.tv_item_overs_side1);
            mTextBalls2 = (TextView) itemView.findViewById(R.id.tv_item_overs_side2);
            mTextGameResult = (TextView) itemView.findViewById(R.id.tv_item_result);
            mImageButton = (ImageButton) itemView.findViewById(R.id.button_edit);
        }
    }
 }

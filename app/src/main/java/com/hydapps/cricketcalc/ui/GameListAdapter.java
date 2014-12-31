package com.hydapps.cricketcalc.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hydapps.cricketcalc.R;

import java.util.List;

/**
 * Created by hrgn76 on 12/26/2014.
 */
public class GameListAdapter extends RecyclerView.Adapter <GameListAdapter.DefaultViewHolder> {

    List<MatchDetails> mList;
    Context mContext;
    private static final String LOG_TAG = "GameListAdapter";

    public GameListAdapter(List<MatchDetails> list, Context context) {
        mList = list;
        mContext = context;
    }

    public void updateList(List<MatchDetails> list) {
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
        MatchDetails details = mList.get(position);
        viewHoler.mTextGameName.setText(details.mGameName);
        viewHoler.mTextSide1.setText(details.mSide1);
        viewHoler.mTextSide2.setText(details.mSide2);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {

        TextView mTextGameName;
        TextView mTextSide1;
        TextView mTextSide2;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            mTextGameName = (TextView) itemView.findViewById(R.id.tv_item_header);
            mTextSide1 = (TextView) itemView.findViewById(R.id.tv_item_side1);
            mTextSide2 = (TextView) itemView.findViewById(R.id.tv_item_side2);
        }
    }
 }

package com.iwebnext.vchatt.adapter;

/**
 * Created by PRIYANKA on 9/15/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.model.Friend;

import java.util.ArrayList;

public class FriendProfileAdapter extends RecyclerView.Adapter<FriendProfileAdapter.ViewHolder> {
    private ArrayList<Friend> friendProfileList;

    public FriendProfileAdapter(ArrayList<Friend> friendProfileList) {
        this.friendProfileList = friendProfileList;
    }

    @Override
    public FriendProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_profile_card_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Friend friend = friendProfileList.get(position);
        holder.friendName.setText(friend.getName());
        holder.friendPhn.setText(friend.getTelephone());
        holder.friendAddress.setText(friend.getAddress());
        holder.friendProfession.setText(friend.getProfession());
    }

    @Override
    public int getItemCount() {
        return friendProfileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView friendPhn,friendName,friendAddress,friendProfession;

        public ViewHolder(View view) {
            super(view);

            friendPhn = (TextView)view.findViewById(R.id.tv_friend_phn);
            friendName = (TextView) view.findViewById(R.id.tv_friend_name);
            friendAddress = (TextView) view.findViewById(R.id.tv_friend_place);
            friendProfession = (TextView) view.findViewById(R.id.tv_friend_profession);

        }
    }



}
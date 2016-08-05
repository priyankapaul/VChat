package com.iwebnext.vchatt.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.activity.CircularNetworkImageView;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.model.FriendRequest;

import java.util.ArrayList;


public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    ArrayList<FriendRequest> friendRequests;

    public FriendRequestAdapter(ArrayList<FriendRequest> friendRequests) {
        this.friendRequests = friendRequests;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_friend_request, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FriendRequest friendRequest = friendRequests.get(position);
        holder.networkImageView.setImageUrl(friendRequest.getImage(), BaseApplication.getInstance().getImageLoader());
        holder.textViewName.setText(friendRequest.getName());
     //   holder.tvfriendRequestSent.setEnabled(friendRequest.getFriendRequestSent());

    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public CircularNetworkImageView networkImageView;
        public TextView textViewName,tvfriendRequestSent;

        public ViewHolder(View itemView) {
            super(itemView);

            networkImageView = (CircularNetworkImageView) itemView.findViewById(R.id.friend_image);
            textViewName = (TextView) itemView.findViewById(R.id.friend_name);
            tvfriendRequestSent = (TextView) itemView.findViewById(R.id.friend_request_sent);

        }
    }


    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public FriendRequest getItem(int position) {
        return friendRequests.get(position);
    }


    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FriendRequestAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FriendRequestAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}

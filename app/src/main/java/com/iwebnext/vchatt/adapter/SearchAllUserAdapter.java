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
import com.iwebnext.vchatt.app.MyApplication;
import com.iwebnext.vchatt.model.User;

import java.util.ArrayList;
import java.util.Calendar;


public class SearchAllUserAdapter extends RecyclerView.Adapter<SearchAllUserAdapter.ViewHolder> {
    public ArrayList<User> userArrayList;
    private static String today;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName,userId;
        public CircularNetworkImageView nivUser;


        public ViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.user_name);
            nivUser = (CircularNetworkImageView) view.findViewById(R.id.iv_user_image);
            userId = (TextView) view.findViewById(R.id.user_id);
        }
    }


    public SearchAllUserAdapter(Context mContext, ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_user_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User User = userArrayList.get(position);
        holder.userName.setText(User.getName());
        holder.userId.setText(User.getId());

        holder.nivUser.setImageUrl(User.getImage(), MyApplication.getInstance().getImageLoader());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public User getItem(int position) {
        return userArrayList.get(position);
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private SearchAllUserAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SearchAllUserAdapter.ClickListener clickListener) {
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

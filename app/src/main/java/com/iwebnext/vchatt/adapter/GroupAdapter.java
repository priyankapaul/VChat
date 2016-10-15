package com.iwebnext.vchatt.adapter;

/**
 * Created by PRIYANKA on 10/14/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.activity.CircularNetworkImageView;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.model.Friend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    public ArrayList<Friend> friendArrayList;

    ArrayList<String> obj = new ArrayList<String>();

    private static String today;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message, timestamp, count;
        public CheckBox checkBoxGroup;
        public CircularNetworkImageView image;
        public ImageView statusImageOnline, statusImageOffline;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            image = (CircularNetworkImageView) view.findViewById(R.id.iv_user_image);
            message = (TextView) view.findViewById(R.id.message);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            count = (TextView) view.findViewById(R.id.count);
            checkBoxGroup = (CheckBox) view.findViewById(R.id.group_checkbox);
        }
    }



    public GroupAdapter(Context mContext, ArrayList<Friend> friendArrayList) {
        this.friendArrayList = friendArrayList;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_row_group, parent, false);

        return new ViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Friend friend = friendArrayList.get(position);
        holder.name.setText(friend.getName());
        holder.image.setImageUrl(friend.getImage(), BaseApplication.getInstance().getImageLoader());


//--------------------------------------------
        holder.checkBoxGroup.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.checkBoxGroup.setChecked(friendArrayList.get(position).isSelected());
        holder.checkBoxGroup.setTag(friendArrayList.get(position));

        holder.checkBoxGroup.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                Friend contact = (Friend) cb.getTag();

                contact.setSelected(cb.isChecked());
                friendArrayList.get(position).setSelected(cb.isChecked());

                Toast.makeText( v.getContext(), "Clicked on Checkbox: " + cb.getText() + " is "+ friendArrayList.get(position).getId(), Toast.LENGTH_LONG).show();
               friendArrayList.add(friend);
            }
        });

        holder.checkBoxGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                friendArrayList.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });

//------------------------------------------

        // holder.status.setImageDrawable(Drawable.createFromPath(friendArrayList.get(position).getStatus()));
        holder.message.setText(friend.getLastMessage());
        if (friend.getUnreadCount() > 0) {
            holder.count.setText(String.valueOf(friend.getUnreadCount()));
            holder.count.setVisibility(View.VISIBLE);
        } else {
            holder.count.setVisibility(View.GONE);

        }

          holder.timestamp.setText(getTimeStamp(friend.getTimestamp()));
//        holder.count.setVisibility(View.INVISIBLE);
//        holder.timestamp.setVisibility(View.INVISIBLE);
          holder.message.setVisibility(View.INVISIBLE);
    }


    @Override
    public int getItemCount()
    {
        return friendArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }




    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FriendListAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FriendListAdapter.ClickListener clickListener) {
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

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        friendArrayList.clear();
        if (charText.length() == 0) {
            friendArrayList.addAll(friendArrayList);

        } else {
            for (Friend friend : friendArrayList) {
                if (charText.length() != 0 && friend.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    friendArrayList.add(friend);

                }
            }
        }
        notifyDataSetChanged();
    }


}

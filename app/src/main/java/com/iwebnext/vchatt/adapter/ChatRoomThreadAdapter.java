package com.iwebnext.vchatt.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.toolbox.NetworkImageView;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.app.MyApplication;
import com.iwebnext.vchatt.model.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatRoomThreadAdapter extends RecyclerView.Adapter<ChatRoomThreadAdapter.ViewHolder> {

    private static String TAG = ChatRoomThreadAdapter.class.getSimpleName();

    private String userId;
    private int SELF = 100;
    private static String today;

    private Context context;
    private ArrayList<Message> messageArrayList;


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;
        NetworkImageView ivImgSent;
        VideoView videoSent;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.message);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            ivImgSent = (NetworkImageView) view.findViewById(R.id.image_sent);
            videoSent = (VideoView) view.findViewById(R.id.video_sent);

        }
    }

    public ChatRoomThreadAdapter(Context context, ArrayList<Message> messageArrayList, String userId) {
        this.context = context;
        this.messageArrayList = messageArrayList;
        this.userId = userId;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_other, parent, false);

        // view type is to identify where to render the chat message
        // left or right
      /*  if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_self, parent, false);
        } else {
            // others message
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_other, parent, false);
        }*/

        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
        //  return new ViewHolder(itemView);
    }

/*
    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.getUser().getId().equals(userId)) {
            return SELF;
        }

    return position;
    }*/


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        String timestamp = getTimeStamp(message.getCreatedAt());
        if (message.getUser().getName() != null)

        holder.message.setText(message.getMessage());
        holder.ivImgSent.setImageUrl(message.getImage(), MyApplication.getInstance().getImageLoader());

//        Uri videoURI=Uri.parse(message.getVideoUrl());
//        holder.videoSent.setVideoURI(videoURI);
//        holder.videoSent.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//                    @Override
//                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                        if (percent == 100) {
//                            //video have completed buffering
//                        }
//
//                    }
//                });
//                holder.videoSent.start();
//            }
//        });
//
//        MediaController vidControl = new MediaController(this.context);
//        vidControl.setAnchorView(holder.videoSent);
//        holder.videoSent.setMediaController(vidControl);
//        holder.videoSent.getBufferPercentage();
//

       /* if(message.getMessageType().contains("image"))
        {
            holder.ivImgSent.setImageUrl(message.getImage(), MyApplication.getInstance().getImageLoader());
            holder.message.setVisibility(View.GONE);

        }
        else{
            holder.ivImgSent.setVisibility(View.GONE);
            holder.message.setText(message.getMessage());

        }*/


        //holder.ivImgSent.setImageUrl(message.getImage(), MyApplication.getInstance().getImageLoader());
        // holder.ivImgSent.setImageUrl("http://inextwebs.com/gcm_chat/image_sent/144.png", MyApplication.getInstance().getImageLoader());
    }


    @Override
    public int getItemCount() {
        return messageArrayList.size();
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
}


package com.iwebnext.vchatt.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.toolbox.NetworkImageView;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.model.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroupChatRoomThreadAdapter extends RecyclerView.Adapter<GroupChatRoomThreadAdapter.ViewHolder> {

    private static String TAG = ChatRoomThreadAdapter.class.getSimpleName();

    private String userId;
    private int SELF = 100;
    private static String today;


    private Context mContext;
    private ArrayList<Message> messageArrayList;


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTextMsg, tvTimeStamp;
        NetworkImageView ivImgSent;
        VideoView videoSent;
        public ProgressBar loadingProgressBar;

        public ViewHolder(View view) {
            super(view);
            tvTextMsg = (TextView) view.findViewById(R.id.tv_text_msg);
            tvTimeStamp = (TextView) view.findViewById(R.id.timestamp);
            ivImgSent = (NetworkImageView) view.findViewById(R.id.niv_image);
            videoSent = (VideoView) view.findViewById(R.id.vv_video);

        }
    }

    public GroupChatRoomThreadAdapter(Context mContext, ArrayList<Message> messageArrayList, String userId) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.userId = userId;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;// = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_other, parent, false);

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_self, parent, false);
        } else {
            // others message
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_other, parent, false);
        }

        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
        //  return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.getUserId().equals(userId)) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);

        int type = message.getType();

        holder.tvTimeStamp.setText(message.getUserName() + ", " + getTimeStamp(message.getCreatedAt()));
        if (type == Message.TEXT) {
            holder.tvTextMsg.setVisibility(View.VISIBLE);
            holder.ivImgSent.setVisibility(View.GONE);
            holder.videoSent.setVisibility(View.GONE);

            holder.tvTextMsg.setText(message.getContent());
        } else if (type == Message.IMAGE) {
            holder.tvTextMsg.setVisibility(View.GONE);
            holder.ivImgSent.setVisibility(View.VISIBLE);
            holder.videoSent.setVisibility(View.GONE);

            holder.ivImgSent.setImageUrl(message.getContent(), BaseApplication.getInstance().getImageLoader());
        } else if (type == Message.VIDEO) {
            holder.tvTextMsg.setVisibility(View.GONE);
            holder.ivImgSent.setVisibility(View.GONE);
            holder.videoSent.setVisibility(View.VISIBLE);

            try {
                // Start the MediaController
                MediaController mediacontroller = new MediaController(mContext);
                mediacontroller.setAnchorView(holder.videoSent);

                // Get the URL from String VideoURL
                Uri video = Uri.parse(message.getContent());
                holder.videoSent.setMediaController(mediacontroller);
                holder.videoSent.setVideoURI(video);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            holder.videoSent.requestFocus();
            holder.videoSent.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
//                    pDialog.dismiss();
                    holder.videoSent.start();
                }
            });
        }
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


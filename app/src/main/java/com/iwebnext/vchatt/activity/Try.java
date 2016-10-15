//package com.iwebnext.vchatt.activity;
//
///**
// * Created by PRIYANKA on 10/15/2016.
// */
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.android.volley.toolbox.NetworkImageView;
//import com.iwebnext.vchatt.R;
//import com.iwebnext.vchatt.app.BaseApplication;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//
//
///**
// * Created by Soms PC on 7/2/2016.
// */
//public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> implements  View.OnLongClickListener, View.OnClickListener {
//
//    private ArrayList<String> _allImageUrl =new ArrayList<String>();
//    private ArrayList<String> _title=new ArrayList<String>();
//    private ArrayList<String> _content=new ArrayList<String>();
//    private ArrayList<String> _idlist=new ArrayList<String>();
//    private ProgressBar progressBar;
//    public static  int items;
//    Context context;
//    public ArrayList<Integer> innerarray = new ArrayList<Integer>();
//    public int pos;
//
//    public static final String DELETEDIARYURL="http://inextwebs.com/arbic/delete_diary.php?id=";
//
//
//
//    public DiaryAdapter(ArrayList<String> allImageUrl, ArrayList<String> title, ArrayList<String> content,ArrayList<String> idlist)
//    {
//        if(_allImageUrl!=null)
//        {
//            _allImageUrl.clear();
//            _title.clear();
//            _content.clear();
//            items=0;
//        }
//        _allImageUrl=allImageUrl;
//        _title=title;
//        _content=content;
//        _idlist=idlist;
//        items=allImageUrl.size();
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
//    {
//
//        final View  view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_list_adapter, parent, false);
//
//        final ViewHolder holder=new ViewHolder(view);
//        return holder;
//
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
//    }
//
//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position)
//    {
//
//        pos=position;
//        holder.setIsRecyclable(false);
//
//        holder.title.setText(_title.get(position));
//        holder.contentdiary.setText(_content.get(position));
//
//        holder.contentdiary.setTag(position);
//        holder.contentdiary.setOnClickListener(this);
//        // holder.imageView.setImageResource(R.drawable.loadingspinner);
//        holder.imageButton.setTag(position);
//        holder.imageButton.setTag(R.id.hiddenidvalue,_idlist.get(position));
//
//        holder.imageButton.setOnLongClickListener(this);
//
//        holder.imageView.setImageUrl(_allImageUrl.get(position), BaseApplication.getInstance().getImageLoader());
//        System.out.println("imageurl:-----"+_allImageUrl.get(position));
//
//    }
//
//    @Override
//    public int getItemCount()
//    {
//        if (_allImageUrl.size()>0) {
//            System.out.println("getcountdiary"+_allImageUrl.size());
//            return _allImageUrl.size();
//        }
//        else {
//            return 0;
//        }
//    }
//
//
//
//    @Override
//    public boolean onLongClick(View v) {
//
//        int index = Integer.parseInt(v.getTag().toString());
//
//        _allImageUrl.remove(index);
//        _title.remove(index);
//        _content.remove(index);
//
//        notifyItemRemoved(index);
//
//        notifyDataSetChanged();
//
//        notifyItemRangeChanged(index, _allImageUrl.size());
//
//        new DeleteDiary().execute(DELETEDIARYURL+v.getTag(R.id.hiddenidvalue));
//
//        return true;
//    }
//
//    @Override
//    public void onClick(View v)
//    {
//        // v.getTag();
//        int position=Integer.parseInt(v.getTag().toString());
//        System.out.println("onclickss"+ v.getTag());
//
//        Intent diary = new Intent(v.getContext(), DiaryDetailsActivity.class);
//        diary.putExtra("title",_title.get(position));
//        diary.putExtra("image",_allImageUrl.get(position));
//        diary.putExtra("content",_content.get(position));
//
//        diary.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        v.getContext().startActivity(diary);
//    }
//
//
//    public class ViewHolder extends RecyclerView.ViewHolder  {
//        public  TextView title;
//        public NetworkImageView imageView;
//        public  TextView contentdiary;
//        public ImageButton imageButton;
//
//
//        public  ViewHolder(View view){
//            super(view);
//
//            title = (TextView) view.findViewById(R.id.title_diary);
//            contentdiary=(TextView)view.findViewById(R.id.content_diary);
//            imageView=(NetworkImageView)view.findViewById(R.id.img_dialry);
//            imageButton=(ImageButton)view.findViewById(R.id.deletediary);
//
//        }
//
//
//    }
//
//    //Delete row from db of diary;
//    private class DeleteDiary extends AsyncTask<String,Void,String> {
//        @Override
//        protected String doInBackground(String... params)
//        {
//            try{
//                URL url = new URL(params[0]);
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                String result=bufferedReader.readLine();
//                return result;
//            }
//            catch (IOException e)
//            {
//                return ""+e.getLocalizedMessage();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            //Toast.makeText(context,""+s,Toast.LENGTH_LONG).show();
//        }
//    }
//
//
//
//}

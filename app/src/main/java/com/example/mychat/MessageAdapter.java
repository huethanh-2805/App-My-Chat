package com.example.mychat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private final Context mContext;
    private final List<Message> mMessage;

    FirebaseUser fUser;

    public String imageUrl;



    public MessageAdapter(Context mContext, List<Message> mMessage, String imageUrl){
        this.mMessage = mMessage;
        this.mContext=mContext;
        this.imageUrl=imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        } else{
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = mMessage.get(position);
        if (message.getType()!=null){
            if (message.getType().equals("image")){
                holder.show_file.setVisibility(View.GONE);
                holder.show_image.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(message.getMessage()).into(holder.show_image);
            }
            else if (message.getType().equals("file") || message.getType().equals("pdf") || message.getType().equals("txt")){
                holder.show_image.setVisibility(View.GONE);
                holder.show_file.setVisibility(View.VISIBLE);
                if (message.getTitle()!=null){
                    holder.title_file.setText(message.getTitle());
                }
                holder.show_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "View",
                                "Cancel"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Choose One");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // we will be downloading the pdf
                                if (which == 0) {
                                    if (message.getType().equals("txt")){
                                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                        intent1.setDataAndType(Uri.parse(message.getMessage()), "text/plain");
                                        mContext.startActivity(intent1);
                                    }
                                    else {
                                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                        intent1.setDataAndType(Uri.parse(message.getMessage()), "application/pdf");
                                        mContext.startActivity(intent1);
                                    }
                                }

                                if (which == 1) {
                                        dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }
            else{
                holder.show_file.setVisibility(View.GONE);
                holder.show_image.setVisibility(View.GONE);
                holder.show_message.setText(message.getMessage());
            }
        }
        else{

            holder.show_image.setVisibility(View.GONE);
            holder.show_message.setText(message.getMessage());
        }




        if (imageUrl.equals("default")){
            holder.profile_image.setImageResource(R.drawable.ic_avt);
        }else{
            Glide.with(mContext).load(imageUrl).into(holder.profile_image);
        }
    }


    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{


        public TextView show_message;

        public ImageView show_image;
        public ImageView profile_image;

        public TextView title_file;

        public LinearLayout show_file;

        public WebView webView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_image=itemView.findViewById(R.id.show_image);
            show_message=itemView.findViewById(R.id.show_message);
            profile_image=itemView.findViewById(R.id.profile_image);
            show_file=itemView.findViewById(R.id.show_file);
            title_file=itemView.findViewById(R.id.title_file);
        }
    }

    public int getItemViewType(int position){
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if (mMessage.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}

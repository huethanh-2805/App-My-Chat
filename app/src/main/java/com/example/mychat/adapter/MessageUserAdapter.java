package com.example.mychat.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychat.object.Message;
import com.example.mychat.iface.OnItemClickListener;
import com.example.mychat.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;



public class MessageUserAdapter extends RecyclerView.Adapter<MessageUserAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final Context mContext;
    private final List<Message> mMessage;
    private OnItemClickListener onItemClickListener;
    FirebaseUser fUser;
    private WebView webView;

    private long downloadID;

    public String imageUrl;
    private Uri localFileUri;




    public MessageUserAdapter(Context mContext, List<Message> mMessage, String imageUrl) {
        this.mMessage = mMessage;
        this.mContext = mContext;
        this.imageUrl = imageUrl;
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener=listener;
    }
    @NonNull
    @Override
    public MessageUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageUserAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageUserAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mMessage.get(position);
        // Chuyển đổi timestamp sang định dạng ngày giờ
        Timestamp firebaseTimestamp = message.getTimestamp(); // Lấy timestamp từ message
        long seconds = firebaseTimestamp.getSeconds(); // Lấy giá trị giây
        long nanoseconds = firebaseTimestamp.getNanoseconds(); // Lấy giá trị nanogisecond

        long timestampInMillis = seconds * 1000 + nanoseconds / 1000000; // Chuyển đổi thành mili giây

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        String dateString = sdf.format(new Date(timestampInMillis));

        holder.show_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    CharSequence options[] = new CharSequence[]{
                            "Gỡ tin nhắn",
                            "Chuyển tiếp",
                            "Cancel"
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Choose One");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                                                   // we will be downloading the pdf
                            if (which == 0) {
                                onItemClickListener.onItemClick(message);
                            }
                            if (which == 1) {
                                onItemClickListener.onItemClickForward(message,message.getType(),message.getTitle());
//                                dialog.dismiss();
                            }
                            if (which == 2) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
            }
        });

        holder.show_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    CharSequence options[] = new CharSequence[]{
                            "Gỡ tin nhắn",
                            "Chuyển tiếp",
                            "Cancel"
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Choose One");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // we will be downloading the pdf
                            if (which == 0) {
                                onItemClickListener.onItemClick(message);
                            }
                            if (which == 1) {
                                onItemClickListener.onItemClickForward(message,message.getType(),message.getTitle());
                            }
                            if (which == 2) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
            }
        });


        if(message.getMessage().equals("Nội dung đã bị gỡ"))
        {
            holder.show_message.setText("Nội dung đã bị gỡ");
            holder.show_message.setBackgroundResource(R.drawable.background_remove);
        }

// Hiển thị ngày giờ vào TextView

        if (message.getType() != null) {
            if (message.getType().equals("image")) {
                holder.show_file.setVisibility(View.GONE);
                holder.videolayout.setVisibility(View.GONE);
                holder.show_image.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams txtShowTime = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                txtShowTime.addRule(RelativeLayout.BELOW, holder.show_image.getId());
                txtShowTime.addRule(RelativeLayout.ALIGN_RIGHT, holder.show_image.getId());

                holder.show_time.setLayoutParams(txtShowTime);
                holder.show_time.setLayoutParams(txtShowTime);
                holder.show_time.setText(dateString);

                Glide.with(mContext).load(message.getMessage()).into(holder.show_image);

            } else if (message.getType().equals("video")){
                holder.show_file.setVisibility(View.GONE);
                holder.show_image.setVisibility(View.GONE);
                holder.videolayout.setVisibility(View.VISIBLE);

                Uri videoUri = Uri.parse(message.getMessage());
                holder.show_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{
                                "Play",
                                "Gỡ tin nhắn",
                                "Chuyển tiếp",
                                "Cancel"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Choose One");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // we will be downloading the pdf
                                if (which == 0) {
                                    holder.show_video.setVideoURI(videoUri);
                                    holder.show_video.start();
                                }
                                if (which == 1) {
                                    onItemClickListener.onItemClick(message);
                                }
                                if(which == 2)
                                {
                                    onItemClickListener.onItemClickForward(message,message.getType(),message.getTitle());
                                }
                                if(which == 3)
                                {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
                    }
                });
                // Set the video URI and start playback

                // Set an error listener to handle any playback errors
                holder.show_video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                        Toast.makeText(mContext,"Error display video",Toast.LENGTH_SHORT);
                        return false;
                    }
                });
            }
            else if (message.getType().equals("file") || message.getType().equals("pdf") || message.getType().equals("txt") || message.getType().equals("docx")) {
                holder.show_image.setVisibility(View.GONE);
                holder.videolayout.setVisibility(View.GONE);
                holder.show_file.setVisibility(View.VISIBLE);
                if (message.getTitle() != null) {
                    holder.title_file.setText(message.getTitle());
                }
                holder.show_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "View",
                                "Gỡ tin nhắn",
                                "Chuyển tiếp",
                                "Cancel"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Choose One");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // we will be downloading the pdf
                                if (which == 0) {
                                    if (message.getType().equals("txt")) {
                                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                        intent1.setDataAndType(Uri.parse(message.getMessage()), "text/plain");
                                        mContext.startActivity(intent1);
                                    } else if (message.getType().equals("pdf")) {
                                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                        intent1.setDataAndType(Uri.parse(message.getMessage()), "application/pdf");
                                        mContext.startActivity(intent1);
                                    }
                                    else{
                                        Intent intent1 = new Intent(Intent.ACTION_VIEW,Uri.parse(message.getMessage()));
                                        mContext.startActivity(intent1);
                                    }
                                }

                                if (which == 1) {
                                    onItemClickListener.onItemClick(message);
                                }
                                if(which == 2)
                                {
                                    onItemClickListener.onItemClickForward(message,message.getType(),message.getTitle());
                                }
                                if(which == 3)
                                {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            } else {
                holder.videolayout.setVisibility(View.GONE);
                holder.show_file.setVisibility(View.GONE);
                holder.show_image.setVisibility(View.GONE);
                holder.show_message.setText(message.getMessage());
                holder.show_time.setText(dateString);

            }
        } else {
            holder.videolayout.setVisibility(View.GONE);
            holder.show_file.setVisibility(View.GONE);
            holder.show_image.setVisibility(View.GONE);
            holder.show_message.setText(message.getMessage());
            holder.show_time.setText(dateString);

        }

        Glide.with(mContext).load(imageUrl).into(holder.profile_image);
    }



    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MediaController mediaController;

        public TextView show_message;

        public ImageView show_image;

        public VideoView show_video;
        public TextView show_time;
        public ImageView profile_image;

        public TextView title_file;

        public LinearLayout show_file;

        public RelativeLayout videolayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_image = itemView.findViewById(R.id.show_image);

            videolayout=itemView.findViewById(R.id.videoLayout);

            show_video = itemView.findViewById(R.id.videoView);
            mediaController = new MediaController(show_video.getContext());

            show_video.setMediaController(mediaController);
            mediaController.setAnchorView(show_video);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            show_time = itemView.findViewById(R.id.show_time);
            show_file = itemView.findViewById(R.id.show_file);
            title_file = itemView.findViewById(R.id.title_file);
        }
    }

    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mMessage.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }


}

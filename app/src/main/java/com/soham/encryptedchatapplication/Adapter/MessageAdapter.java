package com.soham.encryptedchatapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.soham.encryptedchatapplication.MessageActivity;
import com.soham.encryptedchatapplication.Model.Chat;
import com.soham.encryptedchatapplication.Model.User;
import com.soham.encryptedchatapplication.R;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    public String myurl;
    //private String mPass;
    String AES = "AES";
    String encryptedMsg;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl, String myurl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageurl = imageurl;
        this.myurl = myurl;
        //this.mPass = mPass;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        //holder.messageReceiverPicture.setVisibility(View.GONE);
       // holder.messageSenderPicture.setVisibility(View.GONE);
       // holder.show_message.setVisibility(View.GONE);

        Chat chat = mChat.get(position);
        String fromMessageType = chat.getType();

        if (fromMessageType.equals("text")) {

            holder.show_message.setText(chat.getMessage());
        }
        else if(fromMessageType.equals("image")){

            if(mChat.get(position).getSender().equals(fuser.getUid())) {
                holder.show_message.setVisibility(View.GONE);
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                //Picasso.get().load(myurl).into(holder.messageSenderPicture);
                try {
                    Glide.with(mContext).load(chat.getImage())
                            .error(R.drawable.background_right)
                            .into(holder.messageSenderPicture);
                } catch(Exception e) {
                    Log.e("Error", myurl);
                }
            }
            else {
                holder.show_message.setVisibility(View.GONE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(chat.getImage())
                        .error(R.drawable.background_right)
                        .into(holder.messageReceiverPicture);
             //   Log.e("Error",myurl);
            }
        }
        //Log.e("Error",myurl);
        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }
    }

    @Override
    public int getItemCount() {

        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image;
        public ImageView messageSenderPicture, messageReceiverPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image);
        }

    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

}

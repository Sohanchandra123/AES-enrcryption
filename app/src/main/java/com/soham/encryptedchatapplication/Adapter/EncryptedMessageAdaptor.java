package com.soham.encryptedchatapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.soham.encryptedchatapplication.Model.Chat;
import com.soham.encryptedchatapplication.R;

import java.util.List;

public class EncryptedMessageAdaptor extends RecyclerView.Adapter<EncryptedMessageAdaptor.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    private String mPass;
    public String encryptedImage;
    String AES="AES";
    String encryptedMsg;

    FirebaseUser fuser;

    public EncryptedMessageAdaptor(Context mContext, List<Chat> mChat, String imageurl, String mPass, String encryptedImage) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageurl = imageurl;
        this.mPass = mPass;
        this.encryptedImage = encryptedImage;
    }

    @NonNull
    @Override
    public EncryptedMessageAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new EncryptedMessageAdaptor.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new EncryptedMessageAdaptor.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull EncryptedMessageAdaptor.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        //holder.show_message.setText(chat.getEncryptedMsg());
        String fromMessageType = chat.getType();

        if (fromMessageType.equals("text")) {

            holder.show_message.setText(chat.getEncryptedMsg());
        }
        else if(fromMessageType.equals("image")){

            if(mChat.get(position).getSender().equals(fuser.getUid())) {
                holder.show_message.setVisibility(View.GONE);
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                //Picasso.get().load(myurl).into(holder.messageSenderPicture);
                try {
                    Glide.with(mContext).load(chat.getEncryptedImage())
                            .error(R.drawable.background_right)
                            .into(holder.messageSenderPicture);
                } catch(Exception e) {
                    Log.e("Error", encryptedImage);
                }
            }
            else {
                holder.show_message.setVisibility(View.GONE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(chat.getEncryptedImage())
                        .error(R.drawable.background_right)
                        .into(holder.messageReceiverPicture);
                //   Log.e("Error",encryptedImage);
            }
        }
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

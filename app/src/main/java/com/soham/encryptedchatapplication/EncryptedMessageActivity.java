package com.soham.encryptedchatapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.soham.encryptedchatapplication.Adapter.EncryptedMessageAdaptor;
import com.soham.encryptedchatapplication.Adapter.MessageAdapter;
import com.soham.encryptedchatapplication.Model.Chat;
import com.soham.encryptedchatapplication.Model.User;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

//import de.hdodenhof.circleimageview.CircleImageView;

public class EncryptedMessageActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

   // CircleImageView profile_image;
    TextView username;
    public static String mPass;
    String AES="AES";
    String encryptedMsg,encryptedImage;
    String checker = "", myUrl;
    Uri imageUri;
    StorageTask uploadTask;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_send;
    ImageButton btn_send_image;
    EditText text_send;

    EncryptedMessageAdaptor encryptedMessageAdaptor;
    List<Chat> mChat;

    private String saveCurrentTime, saveCurrentDate;
    RecyclerView recyclerView;

    Intent intent;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        btn_send_image = findViewById(R.id.btn_send_image);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        intent = getIntent();
        String userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();



        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                mPass = snapshot.child("secretkey").getValue().toString();
                if (user.getImageURL().equals("default")) {
                    //profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    //Glide.with(EncryptedMessageActivity.this).load(user.getImageURL()).into(profile_image);
                }

                readMessages(fuser.getUid(), userid, user.getImageURL(), mPass, encryptedImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                try {
                    encryptedMsg = encrypt(msg, mPass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!msg.equals((""))) {
                    sendMessage(fuser.getUid(), userid, encryptedMsg, msg, mPass, userid);
                } else {
                    Toast.makeText(EncryptedMessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        btn_send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ImageFiles");

            DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("Chatlist")
                    .child(fuser.getUid())
                    .child(userid);

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    if (!datasnapshot.exists()) {
                        chatRef.child("id").setValue(userid);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            StorageReference filePath = storageReference.child(userid + "." + "jpg");

            uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        assert downloadUrl != null;
                        myUrl = downloadUrl.toString();
                        try {
                            encryptedImage = encrypt(myUrl, mPass);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!myUrl.equals((""))) {
                            sendMessageImage(fuser.getUid(), userid, myUrl, encryptedImage, userid);
                        } else {
                            Toast.makeText(EncryptedMessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                        }
                        //sendMessageImage(fuser.getUid(), userid, myUrl, userid);

                    }

                }
            });
        }
    }

    private void sendMessageImage(String sender, String receiver, String image, String encryptedImage, String userid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("encryptedImage", encryptedImage);
        hashMap.put("Image", image);
        hashMap.put("type", "image");
        hashMap.put("date", saveCurrentDate);
        hashMap.put("time", saveCurrentTime);
        //hashMap.put("privateKey",mPass);
        reference.child("Chats").push().setValue(hashMap);

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (!datasnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String encryptedMsg, String message, String mPass, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("encryptedMsg", encryptedMsg);
        hashMap.put("type", "text");
        hashMap.put("date", saveCurrentDate);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("privateKey",mPass);

        reference.child("Chats").push().setValue(hashMap);

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if(!datasnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //&& (chat.getReceiver().equals(mPass)&&chat.getSender().equals(mPass))
    private void readMessages(String myid, String userid, String imageurl, String mPass, final String encryptedImage) {
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);
                    if ((chat.getReceiver().equals(myid) && chat.getSender().equals(userid)
                            || chat.getReceiver().equals(userid) && chat.getSender().equals(myid))
                    ) {
                        mChat.add(chat);
                    }
                    encryptedMessageAdaptor = new EncryptedMessageAdaptor(EncryptedMessageActivity.this, mChat, imageurl, mPass, encryptedImage);
                    recyclerView.setAdapter(encryptedMessageAdaptor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

  /*  @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.privatekey:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Enter Private Key");
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_dialog, null);
                builder.setView(dialogView);

                final EditText edt = (EditText) dialogView.findViewById(R.id.secretKey);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPass = edt.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
                return true;
        }
        return false;
    }*/

    private String encrypt(String Data, String inputPassword) throws Exception {
        SecretKeySpec key = generateKey(inputPassword);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = cipher.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    private String decrypt(String outputString, String inputPassword) throws Exception {
        SecretKeySpec key = generateKey(inputPassword);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedVal = Base64.decode(outputString, Base64.DEFAULT);
        byte[] decVal = cipher.doFinal(decodedVal);
        String decryptedValue = new String(decVal);
        return decryptedValue;
    }

    private SecretKeySpec generateKey(String inputPassword) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = inputPassword.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

}
package com.soham.encryptedchatapplication.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String encryptedMsg;
    private String image;
    private String encryptedImage;
    private String mPass;
    private String type;
    private String time;
    private String date;

    public Chat(String sender, String receiver, String message, String encryptedMsg, String image,
                String encryptedImage, String mPass, String type, String time, String date) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.encryptedMsg = encryptedMsg;
        this.image = image;
        this.encryptedImage = encryptedImage;
        this.mPass = mPass;
        this.type = type;
        this.time = time;
        this.date = date;
    }

    public Chat() {
    }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getSender() { return sender; }

    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getEncryptedMsg() { return encryptedMsg;}

    public void setEncryptedMsg(String encryptedMsg) { this.encryptedMsg=encryptedMsg;}

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public String getEncryptedImage() { return encryptedImage;}

    public void setEncryptedImage(String encryptedImage) { this.encryptedImage=encryptedImage;}

    public String getmPass() { return mPass;}

    public void setmPass(String mPass) { this.mPass=mPass;}
}

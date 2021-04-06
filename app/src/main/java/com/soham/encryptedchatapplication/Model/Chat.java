package com.soham.encryptedchatapplication.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String encryptedMsg;
    private String mPass;

    public Chat(String sender, String receiver, String message, String encryptedMsg, String mPass) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.encryptedMsg = encryptedMsg;
        this.mPass = mPass;
    }

    public Chat() {
    }

    public String getSender() { return sender; }

    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) { this.message = message; }

    public String getEncryptedMsg() { return encryptedMsg;}

    public void setEncryptedMsg(String encryptedMsg) { this.encryptedMsg=encryptedMsg;}

    public String getmPass() { return mPass;}

    public void setmPass(String mPass) { this.mPass=mPass;}
}

package com.soham.encryptedchatapplication.Model;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String secretkey;

    public User(String id, String username, String imageURL, String secretkey) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.secretkey = secretkey;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) { this.username = username; }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) { this.secretkey = secretkey; }
}

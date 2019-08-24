package com.example.onlinerandommusic.model;

public class Music {
    private int song_id;

    private String title;

    private String images;

    private String author;

    private String album;

    private String description;

    private String mp3_url;

    private String pub_date;

    private int comment_id;

    private int comment_user_id;

    private String comment_nickname;

    private String comment_avatar_url;

    private int comment_liked_count;

    private String comment_content;

    private String comment_pub_date;

    public void setSong_id(int song_id){
        this.song_id = song_id;
    }
    public int getSong_id(){
        return this.song_id;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setImages(String images){
        this.images = images;
    }
    public String getImages(){
        return this.images;
    }
    public void setAuthor(String author){
        this.author = author;
    }
    public String getAuthor(){
        return this.author;
    }
    public void setAlbum(String album){
        this.album = album;
    }
    public String getAlbum(){
        return this.album;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return this.description;
    }
    public void setMp3_url(String mp3_url){
        this.mp3_url = mp3_url;
    }
    public String getMp3_url(){
        return this.mp3_url;
    }
    public void setPub_date(String pub_date){
        this.pub_date = pub_date;
    }
    public String getPub_date(){
        return this.pub_date;
    }
    public void setComment_id(int comment_id){
        this.comment_id = comment_id;
    }
    public int getComment_id(){
        return this.comment_id;
    }
    public void setComment_user_id(int comment_user_id){
        this.comment_user_id = comment_user_id;
    }
    public int getComment_user_id(){
        return this.comment_user_id;
    }
    public void setComment_nickname(String comment_nickname){
        this.comment_nickname = comment_nickname;
    }
    public String getComment_nickname(){
        return this.comment_nickname;
    }
    public void setComment_avatar_url(String comment_avatar_url){
        this.comment_avatar_url = comment_avatar_url;
    }
    public String getComment_avatar_url(){
        return this.comment_avatar_url;
    }
    public void setComment_liked_count(int comment_liked_count){
        this.comment_liked_count = comment_liked_count;
    }
    public int getComment_liked_count(){
        return this.comment_liked_count;
    }
    public void setComment_content(String comment_content){
        this.comment_content = comment_content;
    }
    public String getComment_content(){
        return this.comment_content;
    }
    public void setComment_pub_date(String comment_pub_date){
        this.comment_pub_date = comment_pub_date;
    }
    public String getComment_pub_date(){
        return this.comment_pub_date;
    }
}

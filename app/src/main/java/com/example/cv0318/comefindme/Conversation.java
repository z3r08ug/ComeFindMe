package com.example.cv0318.comefindme;

public class Conversation
{
    private String username, lastMessage, timestamp, profile_pic;
    
    public Conversation(String username, String lastMessage, String timestamp, String profile_pic)
    {
        this.username = username;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.profile_pic = profile_pic;
    }
    
    public Conversation()
    {
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getLastMessage()
    {
        return lastMessage;
    }
    
    public void setLastMessage(String lastMessage)
    {
        this.lastMessage = lastMessage;
    }
    
    public String getTimestamp()
    {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }
    
    public String getProfile_pic()
    {
        return profile_pic;
    }
    
    public void setProfile_pic(String profile_pic)
    {
        this.profile_pic = profile_pic;
    }
    
    @Override
    public String toString()
    {
        return "Conversation{" +
                "username='" + username + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", profile_pic='" + profile_pic + '\'' +
                '}';
    }
}

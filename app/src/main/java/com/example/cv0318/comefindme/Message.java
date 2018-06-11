package com.example.cv0318.comefindme;

public class Message
{
    private String senderId, date, time, content, senderPic, receiverPic;
    
    public Message(String senderId, String date, String time, String content)
    {
        this.senderId = senderId;
        this.date = date;
        this.time = time;
        this.content = content;
    }
    
    public Message()
    {
    
    }
    
    public String getSenderId()
    {
        return senderId;
    }
    
    public void setSenderId(String senderId)
    {
        this.senderId = senderId;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
    public String getTime()
    {
        return time;
    }
    
    public void setTime(String time)
    {
        this.time = time;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
}

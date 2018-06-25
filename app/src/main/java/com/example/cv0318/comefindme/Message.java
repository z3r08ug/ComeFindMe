package com.example.cv0318.comefindme;

public class Message
{
    private String senderId, date, time, content, senderPic, receiverPic, userId;

    public Message(String senderId, String date, String time, String content, String senderPic, String receiverPic, String userId)
    {
        this.senderId = senderId;
        this.date = date;
        this.time = time;
        this.content = content;
        this.senderPic = senderPic;
        this.receiverPic = receiverPic;
        this.userId = userId;
    }

    public Message(String senderId, String date, String time, String content, String senderPic, String receiverPic)
    {
        this.senderId = senderId;
        this.date = date;
        this.time = time;
        this.content = content;
        this.senderPic = senderPic;
        this.receiverPic = receiverPic;
    }

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

    public String getSenderPic()
    {
        return senderPic;
    }

    public void setSenderPic(String senderPic)
    {
        this.senderPic = senderPic;
    }

    public String getReceiverPic()
    {
        return receiverPic;
    }

    public void setReceiverPic(String receiverPic)
    {
        this.receiverPic = receiverPic;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    @Override
    public String toString()
    {
        return "Message{" +
                "senderId='" + senderId + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                ", senderPic='" + senderPic + '\'' +
                ", receiverPic='" + receiverPic + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}

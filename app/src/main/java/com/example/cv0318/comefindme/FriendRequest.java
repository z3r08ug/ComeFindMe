package com.example.cv0318.comefindme;

public class FriendRequest
{
    private String uid;
    private String status;
    
    public FriendRequest(String uid, String status)
    {
        this.uid = uid;
        this.status = status;
    }
    
    public FriendRequest()
    {
    }
    
    public String getUid()
    {
        return uid;
    }
    
    public void setUid(String uid)
    {
        this.uid = uid;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
}

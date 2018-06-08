package com.example.cv0318.comefindme;

public class Posts
{
    private String uid, time, date, postImage, description, profileImage, fullName;

    public Posts(String uid, String time, String date, String postImage, String description, String profileImage, String fullName)
    {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postImage = postImage;
        this.description = description;
        this.profileImage = profileImage;
        this.fullName = fullName;
    }

    public Posts()
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

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getPostImage()
    {
        return postImage;
    }

    public void setPostImage(String postImage)
    {
        this.postImage = postImage;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getProfileImage()
    {
        return profileImage;
    }

    public void setProfileImage(String profileImage)
    {
        this.profileImage = profileImage;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    @Override
    public String toString()
    {
        return "Posts{" +
                "uid='" + uid + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", postImage='" + postImage + '\'' +
                ", description='" + description + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}

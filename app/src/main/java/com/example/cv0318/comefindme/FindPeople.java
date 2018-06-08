package com.example.cv0318.comefindme;

public class FindPeople
{
    public String profile_pic, fullName, status;

    public FindPeople(String profile_pic, String fullName, String status)
    {
        this.profile_pic = profile_pic;
        this.fullName = fullName;
        this.status = status;
    }

    public FindPeople()
    {
    }

    public String getProfile_pic()
    {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic)
    {
        this.profile_pic = profile_pic;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "FindPeople{" +
                "profile_pic='" + profile_pic + '\'' +
                ", fullName='" + fullName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

package com.example.cv0318.comefindme;

import java.util.List;

public class Conversation
{
    private List<Message> messages;

    public Conversation(List<Message> messages)
    {
        this.messages = messages;
    }

    public Conversation()
    {
    }

    public List<Message> getMessages()
    {
        return messages;
    }

    public void setMessages(List<Message> messages)
    {
        this.messages = messages;
    }

    @Override
    public String toString()
    {
        return "Conversation{" +
                "messages=" + messages +
                '}';
    }
}

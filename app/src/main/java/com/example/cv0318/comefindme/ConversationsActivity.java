package com.example.cv0318.comefindme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsActivity extends AppCompatActivity
{
    private static final String TAG = String.format("%s_TAG", ConversationsActivity.class.getSimpleName());
    private Toolbar toolbar;
    private RecyclerView rvConversations;
    private FloatingActionButton fabNewConvo;
    private FirebaseAuth m_auth;
    private String currentUserId;
    private DatabaseReference conversationsRef, usersRef;
    private String username, convoKey;
    private ConversationAdapter conversationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        m_auth = FirebaseAuth.getInstance();
        currentUserId = m_auth.getCurrentUser().getUid();
        conversationsRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        toolbar = findViewById(R.id.tbMessages);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabNewConvo = findViewById(R.id.fabNewConvo);
        rvConversations = findViewById(R.id.rvConversations);

        fabNewConvo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(ConversationsActivity.this, "New convo", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvConversations.setLayoutManager(linearLayoutManager);
        rvConversations.setItemAnimator(new DefaultItemAnimator());

        displayConversations();
    }

    private void displayConversations()
    {
        conversationsRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final List<Conversation> conversations = new ArrayList<>();
                    List<Message> messages = new ArrayList<>();
                    Map<String, Map<String, String>> objectMap;

                    //loop through conversations
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        objectMap = (HashMap<String, Map<String, String>>) snapshot.getValue();
                        messages.clear();

                        //populate messages list for that conversation
                        for (String key : objectMap.keySet())
                        {
                            Message message = new Message();
                            message.setContent(objectMap.get(key).get("content"));
                            message.setDate(objectMap.get(key).get("date"));
                            message.setReceiverPic(objectMap.get(key).get("receiverPic"));
                            message.setSenderId(objectMap.get(key).get("senderId"));
                            message.setSenderPic(objectMap.get(key).get("senderPic"));
                            message.setTime(objectMap.get(key).get("time"));
                            message.setUserId(objectMap.get(key).get("userId"));
                            messages.add(message);

                            convoKey = objectMap.get(key).get("userId");
                        }
                        conversations.add(new Conversation(messages));
                    }
                    //collected info of all conversations.
                    usersRef.child(convoKey).addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                username = dataSnapshot.child("username").getValue().toString();

                                conversationAdapter = new ConversationAdapter(conversations, username, convoKey);
                                rvConversations.setAdapter(conversationAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError)
                        {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void sendUserToConversationActivity(String conversationKey)
    {
        Intent intent = new Intent(ConversationsActivity.this, ConversationActivity.class);
        intent.putExtra("ConvoKey", conversationKey);
        startActivity(intent);
    }
}

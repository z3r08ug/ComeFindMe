package com.example.cv0318.comefindme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity
{
    private static final String TAG = String.format("%s_TAG", MessagesActivity.class.getSimpleName());
    private Toolbar toolbar;
    private RecyclerView rvConversations;
    private FloatingActionButton fabNewConvo;
    private FirebaseAuth m_auth;
    private String currentUserId;
    private DatabaseReference messagesRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        m_auth = FirebaseAuth.getInstance();
        currentUserId = m_auth.getCurrentUser().getUid();
        messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId);

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
                Toast.makeText(MessagesActivity.this, "New convo", Toast.LENGTH_SHORT).show();
            }
        });

        rvConversations.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvConversations.setLayoutManager(linearLayoutManager);

        displayAllConversations();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (firebaseRecyclerAdapter != null)
        {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    private void displayAllConversations()
    {
        Log.d(TAG, "displayAllConversations: ");
        Query query = messagesRef;

        FirebaseRecyclerOptions<Conversation> options = new FirebaseRecyclerOptions.Builder<Conversation>()
                .setQuery(query, Conversation.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Conversation, ConversationsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull ConversationsViewHolder holder, final int position, @NonNull Conversation model)
            {
                Log.d(TAG, "onBindViewHolder: model: " + model);

                final String conversationKey = getRef(position).getKey();
    
                holder.setProfile_pic(model.getProfile_pic());
                holder.setUsername(model.getUsername());
                holder.setLastMessage(model.getLastMessage());
                holder.setTimestamp(model.getTimestamp());
                
                holder.mView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        sendUserToConversationActivity(conversationKey);
                    }
                });
            }

            @NonNull
            @Override
            public ConversationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                Log.d(TAG, "onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_conversations_layout, parent, false);
                return new ConversationsViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        rvConversations.setAdapter(firebaseRecyclerAdapter);
    }
    
    private void sendUserToConversationActivity(String conversationKey)
    {
        Intent intent = new Intent(MessagesActivity.this, ConversationActivity.class);
        intent.putExtra("ConvoKey", conversationKey);
        startActivity(intent);
    }
    
    public static class ConversationsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        TextView tvUsername, tvLastMessage, tvTimestamp;
        CircleImageView civProfilePic;

        public ConversationsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
            
            tvUsername = mView.findViewById(R.id.tvConversationListUserName);
            tvLastMessage = mView.findViewById(R.id.tvConversationListLastMessage);
            tvTimestamp = mView.findViewById(R.id.tvConversationListTimestamp);
            civProfilePic = mView.findViewById(R.id.civConversationListProfilePic);
        }
        
        public void setUsername(String username)
        {
            tvUsername.setText(username);
        }
        
        public void setLastMessage(String message)
        {
            tvLastMessage.setText(message);
        }
        
        public void setTimestamp(String timestamp)
        {
            tvTimestamp.setText(timestamp);
        }
    
        public void setProfile_pic(String profile_pic)
        {
            Picasso.get().load(profile_pic).placeholder(R.drawable.profile).into(civProfilePic);
        }
    }
}

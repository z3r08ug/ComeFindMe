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
import android.widget.ImageButton;
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
        messagesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Messages");

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

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    private void displayAllUsersPosts()
    {
        Log.d(TAG, "displayAllUsersPosts: ");
        Query query = FirebaseDatabase.getInstance().getReference().child("Posts");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(query, Posts.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, MainActivity.PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull MainActivity.PostsViewHolder holder, final int position, @NonNull Posts model)
            {
                Log.d(TAG, "onBindViewHolder: model: "+model);

                final String postKey = getRef(position).getKey();

                holder.setFullName(model.getFullName());
                holder.setTime(model.getTime());
                holder.setDate(model.getDate());
                holder.setDescription(model.getDescription());
                holder.setProfileImage(model.getProfileImage());
                holder.setPostImage(model.getPostImage());

                holder.setLikeButtonStatus(postKey);

                holder.mView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(MessagesActivity.this, ClickPostActivity.class);
                        intent.putExtra("PostKey", postKey);
                        startActivity(intent);
                    }
                });

                holder.btnComment.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(MessagesActivity.this, CommentsActivity.class);
                        intent.putExtra("PostKey", postKey);
                        startActivity(intent);
                    }
                });

                holder.btnLike.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //likeChecker = true;

                        messagesRef.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {

                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public MainActivity.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                Log.d(TAG, "onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                return new MainActivity.PostsViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        rvConversations.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ConversationsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        ImageButton btnLike, btnComment;
        TextView tvLikeTotal;
        int likeCount;
        String currentUserId;
        DatabaseReference likesRef;

        public ConversationsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;

            btnLike = mView.findViewById(R.id.btnLike);
            btnComment = mView.findViewById(R.id.btnComment);
            tvLikeTotal = mView.findViewById(R.id.tvLikeTotal);

            likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }
}

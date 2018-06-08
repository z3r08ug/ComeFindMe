package com.example.cv0318.comefindme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity
{
    private static final String TAG = String.format("%s_TAG", CommentsActivity.class.getSimpleName());
    private ImageButton btnPostComment;
    private EditText etCommentBox;
    private RecyclerView rvComments;
    private String postKey, currentUserId;
    private DatabaseReference usersRef, postRef;
    private FirebaseAuth m_auth;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        postKey = getIntent().getStringExtra("PostKey");

        m_auth = FirebaseAuth.getInstance();
        currentUserId = m_auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("Comments");


        btnPostComment = findViewById(R.id.btnPostComment);
        etCommentBox = findViewById(R.id.etCommentBox);
        rvComments = findViewById(R.id.rvComments);
        rvComments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvComments.setLayoutManager(linearLayoutManager);

        btnPostComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                usersRef.child(currentUserId).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String username = dataSnapshot.child("username").getValue().toString();

                            validateComment(username);

                            etCommentBox.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Query query = postRef;

        FirebaseRecyclerOptions<Comment> options = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, final int position, @NonNull Comment model)
            {
                Log.d("TAGGGG", "onBindViewHolder: model: " + model.toString());

                final String postKey = getRef(position).getKey();

                holder.setComment(model.getComment());
                holder.setDate(model.getDate());
                holder.setTime(model.getTime());
                holder.setUsername(model.getUsername());
            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout, parent, false);
                return new CommentsViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        rvComments.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public CommentsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setComment(String comment)
        {
            TextView tvComment = mView.findViewById(R.id.tvCommentText);
            tvComment.setText(comment);
        }

        public void setDate(String date)
        {
            TextView tvDate = mView.findViewById(R.id.tvCommentDate);
            tvDate.setText(String.format("  Date: %s", date));
        }

        public void setTime(String time)
        {
            TextView tvTime = mView.findViewById(R.id.tvCommentTime);
            tvTime.setText(String.format("  Time: %s", time));
        }

        public void setUsername(String username)
        {
            TextView tvUsername = mView.findViewById(R.id.tvCommentUsername);
            tvUsername.setText(String.format("@%s  ", username));
        }
    }

    private void validateComment(String username)
    {
        String comment = etCommentBox.getText().toString();

        if (comment.isEmpty())
        {
            Toast.makeText(this, "Please enter a comment...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar callForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(callForDate.getTime());


            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(callForTime.getTime());

            final String randomKey = String.format("%s%s%s", currentUserId, saveCurrentDate, saveCurrentTime);

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid", currentUserId);
            commentsMap.put("comment", comment);
            commentsMap.put("date", saveCurrentDate);
            commentsMap.put("time", saveCurrentTime);
            commentsMap.put("username", username);

            postRef.child(randomKey).updateChildren(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener()
                    {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(CommentsActivity.this, "Comment was posted successfully...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(CommentsActivity.this, "Error occurred while posting comment. Please try again...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}

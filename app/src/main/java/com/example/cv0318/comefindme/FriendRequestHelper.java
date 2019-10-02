package com.example.cv0318.comefindme;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendRequestHelper
{
    private FirebaseAuth m_auth;
    private DatabaseReference friendRequestRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView rvFriendRequests;
    
    private String currentUserId;
    private  Context context;
    
    public FriendRequestHelper(Context context)
    {
        this.context = context;
        
        m_auth = FirebaseAuth.getInstance();
        currentUserId = m_auth.getCurrentUser().getUid();
        
        friendRequestRef = FirebaseDatabase.getInstance().getReference()
                .child("FriendRequests")
                .child(currentUserId);
    
//        rvFriendRequests = (R.id.rvConversationMessages);
//        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvFriendRequests.setLayoutManager(linearLayoutManager);
        rvFriendRequests.setHasFixedSize(true);
        
        friendRequestRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                
                }
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
        
            }
        });
    }
}

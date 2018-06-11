package com.example.cv0318.comefindme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationActivity extends AppCompatActivity
{
    private static final String TAG = String.format("%s_TAG", ConversationActivity.class.getSimpleName());
    private CircleImageView civProfilePic;
    private TextView tvAgeLocation, tvCategory;
    private RecyclerView rvConversation;
    private EditText etMessage;
    private ImageButton btnSend;
    private Toolbar toolbar;
    private String convoKey, currentUserId, username, senderPic, receiverPic;
    private FirebaseAuth m_auth;
    private DatabaseReference convoRef, usersRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        
        convoKey = getIntent().getStringExtra("ConvoKey");
        username = getIntent().getStringExtra("username");
        
        m_auth = FirebaseAuth.getInstance();
        currentUserId = m_auth.getCurrentUser().getUid();
        
        usersRef = FirebaseDatabase.getInstance().getReference()
                .child("Users");
        convoRef = FirebaseDatabase.getInstance().getReference()
                .child("Messages")
                .child(currentUserId)
                .child(convoKey);
        
        toolbar = findViewById(R.id.tbConversation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(username);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        civProfilePic = findViewById(R.id.civConversationHeader);
        tvAgeLocation = findViewById(R.id.tvConversationHeaderAgeLocation);
        tvCategory = findViewById(R.id.tvConversationCategory);
        rvConversation = findViewById(R.id.rvConversationMessages);
        etMessage = findViewById(R.id.etConversation);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setEnabled(false);
    
        initTextWatcher();
    
        retrievePartnerInfo();
    
        retrieveCurrentUserPic();
    
        btnSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendMessage();
            }
        });
    }
    
    private void retrieveCurrentUserPic()
    {
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    senderPic = dataSnapshot.child("profile_pic").getValue().toString();
                }
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
        
            }
        });
    }
    
    private void retrievePartnerInfo()
    {
        usersRef.child(convoKey).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String age = "";
                    String location = "";
                    String category = "";
                    
                    receiverPic = dataSnapshot.child("profile_pic").getValue().toString();
                    Log.d(TAG, "onDataChange: profile pic: "+receiverPic);
                    if (dataSnapshot.hasChild("age"))
                    {
                        age = dataSnapshot.child("age").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("location"))
                    {
                        location = dataSnapshot.child("location").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("category"))
                    {
                        category = dataSnapshot.child("category").getValue().toString();
                    }
    
                    Picasso.get().load(receiverPic).placeholder(R.drawable.profile).into(civProfilePic);
                    tvAgeLocation.setText(String.format("Age: %s Location: %s", age, location));
                    tvCategory.setText(category);
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            
            }
        });
    }
    
    private void initTextWatcher()
    {
        etMessage.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
        
            }
    
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                btnSend.setEnabled(true);
            }
    
            @Override
            public void afterTextChanged(Editable s)
            {
            
            }
        });
    }
    
    private void sendMessage()
    {
        String messageContent = etMessage.getText().toString();
    
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(callForDate.getTime());
    
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        String saveCurrentTime = currentTime.format(callForTime.getTime());
    
        String messageRandomName = String.format("%s%s", saveCurrentDate, saveCurrentTime);
        
        Message message = new Message(currentUserId, saveCurrentDate, saveCurrentTime, messageContent);
    
        HashMap convoMap = new HashMap();
        convoMap.put(messageRandomName, message);
    
        convoRef.updateChildren(convoMap).addOnCompleteListener(new OnCompleteListener()
        {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    Log.d(TAG, "onComplete: Message sent successfully.");
                    etMessage.setText("");
                }
            }
        });
    }
}

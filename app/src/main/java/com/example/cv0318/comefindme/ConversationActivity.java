package com.example.cv0318.comefindme;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
    private DatabaseReference convoRef, usersRef, otherConvoRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;

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
        otherConvoRef = FirebaseDatabase.getInstance().getReference()
                .child("Messages")
                .child(convoKey)
                .child(currentUserId);

        toolbar = findViewById(R.id.tbConversation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(username);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        civProfilePic = findViewById(R.id.civConversationHeader);
        tvAgeLocation = findViewById(R.id.tvConversationHeaderAgeLocation);
        tvCategory = findViewById(R.id.tvConversationCategory);

        rvConversation = findViewById(R.id.rvConversationMessages);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvConversation.setLayoutManager(linearLayoutManager);
        rvConversation.setHasFixedSize(true);

        etMessage = findViewById(R.id.etConversation);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setEnabled(false);

        initTextWatcher();

        retrievePartnerInfo();

        retrieveCurrentUserPic();

        displayAllMessages();

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
                    Log.d(TAG, "onDataChange: profile pic: " + receiverPic);
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
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        String saveCurrentTime = currentTime.format(callForTime.getTime());

        String messageRandomName = String.format("%s%s", saveCurrentDate, saveCurrentTime);

        Message outgoingMessage = new Message(currentUserId, saveCurrentDate, saveCurrentTime, messageContent, senderPic, receiverPic, convoKey);

        HashMap convoMap = new HashMap();
        convoMap.put(messageRandomName, outgoingMessage);

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

        convoMap.clear();
        Message incomingMessage = new Message(currentUserId, saveCurrentDate, saveCurrentTime, messageContent, senderPic, receiverPic, currentUserId);
        convoMap.put(messageRandomName, incomingMessage);
        otherConvoRef.updateChildren(convoMap).addOnCompleteListener(new OnCompleteListener()
        {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    Log.d(TAG, "onComplete: Message sent successfully.");
                }
            }
        });


    }

    private void displayAllMessages()
    {
        Log.d(TAG, "displayAllMessages: ");
        Query query = convoRef;

        FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessagesViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull MessagesViewHolder holder, final int position, @NonNull Message model)
            {
                Log.d(TAG, "onBindViewHolder: model: " + model);

                final String conversationKey = getRef(position).getKey();

                if (model.getSenderId().equals(currentUserId))
                {
                    holder.setContent(model.getContent(), 1);
                    holder.setSenderPic(model.getSenderPic());
                }
                else
                {
                    holder.setContent(model.getContent(), 2);
                    holder.setReceiverPic(model.getSenderPic());
                }

                holder.setDate(model.getDate() + " " + model.getTime());

                holder.mView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });
            }

            @NonNull
            @Override
            public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_messages_layout, parent, false);
                return new MessagesViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);

                int messageCount = firebaseRecyclerAdapter.getItemCount();
                int lastViewablePosition = linearLayoutManager.findLastVisibleItemPosition();

                if (lastViewablePosition == -1 || (positionStart >= (messageCount - 1) && lastViewablePosition == (positionStart - 1)))
                {
                    rvConversation.scrollToPosition(positionStart);
                }
            }
        });
        rvConversation.setAdapter(firebaseRecyclerAdapter);
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

    public static class MessagesViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        TextView tvIncoming, tvOutgoing, tvDate;
        CircleImageView civIncoming, civOutgoing;

        public MessagesViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;

            tvIncoming = mView.findViewById(R.id.tvMessageIncoming);
            tvOutgoing = mView.findViewById(R.id.tvMessageOutgoing);
            tvDate = mView.findViewById(R.id.tvMessageDate);
            civIncoming = mView.findViewById(R.id.civMessageIncomingProfilePic);
            civOutgoing = mView.findViewById(R.id.civMessageOutgoingProfilePic);
        }

        public void setDate(String date)
        {
            tvDate.setText(date);
        }

        public void setSenderPic(String senderPic)
        {
            Picasso.get().load(senderPic).placeholder(R.drawable.profile).into(civOutgoing);
        }

        public void setReceiverPic(String receiverPic)
        {
            Picasso.get().load(receiverPic).placeholder(R.drawable.profile).into(civIncoming);
        }

        public void setContent(String content, int flag)
        {
            if (flag == 1)
            {
                tvIncoming.setVisibility(View.GONE);
                civIncoming.setVisibility(View.GONE);
                tvOutgoing.setVisibility(View.VISIBLE);
                civOutgoing.setVisibility(View.VISIBLE);

                tvOutgoing.setText(content);
            }
            else
            {
                tvIncoming.setVisibility(View.VISIBLE);
                civIncoming.setVisibility(View.VISIBLE);
                tvOutgoing.setVisibility(View.GONE);
                civOutgoing.setVisibility(View.GONE);

                tvIncoming.setText(content);
            }
        }
    }
}

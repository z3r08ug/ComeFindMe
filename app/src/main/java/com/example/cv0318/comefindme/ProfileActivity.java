package com.example.cv0318.comefindme;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{
    private static final String TAG = String.format("%s_TAG", ProfileActivity.class.getSimpleName());
    private TextView tvStatus, tvUsername, tvFullName, tvCountry, tvDoB, tvGender, tvRelationship, tvAge, tvLocation, tvCategory;
    private CircleImageView civProfilePic;
    private Button btnMessage;
    private Toolbar toolbar;

    private FirebaseAuth m_auth;
    private String uid, username;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        uid = getIntent().getStringExtra("uid");
        Log.d(TAG, "onCreate: uid: "+uid);

        m_auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        tvStatus = findViewById(R.id.tvProfileStatus);
        tvUsername = findViewById(R.id.tvProfileUsername);
        tvFullName = findViewById(R.id.tvProfileFullName);
        tvCountry = findViewById(R.id.tvProfileCountry);
        tvDoB = findViewById(R.id.tvProfileDoB);
        tvGender = findViewById(R.id.tvProfileGender);
        tvRelationship = findViewById(R.id.tvProfileRelationship);
        tvAge = findViewById(R.id.tvProfileAge);
        tvLocation = findViewById(R.id.tvProfileLocation);
        tvCategory = findViewById(R.id.tvProfileCategory);
        civProfilePic = findViewById(R.id.civProfileProfilePic);
        btnMessage = findViewById(R.id.btnMessage);
        toolbar = findViewById(R.id.tbProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        btnMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ProfileActivity.this, ConversationActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("ConvoKey", uid);
                startActivity(intent);
            }
        });

        usersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("profile_pic").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    username = dataSnapshot.child("username").getValue().toString();
                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String relationship = dataSnapshot.child("relationship").getValue().toString();
                    if (dataSnapshot.hasChild("age"))
                    {
                        String age = dataSnapshot.child("age").getValue().toString();
                        tvAge.setText(String.format("Age: %s", age));
                    }
                    if (dataSnapshot.hasChild("location"))
                    {
                        String location = dataSnapshot.child("location").getValue().toString();
                        tvLocation.setText(String.format("Location: %s", location));
                    }
                    if (dataSnapshot.hasChild("category"))
                    {
                        String category = dataSnapshot.child("category").getValue().toString();
                        tvCategory.setText(String.format("Category: %s", category));
                    }

                    Picasso.get().load(image).placeholder(R.drawable.profile).into(civProfilePic);
                    tvStatus.setText(status);
                    tvUsername.setText(String.format("@%s", username));
                    tvFullName.setText(fullName);
                    tvCountry.setText(String.format("Country: %s", country));
                    tvDoB.setText(String.format("DOB: %s", dob));
                    tvGender.setText(String.format("Gender: %s", gender));
                    tvRelationship.setText(String.format("Relationship: %s", relationship));
                    getSupportActionBar().setTitle(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}

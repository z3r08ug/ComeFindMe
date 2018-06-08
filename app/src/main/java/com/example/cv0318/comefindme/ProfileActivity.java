package com.example.cv0318.comefindme;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private TextView tvStatus, tvUsername, tvFullName, tvCountry, tvDoB, tvGender, tvRelationship;
    private CircleImageView civProfilePic;

    private FirebaseAuth m_auth;
    private String currentUserId;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        m_auth = FirebaseAuth.getInstance();
        currentUserId = m_auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        tvStatus = findViewById(R.id.tvProfileStatus);
        tvUsername = findViewById(R.id.tvProfileUsername);
        tvFullName = findViewById(R.id.tvProfileFullName);
        tvCountry = findViewById(R.id.tvProfileCountry);
        tvDoB = findViewById(R.id.tvProfileDoB);
        tvGender = findViewById(R.id.tvProfileGender);
        tvRelationship = findViewById(R.id.tvProfileRelationship);
        civProfilePic = findViewById(R.id.civProfileProfilePic);

        usersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("profile_pic").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String relationship = dataSnapshot.child("relationship").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.profile).into(civProfilePic);
                    tvStatus.setText(status);
                    tvUsername.setText(String.format("@%s", username));
                    tvFullName.setText(fullName);
                    tvCountry.setText(String.format("Country: %s", country));
                    tvDoB.setText(String.format("DOB: %s", dob));
                    tvGender.setText(String.format("Gender: %s", gender));
                    tvRelationship.setText(String.format("Relationship: %s", relationship));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}

package com.example.cv0318.comefindme;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity
{
    private ImageView ivClickPostImage;
    private TextView tvClickPostDescription;
    private Button btnDeletePost, btnEditPost;
    private DatabaseReference clickPostRef;
    private FirebaseAuth m_auth;
    private String postKey, currentUserId, databaseUserId, description, image;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        m_auth = FirebaseAuth.getInstance();
        currentUserId = m_auth.getCurrentUser().getUid();

        ivClickPostImage = findViewById(R.id.ivClickPostImage);
        tvClickPostDescription = findViewById(R.id.tvClickPostDescription);
        btnDeletePost = findViewById(R.id.btnClickDeletePost);
        btnEditPost = findViewById(R.id.btnClickEditPost);

        btnDeletePost.setVisibility(View.INVISIBLE);
        btnEditPost.setVisibility(View.INVISIBLE);

        postKey = getIntent().getStringExtra("PostKey");
        clickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey);

        clickPostRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    description = dataSnapshot.child("description").getValue().toString();
                    image = dataSnapshot.child("postImage").getValue().toString();
                    databaseUserId = dataSnapshot.child("uid").getValue().toString();

                    tvClickPostDescription.setText(description);
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(ivClickPostImage);

                    if (currentUserId.equals(databaseUserId))
                    {
                        btnDeletePost.setVisibility(View.VISIBLE);
                        btnEditPost.setVisibility(View.VISIBLE);
                    }

                    btnEditPost.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            editCurrentPost(description);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        btnDeletePost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deleteCurrentPost();
            }
        });
    }

    private void editCurrentPost(String description)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Post");

        final EditText inputField = new EditText(this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                clickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post description has been updated...", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);
    }

    private void deleteCurrentPost()
    {
        clickPostRef.removeValue();

        sendUserToMainActivity();

        Toast.makeText(this, "Post has been deleted...", Toast.LENGTH_SHORT).show();
    }

    private void sendUserToMainActivity()
    {
        Intent intent = new Intent(ClickPostActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

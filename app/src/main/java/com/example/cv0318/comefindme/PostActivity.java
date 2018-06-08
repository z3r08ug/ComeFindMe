package com.example.cv0318.comefindme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity
{
    private static final int GALLERY_PICK = 5;
    private Toolbar toolbar;
    private ImageButton btnSelectPostImage;
    private Button btnUpdatePost;
    private EditText etPostDescription;
    private Uri imageUri;
    private StorageReference postImagesRef;
    private String description, saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, currentUserId;
    private DatabaseReference usersRef, postsRef;
    private FirebaseAuth m_auth;
    private ProgressDialog loadingBar;
    private static final String TAG = String.format("%s_TAG", PostActivity.class.getSimpleName());

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        m_auth = FirebaseAuth.getInstance();
        currentUserId = m_auth.getCurrentUser().getUid();

        postImagesRef = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        btnSelectPostImage = findViewById(R.id.ibPostImage);
        btnUpdatePost = findViewById(R.id.btnUpdatePost);
        etPostDescription = findViewById(R.id.etPostDescription);
        loadingBar = new ProgressDialog(this);

        toolbar = findViewById(R.id.tbPostPage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

        btnSelectPostImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openGallery();
            }
        });

        btnUpdatePost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validatePostInfo();
            }
        });
    }

    /**
     * Ensure the user selected an image and provided a description before saving the post.
     */
    private void validatePostInfo()
    {
        description = etPostDescription.getText().toString();

        if (imageUri == null)
        {
            Toast.makeText(this, "Please select an image to post...", Toast.LENGTH_SHORT).show();
        }
        else if (description.isEmpty())
        {
            Toast.makeText(this, "Please enter a description for the post...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Add New Post");
            loadingBar.setMessage("Please wait while the image is being posted...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            storeImageToFirebase();
        }
    }

    /**
     * Save the new post to the FirebaseStorage
     */
    private void storeImageToFirebase()
    {
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        postRandomName = String.format("%s%s", saveCurrentDate, saveCurrentTime);

        Log.d(TAG, "storeImageToFirebase: imageURL: "+imageUri.getLastPathSegment());

        final StorageReference filePath = postImagesRef.child("Post Images").child(String.format("%s%s.png", imageUri.getLastPathSegment(), postRandomName));

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    Log.d(TAG, "onComplete: "+task.getResult().toString());
                    task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            downloadUrl = uri.toString();

                            Toast.makeText(PostActivity.this, "Image uploaded successfully...", Toast.LENGTH_SHORT).show();

                            savingPostInformation();
                        }
                    });
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, String.format("Error occurred: %s", message), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Save information about the post to the FirebaseDatabase
     */
    private void savingPostInformation()
    {
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String fullname = dataSnapshot.child("fullName").getValue().toString();
                    String profilePic = dataSnapshot.child("profile_pic").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", currentUserId);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", description);
                    postsMap.put("postImage", downloadUrl);
                    postsMap.put("profileImage", profilePic);
                    postsMap.put("fullName", fullname);
                    postsRef.child(String.format("%s%s", currentUserId, postRandomName)).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener()
                    {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            loadingBar.dismiss();

                            if (task.isSuccessful())
                            {
                                Toast.makeText(PostActivity.this, "The image was posted successfully...", Toast.LENGTH_SHORT).show();
                                sendUserToMainActivity();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(PostActivity.this, String.format("Error occurred: %s", message), Toast.LENGTH_SHORT).show();
                            }
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

    /**
     * Open the gallery to choose a picture to be posted.
     */
    private void openGallery()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK);
    }

    /**
     * Used to handle the image choosing.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            btnSelectPostImage.setImageURI(imageUri);
        }
    }

    /**
     * If user presses the back button, send them to Main Activity.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            sendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sends the user to the Main Activity.
     */
    private void sendUserToMainActivity()
    {
        Intent intent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(intent);
    }
}

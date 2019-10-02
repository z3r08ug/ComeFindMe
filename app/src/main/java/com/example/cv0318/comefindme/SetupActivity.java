package com.example.cv0318.comefindme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity
{
    private static final int GALLERY_PICK = 5;
    private static final String TAG = SetupActivity.class.getSimpleName() + "_TAG";
    private EditText etUsername, etFullname, etCountry, etAge, etLocation, etCategory;
    private Button btnSave;
    private CircleImageView civProfilePic;
    private FirebaseAuth m_auth;
    private DatabaseReference usersRef;
    private String currentUserId;
    private ProgressDialog loadingBar;
    private StorageReference userProfilePicRef;
    private String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        m_auth = FirebaseAuth.getInstance();
        currentUserId = m_auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfilePicRef = FirebaseStorage.getInstance().getReference().child("profile_pic");


        etUsername = findViewById(R.id.etSetupUsername);
        etFullname = findViewById(R.id.etSetupFullName);
        etCountry = findViewById(R.id.etSetupCountry);
        etAge = findViewById(R.id.etSetupAge);
        etLocation = findViewById(R.id.etSetupLocation);
        etCategory = findViewById(R.id.etSetupCategory);

        btnSave = findViewById(R.id.btnSetupSave);

        civProfilePic = findViewById(R.id.civSetupProfilePic);
        loadingBar = new ProgressDialog(this);

        civProfilePic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_PICK);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveAccountSetupInformation();
            }
        });

        usersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("profile_pic"))
                    {
                        String image = dataSnapshot.child("profile_pic").getValue().toString();

                        Picasso.get().load(image).placeholder(R.drawable.profile).into(civProfilePic);
                    }
                    else
                    {
                        Toast.makeText(SetupActivity.this, "Please select a profile image first...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null)
        {
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Saving Profile Image");
                loadingBar.setMessage("Please wait while we are updating your profile image...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resultUri = result.getUri();

                civProfilePic.setImageURI(resultUri);

                StorageReference filePath = userProfilePicRef.child(currentUserId + ".png");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        loadingBar.dismiss();

                        if (task.isSuccessful())
                        {
                            Toast.makeText(SetupActivity.this, "Profile image was stored successfully...", Toast.LENGTH_SHORT).show();

                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        downloadUrl = task.getResult().toString();
                                        Log.d(TAG, "onComplete: downloadURL for pic: "+downloadUrl);
                                    }
                                }
                            });

//                            usersRef.child("profile_pic").setValue(downloadUrl)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>()
//                                    {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task)
//                                        {
//                                            if (task.isSuccessful())
//                                            {
//                                                Log.d(TAG, "onComplete: profile pic saved to database");
//                                                //Intent intent = new Intent(SetupActivity.this, SetupActivity.class);
//                                                //startActivity(intent);
//
//                                                Toast.makeText(SetupActivity.this, "Profile image was saved successfully...", Toast.LENGTH_SHORT).show();
//                                            }
//                                            else
//                                            {
//                                                String message = task.getException().getMessage();
//                                                Toast.makeText(SetupActivity.this, String.format("Error occurred: %s", message), Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
                        }
                        else
                        {
                            String message = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, String.format("Error occurred: %s", message), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Error occurred: Image could not be cropped. Please try again...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveAccountSetupInformation()
    {
        String username = etUsername.getText().toString();
        String fullname = etFullname.getText().toString();
        String country = etCountry.getText().toString();
        String age = etAge.getText().toString();
        String location = etLocation.getText().toString();
        String category = etCategory.getText().toString();

        if (username.isEmpty())
        {
            Toast.makeText(this, "Username needs to be filled out...", Toast.LENGTH_SHORT).show();
        }
        else if (fullname.isEmpty())
        {
            Toast.makeText(this, "Full name needs to be filled out...", Toast.LENGTH_SHORT).show();
        }
        else if (country.isEmpty())
        {
            Toast.makeText(this, "Country needs to be filled out...", Toast.LENGTH_SHORT).show();
        }
        else if (downloadUrl.isEmpty())
        {
            Toast.makeText(this, "Please select a profile picture first...", Toast.LENGTH_SHORT).show();
        }
        else if (age.isEmpty())
        {
            Toast.makeText(this, "Age needs to be filled out...", Toast.LENGTH_SHORT).show();
        }
        else if (location.isEmpty())
        {
            Toast.makeText(this, "Location needs to be filled out...", Toast.LENGTH_SHORT).show();
        }
        else if (category.isEmpty())
        {
            Toast.makeText(this, "Category needs to be filled out...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait while your account information is being saved...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullName", fullname);
            userMap.put("country", country);
            userMap.put("status", "Hi I am a developer.");
            userMap.put("gender", "Alien");
            userMap.put("dob", "none");
            userMap.put("relationship", "none");
            userMap.put("profile_pic", downloadUrl);
            userMap.put("age", age);
            userMap.put("location", location);
            userMap.put("category", category);
            userMap.put("uid", currentUserId);

            usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    loadingBar.dismiss();

                    if (task.isSuccessful())
                    {
                        sendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your account was created successfully...", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, String.format("Error occurred: %s", message), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity()
    {
        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

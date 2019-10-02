package com.example.cv0318.comefindme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cv0318.comefindme.base.BaseActivity;
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

public class SettingsActivity extends BaseActivity
{
    private static final int GALLERY_PICK = 5;
    private static final String TAG = String.format("%s_TAG", SettingsActivity.class.getSimpleName());
    private EditText etStatus, etUsername, etFullName, etCountry, etDoB, etGender, etLookingFor, etAge, etLocation, etCategory;
    private CircleImageView civProfilePic;
    private Button btnUpdateSettings;

    private String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = findViewById(R.id.tbSettings);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etStatus = findViewById(R.id.etSettingsStatus);
        etUsername = findViewById(R.id.etSettingsUsername);
        etFullName = findViewById(R.id.etSettingsFullName);
        etCountry = findViewById(R.id.etSettingsCountry);
        etDoB = findViewById(R.id.etSettingsDoB);
        etGender = findViewById(R.id.etSettingsGender);
        etLookingFor = findViewById(R.id.etSettingsLookingFor);
        etAge = findViewById(R.id.etSettingsAge);
        etLocation = findViewById(R.id.etSettingsLocation);
        etCategory = findViewById(R.id.etSettingsCategory);
        civProfilePic = findViewById(R.id.civSettingsProfilePic);
        btnUpdateSettings = findViewById(R.id.btnUpdateSettings);

        mUsersRef.addValueEventListener(new ValueEventListener()
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
                    String status = dataSnapshot.child("status").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String relationship = dataSnapshot.child("relationship").getValue().toString();
                    
                    if (dataSnapshot.hasChild("age"))
                    {
                        String age = dataSnapshot.child("age").getValue().toString();
                        etAge.setText(age);
                    }
                    if (dataSnapshot.hasChild("location"))
                    {
                        String location = dataSnapshot.child("location").getValue().toString();
                        etLocation.setText(location);
                    }
                    if (dataSnapshot.hasChild("category"))
                    {
                        String category = dataSnapshot.child("category").getValue().toString();
                        etCategory.setText(category);
                    }

                    etStatus.setText(status);
                    etUsername.setText(username);
                    etFullName.setText(fullName);
                    etCountry.setText(country);
                    etDoB.setText(dob);
                    etGender.setText(gender);
                    etLookingFor.setText(relationship);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        btnUpdateSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validateAccountInfo();
            }
        });

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
                mLoadingBar.setTitle("Saving Profile Image");
                mLoadingBar.setMessage("Please wait while we are updating your profile image...");
                mLoadingBar.setCanceledOnTouchOutside(true);
                mLoadingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = mUserProfilePicRef.child(mUserId + ".png");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        mLoadingBar.dismiss();

                        if (task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this, "Profile image was stored successfully...", Toast.LENGTH_SHORT).show();

                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        downloadUrl = task.getResult().toString();
                                    }
                                }
                            });

                            mUsersRef.child("profile_pic").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                                startActivity(intent);

                                                Toast.makeText(SettingsActivity.this, "Profile image was saved successfully...", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SettingsActivity.this, String.format("Error occurred: %s", message), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            String message = task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this, String.format("Error occurred: %s", message), Toast.LENGTH_SHORT).show();
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

    private void validateAccountInfo()
    {
        String status = etStatus.getText().toString();
        String username = etUsername.getText().toString();
        String fullname = etFullName.getText().toString();
        String country = etCountry.getText().toString();
        String dob = etDoB.getText().toString();
        String gender = etGender.getText().toString();
        String relationship = etLookingFor.getText().toString();
        String age = etAge.getText().toString();
        String location = etLocation.getText().toString();
        String category = etCategory.getText().toString();

        if (status.isEmpty())
        {
            Toast.makeText(this, "Please enter a status...", Toast.LENGTH_SHORT).show();
        }
        else if (username.isEmpty())
        {
            Toast.makeText(this, "Please enter a username...", Toast.LENGTH_SHORT).show();
        }
        else if (fullname.isEmpty())
        {
            Toast.makeText(this, "Please enter your full name...", Toast.LENGTH_SHORT).show();
        }
        else if (country.isEmpty())
        {
            Toast.makeText(this, "Please enter a country...", Toast.LENGTH_SHORT).show();
        }
        else if (dob.isEmpty())
        {
            Toast.makeText(this, "Please enter your birthday...", Toast.LENGTH_SHORT).show();
        }
        else if (gender.isEmpty())
        {
            Toast.makeText(this, "Please enter a gender...", Toast.LENGTH_SHORT).show();
        }
        else if (relationship.isEmpty())
        {
            Toast.makeText(this, "Please enter your relationship status...", Toast.LENGTH_SHORT).show();
        }
        else if (age.isEmpty())
        {
            Toast.makeText(this, "Please enter your age...", Toast.LENGTH_SHORT).show();
        }
        else if (location.isEmpty())
        {
            Toast.makeText(this, "Please enter your location...", Toast.LENGTH_SHORT).show();
        }
        else if (category.isEmpty())
        {
            Toast.makeText(this, "Please enter a category...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mLoadingBar.setTitle("Saving Profile Image");
            mLoadingBar.setMessage("Please wait while we are updating your profile image...");
            mLoadingBar.setCanceledOnTouchOutside(true);
            mLoadingBar.show();

            updateAccountInfo(status, username, fullname, country, dob, gender, relationship, age, location, category);
        }
    }

    private void updateAccountInfo(String status, String username, String fullname,
                                   String country, String dob, String gender,
                                   String relationship, String age, String location,
                                   String category)
    {
        HashMap userMap = new HashMap();
        userMap.put("status", status);
        userMap.put("username", username);
        userMap.put("fullName", fullname);
        userMap.put("country", country);
        userMap.put("dob", dob);
        userMap.put("gender", gender);
        userMap.put("relationship", relationship);
        userMap.put("age", age);
        userMap.put("location", location);
        userMap.put("category", category);

        mUsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener()
        {
            @Override
            public void onComplete(@NonNull Task task)
            {
                mLoadingBar.dismiss();

                if (task.isSuccessful())
                {
                    sendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Account Information saved successfully...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SettingsActivity.this, "Error occurred while saving your information...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendUserToMainActivity()
    {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

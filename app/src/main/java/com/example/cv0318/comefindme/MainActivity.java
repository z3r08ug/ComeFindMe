package com.example.cv0318.comefindme;

import android.content.Intent;
import androidx.annotation.NonNull;

import com.example.cv0318.comefindme.base.BaseActivity;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
{
    private static final String TAG = String.format("%s_TAG", MainActivity.class.getSimpleName());
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar toolbar;
    private FirebaseAuth m_auth;
    private DatabaseReference usersRef, postsRef, likesRef;
    private CircleImageView civNavProfilePic;
    private TextView tvNavUserFullName;
    private String currentUserId, currentUsername;
    private ImageButton ibAddNewPost;
    private FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter;
    private boolean likeChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_auth = FirebaseAuth.getInstance();
        currentUserId = m_auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        toolbar = findViewById(R.id.tbMainPage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        ibAddNewPost = findViewById(R.id.ibAddNewPost);

        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawyer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postList = findViewById(R.id.rvAllUsersPostList);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        civNavProfilePic = navView.findViewById(R.id.civNavProfileImage);
        tvNavUserFullName = navView.findViewById(R.id.tvNavUserFullName);

        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.d(TAG, "onDataChange: dataSnapshot: "+dataSnapshot);
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("fullName"))
                    {
                        String fullName = dataSnapshot.child("fullName").getValue().toString();
                        tvNavUserFullName.setText(fullName);
                    }
                    if (dataSnapshot.hasChild("profile_pic"))
                    {
                        String image = dataSnapshot.child("profile_pic").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(civNavProfilePic);
                    }
                    if (dataSnapshot.hasChild("fullName"))
                    {
                        currentUsername = dataSnapshot.child("fullName").getValue().toString();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Unable to load profile information...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                userMenuSelector(item);
                return false;
            }
        });

        ibAddNewPost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendUserToPostActivity();
            }
        });

        displayAllUsersPosts();
    }

    private void displayAllUsersPosts()
    {
        Log.d(TAG, "displayAllUsersPosts: ");
        Query query = FirebaseDatabase.getInstance().getReference().child("Posts");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(query, Posts.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, final int position, @NonNull final Posts model)
            {
                Log.d(TAG, "onBindViewHolder: model: "+model);

                final String postKey = getRef(position).getKey();

                holder.setFullName(model.getFullName());
                holder.setTime(model.getTime());
                holder.setDate(model.getDate());
                holder.setDescription(model.getDescription());
                holder.setProfileImage(model.getProfileImage());
                holder.setPostImage(model.getPostImage());

                holder.ivPostImage.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(MainActivity.this, ClickPostActivity.class);
                        intent.putExtra("PostKey", postKey);
                        startActivity(intent);
                    }
                });

                holder.civProfilePic.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        intent.putExtra("uid", model.getUid());
                        startActivity(intent);
                    }
                });

                holder.setLikeButtonStatus(postKey);

                holder.btnComment.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                        intent.putExtra("PostKey", postKey);
                        startActivity(intent);
                    }
                });

                holder.btnLike.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        likeChecker = true;

                        likesRef.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (likeChecker)
                                {
                                    if (dataSnapshot.child(postKey).hasChild(currentUserId))
                                    {
                                        likesRef.child(postKey).child(currentUserId).removeValue();
                                        likeChecker = false;
                                    }
                                    else
                                    {
                                        likesRef.child(postKey).child(currentUserId).setValue(true);
                                        likeChecker = false;
                                    }
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

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                Log.d(TAG, "onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                return new PostsViewHolder(view);
            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        ImageButton btnLike, btnComment;
        ImageView ivPostImage;
        TextView tvLikeTotal;
        int likeCount;
        String currentUserId;
        DatabaseReference likesRef;
        CircleImageView civProfilePic;

        public PostsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;

            btnLike = mView.findViewById(R.id.btnLike);
            btnComment = mView.findViewById(R.id.btnComment);
            tvLikeTotal = mView.findViewById(R.id.tvLikeTotal);
            ivPostImage = mView.findViewById(R.id.ivPostImage);
            civProfilePic = mView.findViewById(R.id.civPostProfileImage);

            likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setLikeButtonStatus(final String postKey)
        {
            likesRef.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child(postKey).hasChild(currentUserId))
                    {
                        likeCount = (int) dataSnapshot.child(postKey).getChildrenCount();
                        btnLike.setImageResource(R.drawable.like);
                        tvLikeTotal.setText(Integer.toString(likeCount) + " Likes");
                    }
                    else
                    {
                        likeCount = (int) dataSnapshot.child(postKey).getChildrenCount();
                        btnLike.setImageResource(R.drawable.dislike);
                        tvLikeTotal.setText(Integer.toString(likeCount) + " Likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }

        public void setFullName(String fullName)
        {
            TextView tvUserName = mView.findViewById(R.id.tvPostUserName);
            tvUserName.setText(fullName);
        }

        public void setProfileImage(String profileImage)
        {
            Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(civProfilePic);
        }

        public void setTime(String time)
        {
            TextView tvTime = mView.findViewById(R.id.tvPostTime);
            tvTime.setText(String.format("   %s", time));
        }

        public void setDate(String date)
        {
            TextView tvDate = mView.findViewById(R.id.tvPostDate);
            tvDate.setText(String.format("   %s", date));
        }

        public void setDescription(String description)
        {
            TextView tvDescription = mView.findViewById(R.id.tvPostDescription);
            tvDescription.setText(description);
        }

        public void setPostImage(String postImage)
        {
            Picasso.get().load(postImage).placeholder(R.drawable.profile).into(ivPostImage);
        }
    }

    private void sendUserToPostActivity()
    {
        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        firebaseRecyclerAdapter.startListening();

        FirebaseUser currentUser = m_auth.getCurrentUser();
        if (currentUser == null)
        {
            sendUserToLoginActivity();
        }
        else
        {
            checkUserExistence();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        firebaseRecyclerAdapter.stopListening();
    }

    private void checkUserExistence()
    {
        final String currentUserId = m_auth.getCurrentUser().getUid();

        usersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.hasChild(currentUserId))
                {
                    sendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void sendUserToSetupActivity()
    {
        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToLoginActivity()
    {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToSettingsActivity()
    {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void sendUserToProfileActivity()
    {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra("uid", currentUserId);
        startActivity(intent);
    }

    private void sendUserToFindPeopleActivity()
    {
        Intent intent = new Intent(MainActivity.this, FindPeopleActivity.class);
        startActivity(intent);
    }

    private void sendUserToMessagesActivity()
    {
        Intent intent = new Intent(MainActivity.this, ConversationsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void userMenuSelector(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_post:
                sendUserToPostActivity();
                break;
            case R.id.nav_profile:
                sendUserToProfileActivity();
                break;
            case R.id.nav_home:
                break;
            case R.id.nav_friends:
                break;
            case R.id.nav_find_friends:
                sendUserToFindPeopleActivity();
                break;
            case R.id.nav_messages:
                sendUserToMessagesActivity();
                break;
            case R.id.nav_settings:
                sendUserToSettingsActivity();
                break;
            case R.id.nav_logout:
                m_auth.signOut();
                sendUserToLoginActivity();
                break;
        }
    }


}

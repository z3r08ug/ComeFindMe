package com.example.cv0318.comefindme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindPeopleActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private EditText etSearchBox;
    private ImageButton btnSearch;
    private RecyclerView rvSearchResults;

    private FirebaseAuth m_auth;
    private String currentUserId, uid;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private DatabaseReference allUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        allUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        m_auth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.tbFindPeople);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find People");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etSearchBox = findViewById(R.id.etFindSearchBox);
        btnSearch = findViewById(R.id.btnSearch);
        rvSearchResults = findViewById(R.id.rvResults);

        rvSearchResults.setHasFixedSize(true);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));

        btnSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String criteria = etSearchBox.getText().toString();

                searchPeople(criteria);
            }
        });
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    private void searchPeople(String criteria)
    {
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();

        Query searchQuery = allUsersRef.orderByChild("fullName")
                .startAt(criteria).endAt(criteria + "\uf8ff");
        FirebaseRecyclerOptions<FindPeople> options = new FirebaseRecyclerOptions.Builder<FindPeople>()
                .setQuery(searchQuery, FindPeople.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FindPeople, FindPeopleViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull FindPeopleViewHolder holder, final int position, @NonNull final FindPeople model)
            {
                Log.d("TAGGGG", "onBindViewHolder: model: " + model.toString());

                final String profileKey = getRef(position).getKey();

                holder.setFullName(model.getFullName());
                holder.setStatus(model.getStatus());
                holder.setProfile_pic(model.getProfile_pic());

                holder.view.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(FindPeopleActivity.this, ProfileActivity.class);
                        intent.putExtra("uid", profileKey);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindPeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display, parent, false);
                return new FindPeopleViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        rvSearchResults.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FindPeopleViewHolder extends RecyclerView.ViewHolder
    {
        View view;

        public FindPeopleViewHolder(View itemView)
        {
            super(itemView);
            view = itemView;
        }

        public void setProfile_pic(String profile_pic)
        {
            CircleImageView civProfilePic = view.findViewById(R.id.civAllUsersProfilePic);

            Picasso.get().load(profile_pic).placeholder(R.drawable.profile).into(civProfilePic);
        }

        public void setFullName(String fullName)
        {
            TextView tvFullname = view.findViewById(R.id.tvAllUsersFullName);
            tvFullname.setText(fullName);
        }

        public void setStatus(String status)
        {
            TextView tvStatus = view.findViewById(R.id.tvAllUsersStatus);
            tvStatus.setText(status);
        }
    }
}

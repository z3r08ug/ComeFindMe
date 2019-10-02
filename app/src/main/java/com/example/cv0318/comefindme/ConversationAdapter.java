package com.example.cv0318.comefindme;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>
{
    List<Conversation> conversations;
    String username, convoKey;
    Context context;

    public ConversationAdapter(List<Conversation> conversations, String username, String convoKey)
    {
        this.conversations = conversations;
        this.username = username;
        this.convoKey = convoKey;
    }

    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_conversations_layout, null);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder holder, int position)
    {
        Conversation conversation = conversations.get(position);
        if (conversation != null)
        {
            String dateTime = conversation.getMessages().get(0).getDate() + "  "+ conversation.getMessages().get(0).getTime();

            holder.tvUsername.setText(username);
            holder.tvTimestamp.setText(dateTime);
            holder.tvLastMessage.setText(conversation.getMessages().get(0).getContent());
            Picasso.get().load(conversation.getMessages().get(0).getSenderPic()).placeholder(R.drawable.profile).into(holder.civProfilePic);

            holder.tvLastMessage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, ConversationActivity.class);
                    intent.putExtra("ConvoKey", convoKey);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return conversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView tvUsername, tvLastMessage, tvTimestamp;
        private final CircleImageView civProfilePic;

        public ViewHolder(View itemView)
        {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvConversationListUserName);
            tvLastMessage = itemView.findViewById(R.id.tvConversationListLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvConversationListTimestamp);
            civProfilePic = itemView.findViewById(R.id.civConversationListProfilePic);
        }
    }
}

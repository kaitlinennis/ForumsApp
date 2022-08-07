/*
HW06
ForumRecyclerViewAdapter.java
Kaitlin Ennis
 */
package com.example.ennis_hw06;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ForumRecyclerViewAdapter extends RecyclerView.Adapter<ForumRecyclerViewAdapter.ForumViewHolder>{
    private FirebaseAuth mAuth;

    ArrayList<Forum> forumEntries;
    public ForumRecyclerViewAdapter(ArrayList<Forum> data, Fragment fragment) {

        if(fragment instanceof ForumRecyclerViewAdapter.IRecyclerAdapterListener) {
            Log.d("demo", "UserRecyclerViewAdapter: FRAGMENT: " + fragment.toString());
            recyclerAdapterListener = (ForumRecyclerViewAdapter.IRecyclerAdapterListener) fragment;
        } else {
            throw new RuntimeException(fragment.toString() + " must implement IRecyclerAdapterListener");
        }

        this.forumEntries = data;
    }

    @NonNull
    @Override
    public ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_row_item, parent, false);
        ForumViewHolder forumViewHolder =  new ForumViewHolder(view);

        return forumViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForumViewHolder holder, int position) {

        Forum forumEntry = forumEntries.get(position);
        holder.displayForumTitle.setText(forumEntry.title);
        holder.displayForumCreator.setText(forumEntry.creator);
        holder.displayForumDescription.setText(forumEntry.description);
        holder.displayDateCreated.setText(forumEntry.dateTimeCreated);
        holder.numLikesPerForum.setText(String.valueOf(forumEntry.usersLike.size()));

        Log.d("demo", "onBindViewHolder: users like size: " + forumEntry.usersLike.size());

        mAuth = FirebaseAuth.getInstance();

        //only shows the trash can icon for forums the current user created
        if(forumEntries.get(holder.getAdapterPosition()).creator.equals(mAuth.getCurrentUser().getDisplayName())) {
            holder.deleteImageButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteImageButton.setVisibility(View.INVISIBLE);
        }


        //clicking on a recycler view item (a forum)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("demo", "onClick: Position of the forum you clicked: " + holder.getAdapterPosition());

                for(int i = 0; i < forumEntries.size(); i++) {
                    if(i == holder.getAdapterPosition()) {
                        Log.d("demo", "Title of id: " + forumEntries.get(i).title);
                        recyclerAdapterListener.sendClickedFragment(forumEntries.get(i));
                    }
                }
            }
        });

        holder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();


                Log.d("demo", "onClick: Delete doc id: " + db.collection("forums")
                        .document(forumEntries.get(holder.getAdapterPosition()).docId));

                String id = forumEntries.get(holder.getAdapterPosition()).docId;

                recyclerAdapterListener.sendDeletedForum(id);


            }
        });


        int unlikeId = R.drawable.like_not_favorite;
        int likeId = R.drawable.like_favorite;


        //if current user id is in the map/list of users that like this forum, show the filled heart
        if(forumEntry.usersLike.contains(mAuth.getCurrentUser().getUid())) {
            holder.likeUnlikeImageButton.setImageResource(likeId);
            //holder.likeUnlikeImageButton.setTag("R.drawable.like_favorite");
        } else {
            holder.likeUnlikeImageButton.setImageResource(unlikeId);
            //holder.likeUnlikeImageButton.setTag("R.drawable.like_not_favorite");
        }


        holder.likeUnlikeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if(forumEntry.usersLike.contains(mAuth.getCurrentUser().getUid())) {
                    //unlike
                    db.collection("forums").document(forumEntries.get(holder.getAdapterPosition()).docId);

                    String id = forumEntries.get(holder.getAdapterPosition()).docId;

                    forumEntry.usersLike.remove(mAuth.getCurrentUser().getUid());
                    //update on firebase (remove the curr user id from the list on Firestore for this forum)
                    db.collection("forums").document(id).update("usersLike", forumEntry.usersLike);

                    //forumEntry.usersLike.remove(mAuth.getCurrentUser().getUid());
                    holder.likeUnlikeImageButton.setImageResource(unlikeId);
                    holder.numLikesPerForum.setText(String.valueOf(forumEntry.usersLike.size()));

                } else {
                    //like

                    db.collection("forums").document(forumEntries.get(holder.getAdapterPosition()).docId);

                    String id = forumEntries.get(holder.getAdapterPosition()).docId;

                    //update on firebase (add the curr user id from the list on Firestore for this forum)

                    forumEntry.usersLike.add(mAuth.getCurrentUser().getUid());
                    db.collection("forums").document(id).update("usersLike", forumEntry.usersLike);

                    holder.likeUnlikeImageButton.setImageResource(likeId);
                    holder.numLikesPerForum.setText(String.valueOf(forumEntry.usersLike.size()));

                }




            }
        });


    }

    @Override
    public int getItemCount() {
        if(this.forumEntries == null) {
            return 0;
        }
        return this.forumEntries.size();
    }

    public static class ForumViewHolder extends RecyclerView.ViewHolder {
        TextView displayForumTitle, displayForumCreator, displayForumDescription, displayDateCreated, numLikesPerForum;
        ImageButton deleteImageButton, likeUnlikeImageButton;


        public ForumViewHolder(@NonNull View itemView) {
            super(itemView);

            displayForumTitle = itemView.findViewById(R.id.displayForumTitle);
            displayForumCreator = itemView.findViewById(R.id.displayForumCreator);
            displayForumDescription = itemView.findViewById(R.id.displayForumDescription);
            displayDateCreated = itemView.findViewById(R.id.displayDateCreated);
            deleteImageButton = itemView.findViewById(R.id.deleteImageButton);

            numLikesPerForum = itemView.findViewById(R.id.numLikesPerForum);
            likeUnlikeImageButton = itemView.findViewById(R.id.likeUnlikeImageButton);
            likeUnlikeImageButton.setTag("R.drawable.like_not_favorite");
        }
    }


    ForumRecyclerViewAdapter.IRecyclerAdapterListener recyclerAdapterListener;

    public interface IRecyclerAdapterListener {
        void sendDeletedForum(String deleteForumId);
        void sendClickedFragment(Forum forumSelected);
    }

}



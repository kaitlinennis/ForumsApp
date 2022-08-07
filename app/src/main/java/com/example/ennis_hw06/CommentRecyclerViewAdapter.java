/*
HW06
CommentsRecyclerViewAdapter.java
Kaitlin Ennis
 */
package com.example.ennis_hw06;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentViewHolder>{

    private FirebaseAuth mAuth;

    ArrayList<Comment> commentEntries;
    public CommentRecyclerViewAdapter(ArrayList<Comment> data, Fragment fragment) {

        if(fragment instanceof CommentRecyclerViewAdapter.IRecyclerAdapterListener) {
            Log.d("demo", "UserRecyclerViewAdapter: FRAGMENT: " + fragment.toString());
            recyclerAdapterListener = (CommentRecyclerViewAdapter.IRecyclerAdapterListener) fragment;
        } else {
            throw new RuntimeException(fragment.toString() + " must implement IRecyclerAdapterListener");
        }

        this.commentEntries = data;
    }

    @NonNull
    @Override
    public CommentRecyclerViewAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row_item, parent, false);
        CommentRecyclerViewAdapter.CommentViewHolder commentViewHolder =  new CommentRecyclerViewAdapter.CommentViewHolder(view);

        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRecyclerViewAdapter.CommentViewHolder holder, int position) {

        Comment commentEntry = commentEntries.get(position);

        holder.displayCommentCreator.setText(commentEntry.commentCreator);
        Log.d("demo", "onBindViewHolder: creator of comment: " + commentEntry.commentCreator);
        holder.displayCommentText.setText(commentEntry.comment);
        holder.displayCommentDateTime.setText(commentEntry.commentDateTimePosted);


        mAuth = FirebaseAuth.getInstance();

        //only shows the trash can icon for forums the current user created
        if(commentEntries.get(holder.getAdapterPosition()).commentCreator.equals(mAuth.getCurrentUser().getDisplayName())) {
            holder.deleteCommentImageButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteCommentImageButton.setVisibility(View.INVISIBLE);
        }

        holder.deleteCommentImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                String id = commentEntries.get(holder.getAdapterPosition()).docId;

                recyclerAdapterListener.sendDeletedComment(id);

            }
        });



    }

    @Override
    public int getItemCount() {
        if(this.commentEntries == null) {
            return 0;
        }
        return this.commentEntries.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView displayCommentCreator, displayCommentText,displayCommentDateTime;
        ImageButton deleteCommentImageButton;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            displayCommentCreator = itemView.findViewById(R.id.displayCommentCreator);
            displayCommentText = itemView.findViewById(R.id.displayCommentText);
            displayCommentDateTime = itemView.findViewById(R.id.displayCommentDateTime);
            deleteCommentImageButton = itemView.findViewById(R.id.deleteCommentImageButton);
        }
    }


    CommentRecyclerViewAdapter.IRecyclerAdapterListener recyclerAdapterListener;

    public interface IRecyclerAdapterListener {
        void sendDeletedComment(String deleteCommentId);
    }

}

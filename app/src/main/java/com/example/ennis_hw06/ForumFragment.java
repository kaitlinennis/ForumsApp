/*
HW06
ForumFragment.java
Kaitlin Ennis
 */
package com.example.ennis_hw06;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ForumFragment extends Fragment implements CommentRecyclerViewAdapter.IRecyclerAdapterListener {

    private FirebaseAuth mAuth;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_FORUM_CHOSEN = "ARG_PARAM_FORUM_CHOSEN";

    private Forum forumChosen;

    public ForumFragment() {
        // Required empty public constructor
    }


    public static ForumFragment newInstance(Forum forumChosen) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_FORUM_CHOSEN, forumChosen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forumChosen = (Forum) getArguments().getSerializable(ARG_PARAM_FORUM_CHOSEN);
        }
    }


    //declare variables
    TextView displaySelectedForumTitle, displaySelectedForumCreator, displaySelectedForumDescription, commentsNum;
    EditText editTextWriteComment;
    Button postCommentButton;
    RecyclerView commentsRecyclerView;
    LinearLayoutManager layoutManager;
    CommentRecyclerViewAdapter adapter;
    ArrayList<Comment> commentEntries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_forum, container, false);

        //initialize variables
        displaySelectedForumTitle = view.findViewById(R.id.displaySelectedForumTitle);
        displaySelectedForumCreator = view.findViewById(R.id.displaySelectedForumCreator);
        displaySelectedForumDescription = view.findViewById(R.id.displaySelectedForumDescription);
        commentsNum = view.findViewById(R.id.commentsNum);
        editTextWriteComment = view.findViewById(R.id.editTextWriteComment);
        postCommentButton = view.findViewById(R.id.postCommentButton);

        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);

        commentEntries = new ArrayList<>();
        //commentEntries.clear();
        //getData();

        //read comments collection from Firestore
        mAuth = FirebaseAuth.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("forums").document(forumChosen.docId).collection("comments")

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        commentEntries.clear();
                        for (QueryDocumentSnapshot document: value) {
                            Comment comment = document.toObject(Comment.class);

                            commentEntries.add(comment);
                        }

                        //Adjust size
                        commentsNum.setText(String.valueOf(commentEntries.size()));
                        adapter.notifyDataSetChanged();

                        Log.d("demo", "onEvent: Size of comments list: " + commentEntries.size());
                    }
                });


        layoutManager = new LinearLayoutManager(container.getContext());
        commentsRecyclerView.setLayoutManager(layoutManager);

        adapter = new CommentRecyclerViewAdapter(commentEntries, ForumFragment.this);
        commentsRecyclerView.setAdapter(adapter);

        //add divider lines in between each list item
        commentsRecyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(), DividerItemDecoration.VERTICAL));



        displaySelectedForumTitle.setText(forumChosen.title);
        displaySelectedForumCreator.setText(forumChosen.creator);
        displaySelectedForumDescription.setText(forumChosen.description);
        commentsNum.setText(String.valueOf(commentEntries.size()));
        
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextWriteComment.getText().toString().isEmpty()) {
                    Toast.makeText(container.getContext(), R.string.comment_toast, Toast.LENGTH_SHORT).show();
                } else {
                    //post comment

                    //No missing inputs; store a new comment on Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    HashMap<String, Object> comment = new HashMap<>();


                    Date date = new Date();
                    SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    String formattedDateTime = formatDate.format(date);

                    comment.put("commentDateTimePosted", formattedDateTime);
                    comment.put("comment", editTextWriteComment.getText().toString());
                    comment.put("commentCreator", mAuth.getCurrentUser().getDisplayName());


                    db.collection("forums").document(forumChosen.docId).collection("comments")
                            .add(comment)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //Forum was added; go back to the Forums fragment
                                    String id = documentReference.getId();

                                    Log.d("demo", "onSuccess: new DOC ID: " + id);

                                    comment.put("docId", id);
                                    documentReference.set(comment);

                                    Log.d("demo", "onSuccess: NEW COMMENT: " + comment);

                                    //getData();
                                }
                            });
                }

                //clear input field after the comment is posted
                editTextWriteComment.setText("");

                //Adjust size
                commentsNum.setText(String.valueOf(commentEntries.size() + 1));

                //adapter.notifyDataSetChanged();
            }
        });


        return view;
    }

    @Override
    public void sendDeletedComment(String deleteCommentId) {
        mAuth = FirebaseAuth.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //delete the comment document from the comments subcollection
        db.collection("forums").document(forumChosen.docId).collection("comments")
                .document(deleteCommentId).delete();

        adapter.notifyDataSetChanged();
    }


}
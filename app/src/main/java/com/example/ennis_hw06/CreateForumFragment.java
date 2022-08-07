/*
HW06
CreateForumFragment.java
Kaitlin Ennis
 */
package com.example.ennis_hw06;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreateForumFragment extends Fragment {

    private FirebaseAuth mAuth;

    public CreateForumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //declare variables
    EditText createForumTitle, createForumDescription;
    Button submitCreateButton, cancelCreateButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_forum, container, false);

        //initialize variables
        createForumTitle = view.findViewById(R.id.createForumTitle);
        createForumDescription = view.findViewById(R.id.createForumDescription);
        submitCreateButton = view.findViewById(R.id.submitCreateButton);
        cancelCreateButton = view.findViewById(R.id.cancelCreateButton);

        cancelCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.backToForums();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        submitCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String forumTitle = createForumTitle.getText().toString();
                String forumDescription = createForumDescription.getText().toString();

                //Input validation; checking for missing inputs
                if (forumTitle.isEmpty() && forumDescription.isEmpty()) {
                    Toast.makeText(container.getContext(), R.string.toast_missing_forum_title_description, Toast.LENGTH_SHORT).show();
                } else if (forumTitle.isEmpty()) {
                    Toast.makeText(container.getContext(), R.string.toast_missing_forum_title, Toast.LENGTH_SHORT).show();
                } else if (forumDescription.isEmpty()) {
                    Toast.makeText(container.getContext(), R.string.toast_missing_forum_description, Toast.LENGTH_SHORT).show();
                } else {

                    //No missing inputs; store a new forum on Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    HashMap<String, Object> forum = new HashMap<>();

                    forum.put("title", forumTitle);
                    forum.put("description", forumDescription);
                    forum.put("creator", mAuth.getCurrentUser().getDisplayName());


                    Date date = new Date();
                    SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    String formattedDateTime = formatDate.format(date);
                    //String dateString = date.toString();

                    forum.put("dateTimeCreated", formattedDateTime);
                    forum.put("usersLike", new ArrayList<>());
                    //forum.put("docId", 0);

                    db.collection("forums")
                            .add(forum)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //Forum was added; go back to the Forums fragment
                                    String id = documentReference.getId();

                                    Log.d("demo", "onSuccess: new DOC ID: " + id);

                                    forum.put("docId", id);

                                    documentReference.set(forum);

                                    Log.d("demo", "onSuccess: NEW FORUM: " + forum);
                                    mListener.backToForums();
                                }
                            });

                }
            }
        });

        return view;
    } //end of onCreateView


    CreateForumFragment.CreateForumFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreateForumFragment.CreateForumFragmentListener) context;
    }

    interface CreateForumFragmentListener {
        void backToForums();
    }

}
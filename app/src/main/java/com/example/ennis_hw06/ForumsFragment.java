/*
HW06
ForumsFragment.java
Kaitlin Ennis
 */
package com.example.ennis_hw06;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ForumsFragment extends Fragment implements ForumRecyclerViewAdapter.IRecyclerAdapterListener{

    private FirebaseAuth mAuth;

    public ForumsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //declare variables
    Button logoutButton, newForumButton;
    RecyclerView forumsRecyclerView;
    LinearLayoutManager layoutManager;
    ForumRecyclerViewAdapter adapter;
    ArrayList<Forum> forumEntries;
    ImageButton deleteImageButton;
    ImageButton unlikeImageButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forums, container, false);

        //initialize variables
        logoutButton = view.findViewById(R.id.logoutButton);
        newForumButton = view.findViewById(R.id.newForumButton);
        deleteImageButton = view.findViewById(R.id.deleteImageButton);
        unlikeImageButton = view.findViewById(R.id.likeUnlikeImageButton);
        forumsRecyclerView = view.findViewById(R.id.forumsRecyclerView);


        forumEntries = new ArrayList<>();


        forumEntries.clear();
        getData();


        layoutManager = new LinearLayoutManager(container.getContext());
        forumsRecyclerView.setLayoutManager(layoutManager);

        adapter = new ForumRecyclerViewAdapter(forumEntries, ForumsFragment.this);
        forumsRecyclerView.setAdapter(adapter);

        //add divider lines in between each list item
        forumsRecyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(), DividerItemDecoration.VERTICAL));


        newForumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToNewForum();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sign out the current user
                FirebaseAuth.getInstance().signOut();
                mListener.logoutCurrentUser();
            }
        });

        return view;
    } //end of onCreateView

    private void getData() {
        //read forums collection from Firestore
        mAuth = FirebaseAuth.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("forums")

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        forumEntries.clear();
                        for (QueryDocumentSnapshot document: value) {
                            Forum forum = document.toObject(Forum.class);

                            forumEntries.add(forum);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

            //adapter.notifyDataSetChanged();
    }

    ForumsFragment.ForumsFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ForumsFragment.ForumsFragmentListener) context;
    }



    @Override
    public void sendDeletedForum(String deleteForumId) {
        mAuth = FirebaseAuth.getInstance();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            //CollectionReference comRef = db.collection("forums").document(deleteForumId).collection("comments");

            //delete all comments from the comments subcollection first
        db.collection("forums").document(deleteForumId).collection("comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        //forumEntries.clear();

                        //delete all documents from the comments subcollection first
                        for (QueryDocumentSnapshot document: value) {

                            db.collection("forums").document(deleteForumId).collection("comments")
                                    .document(document.getId()).delete();

                        }
                        adapter.notifyDataSetChanged();
                    }
                });

            //then delete the forum document
            db.collection("forums").document(deleteForumId).delete();
            adapter.notifyDataSetChanged();

    }


    @Override
    public void sendClickedFragment(Forum forumSelected) {
        mListener.sendForumClicked(forumSelected);
    }

    interface ForumsFragmentListener {
        void logoutCurrentUser();
        void goToNewForum();
        void sendForumClicked(Forum selectedForum);
    }
}
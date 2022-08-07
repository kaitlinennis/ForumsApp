/*
HW06
MainActivity.java
Kaitlin Ennis
 */
package com.example.ennis_hw06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, ForumsFragment.ForumsFragmentListener, CreateForumFragment.CreateForumFragmentListener, RegisterFragment.RegisterFragmentListener {

    FirebaseAuth mAuth;

    final String FRAGMENT_TAG = "fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            //Need to login
            setTitle("Login");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerView, new LoginFragment(), FRAGMENT_TAG)
                    .commit();
        } else {
            //Logged in already; proceed to the Forums page
            Log.d("demo", "onCreate: You don't need to log in :) ");
            setTitle("Forums");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerView, new ForumsFragment(), FRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public void goToCreateNewAccount() {
        setTitle("Create New Account");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new RegisterFragment(), FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoForumsFragment() {
        setTitle("Forums");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new ForumsFragment(), FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void logoutCurrentUser() {
        //go back to the Login fragment
        setTitle("Login");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new LoginFragment(), FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void goToNewForum() {
        setTitle("New Forum");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new CreateForumFragment(), FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }



    @Override
    public void sendForumClicked(Forum selectedForum) {
        setTitle("Forum");
        Log.d("demo", "sendForumClicked: Forum title from id (main activity): " + selectedForum);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, ForumFragment.newInstance(selectedForum), FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void backToForums() {
        //go back to the Forums fragment
        setTitle("Forums");
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void cancelRegister() {
        //go back to the Login fragment
        setTitle("Login");
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void registeredUserSuccessfully() {
        setTitle("Forums");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new ForumsFragment(), FRAGMENT_TAG)
                .commit();
    }


    //resets the titles of each fragment when hitting the back button.
    //reverts to the previous fragment title
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) instanceof ForumsFragment) {
            setTitle("Forums");
        } else if(getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) instanceof CreateForumFragment) {
            setTitle("New Forum");
        } else if(getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) instanceof ForumFragment) {
            setTitle("Forum");
        } else if(getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) instanceof RegisterFragment) {
            setTitle("Create New Account");
        } else if(getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) instanceof LoginFragment) {
            setTitle("Login");
        }

    }

}
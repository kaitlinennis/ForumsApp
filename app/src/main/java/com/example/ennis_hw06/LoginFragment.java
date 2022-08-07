/*
HW06
LoginFragment.java
Kaitlin Ennis
 */
package com.example.ennis_hw06;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //declare variables
    EditText loginEmailAddress, loginPassword;
    Button loginButton, createNewAccButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //initialize variables
        loginEmailAddress = view.findViewById(R.id.loginEmailAddress);
        loginPassword = view.findViewById(R.id.loginPassword);
        loginButton = view.findViewById(R.id.loginButton);
        createNewAccButton = view.findViewById(R.id.createNewAccButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Input validation; check if email and password fields are entered

                String email = loginEmailAddress.getText().toString();
                String password = loginPassword.getText().toString();

                if(email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(container.getContext(), R.string.toast_missing_email_password, Toast.LENGTH_SHORT).show();
                } else if(email.isEmpty()) {
                    Toast.makeText(container.getContext(), R.string.toast_missing_email, Toast.LENGTH_SHORT).show();
                } else if(password.isEmpty()) {
                    Toast.makeText(container.getContext(), R.string.toast_missing_password, Toast.LENGTH_SHORT).show();
                } else {
                    //No empty inputs; Attempt to login the user with Firebase Auth
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        //if login is successful, go to the Forums fragment
                                        mListener.gotoForumsFragment();

                                        Log.d("demo", "onComplete: Current user: " + mAuth.getCurrentUser().getUid());
                                    } else {
                                        //If login is not successful, show alert with the error message
                                        Toast.makeText(container.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        createNewAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //replace with the Create New Account Fragment
                mListener.goToCreateNewAccount();
            }
        });

        return view;
    } //end of onCreateView


    LoginFragment.LoginFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (LoginFragment.LoginFragmentListener) context;
    }

    interface LoginFragmentListener {
        void goToCreateNewAccount();
        void gotoForumsFragment();
    }

}
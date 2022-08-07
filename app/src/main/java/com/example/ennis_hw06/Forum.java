/*
HW06
Forum.java
Kaitlin Ennis
 */
package com.example.ennis_hw06;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Forum implements Serializable {
    String title, creator, description;
    //ate dateTimeCreated;
    //Timestamp dateTimeCreated;
    String dateTimeCreated;
    String docId;
    //Comment comments;

    //List of user IDs that have liked the forum
    ArrayList<String> usersLike = new ArrayList<>();


    public Forum() {
    }

    public Forum(String title, String creator, String description, String dateTimeCreated, String docId, ArrayList<String> usersLike) {
        this.title = title;
        this.creator = creator;
        this.description = description;
        this.dateTimeCreated = dateTimeCreated;
        this.docId = docId;
        this.usersLike = usersLike;
    }

    @Override
    public String toString() {
        return "Forum{" +
                "title='" + title + '\'' +
                ", creator='" + creator + '\'' +
                ", description='" + description + '\'' +
                ", dateTimeCreated='" + dateTimeCreated + '\'' +
                ", docId='" + docId + '\'' +
                ", usersLike=" + usersLike +
                '}';
    }

//Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(String dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public ArrayList<String> getUsersLike() {
        return usersLike;
    }

    public void setUsersLike(ArrayList<String> usersLike) {
        this.usersLike = usersLike;
    }
}

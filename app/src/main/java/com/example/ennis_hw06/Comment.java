/*
HW06
Comment.java
Kaitlin Ennis
 */
package com.example.ennis_hw06;

public class Comment {
    String commentCreator, comment, commentDateTimePosted;
    String docId;

    public Comment() {
    }

    public Comment(String commentCreator, String comment, String commentDateTimePosted, String docId) {
        this.commentCreator = commentCreator;
        this.comment = comment;
        this.commentDateTimePosted = commentDateTimePosted;
        this.docId = docId;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentCreator='" + commentCreator + '\'' +
                ", comment='" + comment + '\'' +
                ", commentDateTimePosted='" + commentDateTimePosted + '\'' +
                ", docId='" + docId + '\'' +
                '}';
    }

    public String getCommentCreator() {
        return commentCreator;
    }

    public void setCommentCreator(String commentCreator) {
        this.commentCreator = commentCreator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentDateTimePosted() {
        return commentDateTimePosted;
    }

    public void setCommentDateTimePosted(String commentDateTimePosted) {
        this.commentDateTimePosted = commentDateTimePosted;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}

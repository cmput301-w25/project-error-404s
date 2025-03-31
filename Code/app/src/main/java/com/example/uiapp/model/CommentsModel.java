package com.example.uiapp.model;

public class CommentsModel {
    String id, userImage, userName,userId, comments, documentId;

    public CommentsModel() {
    }

    public CommentsModel(String id, String userImage, String userName, String userId, String comments, String documentId) {
        this.id = id;
        this.userImage = userImage;
        this.userName = userName;
        this.userId = userId;
        this.comments = comments;
        this.documentId = documentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}

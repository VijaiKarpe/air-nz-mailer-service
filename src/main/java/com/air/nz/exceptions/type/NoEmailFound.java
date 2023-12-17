package com.air.nz.exceptions.type;

public class NoEmailFound extends RuntimeException {
    public NoEmailFound(String userEmail, String folder) {

        this.userEmail = userEmail;
        this.folder = folder;
    }

    private String userEmail;

    private String folder;


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}



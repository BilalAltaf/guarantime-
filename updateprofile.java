package com.guarantime;

public class updateprofile {
    private String FirstName;
    private String LastName;
    private String TelephoneNo;
    private String EmailAddress;
    private String Username;

    public updateprofile(String FirstName, String LastName, String TelephoneNo, String EmailAddress,String Username) {
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.TelephoneNo = TelephoneNo;
        this.EmailAddress = EmailAddress;
        this.Username=Username;
    }



    public String getFirstName() { return FirstName; }
    public String getLastName() { return LastName; }
    public String getTelephoneNo() { return TelephoneNo; }
    public String getEmailAddress() { return EmailAddress; }
    public String getUsername() { return Username; }

}

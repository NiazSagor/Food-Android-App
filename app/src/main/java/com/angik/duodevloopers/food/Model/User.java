package com.angik.duodevloopers.food.Model;

public class User {
    private String Name;
    private String PhoneNumber;
    private String ID;
    private String Email;
    private String Balance;

    public User() {

    }

    public User(String balance, String id, String name, String phone, String email) {
        this.Name = name;
        this.PhoneNumber = phone;
        this.ID = id;
        this.Balance = balance;
        this.Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }
}

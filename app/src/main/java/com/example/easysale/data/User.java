package com.example.easysale.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    private Integer userId;



    public User(Integer id, String email, String firstName, String lastName, String avatar, String page) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.page = page;
    }

    public User(User user) {
        id = user.id;
        email = user.email;
        firstName = user.firstName;
        lastName = user.lastName;
        avatar = user.avatar;
        page = user.page;
    }

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    private String page;


    public void setPage(String page) {
        this.page = page;
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getPage() {
        return page;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void updateUserFromUser(User user){
        id = user.id;
        email = user.email;
        firstName = user.firstName;
        lastName = user.lastName;
        avatar = user.avatar;
        page = user.page;
    }

}

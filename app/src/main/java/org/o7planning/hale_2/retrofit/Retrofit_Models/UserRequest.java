package org.o7planning.hale_2.retrofit.Retrofit_Models;

public class UserRequest {

    String email;
    String password;

    public UserRequest(){}

    public UserRequest(String e, String p) {
        email = e;
        password = p;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

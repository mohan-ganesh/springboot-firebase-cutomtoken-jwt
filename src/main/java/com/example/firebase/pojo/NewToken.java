package com.example.firebase.pojo;

import java.io.Serializable;

public class NewToken implements Serializable{

    //new topic name
    public String token;

    
    /**
     * 
     */

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 
     */
    public String getToken(){
        return this.token;
    }

  
    /**
     * 
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("token=").append(token);
        sb.append('}');
        return sb.toString();
    }
  
}
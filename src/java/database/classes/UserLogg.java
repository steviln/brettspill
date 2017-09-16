/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.classes;

import java.util.Date;

/**
 *
 * @author Steinar
 */
public class UserLogg {
    private String sessionID;
    private int userID;
    private int id;
    public UserLogg(){}
    public UserLogg(String uselog, int userid){
        sessionID = uselog;
        userID = userid;
    }
    
    public String getSessionID(){
        return sessionID;
    }
    public void setSessionID(String newval){
        sessionID = newval;
    }
    public int getuserID(){
        return userID;
    }
    public void setUserID(int newval){
        userID = newval;
    }
    public int getId(){
        return id;
    }
    public void setId(int newval){
        id = newval;
    }    

    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.classes;

/**
 *
 * @author Steinar
 */
public class FacebookFailLogin implements java.io.Serializable {
     private Integer id;
     private String navn;
     private String facebookid;
     
     public FacebookFailLogin(){}
     public FacebookFailLogin(String orgnavn, String orgface){
         this.navn = orgnavn;
         this.facebookid = orgface;
     }
     
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNavn() {
        return this.navn;
    }
    
    public void setNavn(String navn) {
        this.navn = navn;
    }
    public String getFacebookid() {
        return this.facebookid;
    }
    
    public void setFacebookid(String nav) {
        this.facebookid = nav;
    }    
     
     
}

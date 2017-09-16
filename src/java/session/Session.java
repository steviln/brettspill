/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.List;
import org.hibernate.Query;
import com.restfb.types.User;

/**
 *
 * @author Steinar
 */
public class Session {
    private int status;
    private String session;
    private int userID;
    private String username;
    
    public Session(int fstat, String tses, int userit, String usename, hibernate.HibernateUtil hiberne){
        status = fstat;
        session = tses;
        userID = userit;
        username = usename;
        database.classes.UserLogg uselog =  hibernate.HibernateStatic.getUserLogg(session, hiberne);
        //System.out.println(session);
        if(uselog != null){
            userID = uselog.getuserID();
            database.classes.Player user =  hibernate.HibernateStatic.getUser(userID, hiberne);
            if(user != null){
                status = user.getRettighet();
                username = user.getBrukernavn();
                //System.out.println("status blir " + status);
            }
            else{
                //System.out.println("user er null");
            }
        }
    }
    
    
    
    public database.classes.Player getLogUser(hibernate.HibernateUtil hiberne){
        return  hibernate.HibernateStatic.getUser(userID, hiberne);
    }
    
    public boolean loggedIn(){
        if(status > 0){
            return true;
        }
        else{
            return false;
        }
    }
    
    public int getStatus(){
        return status;
    }
    
    public void logInUser(int fstat,int userit, String usename){
        status = fstat;
        userID = userit;
        username = usename;   
    }
    public String getSession(){
        return session;
    }
    
    public String logout(hibernate.HibernateUtil hiberne){
        database.classes.UserLogg uselog =  hibernate.HibernateStatic.getUserLogg(session, hiberne);
        hiberne.deleteItem(uselog);
        return "LOGOUT";
    }
    
    public void logObjectIn(database.classes.Player bruker, hibernate.HibernateUtil hiber, org.hibernate.Session sesjon){
    
            logInUser(bruker.getRettighet(), bruker.getId(), bruker.getBrukernavn());
            sesjon.beginTransaction();
            database.classes.UserLogg bruklogg = new database.classes.UserLogg(getSession(), bruker.getId());
                    //retur += "her inne jeg er " + sesjonen.getSession() + " " + bruker.getId();
            sesjon.save(bruklogg);
            sesjon.getTransaction().commit();    
    }
    
    public database.classes.Player finnBruker(hibernate.HibernateUtil hiber, String hql, String retur){
        database.classes.Player bruker = null;
        try{
                org.hibernate.Session sesjon = hiber.Sestart();                
                Query sporring = sesjon.createQuery(hql);
                List resultat = sporring.list();
                if(resultat.size() == 1){
                    bruker = (database.classes.Player) resultat.get(0);
                }
                else if(resultat.size() == 0){
                    System.out.println("bruer ikke funet");
                }
                else{
                    System.out.println("Multiple feil i login");
                }
        }catch(Exception e){ System.out.println(e.toString());}
        return bruker;
    }
    
    public String logItIn(hibernate.HibernateUtil hiber, String hql, String retur){
  
        //retur = "FROM Users U WHERE U.brukernavn = '" + brukernavn + "' AND U.passord = '" + passord + "';" + resultat.size();
         database.classes.Player bruker = finnBruker(hiber, hql, retur);
         if(bruker != null){
              logObjectIn(bruker, hiber, hiber.Sestart());
              retur = "SUCCESS";
        }
        else{
             retur = "FAILURE";
        }
   
        return retur;
    }
    
    public String autenticateUserFacebook(User facebookbruker, hibernate.HibernateUtil hiber){
        String retur = "ERROR";
        
        if(!loggedIn()){
               retur = logItIn(hiber, "FROM Player U WHERE U.facebookId = '" + facebookbruker.getId() + "'", retur);
               if(retur.equals("FAILURE")){
                   database.classes.Player funnet = facebook.FacebookFetcher.findUserWithName(facebookbruker, hiber, this);
                   if(funnet != null){
                       funnet.setFacebookId(facebookbruker.getId());
                       hiber.updateItem(funnet);
                       retur = logItIn(hiber, "FROM Player U WHERE U.facebookId = '" + facebookbruker.getId() + "'", retur);
                       
                   }
                   else{
                       System.out.println("Face credentials: " + facebookbruker.getName() + " " + facebookbruker.getId());
                       database.classes.FacebookFailLogin failinfo = new database.classes.FacebookFailLogin(facebookbruker.getName(), facebookbruker.getId());
                       hiber.addItem(failinfo);
                   }
               }
               database.classes.FacebookFailLogin failinfo = new database.classes.FacebookFailLogin(facebookbruker.getName(), facebookbruker.getId());
               hiber.addItem(failinfo);
        }
        return retur;
    }
    
    public String autenticateUser(String brukernavn, String passord, hibernate.HibernateUtil hiberne){
        String retur = "ERROR";
        retur = logItIn(hiberne, "FROM Player U WHERE U.brukernavn = '" + brukernavn + "' AND U.passord = '" + passord + "'", retur);
        
        return retur;
    }    
    
    
}

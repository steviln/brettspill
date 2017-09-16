/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facebook;

import com.restfb.DefaultFacebookClient;
import com.restfb.types.User;
import java.util.StringTokenizer;

/**
 *
 * @author Steinar
 */
public class FacebookFetcher {
    
    public static User getUserData(String accessToken){
        DefaultFacebookClient faceklient = new DefaultFacebookClient(accessToken);
        User bruker = faceklient.fetchObject("me", User.class);
        return bruker;
    }
    
    public static database.classes.Player findUserWithName(User facebookbruker,hibernate.HibernateUtil hiber, session.Session sesjonen){
        String bruknavn = facebookbruker.getName();
        //bruknavn = "Trond Arvid Røise";
        String[] navnet = bruknavn.split(" ");
      
        int lengde = navnet.length - 1;
        String hqldel = "";
        for(int x = 0; x < lengde; x++){
            String fornavn = navnet[0];
            String etternavn = navnet[lengde];
            for(int y = 1; y < (x + 1); y++){
                fornavn += (" " + navnet[y]);
            }
            for(int y = (x + 1); y < lengde; y++){
                etternavn = navnet[y] + " " + etternavn;
            }  
            if(x > 0)
                hqldel += " OR ";
            hqldel += "(U.fornavn = '" + fornavn + "' AND U.etternavn = '" + etternavn + "')";
        }
        database.classes.Player bruker = sesjonen.finnBruker(hiber, "FROM Player U WHERE " + hqldel, "");

        return bruker;
    }
    
}

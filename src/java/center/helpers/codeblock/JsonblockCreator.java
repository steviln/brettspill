/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center.helpers.codeblock;
import hibernate.HibernateUtil;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.List;
import java.util.Collections;
import java.util.Calendar;

/**
 *
 * @author Steinar
 */
public class JsonblockCreator {
    
    public static JSONObject getPlayersForPrevList(JSONObject modell, session.Session sesjon, hibernate.HibernateUtil hiber){
        JSONArray spillerne = new JSONArray();
        List<Object[]> liste = hibernate.HibernateStatic.getPresecList(0, hiber);
        int counter = 0;
        for(Object[] raden : liste){
            //System.out.println("en " + raden[0] + ":" + raden[1] + ":" + raden[2]);
            JSONObject spiler = new JSONObject();
            spiler.put("fornavn", raden[0]);
            spiler.put("etternavn", raden[1]);
            spiler.put("antspill", raden[2]);
            spiler.put("order", counter);
            spiler.put("divclass","unselec");
            spiler.put("id", raden[3]);
            spillerne.add(spiler);
            counter++;
        }
        modell.put("setspillere", spillerne);
        return modell;
    }
    
    public static int nullCheckInteger(Object checkme){
        int retur = 0;
        try{ retur = Integer.parseInt("" + checkme);  }catch(Exception e){}
        
        return retur;
    }
    
    public static JSONArray getGameRanks(hibernate.HibernateUtil hiber, database.classes.Player spilleren){
        
        String[] rangs = {"Ikke rangert","Ønsker absolutt ikke å spille dette spillet","Vil aller helst ikke spille dette spillet","Kan til nød spille dette spillet","Helt greit spill å spille","Kan gjerne spille dette spillet","Har veldig lyst til å spille dette spillet","Favorittspill"};
        
        JSONArray rankeringer = new JSONArray();
        JSONObject current = null;
        JSONArray gams = null;
        int teller = -1;
        List<Object[]> lister = hibernate.HibernateStatic.getHQLresult("SELECT ga.navn, ga.id, ra.rating, ra.owner FROM Game ga INNER JOIN ga.rangeringer ra WHERE ra.spiller = " + spilleren.getId() + " ORDER BY ra.rating", hiber);
        for(Object[] ob : lister){
            int rangering = nullCheckInteger(ob[2]);
            if(rangering != teller){
                teller = rangering;
                if(current != null){
                    current.put("spill", gams);
                    rankeringer.add(current);
                }
                current = new JSONObject();
                gams = new JSONArray();
                current.put("navn", rangs[teller]);
            }
            JSONObject gam = new JSONObject();
            String navnet = "" + ob[0];
            if(nullCheckInteger(ob[3]) == 1)
                navnet += " (eier)";
            gam.put("navn", navnet);
            gam.put("id", ob[1]);
            gams.add(gam);
            
        }
        if(current != null){
             current.put("spill", gams);
             rankeringer.add(current);
        }
        return rankeringer;
    }
    
}

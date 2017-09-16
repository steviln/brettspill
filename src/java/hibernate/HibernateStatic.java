/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate;
import database.classes.Game;
import database.classes.Player;
import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;


/**
 *
 * @author Steinar
 */
public class HibernateStatic {
    
    public static List<Object[]> getLatestGamesAll(int gameID, int maxres, HibernateUtil hiber){
        List<Object[]> retur = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Gamesession gs INNER JOIN gs.spillet ga ORDER BY gs.dato DESC";
            if(gameID > 0){
                hql = "FROM Gamesession gs INNER JOIN gs.spillet ga WHERE gs.spillet = " + gameID + " ORDER BY gs.dato DESC";
            }
            Query sporring = sesjon.createQuery(hql);
            sporring.setMaxResults(maxres);
            retur = sporring.list();
        }catch(Exception e){  }
        return retur;    
    }
    
    public static database.classes.Daycalendar[] getWeekdayList(int playerID, HibernateUtil hiber){
        database.classes.Daycalendar[] dager = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Daycalendar da WHERE da.spiller = " + playerID + " ORDER BY da.dag";
            List<database.classes.Daycalendar> returen = (List<database.classes.Daycalendar>) sesjon.createQuery(hql).list();
            System.out.println("storrelse " + returen.size());
            dager = (database.classes.Daycalendar[]) returen.toArray(new database.classes.Daycalendar[0]);
        }catch(Exception e){ System.out.println("gikk galt her " + e.toString());}
        return dager;    
    }
    
    public static database.classes.Daycalendar getCreateDaycalendar(HibernateUtil hiber, int day, int playerID){
        database.classes.Daycalendar kal = null;
        try{
            Session sesjon = hiber.Sestart();
            //kal = (database.classes.Daycalendar) sesjon.get(database.classes.Daycalendar.class, sessionID);
            List<database.classes.Daycalendar> returen = (List<database.classes.Daycalendar>) sesjon.createQuery("FROM Daycalendar da WHERE da.spiller = " + playerID + " AND da.dag = " + day).list();
            if(returen.size() == 0){
                kal = new database.classes.Daycalendar(getUser(playerID, hiber), day, 3);
            }
            else{
                kal = (database.classes.Daycalendar) returen.get(0);
            }
        }catch(Exception e){ System.out.println(e.toString()); }
        return kal;
    }
    
    public static List<Object[]> getPresecList(int playerID, HibernateUtil hiber){
        List<Object[]> returen = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "SELECT pl.fornavn, pl.etternavn, COUNT(deltak), pl.id FROM Player pl JOIN pl.deltakelser deltak GROUP BY pl.id ORDER BY pl.fornavn, pl.etternavn";
            returen = sesjon.createQuery(hql).list();
        }catch(Exception e){ System.out.println(e.toString());}
        return returen;
    }
    
    public static List<Object[]> getHQLresult(String hql, HibernateUtil hiber){
         List<Object[]> returen = null;
        try{
            Session sesjon = hiber.Sestart();
            returen = sesjon.createQuery(hql).list();
        }catch(Exception e){ System.out.println(e.toString());}
        return returen;   
    }
    
    public static database.classes.Selskap getCompany(int id, HibernateUtil hiber){
        database.classes.Selskap selskapet = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Selskap se LEFT JOIN FETCH se.spill sp LEFT JOIN FETCH sp.sesjoner WHERE se.id = " + id;
            Query sporring = sesjon.createQuery(hql);
            if(sporring.list().size() == 0){
                selskapet = new database.classes.Selskap("", 0);
            }
            else{
                selskapet = (database.classes.Selskap) sporring.list().get(0);
            }
        }catch(Exception e){ System.out.println("feil inne " + e.toString()); }
        return selskapet;    
    }
    
    public static List<database.classes.Selskap> getCompanies( HibernateUtil hiber){
        List returlist = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "SELECT DISTINCT S FROM Selskap S LEFT JOIN FETCH S.spill P LEFT JOIN FETCH P.sesjoner ORDER BY S.navn";
            Query sporring = sesjon.createQuery(hql);
            returlist = sporring.list();
        }catch(Exception e){   }
        return returlist;  
    }
    
    public static database.classes.Player getUser(int userID, HibernateUtil hiber){
        database.classes.Player userData = null;
        try{
            
            Session sesjon = hiber.Sestart();
           
            userData = (database.classes.Player) sesjon.get(database.classes.Player.class, userID);
          
            if(userData == null){
                //System.out.println("brukerdata er null");
                userData = new database.classes.Player("", "", "", "", "", "", 0, 0,0);
            }
            else{
               // System.out.println("brukerdata er ikke null");
            }
        }catch(Exception e){  System.out.println("fant ikke bruker " + e.toString()); }
        
        return userData;
    }
    
    public static List<database.classes.Player> getUsers(HibernateUtil hiber){
        List<database.classes.Player> userData = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Player pl ORDER BY pl.etternavn, pl.fornavn";
            Query sporring = sesjon.createQuery(hql);
            userData = sporring.list();
        }catch(Exception e){  System.out.println("brukerliste feil " + e.toString()); }
        
        return userData;
    }
    
    public static database.classes.Rating getRelevantRating(int spillerID, int gameID, hibernate.HibernateUtil hiber){
        database.classes.Rating returit = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Rating ra WHERE ra.spiller = " + spillerID + " AND ra.game = " + gameID;
            Query sporring = sesjon.createQuery(hql);
            if(sporring.list().size() > 0){
                returit = (database.classes.Rating) sporring.list().get(0);
            }
            else{
                returit = new database.classes.Rating(null, getGame(gameID, hiber), 0, 0, HibernateStatic.getUser(spillerID, hiber));
            }
        }catch(Exception e){ System.out.println("ratingfinnfeil");}
        return returit;
    }  
    
    public static database.classes.UserLogg getUserLogg(String sessionID, HibernateUtil hiber){
        database.classes.UserLogg usedata = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM UserLogg UL WHERE UL.sessionID = '" + sessionID + "'";
            Query sporring = sesjon.createQuery(hql);
            List resultat = sporring.list();
            if(resultat.size() > 0){
                usedata = (database.classes.UserLogg) resultat.get(0);
            }
        }catch(Exception e){
        }
        
        return usedata;
    } 
    
    public static List<Object[]> getPlayerGamesCross(HibernateUtil hiber){
        List<Object[]> retur = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Player pl INNER JOIN pl.deltakelser del JOIN del.spillsesjon sp GROUP BY pl.id";
            Query sporring = sesjon.createQuery(hql);
            retur = sporring.list();
        }catch(Exception e){  }
        return retur;
    }
    
    public static List<Object[]> getPlayerGamesCrossGame(int gid, HibernateUtil hiber){
        List<Object[]> retur = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Player pl JOIN pl.deltakelser del JOIN del.spillsesjon sp WHERE sp.spillet = " + gid + " ORDER BY pl.id, del.id";
            Query sporring = sesjon.createQuery(hql);
            retur = sporring.list();
        }catch(Exception e){  }
        return retur;
    }  
    
    public static List<Object[]> getFactionGamesCrossGame(int gid, HibernateUtil hiber){
    //public static String getFactionGamesCrossGame(int gid){
        List<Object[]> retur = null; 
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Faction fc JOIN fc.deltakelser del JOIN del.spillsesjon sp WHERE fc.spillet = " + gid + " ORDER BY fc.id, del.id";
            Query sporring = sesjon.createQuery(hql);
            retur = sporring.list();
        }catch(Exception e){  }
        return retur;
    }     
    
    public static List<database.classes.Game> getGamesList(HibernateUtil hiber){
        List<database.classes.Game> spill = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Game ga ORDER BY ga.navn";
            Query sporring = sesjon.createQuery(hql);
            spill = sporring.list();
        }catch(Exception e){}
        return spill;
    }
    
    //public static database.classes.Game getGame(int gid){
    public static database.classes.Game getGame(int gid, HibernateUtil hiber){
        database.classes.Game spillet = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Game ga WHERE ga.id = " + gid;
            Query sporring = sesjon.createQuery(hql);
            
            if(sporring.list().size() == 0){
                spillet = new database.classes.Game("", "", 0, null, 0);
            }
            else{
                spillet = (database.classes.Game) sporring.list().get(0);
            }
        }catch(Exception e){  } 
        return spillet;
    }
    
    public static database.classes.Game getFullGame(int gid, HibernateUtil hiber){
        database.classes.Game spillet = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Game ga LEFT JOIN FETCH ga.sesjoner gs LEFT JOIN FETCH gs.deltakelser de LEFT JOIN FETCH de.spiller sp LEFT JOIN FETCH de.fraksjonId fr WHERE ga.id = " + gid;
            Query sporring = sesjon.createQuery(hql);

            spillet = (database.classes.Game) sporring.list().get(0);
        
        }catch(Exception e){ System.out.println("e to feil " + e.toString()); } 
        return spillet;
    } 
    
    public static List<database.classes.Game> getFullGameRanking(HibernateUtil hiber){
        List<database.classes.Game> spillet = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "SELECT DISTINCT ga FROM Game ga LEFT JOIN FETCH ga.sesjoner gs";
            Query sporring = sesjon.createQuery(hql); 

            spillet = (List<database.classes.Game>) sporring.list();
        
        }catch(Exception e){ System.out.println("e to feil " + e.toString()); } 
        return spillet;
    }     
    
    
     public static database.classes.Gamesession getGameSesssion(int sesjonID, database.classes.Game spillet, session.Session sesj, HibernateUtil hiber){
        database.classes.Gamesession sesjonen = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Gamesession gs WHERE gs.id = " + sesjonID;
            Query sporring = sesjon.createQuery(hql);
            if(sporring.list().size() == 0 && spillet != null){
                sesjonen = new database.classes.Gamesession(spillet, new Date(), null, sesj.getLogUser(hiber), 0);
            }
            else if(spillet == null){ sesjonen = null; }
            else{
                sesjonen = (database.classes.Gamesession) sporring.list().get(0);
            }
        }catch(Exception e){  } 
        return sesjonen;
    }  
     
     public static database.classes.Gamesession getFullGameSesssion(int sesjonID, database.classes.Game spillet, HibernateUtil hiber){
        database.classes.Gamesession sesjonen = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Gamesession gs LEFT JOIN FETCH gs.spillet LEFT JOIN FETCH gs.deltakelser dt LEFT JOIN FETCH dt.spiller sp LEFT JOIN FETCH dt.fraksjonId fr WHERE gs.id = " + sesjonID;
            Query sporring = sesjon.createQuery(hql);
 
            sesjonen = (database.classes.Gamesession) sporring.list().get(0);

        }catch(Exception e){ System.out.println(e.toString()); } 
        return sesjonen;
    } 
     
    public static List<database.classes.Gamesession> getPlayerGameSesssions(int spillerID, HibernateUtil hiber){
        List<database.classes.Gamesession> sesjoner = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "SELECT DISTINCT gs FROM Participation pa LEFT JOIN pa.spillsesjon gs LEFT JOIN FETCH gs.spillet LEFT JOIN FETCH gs.deltakelser dt LEFT JOIN FETCH dt.spiller sp LEFT JOIN FETCH dt.fraksjonId fr WHERE pa.spiller = " + spillerID + " ORDER BY gs.dato";
            Query sporring = sesjon.createQuery(hql);
 
            sesjoner = (List<database.classes.Gamesession>) sporring.list();
            

        }catch(Exception e){ System.out.println(e.toString()); } 
        return sesjoner;
    }
    
    public static List<database.classes.Gamesession> getTwoPlayerGameSesssions(int spillerID, int compareID, HibernateUtil hiber){
        List<database.classes.Gamesession> sesjoner = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "SELECT DISTINCT gs FROM Participation pa LEFT JOIN pa.spillsesjon gs LEFT JOIN FETCH gs.spillet LEFT JOIN FETCH gs.deltakelser dt LEFT JOIN FETCH dt.spiller sp LEFT JOIN FETCH dt.fraksjonId fr WHERE pa.spiller = " + spillerID + " OR pa.spiller = " + compareID + " ORDER BY gs.spillet";
            Query sporring = sesjon.createQuery(hql);
 
            sesjoner = (List<database.classes.Gamesession>) sporring.list();
            

        }catch(Exception e){ System.out.println(e.toString()); } 
        return sesjoner;
    }
    
     public static database.classes.Faction getFaction(center.classes.AjaxCommand command, HibernateUtil hiber){
        database.classes.Faction fraksjonen = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Faction fa WHERE fa.id = " + command.id;
            Query sporring = sesjon.createQuery(hql);
            database.classes.Game spillet = getGame(command.subid, hiber);
            if(sporring.list().size() == 0){
                fraksjonen = new database.classes.Faction("",spillet , 0);
            }
            else{
                fraksjonen = (database.classes.Faction) sporring.list().get(0);
            }
        }catch(Exception e){  } 
        return fraksjonen;
    }
     
     public static database.classes.Scenario getScenario(center.classes.AjaxCommand command, HibernateUtil hiber){
        database.classes.Scenario fraksjonen = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Scenario fa WHERE fa.id = " + command.id;
            Query sporring = sesjon.createQuery(hql);
            database.classes.Game spillet = getGame(command.subid, hiber);
            if(sporring.list().size() == 0){
                fraksjonen = new database.classes.Scenario("",spillet , 0);
            }
            else{
                fraksjonen = (database.classes.Scenario) sporring.list().get(0);
            }
        }catch(Exception e){  } 
        return fraksjonen;
    }    
     

    
    public static List<database.classes.Rating> getRelevantRatings(int gameID, HibernateUtil hiber){
        List<database.classes.Rating> returit = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Rating ra LEFT JOIN FETCH ra.spiller sp WHERE ra.game = " + gameID + " ORDER BY ra.rating";
            Query sporring = sesjon.createQuery(hql);
            returit = sporring.list();


        }catch(Exception e){ System.out.println("ratingfinnfeil");}
        return returit;
    }   
    
    public static List<database.classes.Game> getRelevantGameRatings(int playerID, HibernateUtil hiber){
        List<database.classes.Game> returit = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Game ga LEFT JOIN FETCH ga.rangeringer ra LEFT JOIN FETCH ra.spiller sp GROUP BY ga";
            Query sporring = sesjon.createQuery(hql);
            returit = sporring.list();


        }catch(Exception e){ System.out.println("ratingfinnfeil " + e.toString());}
        return returit;
    }     
     
     public static database.classes.Faction getFactionOrNull(int facid, HibernateUtil hiber){
        database.classes.Faction fraksjonen = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Faction fa WHERE fa.id = " + facid;
            Query sporring = sesjon.createQuery(hql);
            if(sporring.list().size() == 0 || facid <= 0){
                fraksjonen = null;
            }
            else{
                fraksjonen = (database.classes.Faction) sporring.list().get(0);
            }
        }catch(Exception e){  } 
        return fraksjonen;
    }     
     
     public static List<database.classes.Faction> getFactions(int gameID, HibernateUtil hiber){
        List<database.classes.Faction> fraksjonen = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Faction fa JOIN fa.spillet ga WHERE ga.id  = " + gameID;
            Query sporring = sesjon.createQuery(hql);
            fraksjonen = center.helpers.ResultOrganizer.hentFraksjoner(sporring.list());    
        }catch(Exception e){ System.out.println("feil inne " + e.toString()); } 
        return fraksjonen;
    }  
     
     
     public static List<database.classes.Scenario> getScenarios(int gameID, HibernateUtil hiber){
        List<database.classes.Scenario> fraksjonen = null;
        try{
            Session sesjon = hiber.Sestart();
            String hql = "FROM Scenario fa JOIN fa.spillet ga WHERE ga.id  = " + gameID;
            Query sporring = sesjon.createQuery(hql);
            fraksjonen = center.helpers.ResultOrganizer.hentScenarioer(sporring.list());    
        }catch(Exception e){ System.out.println("feil inne " + e.toString()); } 
        return fraksjonen;
    }    
    
}

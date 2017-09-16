/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center.helpers;
import database.classes.Player;
import hibernate.HibernateUtil;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.text.DateFormat;

/**
 *
 * @author Steinar
 */
public class SaveAndEdit {
    
    public static String createUpdateGame(hibernate.HibernateUtil hiberne, HttpServletRequest request){
        database.classes.Game editgame = new database.classes.Game(request.getParameter("navn"), request.getParameter("boardgamegeekID"), Integer.parseInt(request.getParameter("spillerantall")), hibernate.HibernateStatic.getCompany(Integer.parseInt(request.getParameter("selskapID")), hiberne), Integer.parseInt(request.getParameter("id")));
        //System.out.println("den id er " + editgame.getId());
        if(editgame.getId() == 0){
            editgame = (database.classes.Game) hiberne.addItem(editgame);
        }
        else{
            //System.out.println("nytt spillerantall er " + editgame.getAntall());
            database.classes.Game updategame = hibernate.HibernateStatic.getGame(editgame.getId(), hiberne);
            updategame.setAntall(editgame.getAntall());
            updategame.setNavn(editgame.getNavn());
            //updategame.setSelskap();
            editgame = (database.classes.Game) hiberne.updateItem(updategame);
        }
        return editgame.getId().toString();
    }
    
    public static String changeDayCalendar(hibernate.HibernateUtil hiberne, HttpServletRequest request, session.Session sesjonen){
       int spillerID = Integer.parseInt(request.getParameter("spillerID"));
       if(sesjonen.loggedIn() && sesjonen.getLogUser(hiberne).getId() == spillerID){
            database.classes.Daycalendar dagkal = hibernate.HibernateStatic.getCreateDaycalendar(hiberne, Integer.parseInt(request.getParameter("dag")), spillerID);
            dagkal.setStatus(Integer.parseInt(request.getParameter("status")));
            if(dagkal.getId() == null){
                hiberne.addItem(dagkal);
            }
            else{
                hiberne.updateItem(dagkal);
            }
            return dagkal.getDag() + ":" + dagkal.getstatus();
       }else{ return ""; }
    }
    
    public static String createUpdateFaction(hibernate.HibernateUtil hiberne, HttpServletRequest request, center.classes.AjaxCommand command){
        int spillID = Integer.parseInt(request.getParameter("spillID"));
        database.classes.Game partengame = hibernate.HibernateStatic.getGame(spillID, hiberne);
        //System.out.println(spillID);
        database.classes.Faction editgame = new database.classes.Faction(request.getParameter("navn"),partengame, Integer.parseInt(request.getParameter("id")));
        if(editgame.getId() == 0){
            editgame = (database.classes.Faction) hiberne.addItem(editgame);
        }
        else{
            database.classes.Faction updategame = hibernate.HibernateStatic.getFaction(new center.classes.AjaxCommand(editgame.getId()), hiberne);
            updategame.setNavn(editgame.getNavn());
            editgame = (database.classes.Faction) hiberne.updateItem(updategame);
        }
        return partengame.getId().toString();
    } 
    
    public static String createUpdateScenario(hibernate.HibernateUtil hiberne, HttpServletRequest request, center.classes.AjaxCommand command){
        int spillID = Integer.parseInt(request.getParameter("spillID"));
        database.classes.Game partengame = hibernate.HibernateStatic.getGame(spillID, hiberne);
        //System.out.println(spillID);
        database.classes.Scenario editgame = new database.classes.Scenario(request.getParameter("navn"),partengame, Integer.parseInt(request.getParameter("id")));
        if(editgame.getId() == 0){
            editgame = (database.classes.Scenario) hiberne.addItem(editgame);
        }
        else{
            database.classes.Scenario updategame = hibernate.HibernateStatic.getScenario(new center.classes.AjaxCommand(editgame.getId()), hiberne);
            updategame.setNavn(editgame.getNavn());
            editgame = (database.classes.Scenario) hiberne.updateItem(updategame);
        }
        return partengame.getId().toString();
    }    
    
    public static Date getStringToDate(String strengdato){
        Date returdato = new Date();
        String[] datodel = strengdato.split(":");
        SimpleDateFormat dformat= new SimpleDateFormat("dd/M/yyyy");
        try{
            int midato = Integer.parseInt(datodel[1]);
            returdato = dformat.parse(datodel[0] + "/" + midato + "/" + datodel[2]);
        }catch(Exception e){ System.out.println(e.toString()); }
        return returdato;
    }
    
    public static String createAndUpdateSession(hibernate.HibernateUtil hiberne, HttpServletRequest request, session.Session sess){
        
        Map<String, String[]> parameters = request.getParameterMap();
        int spillID = Integer.parseInt(parameters.get("spillID")[0]);
        database.classes.Game spillet = hibernate.HibernateStatic.getGame(spillID, hiberne);
        int scenarioID = Integer.parseInt(parameters.get("scenarioID")[0]);
        database.classes.Scenario scenario = hibernate.HibernateStatic.getScenario(new center.classes.AjaxCommand(scenarioID), hiberne);
        if(scenarioID == 0)
            scenario = null;
        int sesjonID = Integer.parseInt(parameters.get("id")[0]);
        Date dato = getStringToDate(parameters.get("dato")[0]);
        database.classes.Gamesession sesjonen = hibernate.HibernateStatic.getGameSesssion(sesjonID, spillet, sess, hiberne);
        sesjonen.setDato(dato);
        sesjonen.setScenarioId(scenario);
       
        if(sesjonen.getId() == 0){
            hiberne.addItem(sesjonen);
        }
        else{
            hiberne.updateItem(sesjonen);
        }
       
        
        int maxdeltaker = spillet.getAntall();
        database.classes.Participation[] dels = new database.classes.Participation[maxdeltaker];
        for(int x = 0; x < maxdeltaker; x++){
            //System.out.println("starter " + x);
            String[] deltaket = {"deltakelser[" + x + "][id]","deltakelser[" + x + "][spillerID]","deltakelser[" + x + "][poeng]","deltakelser[" + x + "][posisjon]","deltakelser[" + x + "][fraksjonID]"};
            if(parameters.containsKey(deltaket[0])){
                int nyID = Integer.parseInt(parameters.get(deltaket[0])[0]);
                int posisjon = Integer.parseInt(parameters.get(deltaket[3])[0]);
                int poeng = Integer.parseInt(parameters.get(deltaket[2])[0]);
                int spillerID = Integer.parseInt(parameters.get(deltaket[1])[0]);
                int fraksjonID = Integer.parseInt(parameters.get(deltaket[4])[0]);
                if(spillerID > 0){
                    database.classes.Player spiller = hibernate.HibernateStatic.getUser(spillerID, hiberne);
                    database.classes.Faction fraksjon = hibernate.HibernateStatic.getFactionOrNull(fraksjonID, hiberne);
                    dels[x] = new database.classes.Participation(spiller, sesjonen, (float) poeng, posisjon, nyID, fraksjon);
                }
                else{
                    dels[x] = null;
                }
            }
            else{
                dels[x] = null;
            }
     
            try{
               
            if(dels[x] == null){
        
            }
            else if(dels[x].getId() == null || dels[x].getId() == 0){
                hiberne.addItem(dels[x]);
            }
            else{
                hiberne.updateItem(dels[x]);
            }
            }catch(Exception e){ System.out.println(e.toString());}
        }
       
        

        //returstreng = returstreng + " NULL " + parameters.get("deltakelser[2][posisjon]")[0];
        return spillet.getId().toString();
    }
    
    public static String createCompany(hibernate.HibernateUtil hiber,HttpServletRequest request){
        database.classes.Selskap selskapet = new database.classes.Selskap(request.getParameter("first"), null);
        selskapet = (database.classes.Selskap) hiber.addItem(selskapet);
        return selskapet.getId().toString() + ";" + selskapet.getNavn();
    }
    
    public static String rankGame(session.Session sesjonen, hibernate.HibernateUtil hiber,HttpServletRequest request){
        int gameID = Integer.parseInt(request.getParameter("id"));
        int playerID = sesjonen.getLogUser(hiber).getId();
        int returID = -1;
        if(playerID > 0 && gameID > 0){
            database.classes.Rating minrating = hibernate.HibernateStatic.getRelevantRating(playerID, gameID, hiber);
            minrating.setRating(Integer.parseInt(request.getParameter("ranken")));
            if(minrating.getId() == null)
                hiber.addItem(minrating);
            else
                hiber.updateItem(minrating);
            returID = minrating.getId();
        }
        return "" + returID;
    }
    
    public static String setGameOwner(session.Session sesjonen, hibernate.HibernateUtil hiber,HttpServletRequest request){
        int gameID = Integer.parseInt(request.getParameter("id"));
        int playerID = sesjonen.getLogUser(hiber).getId();
        int returID = -1;
        //System.out.println(request.getParameter("eier"));
        if(playerID > 0 && gameID > 0){
            database.classes.Rating minrating = hibernate.HibernateStatic.getRelevantRating(playerID, gameID, hiber);
            minrating.setOwner(Integer.parseInt(request.getParameter("eier")));
            //System.out.println(request.getParameter("eier") + " " + minrating.getOwner());
            if(minrating.getId() == null)
                hiber.addItem(minrating);
            else
                hiber.updateItem(minrating);
            returID = minrating.getId();
        }
        return "" + returID;
    }    
    
    public static String createNewPlayer(hibernate.HibernateUtil hiber,HttpServletRequest request){

        database.classes.Player spilleren = new database.classes.Player(inout.NetGetConnection.backStreng(request.getParameter("first").toString()), inout.NetGetConnection.backStreng(request.getParameter("second").toString()), "", "", "", "", 0, null, 0);
        spilleren = (database.classes.Player) hiber.addItem(spilleren);
        return "" + spilleren.getId() + ";" + spilleren.getFornavn() + " " + spilleren.getEtternavn() + ";" + request.getParameter("redigerid");
    }
    
    public static String createUpdateUser(hibernate.HibernateUtil hiber,HttpServletRequest request,session.Session sesjonen){
        database.classes.Player spilleren = null;
        
        database.classes.Player loggeinn = sesjonen.getLogUser(hiber);
        int spillerID = Integer.parseInt(request.getParameter("id"));
        String fornavn = inout.NetGetConnection.backStreng(request.getParameter("fornavn"));
        String etternavn = inout.NetGetConnection.backStreng(request.getParameter("etternavn"));
        String epost = inout.NetGetConnection.backStreng(request.getParameter("epost"));
        String brukernavn = inout.NetGetConnection.backStreng(request.getParameter("brukernavn"));
        String passord = inout.NetGetConnection.backStreng(request.getParameter("passord"));
        String passordto = inout.NetGetConnection.backStreng(request.getParameter("passordto"));

        if(!passord.equals(passordto) || passord.trim().equals("")){
            passord = "";
        }
        int status = 0;
        int ignore = 0;
        //System.out.println("ignore is " + ignore);
        try{ status = Integer.parseInt(request.getParameter("status")); }catch(Exception e){}
        try{ ignore = Integer.parseInt(request.getParameter("ignore")); }catch(Exception e){}
        if(status > 10 && loggeinn.getRettighet() < 20){
            status = 0;
        }
        //System.out.println("ingore is " + ignore);
        spilleren = new database.classes.Player(fornavn, etternavn, "", epost, brukernavn, passord, status, spillerID, ignore);
        if(spillerID == 0){
            spilleren.setId(null);
            spilleren = (database.classes.Player) hiber.addItem(spilleren);
            return spilleren.getId().toString();
        }
        else if(spillerID > 0){
            database.classes.Player gammel = hibernate.HibernateStatic.getUser(spillerID, hiber);
            if(!passord.equals("") && (loggeinn.getRettighet() >= 20 || loggeinn.getId() == spillerID)){
                gammel.setPassord(passord);
            }
            if(loggeinn.getRettighet() >= 20 || loggeinn.getId() == spillerID){
                gammel.setBrukernavn(brukernavn);
            }
            if(loggeinn.getRettighet() >= 20){
                gammel.setRettighet(status);
            }
            gammel.setEtternavn(etternavn);
            gammel.setFornavn(fornavn);
            gammel.setEpost(epost);
            gammel.setIgnore(ignore);
            hiber.updateItem(gammel);
            return "" + spillerID;
        }
        else{
            return "0";
        }
    }
    
    static public String lagKalenderZone(hibernate.HibernateUtil hiber,HttpServletRequest request,session.Session sesjonen){
        DateFormat datoformat = new SimpleDateFormat("dd/MM/yyyy");
        Date startdato = null, sluttdato = null;
        try{
            startdato = datoformat.parse(request.getParameter("fradato"));
            sluttdato = datoformat.parse(request.getParameter("tildato"));
            if(startdato.after(sluttdato)){
                Date tempdato = sluttdato;
                sluttdato = startdato;
                startdato = tempdato;
            }
        }catch(Exception e){ System.out.println(e.toString());}
        
        String sluttstreng = "ERROR";
        if(startdato != null && sluttdato != null){
            database.classes.Monthcalendar nymaned = new database.classes.Monthcalendar(null, startdato, sluttdato, sesjonen.getLogUser(hiber), Integer.parseInt(request.getParameter("datoSetID")));
            hiber.addItem(nymaned);
            sluttstreng = "" + Math.min(Integer.parseInt(request.getParameter("forsteklikkID")),Integer.parseInt(request.getParameter("andreklikkID"))) + ":" + Math.max(Integer.parseInt(request.getParameter("forsteklikkID")),Integer.parseInt(request.getParameter("andreklikkID"))) + ":" + nymaned.getstatus();
        }
        return sluttstreng;
    }
    
}

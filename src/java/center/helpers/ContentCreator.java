/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center.helpers;
import java.util.ArrayList;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.List;
import java.util.Collections;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Steinar
 */
public class ContentCreator {
    
    public static String skapMeny(session.Session sesjon, hibernate.HibernateUtil hiber){
        JSONArray meny = new JSONArray();
        meny.add(new center.jsons.MenuItem("Hjem", "", 0));
        meny.add(new center.jsons.MenuItem("Spill", "Boardgames", 0));
        meny.add(new center.jsons.MenuItem("Spillinger", "sesjonsliste", 0));
        meny.add(new center.jsons.MenuItem("Spillere", "spillerliste", 0));
        meny.add(new center.jsons.MenuItem("Selskap", "Selskap", 0));
        if(sesjon.getStatus() > 0){
            meny.add(new center.jsons.MenuItem("Profil", "RedigerBruker/" + sesjon.getLogUser(hiber).getId(), 0));
        }
        if(sesjon.getStatus() > 0){
            meny.add(new center.jsons.MenuItem("Log out", "Logout", 0));
        }
        else{
            meny.add(new center.jsons.MenuItem("Login", "Login", 0));
        }
        
        JSONObject menyholder = new JSONObject();
        menyholder.put("meny", meny);
        menyholder.put("status", sesjon.getStatus());
        
        
        return menyholder.toString();
    }
    
    public static String skapGamesList(session.Session sesjon, hibernate.HibernateUtil hiber){
        List<database.classes.Game> spill =  hibernate.HibernateStatic.getGamesList(hiber);
        JSONObject holder = new JSONObject();
        JSONArray returen = new JSONArray();
        for(database.classes.Game spillet : spill){
            new center.jsons.GameJsonStandard(spillet, returen, sesjon);
        }
        holder.put("listespill",returen);
        if(sesjon.loggedIn()){
            holder.put("redlink","#redigerSpill/0");
            holder.put("redtekst", "Legg til spill");
        }
        return holder.toString();
    }
    
    public static JSONArray hentGameRatinger(){
        
        String[] rangs = {"Ikke rangert","Ønsker absolutt ikke å spille dette spillet","Vil aller helst ikke spille dette spillet","Kan til nød spille dette spillet","Helt greit spill å spille","Kan gjerne spille dette spillet","Har veldig lyst til å spille dette spillet","Favorittspill"};
        JSONArray ops = new JSONArray();
        
        for(int c = 0; c < rangs.length; c++){
                JSONObject obs = new JSONObject();
                obs.put("id",rangs[c]);
                obs.put("tekst",c);
                ops.add(obs);
        }
    
        return ops;
    }
    
    public static String skapGameInfo(session.Session sesjon, hibernate.HibernateUtil hiber, center.classes.AjaxCommand kommando){
        
        String[] rangs = {"Ikke rangert","Ønsker absolutt ikke å spille dette spillet","Vil aller helst ikke spille dette spillet","Kan til nød spille dette spillet","Helt greit spill å spille","Kan gjerne spille dette spillet","Har veldig lyst til å spille dette spillet","Favorittspill"};
        
        JSONObject modellen = new JSONObject();

        database.classes.Game spillet =  hibernate.HibernateStatic.getGame(kommando.id, hiber);
       
        modellen.put("navn", spillet.getNavn());
        modellen.put("id", spillet.getId());
        modellen.put("antallspillere", spillet.getAntall());


        modellen.put("spillinger", ResultOrganizer.organiserSpillsesjoner(modellen, kommando, hiber, sesjon));
        
        database.classes.Player relspiller = sesjon.getLogUser(hiber);
        if((relspiller.getId() > 0)){
            
            database.classes.Rating rats = hibernate.HibernateStatic.getRelevantRating(relspiller.getId(), spillet.getId(), hiber);
            if(rats == null)
                modellen.put("ranken",0);
            else
                modellen.put("ranken", rats.getRating());
        } 
        else
            modellen.put("ranken",-1);
        modellen.put("ratingvalg", hentGameRatinger());
        List<database.classes.Rating> spillrangeringer =  hibernate.HibernateStatic.getRelevantRatings(spillet.getId(), hiber);
        int forrigeRank = -1;
        JSONArray ranker = new JSONArray();
        JSONArray ranket = null;
        JSONObject currentrank = null;
        JSONArray eiere = new JSONArray();
        for(database.classes.Rating rang : spillrangeringer){
            if(center.helpers.codeblock.JsonblockCreator.nullCheckInteger(rang.getRating()) != 0){
                if(rang.getRating() != forrigeRank){
                    if(currentrank != null){
                        currentrank.put("spillerne",ranket);
                        ranker.add(currentrank);
                    }
                    forrigeRank = rang.getRating();
                    currentrank = new JSONObject();
                    currentrank.put("rangnavn", rangs[rang.getRating()]);
                    ranket = new JSONArray();
                }
                JSONObject spillrang = new JSONObject();
                spillrang.put("spillernavn", hentNavn(rang.getSpiller(), sesjon));
                spillrang.put("id", rang.getSpiller().getId());
                ranket.add(spillrang);
            }
            if(center.helpers.codeblock.JsonblockCreator.nullCheckInteger(rang.getOwner()) != 0){
                JSONObject nyeier = new JSONObject();
                nyeier.put("navn", hentNavn(rang.getSpiller(), sesjon));
                nyeier.put("id", rang.getSpiller().getId());
                eiere.add(nyeier);
            }
        }
        if(currentrank != null){
            currentrank.put("spillerne",ranket);
            ranker.add(currentrank);
        }
        modellen.put("eiere", eiere);
        modellen.put("rankeringer", ranker);

        return modellen.toString();
    }
    
    public static String skapSesjonsListe(session.Session sesjon, hibernate.HibernateUtil hiber, center.classes.AjaxCommand kommando){
        List<Object[]> sesjoner = hibernate.HibernateStatic.getLatestGamesAll(0, 500, hiber);
        JSONArray spillsesjoner = skapSpillsesjonerListe(sesjoner, sesjon, hiber);
        JSONObject modellen = new JSONObject();
        modellen.put("spillinger", spillsesjoner);
        return modellen.toString();
    }
    
    public static String hentNavn(database.classes.Player spiller, session.Session sesjon){
        String navn = spiller.getFornavn();
        if(sesjon.getStatus() >= 20){
            navn += " " + spiller.getEtternavn();
        }else if(spiller.getIgnore() == 1)
            navn = "Ukjent";
        
        return navn;
    }
    
    public static String hentSesjonsInfo(session.Session sesjon, hibernate.HibernateUtil hiber, center.classes.AjaxCommand kommando){
        database.classes.Gamesession sesjondata =  hibernate.HibernateStatic.getFullGameSesssion(kommando.id, null, hiber);
        database.classes.Game spillet = sesjondata.getSpillet();
        
        JSONObject sesjonen = new JSONObject();
        sesjonen.put("spillnavn", spillet.getNavn());
        String[] dats = sesjondata.getDato().toString().split("-");
        sesjonen.put("dato", dats[2] + ":" + dats[1] + ":" + dats[0]);
        Set<database.classes.Participation> deltakelser = sesjondata.getDeltakelser();
        JSONArray deltaks = new JSONArray();
        for(database.classes.Participation delt : deltakelser){
            JSONObject obe = new JSONObject();
            obe.put("spiller",hentNavn(delt.getSpiller(), sesjon));
            obe.put("posisjon", delt.getPosisjon());
            obe.put("poeng", delt.getPoeng());
            if(delt.getFraksjonId() != null){
                if(delt.getFraksjonId().getClass().getSimpleName().equals("Faction")){
                    obe.put("fraksjon", delt.getFraksjonId().getNavn());
                }
            }
            deltaks.add(obe);
        }
        sesjonen.put("deltakelser", deltaks);
        return sesjonen.toString();
    }
    
    public static String skapEditGameInfo(session.Session sesjon, hibernate.HibernateUtil hiber, center.classes.AjaxCommand kommando){
        //System.out.println("starter ny metode");
        JSONObject modellen = new JSONObject();

        database.classes.Game spillet =  hibernate.HibernateStatic.getGame(kommando.id, hiber);
       
        modellen.put("navn", spillet.getNavn());
        modellen.put("id", spillet.getId());
        modellen.put("spillerantall", spillet.getAntall());
        if(spillet.getSelskap() != null){
            modellen.put("selskapID", spillet.getSelskap().getId());
        }
        else{
            modellen.put("selskapID", 0);
        }

        List<database.classes.Selskap> selskapene = hibernate.HibernateStatic.getCompanies(hiber);
        JSONArray opsjoner = new JSONArray();
        JSONObject sels = new JSONObject();
        sels.put("navn", "Ikke valgt");
        sels.put("id", 0);
        opsjoner.add(sels);
        for(database.classes.Selskap selskapet : selskapene){
            sels = new JSONObject();
            sels.put("navn", selskapet.getNavn());
            sels.put("id", selskapet.getId());
            opsjoner.add(sels);
        }
        modellen.put("opsjoner", opsjoner);
        List<database.classes.Faction> fraksjoner =  hibernate.HibernateStatic.getFactions(spillet.getId(), hiber);
        JSONArray fraks = new JSONArray();
        JSONObject fra;
        try{
        for(database.classes.Faction selskapet : fraksjoner){
            fra = new JSONObject();
            fra.put("navn", selskapet.getNavn());
            fra.put("id", selskapet.getId());
            fraks.add(fra);
        }
        }catch(Exception e){ System.out.println(e.toString());}
      
        modellen.put("fraksjoner", fraks);  
        
        List<database.classes.Scenario> scense =  hibernate.HibernateStatic.getScenarios(spillet.getId(), hiber);
        JSONArray sens = new JSONArray();
        JSONObject sen;
        try{
        for(database.classes.Scenario selskapet : scense){
            sen = new JSONObject();
            sen.put("navn", selskapet.getNavn());
            sen.put("id", selskapet.getId());
            sens.add(sen);
        }
        }catch(Exception e){ System.out.println(e.toString());}
      
        modellen.put("scenarier", sens);          

        return modellen.toString();
    }  
    
    public static String skapEditFactionInfo(session.Session sesjon, hibernate.HibernateUtil hiber, center.classes.AjaxCommand kommando){
        JSONObject modellen = new JSONObject();

        database.classes.Faction fraksjonen =  hibernate.HibernateStatic.getFaction(kommando, hiber);
       
        modellen.put("navn", fraksjonen.getNavn());
        modellen.put("id", fraksjonen.getId());
        modellen.put("spillID", fraksjonen.getSpillet().getId());
     
        //return undersokArrays(playlist);
        //return hiber.getFactionGamesCrossGame(kommando.id).toString();
        return modellen.toString();
    }  
    
    public static String skapEditScenarioInfo(session.Session sesjon, hibernate.HibernateUtil hiber, center.classes.AjaxCommand kommando){
        JSONObject modellen = new JSONObject();

        database.classes.Scenario scenario =  hibernate.HibernateStatic.getScenario(kommando, hiber);
       
        modellen.put("navn", scenario.getNavn());
        modellen.put("id", scenario.getId());
        modellen.put("spillID", scenario.getSpillet().getId());
     
        //return undersokArrays(playlist);
        //return hiber.getFactionGamesCrossGame(kommando.id).toString();
        return modellen.toString();
    }    
    
    public static String skapSpillerliste(session.Session sesjon, hibernate.HibernateUtil hiber){
        JSONArray spillerliste = new JSONArray();
        JSONObject mainob = new JSONObject();
        List<database.classes.Player> spilleliste = hibernate.HibernateStatic.getUsers(hiber);
        for(database.classes.Player play : spilleliste){
            JSONObject obj = new JSONObject();
            obj.put("navn", hentNavn(play, sesjon));
            obj.put("id", play.getId());
            if(sesjon.loggedIn() && (play.getId() == sesjon.getLogUser(hiber).getId() || sesjon.getLogUser(hiber).getRettighet() >= 20)){
                obj.put("redtekst","Rediger");
                obj.put("redlink", "#RedigerBruker/" + play.getId());
            }
            if(play.getIgnore() == 0 || sesjon.getStatus() >= 20)
                spillerliste.add(obj);
        }
        mainob.put("spillerliste", spillerliste);
        return mainob.toString();
    }
    
    public static String skapFrontpage(session.Session sesjon, hibernate.HibernateUtil hiber){
        List<Object[]> utav =  hibernate.HibernateStatic.getPlayerGamesCross(hiber);
         
        JSONArray returen = getSpillerneRangering(utav, null, 8, sesjon);
        JSONArray nylige = getSpillerneRangering(utav, getOldDate(), 4, sesjon);
        List<Object[]> sesjoner = hibernate.HibernateStatic.getLatestGamesAll(0, 20, hiber);
        JSONArray spillsesjoner = skapSpillsesjonerListe(sesjoner, sesjon, hiber);
        JSONArray spillv = skapMestSpilte(hiber);
        
        
        JSONObject returmodell = new JSONObject();
        returmodell.put("spillere", returen);
        returmodell.put("sesjoner", spillsesjoner);
        returmodell.put("nylige", nylige);
        returmodell.put("spillene", spillv);
        
        String retur = returmodell.toString();   
    
        return retur;
    }
    
    public static JSONArray skapMestSpilte(hibernate.HibernateUtil hiber){
        
        List<database.classes.Game> spillene = hibernate.HibernateStatic.getFullGameRanking(hiber);
        JSONArray spillrang = new JSONArray();
        for(database.classes.Game spillet : spillene){
            new center.jsons.GameMostPlayed(spillet, spillrang);
        }
        Collections.sort(spillrang);
    
        return spillrang;
    }
    
    public static Date getOldDate(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        return cal.getTime();    
           
    }
    
    public static JSONArray skapSpillsesjonerListe(List<Object[]> sesjoner, session.Session sesjon, hibernate.HibernateUtil hiber){
        JSONArray spillsesjoner = new JSONArray();
        for(Object[] raden : sesjoner){
            database.classes.Gamesession gses = (database.classes.Gamesession) raden[0];
            JSONObject obj = new JSONObject();
            obj.put("navn",gses.getSpillet().getNavn());
            obj.put("id", gses.getId());
            String[] dats = gses.getDato().toString().split("-");
            obj.put("dato", dats[2] + ":" + dats[1] + ":" + dats[0]);
            database.classes.Player minbruker = sesjon.getLogUser(hiber);
            //if(sesjon.loggedIn())
            //System.out.println(gses.getId() + " " + minbruker.getId() + " " + gses.getRegistrar().getId());
            if(sesjon.loggedIn() && (minbruker.getId() == gses.getRegistrar().getId())){
                obj.put("redtekst","Rediger");
                obj.put("redlink", "#redigerSesjon/" + gses.getId() + "/" + gses.getSpillet().getId());
            }
           
            spillsesjoner.add(obj);
        } 
        return spillsesjoner;
    }
    
    public static String getPlayerProfile(hibernate.HibernateUtil hiber, int spillerID, session.Session sesjonen){
                JSONObject modell = new JSONObject();
                database.classes.Player spilleren = hibernate.HibernateStatic.getUser(spillerID, hiber);
                List<database.classes.Gamesession> sesjoner =  hibernate.HibernateStatic.getPlayerGameSesssions(spillerID, hiber);
                modell.put("antallspillinger",sesjoner.size());
                modell.put("navn",hentNavn(spilleren, sesjonen));
                modell.put("id", spilleren.getId());
                JSONArray spillinger = new JSONArray();
                JSONArray spillpartner = new JSONArray();
                float delpoeng = 0;
                List<center.jsons.GamerBestRank> spill = new ArrayList<center.jsons.GamerBestRank>();
                List<database.classes.Player> medspillere = new ArrayList<database.classes.Player>();
                for(database.classes.Gamesession part : sesjoner){
                    JSONObject spilling = new JSONObject();
                    spilling.put("antallspillere",part.getDeltakelser().size());
                    spilling.put("spillnavn", part.getSpillet().getNavn());
                    spilling.put("id", part.getId());
                    for(database.classes.Participation past : part.getDeltakelser()){
                        database.classes.Player comp = past.getSpiller();
                        if(comp.getId() == spillerID){                         
                            spilling.put("poeng", past.getPoeng());
                            spilling.put("posisjon", past.getPosisjon());
                            float spillpoengene = ((((part.getDeltakelser().size() - 1) - (past.getPosisjon() - 1)) * 100) / (part.getDeltakelser().size() - 1));
                            spilling.put("rank", Math.floor(spillpoengene));
                            delpoeng += spillpoengene;
                            spill = ResultOrganizer.organizeGameList(spill, part.getSpillet().getId(), part.getSpillet().getNavn(), Math.round(spillpoengene));
                            database.classes.Faction faks = past.getFraksjonId();
                            
                            if(faks != null){
                                if(faks.getClass().getSimpleName().equals("Faction")){
                                  spilling.put("fraksjon", faks.getNavn());
                                }
                            }
                        }
                        else if(!medspillere.contains(comp) && comp.getIgnore() == 0){
                            medspillere.add(comp);
                        }
                    }
                    spillinger.add(spilling);
                    
                }
                for(database.classes.Player pay : medspillere){
                    JSONObject nyobj = new JSONObject();
                    nyobj.put("id", pay.getId());
                    nyobj.put("navn", hentNavn(pay, sesjonen));
                    spillpartner.add(nyobj);
                }
                for(center.jsons.GamerBestRank rank : spill){
                    rank.finalScoring();
                    rank.setToJson();
                }
                Collections.sort(spill);
                JSONArray jspill = new JSONArray();
                for(center.jsons.GamerBestRank rank : spill){
                    jspill.add(rank);
                }
                modell.put("bestspill", jspill);
                modell.put("spillinger", spillinger);
                modell.put("rangering", Math.round(delpoeng / sesjoner.size()));
                modell.put("medspillere", spillpartner);
                modell.put("rankeringer", center.helpers.codeblock.JsonblockCreator.getGameRanks(hiber, spilleren));
                return modell.toString();
    }
    
    public static String undersokArrays(List<Object[]> utav){
        String returen = "";
        try{
        for(Object[] raden : utav){
            returen = returen + "<br />";
            if(raden[0] != null){
                database.classes.Player spilleren = (database.classes.Player) raden[0];
                returen = returen + " " + spilleren.getId(); 
            }
            if(raden[1] != null){
                database.classes.Participation deltak = (database.classes.Participation) raden[1];
                returen = returen + " " + deltak.getId(); 
            }
            if(raden[2] != null){
                database.classes.Gamesession sesjonen = (database.classes.Gamesession) raden[2];
                returen = returen + " " + sesjonen.getId(); 
            }

        }
        }catch(Exception e){ returen = e.toString();}
    
        return returen;
    }
    
    public static JSONArray getSpillerneRangering(List<Object[]> utav, Date fra, int min, session.Session sesjonen){
        ArrayList<center.jsons.ScoreObject> spillerne = new ArrayList<center.jsons.ScoreObject>();
        for(Object[] raden : utav){
            database.classes.Player spilleren = (database.classes.Player) raden[0];
            spillerne.add(ResultOrganizer.SpillerForRangering(spilleren, fra, sesjonen));               
        }
      
        Collections.sort(spillerne);
        JSONArray returen = new JSONArray();
        for(center.jsons.ScoreObject spiller : spillerne){
          if(spiller.antallspill >= min)
            returen.add(spiller);
        } 
        
        return returen;
    }
    
    public static JSONObject skapDeltakJson(database.classes.Participation grunn, int idnumber){
        JSONObject retur = new JSONObject();
        retur.put("id", grunn.getId());
        retur.put("poeng", grunn.getPoeng());
        retur.put("posisjon", grunn.getPosisjon());
        retur.put("redigerid", "red" + idnumber);
        if(grunn.getSpiller() == null){
            retur.put("spillerID", 0);
        }
        else{
            retur.put("spillerID", grunn.getSpiller().getId());
        }
        if(grunn.getFraksjonId() == null){
            retur.put("fraksjonID", 0);
        }
        else{
            retur.put("fraksjonID", grunn.getFraksjonId().getId());
        }
        return retur;
    }
    
    public static String toTwoFormat(int datoetekst){
        if(datoetekst < 10)
            return "0" + Integer.toString(datoetekst);
        else
            return Integer.toString(datoetekst);
        
    }
    
    public static String skapEditSessionInfo(session.Session sesjon, hibernate.HibernateUtil hiber,center.classes.AjaxCommand kommando){
        database.classes.Game spillet =  hibernate.HibernateStatic.getGame(kommando.subid, hiber);
        database.classes.Gamesession sesjonen =  hibernate.HibernateStatic.getGameSesssion(kommando.id, spillet, sesjon, hiber);
        
        JSONObject spiffo = new JSONObject();
        spiffo.put("spillNavn", spillet.getNavn());
        spiffo.put("spillID", spillet.getId());
        spiffo.put("id", sesjonen.getId());
        int scenarioID = 0;
        try{ scenarioID = sesjonen.getScenarioId().getId(); }catch(Exception e){}
        spiffo.put("scenarioID", scenarioID);
        //System.out.println(scenarioID);
        Calendar datobjekt = Calendar.getInstance();
        datobjekt.setTime(sesjonen.getDato());
        spiffo.put("dato", toTwoFormat(datobjekt.get(Calendar.DAY_OF_MONTH)) + ":" + toTwoFormat(datobjekt.get(Calendar.MONTH) + 1) + ":" + datobjekt.get(Calendar.YEAR));
        JSONArray deltakelser = new JSONArray();
        int spillerantall = spillet.getAntall();
        database.classes.Participation[] delt = sesjonen.getDeltakelser().toArray(new database.classes.Participation[0]);
        String[] presets = kommando.setStreng.split(":");
        for(int x = 0; x < spillerantall; x++){
            database.classes.Participation bruk = null;
            if(x < delt.length){
                bruk = delt[x];
            }
            else{
                database.classes.Player spilleren = null;
                if(x < presets.length){
                    try{ spilleren = hibernate.HibernateStatic.getUser(Integer.parseInt(presets[x]), hiber); }catch(Exception e){}
                }
                bruk = new database.classes.Participation(spilleren, sesjonen,(float) 0, 0, 0, null);
            }
            deltakelser.add(skapDeltakJson(bruk, x));
        }
        spiffo.put("deltakelser", deltakelser);
        
        database.classes.Faction[] fraksjoner =  hibernate.HibernateStatic.getFactions(spillet.getId(), hiber).toArray(new database.classes.Faction[0]);
        JSONArray fraksjonene = new JSONArray();
        JSONObject frak = new JSONObject();
        frak.put("navn", "ikke spesifisert");
        frak.put("id", 0);
        fraksjonene.add(frak);        
        for(int y = 0; y < fraksjoner.length; y++){
            frak = new JSONObject();
            frak.put("navn", fraksjoner[y].getNavn());
            frak.put("id", fraksjoner[y].getId());
            fraksjonene.add(frak);
        }
        spiffo.put("fraksjoner", fraksjonene);
        
        database.classes.Scenario[] scenarioer =  hibernate.HibernateStatic.getScenarios(spillet.getId(), hiber).toArray(new database.classes.Scenario[0]);
        JSONArray scens = new JSONArray();
        JSONObject scen = new JSONObject();
        scen.put("navn", "ikke spesifisert");
        scen.put("id", 0);
        scens.add(scen);        
        for(int y = 0; y < scenarioer.length; y++){
            scen = new JSONObject();
            scen.put("navn", scenarioer[y].getNavn());
            scen.put("id", scenarioer[y].getId());
            scens.add(scen);
        }
        spiffo.put("scenarioer", scens);        
        
        database.classes.Player[] spillerne = hibernate.HibernateStatic.getUsers(hiber).toArray(new database.classes.Player[0]);
        JSONArray spillerjson = new JSONArray();
        JSONObject dek = new JSONObject();
        dek.put("navn", "Ikke spesifisert");
        dek.put("id", -1);
        dek = new JSONObject();
        dek.put("navn", "Ikke i bruk");
        dek.put("id", 0);        
        spillerjson.add(dek);        
        for(int y = 0; y < spillerne.length; y++){
            dek = new JSONObject();
            dek.put("navn", hentNavn(spillerne[y], sesjon));
            dek.put("id", spillerne[y].getId());
            spillerjson.add(dek);
        }   
        spiffo.put("spillere", spillerjson);
        return spiffo.toString();
    }
    
    public static String getCompanyList(session.Session sesjon, hibernate.HibernateUtil hiber){
        List<database.classes.Selskap> selskap = hibernate.HibernateStatic.getCompanies(hiber);
        JSONObject modell = new JSONObject();
        JSONArray selskapene = new JSONArray();
        for(database.classes.Selskap ut : selskap){
            JSONObject selskapet = new JSONObject();
            selskapet.put("navn", ut.getNavn());
            selskapet.put("id", ut.getId());
            selskapet.put("antallspill", ut.getSpill().size());
            int antallspillinger = 0;
            for(database.classes.Game tellspill : ut.getSpill()){
                antallspillinger += tellspill.getSesjoner().size();
            }
            selskapet.put("antallsesjoner",antallspillinger);
            if(sesjon.loggedIn()){
                selskapet.put("redlink","#redigerSelskap/" + ut.getId());
                selskapet.put("redtekst", "Rediger");
            }
            selskapene.add(selskapet);
        }
        modell.put("spill", selskapene);
        return modell.toString();
    }
    
    public static String getCompanyInfo(session.Session sesjon, hibernate.HibernateUtil hiber, center.classes.AjaxCommand command){
        database.classes.Selskap selskap = hibernate.HibernateStatic.getCompany(command.id, hiber);
        JSONObject modell = new JSONObject();
        modell.put("navn", selskap.getNavn());
        modell.put("id", selskap.getId());
        JSONArray spillene = new JSONArray();
        for(database.classes.Game spillet : selskap.getSpill()){
            JSONObject spillinf = new JSONObject();
            spillinf.put("navn", spillet.getNavn());
            spillinf.put("id", spillet.getId());
            int antallspillinger = spillet.getSesjoner().size();
            spillinf.put("antallsesjoner", antallspillinger);          
            if(sesjon.loggedIn()){
                spillinf.put("redlink","#redigerSpill/" + spillet.getId());
                spillinf.put("redtekst", "Rediger");
            }
            spillene.add(spillinf);
        }
        modell.put("spill", spillene);
        return modell.toString();
    }
    
    public static String getUserEditInfo(session.Session sesjon, hibernate.HibernateUtil hiber, center.classes.AjaxCommand kommando){
        database.classes.Player spiller = hibernate.HibernateStatic.getUser(kommando.id, hiber);
        database.classes.Player inuser = sesjon.getLogUser(hiber);
        JSONObject modellen = new JSONObject();
        JSONArray statuslist = new JSONArray();
        JSONArray gameranklist = new JSONArray();
        modellen.put("fornavn", spiller.getFornavn());
        modellen.put("etternavn", spiller.getEtternavn());
        modellen.put("id", spiller.getId());
        if(spiller.getRettighet() < inuser.getRettighet() || inuser.getRettighet() >= 25){
            modellen.put("status", spiller.getRettighet());
            Object[][] statuslisten = {{"Utestengt",0},{"Vanlig bruker",1},{"Aktiv bruker",10},{"Administrator",20},{"Eier",25}};
            for(int i = 0; i < statuslisten.length; i++){
                JSONObject statpunkt = new JSONObject();
                statpunkt.put("id", statuslisten[i][1]);
                statpunkt.put("navn", statuslisten[i][0]);
                statuslist.add(statpunkt);
            }
        }

        modellen.put("statusliste",statuslist);
        modellen.put("epost", spiller.getEpost());
        modellen.put("ignore", spiller.getIgnore());
        if(spiller.getId() == inuser.getId() || inuser.getRettighet() >= 25){
            modellen.put("brukernavn", spiller.getBrukernavn());
            modellen.put("passord", spiller.getPassord());
            modellen.put("passordto", spiller.getPassord());
        }
        List<database.classes.Game> glist = hibernate.HibernateStatic.getRelevantGameRatings(spiller.getId(), hiber);
        for(database.classes.Game gam : glist){
            JSONObject obp = new JSONObject();
            obp.put("spillnavn", gam.getNavn());
            obp.put("id", gam.getId());
            int pos = 0, eier = 0;
            for(database.classes.Rating rag : gam.getRangeringer()){
                if(rag.getSpiller().getId() == spiller.getId()){                   
                    try{ pos = rag.getRating(); }catch(Exception e){ }
                    try{ eier = rag.getOwner(); }catch(Exception e){ }
                }
            }
            obp.put("ranken", pos);
            obp.put("eier", eier);
            gameranklist.add(obp);
            //System.out.println("g:avb " + gam.getNavn() + " " + gam.getRangeringer().size());
        }
        if(spiller.getId() == inuser.getId())
            modellen.put("gamerank", gameranklist);
        modellen.put("ratingvalg", hentGameRatinger());
    
        return modellen.toString();
    }
    
    public static String comparePlayers(session.Session sesjon, hibernate.HibernateUtil hiber, center.classes.AjaxCommand kommando){
        List<database.classes.Gamesession> spillinger =  hibernate.HibernateStatic.getTwoPlayerGameSesssions(kommando.id, kommando.subid, hiber);
        database.classes.Player firstplayer = hibernate.HibernateStatic.getUser(kommando.id, hiber);
        database.classes.Player secondplayer = hibernate.HibernateStatic.getUser(kommando.subid, hiber);
        int lastgame = -1;
        center.jsons.CompareGame comp = null;
        List<center.jsons.CompareGame> spillene = new ArrayList<center.jsons.CompareGame>();
        center.jsons.CompareGameMainResult compresultat = new center.jsons.CompareGameMainResult();
        JSONArray medspill = new JSONArray();
        for(database.classes.Gamesession spilling : spillinger){
            database.classes.Game spillet = spilling.getSpillet();
            if(lastgame != spillet.getId()){
                comp = new center.jsons.CompareGame(spillet, kommando.id, kommando.subid);
                spillene.add(comp);
                medspill.add(comp);
                lastgame = spillet.getId();
            }
            comp.addResultMatch(spilling, compresultat);
        }
        
        for(center.jsons.CompareGame cop : spillene){
            cop.sumResults();
            cop.toJson();
        }
        int[] compres = {0,0,0};
        int[] comptell = {0,0,0};

        for(center.jsons.CompareGame cop : spillene){
            if(cop.resultsum[0] > -1 && cop.resultsum[1] > -1){
                compres[1] += cop.resultsum[0];
                compres[2] += cop.resultsum[1];
                comptell[1]++;
                comptell[2]++;
            }
            else if(cop.resultsum[0] > -1){
                compres[0] += cop.resultsum[0];
                comptell[0]++;            
            }
        }
        
        for(int i = 0; i < 3; i++){
            if(comptell[i] != 0)
                compres[i] = Math.round(compres[i] / comptell[i]);
        }
        
        String sumstreng = "";
        String hovstreng = "";
        if(comptell[0] > 0){
            int fikverdi = Math.round(compres[0] - (compres[1] + compres[2]) / 2);
            if(fikverdi > 0)
                hovstreng = "bedre";
            else
                hovstreng = "dårligere";
            fikverdi = Math.abs(fikverdi);
            sumstreng += "" + firstplayer.getFornavn() + " gjør det i snitt " + fikverdi + " prosentpoeng " + hovstreng + " i spill som " + secondplayer.getFornavn() + " ikke spiller. ";
        }
        if(comptell[1] > 0 && comptell[2] > 0){
            int fikverdi = Math.round(compres[1] - compres[2]);
            if(fikverdi > 0)
                hovstreng = "bedre";
            else
                hovstreng = "dårligere";
            fikverdi = Math.abs(fikverdi);
            sumstreng += "" + firstplayer.getFornavn() + " gjør det i snitt " + fikverdi + " prosentpoeng " + hovstreng + " i spillinger som " + secondplayer.getFornavn() + " ikke er med på, i spill som begge spiller. ";
        }
        
        sumstreng += compresultat.addToResults(firstplayer.getFornavn(),secondplayer.getFornavn());
        
        JSONObject modell = new JSONObject();
        modell.put("forstenavn", firstplayer.getFornavn());
        modell.put("andrenavn", secondplayer.getFornavn());
        modell.put("forsteid", kommando.id);
        modell.put("andreid", kommando.subid);
        modell.put("spillene", medspill);
        modell.put("sumstreng", sumstreng);
        return modell.toString();
    }
    
    public static String gameMatrix(session.Session sesjon, hibernate.HibernateUtil hiber, center.classes.AjaxCommand kommando){

        database.classes.Game spillet =  hibernate.HibernateStatic.getFullGame(kommando.id, hiber);
        Set<database.classes.Gamesession> spillinger = spillet.getSesjoner();
        List<database.classes.Player> spillere = new ArrayList<database.classes.Player>();
        List<database.classes.Faction> fraksjoner = new ArrayList<database.classes.Faction>();
        for(database.classes.Gamesession spilling : spillinger){
            for(database.classes.Participation deltakelse : spilling.getDeltakelser()){
                if(!spillere.contains(deltakelse.getSpiller()))
                    spillere.add(deltakelse.getSpiller());
                try{
                if(deltakelse.getFraksjonId() != null && deltakelse.getFraksjonId().getClass().getSimpleName().equals("Faction") && !fraksjoner.contains(deltakelse.getFraksjonId()))
                    fraksjoner.add(deltakelse.getFraksjonId());
                }catch(Exception e){ System.out.println("FEIL HER " + e.toString() + " \n fra ID " + deltakelse.getId()); }
            }
        }
        int[][] resultatmatrise = new int[fraksjoner.size()][spillere.size()];
        float[][] poengmatrise = new float[fraksjoner.size()][spillere.size()];
        int[][] telling = new int[fraksjoner.size()][spillere.size()];
        for(int x = 0; x < resultatmatrise.length; x++){
            for(int y = 0; y < resultatmatrise[0].length; y++){
                resultatmatrise[x][y] = 0;
                telling[x][y] = 0;
                poengmatrise[x][y] = 0;
            }
        }
        
        boolean includepoeng = false;
        
        for(database.classes.Gamesession spilling : spillinger){
            for(database.classes.Participation deltakelse : spilling.getDeltakelser()){
                database.classes.Faction frak = deltakelse.getFraksjonId();
                database.classes.Player spil = deltakelse.getSpiller();
                if(fraksjoner.indexOf(frak) >= 0){
                    resultatmatrise[fraksjoner.indexOf(frak)][spillere.indexOf(spil)] += Math.round(center.helpers.ResultOrganizer.calcResult(spilling, deltakelse));
                    telling[fraksjoner.indexOf(frak)][spillere.indexOf(spil)]++;
                    poengmatrise[fraksjoner.indexOf(frak)][spillere.indexOf(spil)] += Math.round(deltakelse.getPoeng());
                    if(!includepoeng && Math.round(deltakelse.getPoeng()) > 0)
                        includepoeng = true;
                }
            }
        }
        
        JSONArray rader = new JSONArray();
        JSONArray antallrader = new JSONArray();
        JSONArray poengrader = new JSONArray();
        
        JSONArray[] radliste = {rader, antallrader, poengrader};
        
        JSONArray kolonner = new JSONArray();
        JSONArray antallkolonner = new JSONArray();
        JSONArray poengkolonner = new JSONArray();
        ResultOrganizer.putToJsonArray(kolonner, "");
        ResultOrganizer.putToJsonArray(antallkolonner, "");
        ResultOrganizer.putToJsonArray(poengkolonner, "");
        
        JSONArray[] kolonneliste = {kolonner, antallkolonner, poengkolonner};
        
        //ResultOrganizer.putAllToJsonArray(kolonneliste, new String[]{" ", ""," "});
                
        
        for(database.classes.Faction frak : fraksjoner){
            ResultOrganizer.putAllToJsonArray(kolonneliste, new String[]{frak.getNavn(), frak.getNavn(),frak.getNavn()});
        }
        //rader.add(kolonner);
        ResultOrganizer.putRowToJsonArray(rader, kolonner);
        ResultOrganizer.putRowToJsonArray(antallrader, antallkolonner);
        ResultOrganizer.putRowToJsonArray(poengrader, poengkolonner);
        int[] fraksjonsum = new int[fraksjoner.size()];
        int[] fraksjonpoeng = new int[fraksjoner.size()];
        int[] fraksjonantall = new int[fraksjoner.size()];
        int[] fraksjonstell = new int[fraksjoner.size()];
        for(int a = 0; a < fraksjoner.size(); a++){
            fraksjonsum[a] = 0; fraksjonstell[a] = 0; fraksjonpoeng[a] = 0; fraksjonantall[a] = 0;
        }
        for(int y = 0; y < spillere.size(); y++){
            kolonner = new JSONArray();
            antallkolonner = new JSONArray();
            poengkolonner = new JSONArray();
            kolonneliste = new JSONArray[]{kolonner, antallkolonner, poengkolonner};
            int sum = 0;
            int antall = 0;
            int kolonneantall = 0;
            float poengsum = 0;

            String innavn = hentNavn(spillere.get(y), sesjon);
            ResultOrganizer.putAllToJsonArray(kolonneliste, new String[]{innavn, innavn, innavn});
            
            for(int x = 0; x < fraksjoner.size(); x++){
                if(telling[x][y] > 0){
                    int score = Math.round(resultatmatrise[x][y] / telling[x][y]);

                    ResultOrganizer.putAllToJsonArray(kolonneliste, new String[]{"" + score, "" + telling[x][y], "" + (poengmatrise[x][y] / telling[x][y])});
                    sum += score;
                    poengsum += Math.round(poengmatrise[x][y] / telling[x][y]);
                    antall++;
                    kolonneantall += telling[x][y];
                    fraksjonsum[x] += score;
                    fraksjonpoeng[x] += (poengmatrise[x][y] / telling[x][y]);
                    fraksjonstell[x] += telling[x][y];
                }
                else{
                    ResultOrganizer.putAllToJsonArray(kolonneliste, new String[]{"", "", ""});
                }
            }
            
            ResultOrganizer.putAllToJsonArray(kolonneliste, new String[]{"" + Math.round(sum / antall), "" + kolonneantall, "" + (poengsum / antall)});
            //rader.add(kolonner);
            ResultOrganizer.putRowToJsonArray(rader, kolonner);
            ResultOrganizer.putRowToJsonArray(antallrader, antallkolonner);
            ResultOrganizer.putRowToJsonArray(poengrader, poengkolonner);
        }
        kolonner = new JSONArray();
        antallkolonner = new JSONArray();
        poengkolonner = new JSONArray();
        kolonneliste = new JSONArray[]{kolonner, antallkolonner, poengkolonner};

        ResultOrganizer.putAllToJsonArray(kolonneliste, new String[]{" ", " ", " "});
        for(int x = 0; x < fraksjoner.size(); x++){
            if(fraksjonstell[x] > 0){
                int score = Math.round(fraksjonsum[x] / fraksjonstell[x]);
                ResultOrganizer.putAllToJsonArray(kolonneliste, new String[]{"" + score, "" + fraksjonstell[x], "" + (fraksjonpoeng[x] / fraksjonstell[x])});
            }
            else{
                ResultOrganizer.putAllToJsonArray(kolonneliste, new String[]{" ", " ", " "});
            }
        }
        ResultOrganizer.putRowToJsonArray(rader, kolonner);
        ResultOrganizer.putRowToJsonArray(antallrader, antallkolonner);
        ResultOrganizer.putRowToJsonArray(poengrader, poengkolonner);
        
        
        JSONObject modellen = new JSONObject();
        modellen.put("matrise", rader);
        modellen.put("antallmatrise", antallrader);
        if(includepoeng)
            modellen.put("poengmatrise", poengrader);
        modellen.put("id", spillet.getId());
        modellen.put("navn", spillet.getNavn());
        return modellen.toString();
    }
    
    public static String hentSpillerForPrerang(session.Session sesjon, hibernate.HibernateUtil hiber){
        JSONObject modell = center.helpers.codeblock.JsonblockCreator.getPlayersForPrevList(new JSONObject(), sesjon, hiber);
        return modell.toString();
    }
    
    public static String skapVisKalender(session.Session sesjon, hibernate.HibernateUtil hiber, center.classes.AjaxCommand kommando){
        center.classes.UserCalendar brukekalender = new center.classes.UserCalendar(hiber, kommando, sesjon);
        return brukekalender.hentUtModell();
    }
    
}

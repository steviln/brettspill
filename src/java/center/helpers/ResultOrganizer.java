/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center.helpers;

import static center.helpers.ContentCreator.skapSpillsesjonerListe;
import center.jsons.ScoreObject;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 *
 * @author Steinar
 */
public class ResultOrganizer {
    
    public static JSONArray organiserSpillsesjoner(JSONObject modellen, center.classes.AjaxCommand kommando, hibernate.HibernateUtil hiber, session.Session sesjon){
        List<Object[]> playlist =  hibernate.HibernateStatic.getPlayerGamesCrossGame(kommando.id, hiber);
        ResultOrganizer.organiserSpillerRangeringer(playlist, modellen, sesjon);
        ResultOrganizer.organiserFactionRangeringer( hibernate.HibernateStatic.getFactionGamesCrossGame(kommando.id, hiber), modellen);
        List<Object[]> sesjoner = hibernate.HibernateStatic.getLatestGamesAll(kommando.id, 500, hiber);
        JSONArray spillsesjoner = skapSpillsesjonerListe(sesjoner, sesjon, hiber);    
    
        return spillsesjoner;
    
    }
    
    public static void organiserSpillerRangeringer(List<Object[]> playlist, JSONObject addeto, session.Session sesjon){
        int lastplayer = -1;
        ArrayList<center.jsons.ScoringPlayer> spillerne = new ArrayList<center.jsons.ScoringPlayer>();
        center.jsons.ScoringPlayer currentplayer = null;
        Hashtable<String, center.jsons.ScoringGamesession> spillsesjoner = new Hashtable<String, center.jsons.ScoringGamesession>();
        center.jsons.ScoringGamesession currentsession = null;
        for(Object[] raden: playlist){
            database.classes.Player spilleren = (database.classes.Player) raden[0];
            if(spilleren.getId() != lastplayer){
                currentplayer = new center.jsons.ScoringPlayer(center.helpers.ContentCreator.hentNavn(spilleren, sesjon), spilleren.getId());
                spillerne.add(currentplayer);
                lastplayer = spilleren.getId();
            }
            database.classes.Gamesession sesjonen = (database.classes.Gamesession) raden[2];
            if(spillsesjoner.containsKey(sesjonen.getId().toString())){
                currentsession = (center.jsons.ScoringGamesession) spillsesjoner.get(sesjonen.getId().toString());
            }
            else{
                currentsession = new center.jsons.ScoringGamesession();
                spillsesjoner.put(sesjonen.getId().toString(), currentsession);
            }
            database.classes.Participation deltak = (database.classes.Participation) raden[1];  
            center.jsons.ScoringDeltakelser scotemp = new center.jsons.ScoringDeltakelser(currentsession, currentplayer, deltak.getPoeng(), deltak.getPosisjon());
            currentplayer.deltakelser.add(scotemp);
            currentsession.deltakelser.add(scotemp);
            currentplayer.poeng += scotemp.poengsum;
            if(scotemp.plassering == 1){ currentplayer.wincount++; }
        }
        for(center.jsons.ScoringPlayer spiller : spillerne){
            float totposisjon = 0;
            for(center.jsons.ScoringDeltakelser delt : spiller.deltakelser){
                float uspos = delt.plassering - 1, usant = delt.sesjon.deltakelser.size() - 1;
                totposisjon += (float) (((usant - uspos) * 100) / usant);
            }
            spiller.prosentpoeng = (float)(totposisjon / spiller.deltakelser.size());
            //spiller.prosentpoeng = totposisjon;
        }
        Collections.sort(spillerne);
        JSONArray jspillerne = new JSONArray();
        for(center.jsons.ScoringPlayer spiller : spillerne){
            spiller.calc();
            jspillerne.add(spiller);
        }
        addeto.put("spillere", jspillerne);
 
    }
    
    public static List<database.classes.Faction> hentFraksjoner(List<Object[]> frakes){
        ArrayList<database.classes.Faction> fraksjoner = new ArrayList();
        for(Object[] raden: frakes){
            database.classes.Faction frak = (database.classes.Faction) raden[0];
            fraksjoner.add(frak);
        }
        return fraksjoner;
    }
    
    public static List<database.classes.Scenario> hentScenarioer(List<Object[]> frakes){
        ArrayList<database.classes.Scenario> fraksjoner = new ArrayList();
        for(Object[] raden: frakes){
            database.classes.Scenario frak = (database.classes.Scenario) raden[0];
            fraksjoner.add(frak);
        }
        return fraksjoner;
    }    
    
    public static void organiserFactionRangeringer(List<Object[]> playlist, JSONObject addeto){
        int lastplayer = -1;
        ArrayList<center.jsons.ScoringFaction> spillerne = new ArrayList<center.jsons.ScoringFaction>();
        center.jsons.ScoringFaction currentplayer = null;
        Hashtable<String, center.jsons.ScoringGamesession> spillsesjoner = new Hashtable<String, center.jsons.ScoringGamesession>();
        center.jsons.ScoringGamesession currentsession = null;
        for(Object[] raden: playlist){
            database.classes.Faction spilleren = (database.classes.Faction) raden[0];
            if(spilleren.getId() != lastplayer){
                currentplayer = new center.jsons.ScoringFaction(spilleren.getNavn(), spilleren.getId());
                spillerne.add(currentplayer);
                lastplayer = spilleren.getId();
            }
            database.classes.Gamesession sesjonen = (database.classes.Gamesession) raden[2];
            if(spillsesjoner.containsKey(sesjonen.getId().toString())){
                currentsession = (center.jsons.ScoringGamesession) spillsesjoner.get(sesjonen.getId().toString());
            }
            else{
                currentsession = new center.jsons.ScoringGamesession();
                spillsesjoner.put(sesjonen.getId().toString(), currentsession);
            }
            database.classes.Participation deltak = (database.classes.Participation) raden[1];  
            center.jsons.ScoringDeltakelserFaction scotemp = new center.jsons.ScoringDeltakelserFaction(currentsession, deltak.getPoeng(), deltak.getPosisjon(), currentplayer);
            currentplayer.deltakelser.add(scotemp);
            currentsession.deltakelser.add(scotemp);
            currentplayer.poeng += deltak.getPoeng();

            if(scotemp.plassering == 1){ currentplayer.wincount++; }
        }
        for(center.jsons.ScoringFaction spiller : spillerne){
            float totposisjon = 0;
            for(center.jsons.ScoringDeltakelser delt : spiller.deltakelser){
                float uspos = delt.plassering - 1, usant = delt.sesjon.deltakelser.size() - 1;
                totposisjon += (float) (((usant - uspos) * 100) / usant);
            }
            spiller.prosentpoeng = (float)(totposisjon / spiller.deltakelser.size());
            //spiller.prosentpoeng = totposisjon;
        }
        Collections.sort(spillerne);
        JSONArray jspillerne = new JSONArray();
        JSONArray jfraksjoner = new JSONArray();
        for(center.jsons.ScoringFaction spiller : spillerne){
            spiller.calc();
            jspillerne.add(spiller);
            JSONObject gobjekt = new JSONObject();
            gobjekt.put("navn", spiller.navn);
            gobjekt.put("id", spiller.id);
            jfraksjoner.add(gobjekt);
        }
        addeto.put("fraks", jspillerne);
        addeto.put("fraksjoner", jfraksjoner);
 
    }  
    
    public static List<center.jsons.GamerBestRank> organizeGameList(List<center.jsons.GamerBestRank> spillene, int spillID, String spillNavn, int spillscore){
        for(center.jsons.GamerBestRank spillet : spillene){
            if(spillet.getGameID() == spillID){
                spillet.addSesjon(spillscore);
                spillID = 0;
            }
        }
        if(spillID > 0){
            center.jsons.GamerBestRank nyrank = new center.jsons.GamerBestRank(spillID, spillNavn);
            nyrank.addSesjon(spillscore);
            spillene.add(nyrank);
        }
        return spillene;
    }
    
    public static float calcResult(database.classes.Gamesession delt, database.classes.Participation part){
           
        float uspos = part.getPosisjon() - 1, usant = delt.getDeltakelser().size() - 1;
        float totposisjon = (float) (((usant - uspos) * 100) / usant);  
        
        return totposisjon;
    }
    
    public static void putToJsonArray(JSONArray arra, String textvalue){
        JSONObject obj = new JSONObject();
        obj.put("column", textvalue);
        arra.add(obj);
    }
    
    public static void putRowToJsonArray(JSONArray dest, JSONArray row){
        JSONObject obj = new JSONObject();
        obj.put("row", row);
        dest.add(obj);
    }  
    
    public static void putAllToJsonArray(JSONArray[] liste, String[] strenger){
        for(int i = 0; i < liste.length; i++){
            putToJsonArray(liste[i], strenger[i]);
        }
    }
    
    public static center.jsons.ScoreObject SpillerForRangering(database.classes.Player spiller, Date fra, session.Session sesjonen){
            int winrate = 0, lossrate = 0, rangering = 0, antallspill = 0;

            float totrang = 0;
            winrate = 0;
            lossrate = 0;
            Set<database.classes.Participation> deltakelsene = spiller.getDeltakelser();
            antallspill = deltakelsene.size();
            int fantallspill = 0;
            

            for(database.classes.Participation delta : deltakelsene){
                if(fra == null || fra.before(delta.getSpillsesjon().getDato())){
                    int posisjon = delta.getPosisjon();
                    int antspillere = delta.getSpillsesjon().getDeltakelser().size();
                    float uspos = posisjon - 1, usant = antspillere - 1;
                    float temprang = (float) (((usant - uspos) * 100) / usant);
                    totrang += temprang;
                    //System.out.println("dato " + delta.getSpillsesjon().getDato());
                    if(posisjon == 1){
                        winrate++;
                    }
                    else if(posisjon == antspillere){
                        lossrate++;
                    }
                    fantallspill++;
                }
            }  
            
            if(fantallspill == 0)
                fantallspill = 1;
            
            float frangering = (totrang / fantallspill);
            float flossrate = (lossrate / fantallspill) * 100;
            float fwinrate = (winrate / fantallspill) * 100;
            
            rangering = Math.round(frangering);
            lossrate = Math.round(flossrate);
            winrate = Math.round(fwinrate);
            
            
            ScoreObject myscore = new center.jsons.ScoreObject(spiller,winrate,lossrate,rangering,fantallspill, sesjonen);

            return myscore;
    }    
    
}

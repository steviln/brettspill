/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center.jsons;
import java.util.ArrayList;
import org.json.simple.JSONObject;


/**
 *
 * @author Steinar
 */
public class ScoringPlayer extends JSONObject implements Comparable<ScoringPlayer>{
    
    public ArrayList<ScoringDeltakelser> deltakelser;
    public ArrayList<ScoringDeltakelserFaction> deltagelser;
    public String navn;
    public int id;
    public float prosentpoeng = 0;
    public float poeng = 0;
    public int wincount = 0;
    
    public ScoringPlayer(String nav, int tid){
        deltakelser = new ArrayList<ScoringDeltakelser>();
        deltagelser = new ArrayList<ScoringDeltakelserFaction>();
        navn = nav;
        id = tid;
    }
    
    public void calc(){
        float deltaksize = deltakelser.size();
        this.put("navn", navn);
        this.put("rating", Math.round(prosentpoeng));
        this.put("id",id);
        this.put("antall", deltakelser.size());
        this.put("snittscore", (poeng / deltaksize));
        this.put("winrate", ((wincount / deltaksize) * 100));
        //this.put("winrate", wincount);
    }
    
    public int compareTo(ScoringPlayer comp){
        if(this.prosentpoeng > comp.prosentpoeng)
            return -1;
        else
            return 1;
    }
    
}

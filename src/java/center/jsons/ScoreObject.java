/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center.jsons;

import org.json.simple.JSONObject;

/**
 *
 * @author Steinar
 */
public class ScoreObject extends JSONObject implements Comparable<ScoreObject>{
    
    public int winrate = 0, lossrate = 0, rangering = 0, antallspill = 0;
    
    public ScoreObject(database.classes.Player spilleren, int twinrate,int tlossrate,int trangering,int tantallspill, session.Session sesjonen){
        winrate = twinrate;
        lossrate = tlossrate;
        rangering = trangering;
        antallspill = tantallspill;
        
        this.put("fornavn",center.helpers.ContentCreator.hentNavn(spilleren, sesjonen));
        this.put("rating",rangering);
        this.put("winrate",winrate);
        this.put("lossrate",lossrate);
        this.put("antall", antallspill);
        this.put("id", spilleren.getId());
    }
    
    public int compareTo(ScoreObject o){
        int enspiller = this.rangering;
        int tospiller = o.rangering;

        if(enspiller > tospiller)
            return -1;
        else if(enspiller == tospiller)
            return 0;
        else
            return 1;
    }
    
}

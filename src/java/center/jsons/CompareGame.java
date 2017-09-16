/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center.jsons;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 *
 * @author Steinar
 */
public class CompareGame extends JSONObject{
    
    database.classes.Game spillet = null;
    int playerone = 0, playertwo = 0;
    ArrayList<int[]> resultatmatch;
    public int[] resultsum = {0,0,0,0};
    
    public CompareGame(database.classes.Game tempspill, int mid, int tint){
        spillet = tempspill;
        playerone = mid;
        playertwo = tint;
        resultatmatch = new ArrayList<int[]>();
    }
    
    public void addResultMatch(database.classes.Gamesession sess, CompareGameMainResult mapresult){
        int[] results = {-1,-1,-1,-1};
        int forste = -1, andre = -1;
        for(database.classes.Participation par : sess.getDeltakelser()){
            if(par.getSpiller().getId() == playerone){
                forste = Math.round(center.helpers.ResultOrganizer.calcResult(sess, par));
            }
            else if(par.getSpiller().getId() == playertwo){
                andre = Math.round(center.helpers.ResultOrganizer.calcResult(sess, par));
            }            
        }
        mapresult.addResults(forste, andre);
        if(forste >= 0 && andre == -1){
            results[0] = forste;
        }
        else if(forste >= 0 && andre >= 0){
            results[1] = forste;
            results[2] = andre;
        }
        else if(forste == -1 && andre >= 0){
            results[3] = andre;
        }
        resultatmatch.add(results);
    }
    public void sumResults(){
        int[] resultcount = {0,0,0,0};
        for(int[] tres : resultatmatch){
            for(int i = 0; i < 4; i++){               
                if(tres[i] > -1){
                    resultcount[i]++;
                    resultsum[i] += tres[i]; 
                }
            }
        }
        for(int i = 0; i < 4; i++){
            if(resultcount[i] == 0){
                resultsum[i] = -1;
            }
            else{
                resultsum[i] = Math.round(resultsum[i] / resultcount[i]);
            }
        }
    }
    
    public void toJson(){
    
        this.put("spillnavn", spillet.getNavn());
        this.put("spillid", spillet.getId());
        JSONArray tallene = new JSONArray();
        for(int i = 0; i < 4; i++){
            JSONObject obv = new JSONObject();
            if(resultsum[i] == -1)
                obv.put("sum"," ");
            else
                obv.put("sum",resultsum[i]);
            tallene.add(obv);
        }
        this.put("tallene", tallene);
    
    }
    
}

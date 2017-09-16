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
public class GameMostPlayed extends JSONObject implements Comparable<GameMostPlayed>{
    
    public int gamesPlayed = 0;
    
    public GameMostPlayed(database.classes.Game spillet, JSONArray array){
            this.put("navn", spillet.getNavn());
            this.put("id", spillet.getId());
            gamesPlayed = spillet.getSesjoner().size();
            this.put("antall", gamesPlayed);

            array.add(this);
    }
    
    public int compareTo(GameMostPlayed o){
        int enspiller = this.gamesPlayed;
        int tospiller = o.gamesPlayed;

        if(enspiller > tospiller)
            return -1;
        else if(enspiller == tospiller)
            return 0;
        else
            return 1;
    }     
    
}

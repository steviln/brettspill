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
public class GameJsonStandard extends JSONObject{
    
    public GameJsonStandard(database.classes.Game spillet, JSONArray array, session.Session sesjon){
            this.put("navn", spillet.getNavn());
            this.put("id", spillet.getId());
            if(sesjon.loggedIn()){
                this.put("redlink","#redigerSpill/" + spillet.getId());
                this.put("redtekst", "Rediger");
            }
            array.add(this);
    }
    
}

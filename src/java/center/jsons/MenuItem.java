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
public class MenuItem extends JSONObject{

    
    public MenuItem(String side, String men, int sid){

        this.put("sideTekst",side);
        this.put("menypunkt", men);
        this.put("sideID", sid);
    }
}



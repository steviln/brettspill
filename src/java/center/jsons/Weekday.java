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
public class Weekday extends JSONObject {
    
    private int day, status;
    
    public Weekday(int theday, int tstatus){
        day = theday;
        status = tstatus;
    }
    
    public int getStatus(){
        return status;
    }
    
    public void prep(int spillerID){
        String[] dags = {"mandag","tirsdag","onsdag","torsdag","fredag","lørdag","søndag"};
        this.put("dag", day);
        this.put("status", status);
        this.put("navn",dags[day]);
        this.put("dagclass", "ukedag" + status);
        this.put("inclass", "inclasshidden");
        this.put("spillerID", spillerID);
    }
}

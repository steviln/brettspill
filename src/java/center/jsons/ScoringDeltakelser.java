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
 * 
 */
public class ScoringDeltakelser{
    
    public ScoringGamesession sesjon;
    public ScoringPlayer spilleren;
    public float poengsum = 0;
    public int plassering = 0;
    
    
    public ScoringDeltakelser(ScoringGamesession tses, ScoringPlayer tspi, float tpo, int top){
        sesjon = tses;
        spilleren = tspi;
        poengsum = tpo;
        plassering = top;
    }
    
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center.jsons;
import org.json.simple.JSONObject;
import java.util.Set;

/**
 *
 * @author Steinar
 */
public class GamerBestRank extends JSONObject implements Comparable<GamerBestRank>{
    
    private int gameID;
    private int gameNumber;
    private int gameScore;
    private String spillNavn;
    private int finalScore;
    
    public GamerBestRank(int nygame, String spillnavnet){
        gameID = nygame;
        gameNumber = 0;
        gameScore = 0;
        spillNavn = spillnavnet;
        finalScore = 0;
    }
    
    public void finalScoring(){
        finalScore = (int) Math.floor(gameScore / gameNumber);
    }
    
    public void setToJson(){
        this.put("spillnavn", spillNavn);
        this.put("snittscore", finalScore);
        this.put("antall", gameNumber);
    }
    
    public void addSesjon(int nyscore){
        gameNumber++;
        gameScore += nyscore;
    }
    
    public int getGameID(){
        return gameID;
    }
    
    public int compareTo(GamerBestRank o){
        int enspiller = this.finalScore;
        int tospiller = o.finalScore;

        if(enspiller > tospiller)
            return -1;
        else if(enspiller == tospiller)
            return 0;
        else
            return 1;
    }    
    
}

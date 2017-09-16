/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center.jsons;
import java.util.ArrayList;


/**
 *
 * @author Steinar
 */
public class CompareGameMainResult {
    
    public int[] rests = {0,0,0,0};
    public int[] restc = {0,0,0,0};
    
    public CompareGameMainResult(){}
    
    public void addResults(int forste, int andre){
        if(forste > -1){
            rests[0] += forste;
            restc[0]++;
        }
        if(andre > -1){
            rests[3] += andre;
            restc[3]++;
        }  
        if(andre > -1 && forste > -1){
            rests[1] += forste;
            restc[1]++;
            rests[2] += andre;
            restc[2]++;            
        }
    }
    
    public String addToResults(String firstname, String secondname){
        String returstreng = "";
        
        if(restc[0] > 0 && restc[3] > 0){
            int differens = Math.round((rests[0] / restc[0]) - (rests[3] / restc[3]));
            if(differens > 0)
                returstreng += firstname + " har bedre resultat enn " + secondname + " med " + Math.abs(differens) + " prosentpoeng totalt. ";
            else
                returstreng += firstname + " har dårligere resultat enn " + secondname + " med " + Math.abs(differens) + " prosentpoeng totalt. ";
        }
        if(restc[1] > 0 && restc[2] > 0){
            int differens = Math.round((rests[1] / restc[1]) - (rests[2] / restc[2]));
            if(differens > 0)
                returstreng += firstname + " har bedre resultat enn " + secondname + " med " + Math.abs(differens) + " prosentpoeng når de spiller mot hverandre. ";
            else
                returstreng += firstname + " har dårligere resultat enn " + secondname + " med " + Math.abs(differens) + " prosentpoeng når de spiller mot hverandre.. ";
        }        
        
        
        return returstreng;
    }
    
}

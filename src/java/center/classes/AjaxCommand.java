/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center.classes;
import java.util.*;

/**
 *
 * @author Steinar
 */
public class AjaxCommand {
    
    public String command = "";
    public int id = 0, subid = 0;
    public String setStreng = "";
   
    public AjaxCommand(String commandstring){
        //System.out.println(commandstring);
        String[] linjer = commandstring.split("/");
        if(linjer.length >= 4){
            command = linjer[3];
            if(linjer.length >= 5){
                try{
                    id = Integer.parseInt(linjer[4]);
                }catch(Exception e){ System.out.println(e.toString()); }
                if(linjer.length >= 6){
                    try{
                        subid = Integer.parseInt(linjer[5]);
                    }catch(Exception e){ System.out.println(e.toString());}
                }
                if(linjer.length >= 7){
                    setStreng = linjer[6];
                }
            }
        }
    }
    
    public AjaxCommand(int mid){
     this.id = mid;
    }
    
}

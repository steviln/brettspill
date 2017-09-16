/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.classes;

import java.util.Date;

/**
 *
 * @author Steinar
 */
public class Daycalendar implements java.io.Serializable{
    
    private Integer id;
    private Integer dag;
    private Player spiller;
    private Integer status;
    
    public Daycalendar(){}
    
    public Daycalendar(Player tspiller, Integer tdag, Integer tstatus){
        this.dag = tdag;
        this.spiller = tspiller;
        this.status = tstatus;
    }
    
    public Integer getId(){
        return this.id;
    }
    public void setId(int tid){
        this.id = tid;
    }
    public Integer getstatus(){
        return this.status;
    }
    public void setStatus(int tid){
        this.status = tid;
    } 
    public Player getSpiller(){
        return this.spiller;
    }
    public void setSpiller(Player tspiller){
        this.spiller = tspiller;
    }
    public Integer getDag(){
        return this.dag;
    }
    public void setDag(Integer tdato){
        this.dag = tdato;
    }
}

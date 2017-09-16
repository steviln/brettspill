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
public class Monthcalendar {
    
    private Integer id;
    private Date fra;
    private Date til;
    private Player spiller;
    private Integer status;
    
    public Monthcalendar(){}
    
    public Monthcalendar(Integer tid, Date tfra, Date ttil, Player tspiller, Integer tstatus){
        this.id = tid;
        this.fra = tfra;
        this.til = ttil;
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
    public Date getFra(){
        return this.fra;
    }
    public void setFra(Date tdato){
        this.fra = tdato;
    }
    public Date getTil(){
        return this.til;
    }
    public void setTil(Date tdato){
        this.til = tdato;
    }    
    
}

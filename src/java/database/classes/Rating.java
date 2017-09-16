/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.classes;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Steinar
 */
public class Rating implements java.io.Serializable{
 
    private Integer id;
    private Integer rating;
    private Integer owner;
    private Game game;
    private Player spiller;
     
    public Rating(){}
    
    public Rating(Integer orgId, Game gamet, Integer spillId, Integer towner, Player spile) {
       this.game = gamet;
       this.rating = spillId;
       this.owner = towner;
       this.id = orgId;
       this.spiller = spile;
    }
    
    public Integer getId() {
        return this.id;
    }    
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getRating() {
        return this.rating;
    }    
    public void setRating(Integer iu) {
        this.rating = iu;
    }
    public Integer getOwner() {
        return this.owner;
    }    
    public void setOwner(Integer pd) {
        this.owner = pd;
    } 
    public Game getGame() {
        return this.game;
    }    
    public void setGame(Game ge) {
        this.game = ge;
    } 
    public Player getSpiller() {
        return this.spiller;
    }    
    public void setSpiller(Player ge) {
        this.spiller = ge;
    }     
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center.classes;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.Calendar;
import java.util.List;
import java.util.GregorianCalendar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.time.DayOfWeek;
import java.time.YearMonth;
/**
 *
 * @author Steinar
 */
public class UserCalendar{
    
    public String[] statuses = {"Aldri","Sjelden","Nøytral","Ofte","Fast"};
    public String[] statuskoder = {"aldriclass","sjeldenclass","noytralclass","ofteclass","fastclass"};
    public JSONArray ukedager = new JSONArray();
    hibernate.HibernateUtil hiber = null;
    AjaxCommand kommando = null;
    session.Session sesjonen = null;
    database.classes.Player eier = null, viewer = null;
    Maaned[] maaneder = null;
    String[] maanednavn = {"","Januar","Februar","Mars","April","Mai","Juni","Juli","August","September","Oktober","November","Desember"};
    String[] dagnavn = {"Ma","Ti","On","To","Fr","Lø","Sø"};
    
    public UserCalendar(hibernate.HibernateUtil thiber, AjaxCommand tkommando, session.Session tsesjonen){
        hiber = thiber;
        kommando = tkommando;
        sesjonen = tsesjonen;
        eier = hibernate.HibernateStatic.getUser(kommando.id, hiber);
        if(sesjonen.loggedIn())
            viewer = sesjonen.getLogUser(hiber);
        database.classes.Daycalendar[] ukda = hibernate.HibernateStatic.getWeekdayList(eier.getId(), hiber);
        int dagteller = 0;
        for(int x = 0; x < 7; x++){
            int setstatus = 2;
            if(dagteller < ukda.length){
                if(ukda[dagteller].getDag() == x){
                    setstatus = ukda[dagteller].getstatus();
                    dagteller++;
                }
            }
            center.jsons.Weekday nuday = new center.jsons.Weekday(x,setstatus);
            nuday.prep(eier.getId());
            ukedager.add(nuday);
        }
        GregorianCalendar now = new GregorianCalendar(); 
        int currmonth = now.get(Calendar.MONTH) + 1;
        int curryear = now.get(Calendar.YEAR);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        LocalDate startdato = LocalDate.parse("1/" + currmonth + "/" + curryear, formatter);
        DayOfWeek startdag = startdato.getDayOfWeek();
        int startvalue = startdag.getValue();
        int useMonth = currmonth;
        
        int dagID = 1;
        maaneder = new Maaned[6];
        for(int a = 0; a < 6; a++){
            maaneder[a] = new Maaned(useMonth, curryear, maanednavn[useMonth]);
            YearMonth yearMonthObject = YearMonth.of(curryear, useMonth);
            int antalldager = yearMonthObject.lengthOfMonth();
            int datoteller = 1;
            int datorad = 0;
            for(int dag = 0; dag < antalldager; dag++){
               center.jsons.Weekday reldag = (center.jsons.Weekday) ukedager.get(startvalue - 1);
               //System.out.println("maaned " + a + " ukedag " + startvalue + " dato " + datoteller + " maaned " + useMonth + " id " + dagID + " status " + reldag.getStatus());
               maaneder[a].dagene[datorad][startvalue - 1] = new Dag(dagID, startvalue, datoteller, reldag.getStatus()); 
               
               dagID++;
               datoteller++;
               if(startvalue == 7){
                   startvalue = 1;
                   datorad++;
               }
               else{
                   startvalue++;
               }
            }
            useMonth++;
            if(useMonth == 13){
                useMonth = 1;
                curryear++;
            }
        }
        for(int a = 0; a < 6; a++){
            maaneder[a].prep(statuskoder);
        }
        //System.out.println("starval " + startvalue);
       
    }
    
    public String hentUtModell(){
        JSONObject modell = new JSONObject();
        modell.put("eierID", eier.getId());
        if(viewer == null)
            modell.put("userID",0);
        else
            modell.put("userID",viewer.getId());
        modell.put("ukedager",ukedager);
        JSONArray mands = new JSONArray();
        for(int a = 0; a < 6; a++){
            mands.add(maaneder[a]);
        }        
        modell.put("ukelandender",mands);
        JSONArray dager = new JSONArray();
        for(int b = 0; b < dagnavn.length; b++){
            dager.add(dagnavn[b]);
        }
        modell.put("dagnavn", dager);
        if(viewer != null & eier != null && viewer.getId() == eier.getId()){
            JSONArray statusenej = new JSONArray();
            for(int a = 0; a < statuses.length; a++){
                JSONObject nu = new JSONObject();
                nu.put("id", a);
                nu.put("navn", statuses[a]);
                nu.put("innklasse","ukedag" + a);
                nu.put("omklasse","calselunselect");
                statusenej.add(nu);
            }
            modell.put("statuser", statusenej);
        }
        
        return modell.toString();
    }
    
}


class Maaned extends JSONObject{
    
    int id;
    int aar;
    String name;
    Dag[][] dagene = null;

    public Maaned(int monthnumb, int numbaar, String monthname){
        id = monthnumb;
        name = monthname;
        aar = numbaar;
        dagene = new Dag[6][7];
        for(int x = 0; x < dagene.length; x++){
            for(int y = 0; y < dagene[0].length; y++){
                dagene[x][y] = null;
            }
        }
    }
    
    public void prep(String[] stats){
        JSONArray uker = new JSONArray();
        for(int x = 0; x < dagene.length; x++){
            JSONArray uke = new JSONArray();
            for(int y = 0; y < dagene[0].length; y++){
                if(dagene[x][y] == null){
                    uke.add(new NullDag());
                }
                else{
                    dagene[x][y].prep(stats, this);
                    uke.add(dagene[x][y]);
                }
            }
            uker.add(uke);
        }
        this.put("uker", uker);
        this.put("navn", name);
    }

}

class Dag extends JSONObject{

    int id;
    int ukedag;
    int dag;
    int status;
    
    public Dag(){}
    
    public Dag(int tud, int ukd, int tdag, int tstatus){
        id = tud;
        ukedag = ukd;
        dag = tdag;
        status = tstatus;
    }
    
    public void prep(String[] stats, Maaned parent){
       this.put("klasse",stats[status]);
       this.put("dato", dag);
       this.put("id", id);
       this.put("datestring", "" + dag + "/" + parent.id + "/" + parent.aar);
    }

}

class NullDag extends Dag{
    
    public NullDag(){
        this.put("klasse","calendarnull");
        this.put("dato"," ");
        this.put("id",0);
    }
}



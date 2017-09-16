/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate;


import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author Steinar
 */
public class HibernateUtil {

    private SessionFactory sessionFactory;
    public String feilmelding = "ingenfeil";
    private Session dbaseses = null;
    public String baseURL = "http://www.drammenbrettspill.com";
    
    public HibernateUtil(String miljo){
        
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            Class.forName("com.mysql.jdbc.Driver");
            //System.out.println(org.hibernate.Version.getVersionString());
            
            //sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
            if(miljo.equals("localhost")){
                sessionFactory = new Configuration().configure("local.cfg.xml").buildSessionFactory();
                baseURL = "http://localhost:8080";
            }
            else{
                //Configuration configuration = new Configuration();
                //configuration.configure("production.cfg.xml");
                //StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                //sessionFactory = configuration.buildSessionFactory(ssrb.build());
                sessionFactory = new Configuration().configure("production.cfg.xml").buildSessionFactory();
           
                //sessionFactory = new Configuration().configure("production.cfg.xml").buildSessionFactory();
            }
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed to string: " + ex.getCause());
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public org.hibernate.Session Sestart(){
        if(dbaseses == null){
            dbaseses = getFabrikkSession();
            return dbaseses;
        }else{
            return dbaseses;
        }
    }
    
    public org.hibernate.Session getFabrikkSession(){
        Session sesjon = null;
        try{
            sesjon = sessionFactory.openSession();
        }catch(Exception e){ System.out.println("ville ikke starte " + e.toString()); }
        return sesjon;
    }     
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
   
    
  
     

    
    public Object addItem(Object newItem){
        try{
            Session sesjon = Sestart();
            sesjon.beginTransaction();
            sesjon.save(newItem);
            sesjon.getTransaction().commit();
        }catch(Exception e){ System.out.println(e.toString()); }
        return newItem;
    }
    
    public Object updateItem(Object oldItem){
        try{
            Session sesjon = Sestart();
            sesjon.beginTransaction();
            sesjon.update(oldItem);
            sesjon.getTransaction().commit();
            //System.out.println("Should have changed");
        }catch(Exception e){ System.out.println("Her går det dårlig " + e.toString()); }
        return oldItem;
    } 
    
    public void deleteItem(Object theItem){
        try{
            Session sesjon = Sestart();
            sesjon.beginTransaction();
            sesjon.delete(theItem);
            sesjon.getTransaction().commit(); 
        }catch(Exception e){ System.out.println("Did not delete " + e.toString());}
    }
    

    
    public void lukk(){
 
        if(dbaseses != null){
            dbaseses.close();
            dbaseses = null;
        }
        if(!sessionFactory.isClosed()){
            sessionFactory.close();
        }
    }    
    
    @Override
    protected void finalize() throws Throwable {
        System.out.println("finaliseres og stenges");
        super.finalize();
        if(dbaseses != null){
            dbaseses.close();
        }
        else{
            System.out.println("var stengt allerede");
        }
        if(!sessionFactory.isClosed()){
            sessionFactory.close();
        }
    }
}

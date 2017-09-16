/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center;

import center.classes.AjaxCommand;
import com.restfb.types.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;
import java.util.Map;
import org.hibernate.cfg.Configuration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Steinar
 */
public class Facebook extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        hibernate.HibernateUtil hiber = null;
        
        try (PrintWriter out = response.getWriter()) {
            
            
  
            AjaxCommand kommando = new AjaxCommand(request.getRequestURI()); 
            //out.write(kommando.command);
            String miljo = request.getServerName();
            hiber = new hibernate.HibernateUtil(miljo);
            session.Session sesjonen = new session.Session(0, request.getSession().getId(), 0, "", hiber);
            
            
            switch(kommando.command){
               
                    case "Namecheck":
                        //System.out.println("Facebook log med ret: " + request.getParameter("uid") + " token: " + request.getParameter("accessToken"));
                        out.write(sesjonen.autenticateUserFacebook(facebook.FacebookFetcher.getUserData(request.getParameter("accessToken")), hiber));
                    break;
                    case "ReditToFacebook":
                        //out.write(request.getParameter("code"));
                        if(request.getParameterMap().containsKey("code")){
                            String redistreng = "https://graph.facebook.com/v2.8/oauth/access_token?" +
                                            "client_id=941343709256136" +
                                            "&redirect_uri=" + hiber.baseURL + "/Boardgame/Facebook/ReditToFacebook" +
                                            "&client_secret=8c938063c5fb8f919dfeed0628751a14" +
                                            "&code=" + request.getParameter("code");
                        //out.write(sesjonen.autenticateUserFacebook(facebook.FacebookFetcher.getUserData(request.getParameter("code")), hiber));
                            JSONParser parser = new JSONParser();
                            JSONObject json = (JSONObject) parser.parse(inout.NetGetConnection.getUrlAsString(redistreng));
                            String access_token = json.get("access_token").toString();
                            String resp = sesjonen.autenticateUserFacebook(facebook.FacebookFetcher.getUserData(access_token), hiber);
                            if(resp.equals("SUCCESS")){
                                response.sendRedirect(hiber.baseURL + "/Boardgame");
                            }
                            else{
                                response.sendRedirect(hiber.baseURL + "/Boardgame/facebooklog/1");
                            }
                        }
                        else{
                            response.sendRedirect(hiber.baseURL + "/Boardgame/facebooklog/0");
                        }
                    break;

            }
            hiber.lukk();
            hiber = null;
            out.close();

        }
        catch(Exception e){ e.printStackTrace(System.out); if(hiber != null){ hiber.lukk(); } }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package center;

import center.classes.AjaxCommand;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Steinar
 */
public class Boardgame extends HttpServlet {

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
            hiber = new hibernate.HibernateUtil(request.getServerName());
            session.Session sesjonen = new session.Session(0, request.getSession().getId(), 0, "", hiber);
            
            String[] logables = {"CreateUpdateGame","CreateUpdateFaction","LogGame","CreateCompany","CreateNewPlayer","CreateUpdateUser","LagKalenderKjede"};
            //System.out.println("vis den " + kommando.command + ":" + kommando.setStreng + ":" + kommando.setStreng.length());
            if(Arrays.asList(logables).contains(kommando.command) && !sesjonen.loggedIn()){
                out.write("NOTLOGGEDIN");
            }
            else{
            
                switch(kommando.command){
               
                    case "DoLogin":
                        if(!sesjonen.loggedIn()){
                            out.write(sesjonen.autenticateUser(request.getParameter("username"),request.getParameter("password"), hiber));
                        }
                        else
                            out.write("ERROR");
                        break;
                    case "Logout":
                        out.write(sesjonen.logout(hiber));
                        break;   
                    case "Tester":
                        out.write("data tester");
                        break;                        
                    case "GetMenu":
                        out.write(center.helpers.ContentCreator.skapMeny(sesjonen, hiber));
                        break;
                    case "Calendar":
                        out.write(center.helpers.ContentCreator.skapVisKalender(sesjonen,hiber,kommando));
                        break;
                    case "EndreDagKalender":
                        out.write(center.helpers.SaveAndEdit.changeDayCalendar( hiber, request, sesjonen));
                        break;
                    case "GetFrontpage":
                        out.write(center.helpers.ContentCreator.skapFrontpage(sesjonen, hiber));
                        break;
                    case "GetPlayersForPreset":
                        out.write(center.helpers.ContentCreator.hentSpillerForPrerang(sesjonen, hiber));
                        break;
                    case "GetGamesList":
                        out.write(center.helpers.ContentCreator.skapGamesList(sesjonen, hiber));
                        break;
                    case "GetGameInfo":
                        out.write(center.helpers.ContentCreator.skapGameInfo(sesjonen, hiber, kommando));
                        break;
                    case "EditGameInfo":
                        out.write(center.helpers.ContentCreator.skapEditGameInfo(sesjonen, hiber, kommando));
                        break;
                    case "CreateUpdateGame":
                        out.write(center.helpers.SaveAndEdit.createUpdateGame(hiber, request));
                        break;
                    case "CreateUpdateUser":
                        out.write(center.helpers.SaveAndEdit.createUpdateUser(hiber, request, sesjonen));
                        break;
                    case "EditFactionInfo":
                        out.write(center.helpers.ContentCreator.skapEditFactionInfo(sesjonen, hiber, kommando));
                        break;
                    case "CreateUpdateFaction":
                        out.write(center.helpers.SaveAndEdit.createUpdateFaction(hiber, request, kommando));
                        break; 
                    case "EditScenarioInfo":
                        out.write(center.helpers.ContentCreator.skapEditScenarioInfo(sesjonen, hiber, kommando));
                        break;
                    case "CreateUpdateScenario":
                        out.write(center.helpers.SaveAndEdit.createUpdateScenario(hiber, request, kommando));
                        break;                        
                    case "EditSession":
                        out.write(center.helpers.ContentCreator.skapEditSessionInfo(sesjonen, hiber, kommando));
                        break;
                    case "LogGame":
                        String returstreng = "";
                        returstreng = center.helpers.SaveAndEdit.createAndUpdateSession(hiber, request, sesjonen);      

                        out.write(returstreng);
                        break;
                    case "CreateCompany":
                        out.write(center.helpers.SaveAndEdit.createCompany(hiber, request));
                        break;
                    case "CreateNewPlayer":
                        out.write(center.helpers.SaveAndEdit.createNewPlayer(hiber, request));
                        break;
                    case "GetSessionList":
                        out.write(center.helpers.ContentCreator.skapSesjonsListe(sesjonen, hiber, kommando));
                        break;
                    case "GetSessionVis":
                        out.write(center.helpers.ContentCreator.hentSesjonsInfo(sesjonen, hiber, kommando));
                        break;
                    case "GetSpillerliste":
                        out.write(center.helpers.ContentCreator.skapSpillerliste(sesjonen, hiber));
                        break;
                    case "GetPlayerProfile":
                        out.write(center.helpers.ContentCreator.getPlayerProfile(hiber, kommando.id, sesjonen));
                        break;
                    case "GetCompanyList":
                        out.write(center.helpers.ContentCreator.getCompanyList(sesjonen, hiber));
                        break;
                    case "GetCompanyInfo":
                        out.write(center.helpers.ContentCreator.getCompanyInfo(sesjonen, hiber, kommando));
                        break; 
                    case "GetUserDetails":
                        out.write(center.helpers.ContentCreator.getUserEditInfo(sesjonen, hiber, kommando));
                        break;
                    case "RankGame":
                        out.write(center.helpers.SaveAndEdit.rankGame(sesjonen, hiber, request));
                        break;
                    case "SetGameOwner":
                        out.write(center.helpers.SaveAndEdit.setGameOwner(sesjonen, hiber, request));
                        break;
                    case "ComparePlayers":
                        out.write(center.helpers.ContentCreator.comparePlayers(sesjonen, hiber, kommando));
                        break;
                    case "GetGameMatrix":
                        out.write(center.helpers.ContentCreator.gameMatrix(sesjonen, hiber, kommando));
                        break;
                    case "LagKalenderKjede":
                        out.write(center.helpers.SaveAndEdit.lagKalenderZone(hiber,request, sesjonen));
                        break;
                }
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

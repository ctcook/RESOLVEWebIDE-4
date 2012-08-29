/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ui.projectGeneration.WorkspaceJsonGenerator;

/**
 *
 * @author Chuck
 */
@WebServlet(name = "GetComponents", urlPatterns = {"/GetComponents"})
public class GetComponents extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("application/json");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
            //out.println("<html>");
            //out.println("<head>");
            //out.println("<title>Servlet GetComponents</title>");            
            //out.println("</head>");
            //out.println("<body>");
            //out.println("<h1>Servlet GetComponents at " + request.getContextPath() + "</h1>");
            WorkspaceJsonGenerator wsg = new WorkspaceJsonGenerator();
            String json = wsg.generateJSON("Chuck_Workspace");
            BufferedWriter bw = null;
            try{
                String path = request.getServletContext().getRealPath("");
                File jsonFile = new File(path+"\\data.json");
                System.out.println(jsonFile.getAbsolutePath());
                //if(!jsonFile.exists()){
                jsonFile.createNewFile();
                bw = new BufferedWriter(new FileWriter(jsonFile));
                bw.write(json);
                bw.close();
                //}
            } catch(Exception ex){
                System.out.println(ex);
            }
            
            out.println();
            //out.println("</body>");
            //out.println("</html>");
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
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
     * Handles the HTTP
     * <code>POST</code> method.
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

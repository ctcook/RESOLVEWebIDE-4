/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import ui.init.InterfaceConfig;

/**
 *
 * @author Chuck
 */
@WebServlet(name = "ContextParamTest", urlPatterns = {"/ContextParamTest"})
public class ContextParamTest extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(true);
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
            //out.println("<html>");
            //out.println("<head>");
            //out.println("<title>Servlet ContextParamTest</title>");            
            //out.println("</head>");
            //out.println("<body>");
            ServletContext context = getServletContext();
            
            InterfaceConfig ic = new InterfaceConfig();
            if(ic.getInterfaceConfig(request, context, session)){
                String targetJSON = request.getParameter("target");
                if(targetJSON != null){
                    JSONObject target = new JSONObject(targetJSON);
                    String ws = target.getString("ws");
                    out.println("workspace -> "+ws+"<br/>");
                    JSONObject component = target.getJSONObject("component");
                    out.println("component -> "+component.getString("name"));
                }
                //out.println(targetJSON);
                String userJSON = request.getParameter("userComponents");
                if(userJSON != null){
                    JSONArray userComponents = new JSONArray(userJSON);
                    for(int i = 0; i < userComponents.length(); i++){
                        JSONObject uc = userComponents.getJSONObject(i).getJSONObject("component");
                        
                        String name = uc.getString("name");
                        String pkg = uc.isNull("pkg")?"null":uc.getString("pkg");
                        out.println(name + " -> " + pkg + "<br/>");
                    }
                }
            }
            else{
                out.println("<h1>Error initializing config</h1>");
            }
            
            //out.println("</body>");
            //out.println("</html>");
        } catch (Exception ex) {
            System.out.println(ex);
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

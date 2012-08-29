/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.frontend;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Chuck
 */
@WebServlet(name = "Index", urlPatterns = {"/Index"})
public class Index extends HttpServlet {

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
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
            out.println("<html>");
            out.println(genHead(request));
            out.println("<body>");
            out.println("<h1>New RESOLVE Web Interface</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }
    
        private String genHead(HttpServletRequest request){
        StringBuilder sb = new StringBuilder();
        String[] scripts = {
            "js/lib/jquery-1.7.2.min.js",
            //"js/lib/jquery-ui-1.8.17.custom.min.js",
            //"js/lib/jquery-ui-1.8.5.custom.min.js",
            //"js/lib/ui.tabs.closable.min.js",
            //"js/lib/modernizr-1.6.min.js",
            //"js/lib/antlr/antlr3-all-min.js",
            //"js/lib/antlr/RLexer-min.js",
            //"js/lib/antlr/RParser-min.js",
            //"js/parserJS.js",
            //"js/errorJS.js",
            //"js/vcJS.js",
            //"js/highlight/shCore.js",
            //"js/highlight/shBrushJava.js",
            //"js/utilityFunctions.js",
            //"js/appGlobalVars.js",
            //"js/appClasses.js",
            //"js/handlers.js",
            //"js/mainJS.js",
            //"js/browserJS.js",
            //"js/editor/ace.js",
            //"js/editor/theme-chrome.js",
            //"js/editor/theme-textmate.js", this is broken as of the latest ACE editor
            //"js/editor/mode-resolve.js",
            //"js/editor/mode-java.js"
        };
        String[] styleSheets = {
            //"css/main.css",
            //"css/jquery.ui.base.css",
            //"css/jquery-ui-1.8.5.custom.css",
            //"css/shCore.css",
            //"css/shThemeDefault.css",
            //"css/shThemeEclipse.css"
        };
        sb.append("<head>\n");
        sb.append("<title>RESOLVE Web IDE</title>\n");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9\">\n");
        for(String js : scripts){
            sb.append("<script type=\"text/javascript\" src=\"");
            sb.append(js);
            sb.append("\"></script>\n");
        }
        for(String css : styleSheets){
            sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            sb.append(css);
            sb.append("\"/>\n");
        }
        sb.append("</head>\n");
        return sb.toString();
    }
    
    private String genBody(String selectedWsName, HttpSession session){
        StringBuilder sb = new StringBuilder();
        sb.append("<body id=\"body\" onload=\"loadEnv(");
        if(selectedWsName != null){
            sb.append("&quot;");
            sb.append(selectedWsName);
            sb.append("&quot;");
        }
        sb.append(")\">\n");
        sb.append(genMasterDiv(session));
        sb.append("\n");
        sb.append("</body>\n");
        return sb.toString();
    }
    
    private String genMasterDiv(HttpSession session){
        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"masterDiv\">\n");
        sb.append(genUpperMenu(session));
        sb.append("<div id=\"infoBlock\" class=\"infoBlockSmall\"></div>");
        sb.append(genContentTabs(session));
        sb.append("</div>\n");
        return sb.toString();
    }
    
    private String genUpperMenu(HttpSession session){
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        if(loggedIn == null){
            loggedIn = false;
        }
        String userType = (String) session.getAttribute("userType");
        if(userType == null){
            userType = "user";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"upperMenu\">\n");
            sb.append("<div id=\"workspaceInfo\" class=\"top_menu_left\">\n");
                sb.append("<div id=\"pDiv\" class=\"topMenuItem top_menu_left\">Initializing...\n");
                sb.append("</div>\n");
            sb.append("</div>\n");
            //sb.append("<div id=\"topRightMenuItems\" class=\"top_menu_right\">\n");
            sb.append("<div class=\"top_menu_right\">\n");
                sb.append("<div id=\"html5Upload\" class=\"topMenuItem top_menu_left\" title=\"Click and drag a workspace file here to upload\">\n");
                    sb.append("<img id=\"html5UploadImg\" alt=\"Quick Load\" title=\"Click and drag a workspace file here to upload\" src=\"images/Load.png\"/>\n");
                sb.append("</div>\n");
                sb.append("<div id=\"menuItemsEnv\" class=\"topMenuItem top_menu_left\">\n");
                    sb.append("<span id=\"envLink\">Environment</span>\n");
                    sb.append("<div id=\"envMenu\" class=\"topMenuItemOptions\" align=\"center\">\n");
                        sb.append("<a class=\"menuItemsInner env_info\" href=\"#\" title=\"Display Environment Info\">Info</a>\n");
                        sb.append("<a class=\"menuItemsInner env_load\" href=\"#\" title=\"Import Environment Backup From File\">Import</a>\n");
                        sb.append("<a class=\"menuItemsInner env_backup\" href=\"#\" title=\"Export Environment as File\">Export</a>\n");
                        sb.append("<a class=\"menuItemsInner env_reset\" href=\"#\" title=\"Reset Environment to Default\">Reset</a>\n");
                        if(loggedIn){
                            if(userType.equals("admin")){
                                sb.append("<a class=\"menuItemsInner env_admin\" href=\"#\" title=\"Environment Administration\">Admin</a>\n");
                            }
                            sb.append("<a class=\"menuItemsInner env_profile\" href=\"#\" title=\"User Profile\">Profile</a>\n");
                            sb.append("<a class=\"menuItemsInner env_logout\" href=\"#\" title=\"User Logout\">Logout</a>\n");
                        }
                        else{
                            sb.append("<a class=\"menuItemsInner env_login\" href=\"#\" title=\"User Login\">Login</a>\n");
                        }
                    sb.append("</div>\n");
                sb.append("</div>\n");
                sb.append("<div id=\"menuItemsHelp\" class=\"topMenuItem top_menu_left\">\n");
                    sb.append("<span id=\"helpLink\">Help&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>\n");
                    sb.append("<div id=\"helpMenu\" class=\"topMenuItemOptions\" align=\"center\">\n");
                        sb.append("<a class=\"menuItemsInner help_overview\" href=\"#\">IDE Overview</a>\n");
                        sb.append("<a class=\"menuItemsInner help_rvc\" href=\"#\">Verifying Compiler</a>\n");
                        sb.append("<a class=\"menuItemsInner help_tutorial\" href=\"#\">Tutorials</a>\n");
                        sb.append("<a class=\"menuItemsInner help_bugreport\" href=\"#\">Report Bug</a>\n");
                        //sb.append("<a class=\"menuItemsInner help_tips\" href=\"#\">Tips</a>\n");
                    sb.append("</div>\n");
                sb.append("</div>\n");
            sb.append("</div>\n");
        sb.append("</div>\n");
        return sb.toString();
    }
    
    private String genContentTabs(HttpSession session){
        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"contentTabs\">\n");
            sb.append(genUpperTabs(session));
            sb.append(genLowerTabs(session));
            //sb.append("<div id=\"infoTab\" class=\"infoBlockLarge\">\n");
                //sb.append("<div id=\"infoContent\"></div>\n");
            //sb.append("</div>\n");
            sb.append("<div id=\"vcBox\"></div>");
            sb.append("<div id=\"notificationBox\"></div>");
            sb.append("<div id=\"console\"></div>\n");
            sb.append("<div id=\"hitCount\" class=\"footer\"></div>\n");
            sb.append("<div class=\"footer\">\n");
                sb.append("This research is funded in part by NSF grants DUE-1022941, DUE-0633506, DMS-0701187, DMS-0811748, CCF-0811748.<br/>\n");
            sb.append("</div>\n");
            sb.append("<div class=\"footer\">\n");
                //sb.append("Site maintained by <a href=\"mailto:ctcook@clemson.edu\" style=\"text-decoration: underline\" >Chuck Cook</a><br/>\n");
                sb.append("<a href=\"http://www.cs.clemson.edu/group/resolve\" style=\"text-decoration: underline\" target=\"_blank\">Clemson RESOLVE RSRG</a>\n");
            sb.append("</div>\n");
            sb.append("<div id=\"downloadDiv\" class=\"hiddenDiv\"></div>\n");
            sb.append("<div id=\"background\" class=\"background\"></div>\n");
            sb.append("<div id=\"VCs\">&nbsp;</div>\n");
        sb.append("</div>\n");
        return sb.toString();
    }
    
    private String genUpperTabs(HttpSession session){
        String userType = (String) session.getAttribute("userType");
        if(userType == null){
            userType = "user";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"upperTabs\">\n");
            sb.append("<ul>\n");
                sb.append("<li>\n");
                    sb.append("<div id=\"fDropDownDiv\" class=\"dropDownDiv\">\n");
                        sb.append("<img id=\"fList\" src=\"images/down.gif\" alt=\"\" class=\"fileListLink\"/>\n");
                    sb.append("</div>\n");
                    sb.append("<div id=\"facType\" class=\"menuHead tabHead\">Facility Menu</div>\n");
                    sb.append("<a href=\"#fTab\" style=\"text-align:left\">\n");
                        sb.append("<span id=\"facName\" class=\"fName\">Facilities</span>\n");
                    sb.append("</a>\n");
                    sb.append("<div id=\"fMenu\" class=\"listItemDiv\"></div>\n");
                sb.append("</li>\n");
                sb.append("<li>\n");
                    sb.append("<div id=\"cDropDownDiv\" class=\"dropDownDiv\">\n");
                        sb.append("<img id=\"cList\" src=\"images/down.gif\" alt=\"\" class=\"fileListLink\"/>\n");
                    sb.append("</div>\n");
                    sb.append("<div id=\"conType\" class=\"menuHead tabHead\">Concept Menu</div>\n");
                    sb.append("<a href=\"#cTab\" style=\"text-align:left\">\n");
                        sb.append("<span id=\"conName\" class=\"fName\">Concepts</span>\n");
                    sb.append("</a>\n");
                    sb.append("<div id=\"conceptMenu\" class=\"listItemDiv\"></div>\n");
                sb.append("</li>\n");
                sb.append("<li>\n");
                    sb.append("<div id=\"crDropDownDiv\"  class=\"dropDownDiv\">\n");
                        sb.append("<img id=\"crList\" src=\"images/down.gif\" alt=\"\" class=\"fileListLink\"/>\n");
                    sb.append("</div>\n");
                    sb.append("<div id=\"conRealizType\" class=\"menuHead tabHead\">Concept Realization Menu</div>\n");
                    sb.append("<a href=\"#cRealizTab\">\n");
                        sb.append("<span id=\"conRealizName\" class=\"fName\">Concept Realizations</span>\n");
                    sb.append("</a>\n");
                    sb.append("<div id=\"crMenu\" class=\"listItemDiv\"></div>\n");
                sb.append("</li>\n");
                sb.append("<li>\n");
                    sb.append("<div id=\"eDropDownDiv\"  class=\"dropDownDiv\">\n");
                        sb.append("<img id=\"enhList\" src=\"images/down.gif\" alt=\"\" class=\"fileListLink\"/>\n");
                    sb.append("</div>\n");
                    sb.append("<div id=\"enhType\" class=\"menuHead tabHead\">Enhancement Menu</div>\n");
                    sb.append("<a href=\"#eTab\">\n");
                        sb.append("<span id=\"enhName\" class=\"fName\">Enhancements</span>\n");
                    sb.append("</a>\n");
                    sb.append("<div id=\"enhMenu\" class=\"listItemDiv\"></div>\n");
                sb.append("</li>\n");
                sb.append("<li>\n");
                    sb.append("<div id=\"erDropDownDiv\"  class=\"dropDownDiv\">\n");
                        sb.append("<img id=\"erList\" src=\"images/down.gif\" alt=\"\" class=\"fileListLink\"/>\n");
                    sb.append("</div>\n");
                    sb.append("<div id=\"enhRealizType\" class=\"menuHead tabHead\">Enhancement Realization Menu</div>\n");
                    sb.append("<a href=\"#eRealizTab\">\n");
                        sb.append("<span id=\"enhRealizName\" class=\"fName\">Enhancement Realizations</span>\n");
                    sb.append("</a>\n");
                    sb.append("<div id=\"erMenu\" class=\"listItemDiv\"></div>\n");
                sb.append("</li>\n");
                if(userType.equals("superuser") || userType.equals("admin")){
                    sb.append("<li><span id=\"bSpan\"><a href=\"#bTab\">Browser</a></span></li>\n");
                }
                //sb.append("<li><span id=\"bSpan\"><a href=\"#bTab\">Browser</a></span></li>\n");
            sb.append("</ul>\n");
        sb.append("</div>\n");
        return sb.toString();
    }
    
    private String genLowerTabs(HttpSession session){
        String userType = (String) session.getAttribute("userType");
        if(userType == null){
            userType = "user";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"lowerTabs\">\n");
            sb.append("<div id=\"fTab\">\n");
                sb.append("<div id=\"fContent\" class=\"contentDiv\">Facility tab</div>\n");
            sb.append("</div>\n");
            sb.append("<div id=\"cTab\">\n");
                sb.append("<div id=\"cContent\" class=\"contentDiv\">Please select a concept</div>\n");
            sb.append("</div>\n");
            sb.append("<div id=\"cRealizTab\">\n");
                sb.append("<div id=\"crContent\" class=\"contentDiv\">Please select a Concept Realization</div>\n");
            sb.append("</div>\n");
            sb.append("<div id=\"eTab\">\n");
                sb.append("<div id=\"eContent\" class=\"contentDiv\">Please select an Enhancement</div>\n");
            sb.append("</div>\n");
            sb.append("<div id=\"eRealizTab\">\n");
                sb.append("<div id=\"erContent\" class=\"contentDiv\">Please select an Enhancement Realization</div>\n");
            sb.append("</div>\n");
            if(userType.equals("superuser") || userType.equals("admin")){
                sb.append("<div id=\"bTab\">\n");
                    sb.append("<div id=\"bContent\"></div>\n");
                sb.append("</div>\n");
            }
            /*sb.append("<div id=\"bTab\">\n");
                sb.append("<div id=\"bContent\"></div>\n");
            sb.append("</div>\n");*/
            sb.append("<div id=\"infoTab\" class=\"infoBlockLarge\">\n");
                sb.append("<div id=\"infoContent\"></div>\n");
            sb.append("</div>\n");
        sb.append("</div>\n");
        return sb.toString();
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

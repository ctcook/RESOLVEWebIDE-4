/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.compiler;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.data.MetaFile;
import edu.clemson.cs.r2jt.data.ModuleKind;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.json.JSONException;
import org.json.JSONObject;
import ui.init.InterfaceConfig;

/**
 *
 * @author Chuck
 */
public class WebProver extends WebSocketServlet {
    HttpSession mySession;
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
    /*protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            session = request.getSession(true);
            session.setAttribute("test", "hello");
            
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet WebProver</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet WebProver at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }*/

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
        mySession = request.getSession();
        InterfaceConfig ic = new InterfaceConfig();
        if(ic.getInterfaceConfig(request, getServletContext(), mySession)){
            mySession.setAttribute("ic", ic);
            super.doGet(request, response);
        }
        //session.setAttribute("message", "initialized");
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


    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol) {
        return new VerifierInbound();
    }

    private final class VerifierInbound extends MessageInbound {

        //private final String nickname;

        private VerifierInbound() {
            //this.nickname = GUEST_PREFIX;
        }

        @Override
        protected void onOpen(WsOutbound outbound) {
            //String message = String.format("* %s %s",
                    //nickname, "has joined.");
            broadcast("Socket established");
        }

        @Override
        protected void onClose(int status) {
            //String message = String.format("* %s %s",
                    //nickname, "has disconnected.");
            //broadcast(message);
        }

        @Override
        protected void onBinaryMessage(ByteBuffer message) throws IOException {
            throw new UnsupportedOperationException(
                    "Binary message not supported.");
        }

        @Override
        protected void onTextMessage(CharBuffer message) throws IOException {
            // Never trust the client
            //String filteredMessage = String.format("%s: %s",
                    //nickname, HTMLFilter.filter(message.toString()));
            if(!message.toString().equals("ping")){
                verify(message.toString(), getWsOutbound());
            }
            //broadcast(message.toString());
        }

        private void broadcast(String message) {
            //for (ChatMessageInbound connection : connections) {
                try {
                    //String prevMessage = (String)session.getAttribute("message");
                    //session.setAttribute("message", message);
                    CharBuffer buffer = CharBuffer.wrap(message);
                    //System.out.println(buffer);
                    WsOutbound outbound = getWsOutbound();
                    outbound.writeTextMessage(buffer);
                    //buffer = CharBuffer.wrap("previous message: "+prevMessage);
                    //outbound.writeTextMessage(buffer);
                } catch (IOException ignore) {
                    // Ignore
                }
            //}
        }
        
        private void verify(String targetJSON, WsOutbound outbound){
            MetaFile inputFile = null;
            InterfaceConfig ic = (InterfaceConfig) mySession.getAttribute("ic");
            String selectedWsDir = ic.getWorkspacesDir() + "Default_Project" + System.getProperty("file.separator");
            String myCompilerMainDir = selectedWsDir + ic.getRelativeMainDir();
            //String targetJSON = request.getParameter("target");
            if(targetJSON != null){
                try {
                    JSONObject target = new JSONObject(targetJSON);
                    String ws = target.getString("ws");
                    JSONObject componentJSON = target.getJSONObject("component");
                    inputFile = getTargetMetaFile(componentJSON);
                    mySession.setAttribute("fileName", inputFile.getMyFileName());
                    
                } catch (JSONException ex) {
                    Logger.getLogger(WebCompiler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }

            //Create an object then use it to make a usable hashmap of user files
            InputFileModule fileModule = new InputFileModule(inputFile);
            //InputFileModule fileModule = new InputFileModule(request, fileSource, kind);
            fileModule.createMetaFiles(ic, "");
            HashMap<String, MetaFile> userFileMap = fileModule.getMetaFiles();


            StringBuffer opBuffer = new StringBuffer();
            try{
                int myProverTimeoutLength = ic.getProverTimeout() * 1000;
                String[] args = {"-maindir", myCompilerMainDir, "-vcs", 
                    "-listVCs", "-quickprove", "-webinterface",
                    "-timeout", Integer.toString(5000)};
                ResolveCompiler r = new ResolveCompiler(args, inputFile, userFileMap);

                VerifyInvoker vi = new VerifyInvoker(r, args, outbound);
                CharBuffer buffer = CharBuffer.wrap("Verifier invoked");
                outbound.writeTextMessage(buffer);
                opBuffer = vi.verifyResolve();

            }catch(Exception ex){
                System.out.println(ex);
                //out.println();
                //out.println();
            }
            broadcast("completed");
            //broadcast(opBuffer.toString());
            /*finally {
                // If the user's browser supports gzip encoding we gzip and send it
                if(supportsGzip){
                    try{
                        GZIPOutputStream gzos = new GZIPOutputStream(response.getOutputStream());
                        gzos.write(opBuffer.toString().getBytes());
                        gzos.close();
                    }catch(IOException e){

                    }
                }
                else{
                    //out.close();
                }
            }*/

            // delete all the files created
            /*if(tempWsDirFile != null){
                String dir = tempWsDirFile.getAbsolutePath();
                if(deleteDir(tempWsDirFile)){
                    System.out.println("Successfully deleted temp dir: "+dir);
                }
            }*/

            //mySession.removeAttribute("userFileNames");
            //mySession.removeAttribute("userNamePairs");
            //mySession.removeAttribute("fileName");
        }
        
        private MetaFile getTargetMetaFile(JSONObject componentJSON){
            MetaFile targetFile = null;
            try {
                String fileName = componentJSON.getString("name");
                String fileSource = decodePlusSign(decode(componentJSON.getString("content")));
                String pkg = componentJSON.getString("pkg");
                ModuleKind kind = getFileKind(componentJSON.getString("type"));
                targetFile = new MetaFile(fileName, pkg, pkg, fileSource, kind);
                return targetFile;
            } catch (JSONException ex) {
                Logger.getLogger(WebCompiler.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                return targetFile;
            }
        }
        
        private String decode(String raw){
            String encoded = null;
            try {
                encoded = URLDecoder.decode(raw.replaceAll("%20", " "), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(WebCompiler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return encoded;
        }
        
        private String decodePlusSign(String input){
            String target = "\\%2B";
            String replace = "+";
            Pattern pattern = Pattern.compile(target);
            Matcher matcher = pattern.matcher(input);
            return matcher.replaceAll(replace);
        }
        
        private ModuleKind getFileKind(String fileType){
            ModuleKind kind = null;
            if(fileType.equals("c")){
                kind = ModuleKind.CONCEPT;
            }
            else if(fileType.equals("r")){
                kind = ModuleKind.CONCEPT_BODY;
            }
            else if(fileType.equals("e")){
                kind = ModuleKind.ENHANCEMENT;
            }
            else if(fileType.equals("er")){
                kind = ModuleKind.ENHANCEMENT_BODY;
            }
            else if(fileType.equals("f")){
                kind = ModuleKind.FACILITY;
            }
            return kind;
        }
    }
}

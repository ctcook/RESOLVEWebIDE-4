package ui.compiler;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.data.MetaFile;
import edu.clemson.cs.r2jt.data.ModuleKind;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Stack;
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
import org.json.JSONException;
import org.json.JSONObject;
import ui.init.InterfaceConfig;
import ui.init.UserEvent;
/**
 *
 * @author Chuck
 */
public class WebCompiler extends HttpServlet {

    private String myConceptsDir = null;
    private String myFacilitiesLoc = null;
    private String myCompilerMainDir = null;
    private String myTempFileDir = null;
    private Integer myProverTimeoutLength = 0;

    StringBuffer outputBuffer = new StringBuffer();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/xml");
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        }

        //String fileName = null;
        //Stack<MetaFile> userMetaFiles = new Stack<MetaFile>();
        HashMap<String, MetaFile> userFileMap = new HashMap<String, MetaFile>();
        StringBuffer opBuffer = new StringBuffer();
        //PrintWriter out = response.getWriter();
        //OutputStream outputStream = response.getOutputStream();
        //PrintStream redirect = new PrintStream(outputStream);
        //PrintWriter out = new PrintWriter(outputStream);
        
        //Compiler that will be used for everything
        ResolveCompiler r = null;
        

        //we now check for gzip capable browsers
        //resolves potential encoding problems
        String encoding = request.getHeader("Accept-Encoding");
        boolean supportsGzip = false;
        if(encoding != null){
            if(encoding.toLowerCase().indexOf("gzip") > -1){
                supportsGzip = true;
                response.setHeader("Content-Encoding", "gzip");
            }
        }
        

        
        //Makes sure the user has a valid configuration file.
        //If the session runs out it generates a new one.
        HttpSession session = request.getSession(true);
        //String selectedWsName = request.getParameter("selectedWsName");
        String selectedWsName = "Chuck_Workspace";
        /*ConfigFile cf = (ConfigFile) session.getAttribute("servletConfig");
        if(cf == null){
            cf = new ConfigFile(getServletConfig().getServletContext());
            if(cf.loadConfigFile()){
                cf.resetSessionVariables(session);
            }
            else{
                return;
            }
        }*/

        //UserEvent event = new UserEvent(request, session);

        InterfaceConfig ic = new InterfaceConfig();
        if(ic.getInterfaceConfig(request, getServletContext(), session)){
            myConceptsDir = ic.getRelativeConceptsDir();
            String selectedWsDir = ic.getWorkspacesDir() + selectedWsName + System.getProperty("file.separator");
            myFacilitiesLoc = selectedWsDir + ic.getRelativeFacilitiesDir();
            myCompilerMainDir = selectedWsDir + ic.getRelativeMainDir();
            myTempFileDir = ic.getTempFileDir();
            
            File tempWsDirFile = null;

            // type is the action to take (i.e. gencode, verify, etc)
            String job = request.getParameter("job");
            //String fileType = request.getParameter("fileType");
            //String filePkg = request.getParameter("pkg");
            //String fileConcept = request.getParameter("concept");

            /*if(job.compareTo("updateVerify") == 0){
                opBuffer = updateVerify(out, session, event);
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
                    out.print(opBuffer.toString());
                    out.close();
                }
                return;
            }
            else if(job.compareTo("simpleTranslate") == 0){
                opBuffer = simpleTranslate(request, event);
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
                    out.print(opBuffer.toString());
                    out.close();
                }
                return;
            }*/

            //variables that will be required for user files
            //fileName = request.getParameter("fileName");
            //session.setAttribute("fileName", fileName);


            //String fileSource = decodePlusSign(request.getParameter("contents"));
            // kind = getFileKind(fileType);
            //MetaFile inputFile = new MetaFile(fileName, fileConcept, filePkg, fileSource, kind);            
            MetaFile inputFile = null;
            String targetJSON = request.getParameter("target");
            if(targetJSON != null){
                try {
                    JSONObject target = new JSONObject(targetJSON);
                    String ws = target.getString("ws");
                    JSONObject componentJSON = target.getJSONObject("component");
                    inputFile = getTargetMetaFile(componentJSON);
                    session.setAttribute("fileName", inputFile.getMyFileName());
                    
                } catch (JSONException ex) {
                    Logger.getLogger(WebCompiler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }

            //Create an object then use it to make a usable hashmap of user files
            InputFileModule fileModule = new InputFileModule(inputFile);
            //InputFileModule fileModule = new InputFileModule(request, fileSource, kind);
            fileModule.createMetaFiles(ic, job);
            userFileMap = fileModule.getMetaFiles();



            try{    
                if(job.compareTo("verify") == 0){
                    myProverTimeoutLength = ic.getProverTimeout() * 1000;
                    String[] args = {"-maindir", myCompilerMainDir, "-vcs", 
                        "-listVCs", "-quickprove", "-webinterface"};//,
                        //"-timeout", Integer.toString(myProverTimeoutLength)};
                    r = new ResolveCompiler(args, inputFile, userFileMap);

                    //VerifyInvoker vi = new VerifyInvoker(r, args);
                    //opBuffer = vi.verifyResolve();
                    //proverTimeoutLength = cf.getProverTimeout() * 1000;
                    //opBuffer = verifyResolve(out, redirect, inputFile, "", 
                            //fileConcept, "", session, fileType, userFileMap, event);
                    //out.println(opBuffer.toString());
                }

                if(job.compareTo("genVCs") == 0){
                    String[] args = {"-maindir", myCompilerMainDir, "-vcs", 
                        "-listVCs", "-webinterface"};
                    r = new ResolveCompiler(args, inputFile, userFileMap);

                    VCGeneratorInvoker vcgi = new VCGeneratorInvoker(r, args);
                    opBuffer = vcgi.generateVcs();
                }

                if(job.compareTo("translateJava") == 0){
                    //Constructing compiler
                    String[] args = {"-maindir", myCompilerMainDir, "-translate", 
                        "-webinterface"};
                    r = new ResolveCompiler(args, inputFile, userFileMap);

                    //invoking the translator with the compiler specially created for
                    //translating
                    JavaTranslatorInvoker gji = new JavaTranslatorInvoker(r, args);
                    opBuffer = gji.generateJava();//event);
                }

                if(job.compareTo("buildDriver") == 0){
                    String name = (String) session.getAttribute("fileName");
                    String[] args = {"-maindir", myCompilerMainDir, "-createJar", 
                        "-verboseJar", "-webinterface"};
                    r = new ResolveCompiler(args, inputFile, name, userFileMap);

                    JarBuilderInvoker fgi = new JarBuilderInvoker(r, args, 
                            myFacilitiesLoc, myTempFileDir);
                    opBuffer = fgi.generateFacility(inputFile, name);
                }

            }catch(Exception ex){
                System.out.println(ex);
                //out.println();
                //out.println();
            }finally {
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
            }

            // delete all the files created
            if(tempWsDirFile != null){
                String dir = tempWsDirFile.getAbsolutePath();
                if(deleteDir(tempWsDirFile)){
                    System.out.println("Successfully deleted temp dir: "+dir);
                }
            }

            session.removeAttribute("userFileNames");
            session.removeAttribute("userNamePairs");
            session.removeAttribute("fileName");

            // log the event to the DB
            //event.logEvent(ic);
        }
/*        //uploadedFileConceptsLoc = cf.getRelativeConceptsDir();
        //uploadedFileFacilityLoc = cf.getWorkspacesDir() + selectedWsName + System.getProperty("file.separator") + "RESOLVE/Main/Facilities/Facilities/";
        //compilerMainDirLoc = cf.getWorkspacesDir() + selectedWsName + System.getProperty("file.separator") + cf.getRelativeMainDirPath();
        //jarTempLoc = cf.getJarTempLoc();
        File tempWsDirFile = null;

        // type is the action to take (i.e. gencode, verify, etc)
        String type = request.getParameter("type");
        String fileType = request.getParameter("fileType");
        String filePkg = request.getParameter("pkg");
        String fileConcept = request.getParameter("concept");

        if(type.compareTo("updateVerify") == 0){
            opBuffer = updateVerify(out, session, event);
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
                out.print(opBuffer.toString());
                out.close();
            }
            return;
        }
        else if(type.compareTo("simpleTranslate") == 0){
            opBuffer = simpleTranslate(request, event);
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
                out.print(opBuffer.toString());
                out.close();
            }
            return;
        }

        //variables that will be required for user files
        fileName = request.getParameter("fileName");
        session.setAttribute("fileName", fileName);
        
        
        //String fileSource = decodePlusSign(request.getParameter("contents"));
        // kind = getFileKind(fileType);
        //MetaFile inputFile = new MetaFile(fileName, fileConcept, filePkg, fileSource, kind);
        MetaFile inputFile = getTargetMetaFile
        
        //Create an object then use it to make a usable hashmap of user files
        InputFileModule fileModule = new InputFileModule(request, fileSource, kind);
        fileModule.createMetaFiles(ic, type);
        userFileMap = fileModule.getMetaFiles();
        
        

        try{    
            if(type.compareTo("verifyResolve") == 0){
                myProverTimeoutLength = ic.getProverTimeout() * 1000;
                //proverTimeoutLength = cf.getProverTimeout() * 1000;
                opBuffer = verifyResolve(out, redirect, inputFile, "", 
                        fileConcept, "", session, fileType, userFileMap, event);
                out.println(opBuffer.toString());
            }

            if(type.compareTo("genVCs") == 0){
                String[] args = {"-maindir", myCompilerMainDir, "-vcs", 
                    "-listVCs", "-webinterface"};
                r = new ResolveCompiler(args, inputFile, userFileMap);
                
                VCGeneratorInvoker vcgi = new VCGeneratorInvoker(r, args);
                opBuffer = vcgi.generateVcs();
            }

            if(type.compareTo("genCode") == 0){
                //Constructing compiler
                String[] args = {"-maindir", myCompilerMainDir, "-translate", 
                    "-webinterface"};
                r = new ResolveCompiler(args, inputFile, new HashMap<String, 
                        MetaFile>());
                
                //invoking the translator with the compiler specially created for
                //translating
                JavaTranslatorInvoker gji = new JavaTranslatorInvoker(r, args);
                opBuffer = gji.generateJava();//event);
            }

            if(type.compareTo("buildDriver") == 0){
                String name = (String) session.getAttribute("fileName");
                String[] args = {"-maindir", myCompilerMainDir, "-createJar", 
                    "-verboseJar", "-webinterface"};
                r = new ResolveCompiler(args, inputFile, name, userFileMap);
                
                JarBuilderInvoker fgi = new JarBuilderInvoker(r, args, 
                        myFacilitiesLoc, myTempFileDir);
                opBuffer = fgi.generateFacility(inputFile, name);
            }

        }catch(Exception ex){
            out.println();
            out.println();
        }finally {
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
                out.close();
            }
        }

        // delete all the files created
        if(tempWsDirFile != null){
            String dir = tempWsDirFile.getAbsolutePath();
            if(deleteDir(tempWsDirFile)){
                System.out.println("Successfully deleted temp dir: "+dir);
            }
        }
        
        session.removeAttribute("userFileNames");
        session.removeAttribute("userNamePairs");
        session.removeAttribute("fileName");
        
        // log the event to the DB
        event.logEvent(ic);*/
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

    private StringBuffer verifyResolve(PrintWriter out, PrintStream redirect,
                                MetaFile inputFile,
                                String path, String conceptDir, String hex,
                                HttpSession session, String fileType,
                                HashMap<String,MetaFile> userFileMap,
                                UserEvent event){
        StringBuffer opBuffer = new StringBuffer();
        //StringBuffer outputBuffer = new StringBuffer();
        Boolean proved = false;
        String syntaxError = "true";
        String proveComplete = "false";
        String proveVCs = "";
        if(session.getAttribute("tid") == null){

        }
        String[] args;
        if(myProverTimeoutLength == 0){
            args = new String[6];
        }
        else{
            args = new String[8];
        }
        args[0] = "-maindir";
        args[1] = myCompilerMainDir;
        args[2] = "-vcs";
        args[3] = "-listVCs";
        args[4] = "-quickprove";
        args[5] = "-webinterface";
        if(myProverTimeoutLength != 0){
            args[6] = "-timeout";
            args[7] = myProverTimeoutLength.toString();
        }

        opBuffer.append("<compile>");
        opBuffer.append("<verifyResults>");
        opBuffer.append("<compilerOutput>");
        //out.flush();

        CompilerLauncher c = new CompilerLauncher(args, inputFile, out, false, session, userFileMap);
        Thread t = new Thread(c);
        session.setAttribute("tid", t);
        String proveFile = getNewFilePath(conceptDir, "proof", hex, fileType);
        session.setAttribute("proofFile", proveFile);
        t.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(WebCompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(session.getAttribute("tid") == null){
            CompileReport cr = (CompileReport)session.getAttribute("compileReport");
            proved = cr.proveSuccess();
            if(!cr.hasError()){
                syntaxError = "false";
                proveComplete = "true";
                proveVCs = cr.getProveVCs();
                if(proveVCs == null){
                proveVCs = (String) session.getAttribute("proveVCs");
                }
                outputBuffer.append(proveVCs);
            }
            else{
                outputBuffer.append("{\"results\":[{");
                if(cr.hasErrors()){
                    opBuffer.append(cr.getErrors());
                    event.setMyCompileSuccess(false);
                    event.setMyCompilerErrors(cr.getErrors());
                }
                if(cr.hasBugReports()){
                    if(cr.hasErrors()){
                        opBuffer.append("},{");
                    }
                    opBuffer.append(cr.getBugReports());
                    event.setMyCompileSuccess(false);
                    event.setMyBugReports(cr.getBugReports());
                }
                outputBuffer.append("}]}");
            }
        }
        else{
            CompileReport cr = (CompileReport)session.getAttribute("compileReport");
            synchronized(cr){
                if(!cr.hasError()){
                    syntaxError = "false";
                }
                proveVCs = cr.getProveVCs();
            }
        }

        if(syntaxError.equals("true")){
            opBuffer.append(encode(outputBuffer.toString()));
        }
        else{
            opBuffer.append(outputBuffer.toString());
        }
        outputBuffer.delete(0, outputBuffer.length());
        opBuffer.append("</compilerOutput>");
        opBuffer.append("<proveComplete>" + proveComplete + "</proveComplete>");
        opBuffer.append("");
        opBuffer.append("<syntaxError>" + syntaxError + "</syntaxError>");
        opBuffer.append("<result>");
        if(proved){
            opBuffer.append("CORRECT");
        }
        else{
            opBuffer.append("NOT CORRECT");
        }
        opBuffer.append("</result>");
        
        opBuffer.append("<vc>");
        String asrtFile = getNewFilePath(conceptDir, "assert", hex, fileType);
        Stack<String> userFileNames = (Stack<String>) session.getAttribute("userFileNames");
        userFileNames.push(asrtFile);
        opBuffer.append("</vc>");

        opBuffer.append("</verifyResults>");
        opBuffer.append(printHex(out, hex, session));
        opBuffer.append("</compile>");
        return opBuffer;
    }

    private StringBuffer updateVerify(PrintWriter out,  HttpSession session, UserEvent event){
        StringBuffer opBuffer = new StringBuffer();
        String proveVCs = "Incorrect";
        String proveComplete = "false";
        String bugReport = "";
        String proveFile = (String)session.getAttribute("proofFile");
        CompileReport cr = (CompileReport)session.getAttribute("compileReport");
        synchronized(cr){
            proveVCs = cr.getProveVCs();
            if(cr.hasBugReports()){
                bugReport = "{" + cr.getBugReports() + "}";
            }
        }
        if(session.getAttribute("tid") == null){
            // prover finished
            proveComplete = "true";
            session.removeAttribute("tid");
        }
        opBuffer.append("<verifyResults>");
        opBuffer.append("<proveComplete>" + proveComplete + "</proveComplete>");
        opBuffer.append("<bugReports>");
        if(!bugReport.equals("")){
            opBuffer.append(bugReport);
        }
        opBuffer.append("</bugReports>");
        opBuffer.append("<compilerOutput>" + proveVCs + "</compilerOutput>");
        if(proveComplete.equals("true")){
            opBuffer.append("<proof>");
            opBuffer.append(readFile(out, proveFile, ""));
            opBuffer.append("</proof>");
            File f = new File(proveFile);
            f.delete();
        }
        opBuffer.append("</verifyResults>");
        return opBuffer;
    }
    
    private StringBuffer simpleTranslate(HttpServletRequest request, UserEvent event){
        StringBuffer opBuffer = new StringBuffer();
        String fileName = request.getParameter("fileName");
        String fileSource = decodePlusSign(request.getParameter("fileSource"));
        String fileType = request.getParameter("fileType");
        ModuleKind kind = getFileKind(fileType);
        MetaFile inputFile = new MetaFile(fileName, "", "", fileSource, kind);
        String syntaxError = "false";
        String[] args = {
                        "-maindir",
                        myCompilerMainDir,
                        "-translateSimple",
                        "-webinterface"};

        opBuffer.append("<compile>");
        opBuffer.append("<genCodeResults>");
        opBuffer.append("<compilerOutput>");
        ResolveCompiler r = null;
        try{
            r = new ResolveCompiler(args, inputFile, new HashMap<String, MetaFile>());
            r.compile(args);
            if(r.hasError()){
                syntaxError = "true";
            }
        }catch(Exception ex){
            System.out.println(ex);
        } finally{
            
        }
        CompileReport cr = r.getReport();
        opBuffer.append("{\"results\":[{");
        if(cr.hasErrors()){
            opBuffer.append(cr.getErrors());
            event.setMyCompileSuccess(false);
            event.setMyCompilerErrors(cr.getErrors());
        }
        if(cr.hasBugReports()){
            if(cr.hasErrors()){
                opBuffer.append("},{");
            }
            opBuffer.append(cr.getBugReports());
            event.setMyCompileSuccess(false);
            event.setMyBugReports(cr.getBugReports());
        }
        opBuffer.append("}]}");
        opBuffer.append("</compilerOutput>");
        opBuffer.append("<syntaxError>" + syntaxError + "</syntaxError>");

        opBuffer.append("<code>");
        String success = "false";
        if(cr.translateSuccess()){
            opBuffer.append(encode(cr.getOutput()));
            success = "true";
        }
        opBuffer.append("</code>");
        opBuffer.append("<translateSuccess>" + success + "</translateSuccess>");

        opBuffer.append("<javaFile>");
        opBuffer.append("<name>" + fileName + "</name>");
        opBuffer.append("<userName>" + fileName + "</userName>");
        opBuffer.append("</javaFile>");

        opBuffer.append("</genCodeResults>");
        opBuffer.append("</compile>");
        
        return opBuffer;
    }
    
    // This needs to be updated to work with up-to-date version of the compiler
    // it isn't actively used in the web interface right now
    private StringBuffer checkSyntax(PrintWriter out, PrintStream redirect,
                                MetaFile inputFile,
                                String path, String javaFile, String concept,
                                String name, String hex, HttpSession session,
                                HashMap<String,MetaFile> userFileMap,
                                UserEvent event){
        StringBuffer opBuffer = new StringBuffer();
        String syntaxError = "false";
        String[] args = {
                        "-maindir",
                        myCompilerMainDir,
                        "-translate",
                        "-webinterface",
                        path};

        opBuffer.append("<compile>");
        opBuffer.append("<checkSyntaxResults>");
        opBuffer.append("<compilerOutput>");
        out.flush();

        ResolveCompiler r = null;
        try{
            String fileName = (String) session.getAttribute("fileName");
            r = new ResolveCompiler(args, inputFile, userFileMap);
            r.compile(args);
            redirect.flush();
            if(r.hasError()){
                syntaxError = "true";
            }
        }catch(Exception ex){
            
        } finally{
            
        }
        
        CompileReport cr = r.getReport();
        opBuffer.append("{\"results\":[{");
        if(cr.hasErrors()){
            opBuffer.append(cr.getErrors());
        }
        if(cr.hasBugReports()){
            if(cr.hasErrors()){
                opBuffer.append("},{");
            }
            opBuffer.append(cr.getBugReports());
        }
        opBuffer.append("}]}");
        opBuffer.append("</compilerOutput>");
        opBuffer.append("<syntaxError>" + syntaxError + "</syntaxError>");

        String success = "false";
        File file = new File(javaFile);
        if(file.exists()){
            success = "true";
            Stack<String> userFileNames = (Stack<String>) session.getAttribute("userFileNames");
            userFileNames.push(javaFile);
        }
        opBuffer.append("<translateSuccess>" + success + "</translateSuccess>");

        opBuffer.append("</checkSyntaxResults>");
        opBuffer.append(printHex(out, hex, session));
        opBuffer.append("</compile>");
        out.flush();
        System.err.flush();
        System.out.flush();
        return opBuffer;
    }
    
    private boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
        String[] children = dir.list();
        for (int i=0; i<children.length; i++) {
            boolean success = deleteDir(new File(dir, children[i]));
            if (!success) {
                return false;
            }
        }
    }
    // The directory is now empty so delete it
    return dir.delete();
} 

    private String readFile(PrintWriter out, String f, String type){
        String fileReturn = "";
        File file = new File(f);
        if(file.exists()){
            try{
                FileInputStream fstream = new FileInputStream(f);
                DataInputStream is = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while((line = br.readLine()) != null){
                    fileReturn = fileReturn + line + "\n";
                }

                // VC files are now properly encoded by the compiler
                if(type.compareTo("VC") == 0){
                    //out.println(fileReturn);
                }
                else {
                    fileReturn = encode(fileReturn);
                    //out.println(encode(fileReturn));
                }
                fstream.close();
                is.close();
                br.close();
            }catch(Exception ex){
                out.println(type + " file not found");
                out.println();
                out.println(ex);
                out.println();
                out.println(ex.getMessage());
            }finally{
                return fileReturn;
            }
        }
        else{
            return null;
        }
    }
    
    // @todo see if we can get rid of this method
    private String getNewFilePath(String conceptDir, String type, String hex, String fileType){
        if(type.equals("facility")){
            String path = myFacilitiesLoc+hex;
            return path += ".fa";
        }
        String path = myConceptsDir+conceptDir+"/"+hex;
        if(type.equals("concept")){
            return path += ".co";
        }
        else if(type.equals("conRealiz") || type.equals("enhRealiz")){
            return path += ".rb";
        }
        else if(type.equals("enh")){
            return path += ".en";
        }
        else if(type.equals("assert")){
            if(fileType.equals("facility")){
                return myFacilitiesLoc+hex+".asrt";
            }
            else{
                return path += ".asrt";
            }
        }
        else if(type.equals("java")){
            return path += ".java";
        }
        else if(type.equals("proof")){
            return path += ".proof";
        }
        else
            return null;
    }

    private File getUserJavaFile(String pkg, String hex, ModuleKind kind){
        String path = "";
        if(kind.equals(ModuleKind.FACILITY)){
            path = myFacilitiesLoc+ hex + ".java";
        }
        else{
            path = myConceptsDir + pkg + "/" + hex + ".java";
        }
        return new File(path);
    }

    private void createFile(String path, String contents){
        BufferedWriter b = null;
        try {
            b = new BufferedWriter(new FileWriter(path));
            b.write(decodePlusSign(contents));
        } catch (IOException ex) {
            Logger.getLogger(WebCompiler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                b.close();
            } catch (IOException ex) {
                Logger.getLogger(WebCompiler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void copyFileToTempDir(String file, String name, String loc){
        File j = new File(file);
        if(j.exists()){

            FileChannel source = null;
            FileChannel destination = null;
            File jNew = new File(loc+name);
            if(!jNew.exists()) {
                try {
                    jNew.createNewFile();
                    source = new FileInputStream(j).getChannel();
                    destination = new FileOutputStream(jNew).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    if(source != null) {
                      source.close();
                    }
                    if(destination != null) {
                      destination.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(WebCompiler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private String encode(String raw){
        String encoded = null;
        try {
            encoded = URLEncoder.encode(raw.replaceAll(" ", "%20"), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WebCompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encoded;
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

    public static String replaceFileName(String src, String target, String replace){
        String endExp = "([\\s[\\W]])";
        String modTarget = "(" + target + ")";
        Pattern pattern = Pattern.compile("(\\s)"+modTarget+endExp);
        Matcher matcher = pattern.matcher(src);
        String modReplace = "$1" + replace + "$3";
        return matcher.replaceAll(modReplace);
    }
    
    private String replaceTargetName(String src, String target, String replace){
        String endExp = "([\\s[\\W]])";
        String modTarget = "(" + target + ")";
        Pattern pattern = Pattern.compile("(\\s)"+modTarget+endExp);
        Matcher matcher = pattern.matcher(src);
        String modReplace = "$1" + replace + "$3";
        return matcher.replaceFirst(modReplace);
    }

    private String printHex(PrintWriter out, String hex, HttpSession session){
        String output = "";
        output += "<hex>" + hex + "</hex>";
        output += "<files>";
        output += "</files>";
        return output;
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
    
    /*private void logBugReport(String bugReport, String type, MetaFile inputFile, HttpSession session){
        ConfigFile servletConfig = (ConfigFile) session.getAttribute("servletConfig");
        DataCollector dc;
        dc = new DataCollector(servletConfig);
        dc.logBugReport(URLDecoder.decode(decode(bugReport)), type, inputFile, session);
    }*/

    

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

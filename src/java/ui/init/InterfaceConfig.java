/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.init;

import java.io.File;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Chuck
 */
public class InterfaceConfig {
    private class Config{
        //public String mySystemSlash = System.getProperty("file.separator");
        public String myWorkingDir;
        public String myRelativeConceptsDir;
        public String myRelativeFacilitiesDir;
        public String myRelativeMainDir;
        public String myTempFileDir;
        public String myWorkspacesDir;
        public Integer myProverTimeout;
        public String myDefaultWsName;

        public String myDbURL;
        public String myDbUser;
        public String myDbPassword;

        public String myMailHost;
        public String myMailURL;
        public String myMailUser;
        public String myMailPassword;
    }
    private Config myConfig;
    
    private String mySystemSlash = System.getProperty("file.separator");
    /*private String myWorkingDir;
    private String myRelativeConceptsDir;
    private String myRelativeFacilitiesDir;
    private String myRelativeMainDir;
    private String myTempFileDir;
    private String myWorkspacesDir;
    private Integer myProverTimeout;
    private String myDefaultWsName;
    
    private String myDbURL;
    private String myDbUser;
    private String myDbPassword;
    
    private String myMailHost;
    private String myMailURL;
    private String myMailUser;
    private String myMailPassword;*/
    
    public InterfaceConfig(){
        myConfig = new Config();
    }
    
    public String getRelativeConceptsDir(){
        return myConfig.myRelativeConceptsDir;
    }
    
    public String getRelativeFacilitiesDir(){
        return myConfig.myRelativeFacilitiesDir;
    }
    
    public String getRelativeMainDir(){
        return myConfig.myRelativeMainDir;
    }
    
    public String getTempFileDir(){
        return myConfig.myTempFileDir;
    }
    
    public String getWorkspacesDir(){
        return myConfig.myWorkspacesDir;
    }
    
    public Integer getProverTimeout(){
        return myConfig.myProverTimeout;
    }
    
    public String getDefaultWorkspaceName(){
        return myConfig.myDefaultWsName;
    }
    
    public Connection getDBConnection(){
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(myConfig.myDbURL, myConfig.myDbUser, myConfig.myDbPassword);
        } catch (SQLException ex) {
            Logger.getLogger(InterfaceConfig.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return con;
        }
    }
    
    // this is where we set the mail server options
    public Session getMailSession(){
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", myConfig.myMailHost);
        props.setProperty("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "false");
        //props.put("mail.smtp.starttls.enable", "true");
        //Authenticator auth = new SMTPAuthenticator(myMailUser, myMailPassword);
        Session mailSession = Session.getInstance(props);
        return mailSession;
    }
    
    public boolean getInterfaceConfig(HttpServletRequest request, ServletContext context, HttpSession session){
        boolean success = false;
        Config c = (Config) session.getAttribute("interfaceConfig");
        if(c == null){
            /*
             * for the interface to function properly the directory containing the 
             * config file,  workspaces, etc must be specified in either the tomcat
             * web.xml or the application specific web.xml.
             * 
             * The entry should look like this:
             * 
             * <context-param>
                    <param-name>interfaceConfigFile</param-name>
                    <param-value>C:\home\webapps\interface\config.xml</param-value>
               </context-param>
             * 
             * where param-value is the absolute path to the configuration file 
             * in the interface working directory
             */
            String configFile = context.getInitParameter("interfaceConfigFile");
            if(configFile != null){
                if(loadinterfaceConfigFile(configFile)){
                    session.setAttribute("interfaceConfig", myConfig);
                    success = true;
                }
            }
            else{
                System.err.println("Unable to locate context-param interfaceConfigFile in web.xml");
            }
        }
        else{
            myConfig = c;
            success = true;
        }
        return success;
    }
    
    private boolean loadinterfaceConfigFile(String configFile){
        boolean success = false;
        boolean setXmlConfigSuccess = false;
        try {
            /*
             * Load configuration info from the config.xml file
             */
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            try {
                // Set up relative directories we will need  
                myConfig.myRelativeMainDir = "RESOLVE"+ mySystemSlash + "Main"+ mySystemSlash;              
                myConfig.myRelativeConceptsDir = myConfig.myRelativeMainDir + "Concepts"+ mySystemSlash;
                myConfig.myRelativeFacilitiesDir = myConfig.myRelativeMainDir + "Facilities"+ mySystemSlash + "Facilities"+ mySystemSlash;
                String workspaceDirPath;

                // Read in the config.xml file
                File f = new File(configFile);
                myConfig.myWorkingDir = f.getParent() + mySystemSlash;
                db = dbf.newDocumentBuilder();
                Document doc = db.parse(f);
                doc.getDocumentElement().normalize();
                NodeList fileLoc = null;
                //Element el = null;
                
                // First we read in the db login info
                fileLoc = doc.getElementsByTagName("dbInfo");
                loadDbInfo(fileLoc);
                
                // First we read in the mail server login info
                fileLoc = doc.getElementsByTagName("mailInfo");
                loadMailInfo(fileLoc);
                
                // set the temp dir for generated jar download and local temp user file storage
                fileLoc = doc.getElementsByTagName("temp");
                String tempDir = fileLoc.item(0).getFirstChild().getNodeValue();
                myConfig.myTempFileDir = myConfig.myWorkingDir + tempDir + mySystemSlash;
                
                // set the dir where the logs are saved
                fileLoc = doc.getElementsByTagName("logs");
                String logDir = fileLoc.item(0).getFirstChild().getNodeValue();
                /*myLogFileDir = myWorkingDir + logDir + slash;*/
                
                // set the location of the workspace directories
                fileLoc = doc.getElementsByTagName("workspaces");
                String wsDir = fileLoc.item(0).getFirstChild().getNodeValue();
                myConfig.myWorkspacesDir = myConfig.myWorkingDir + wsDir + mySystemSlash;
                
                /*myDefaultWorkspaceDir = myWorkspacesDir;*/
                
                fileLoc = doc.getElementsByTagName("proverTimeout");
                myConfig.myProverTimeout = Integer.parseInt(fileLoc.item(0).getFirstChild().getNodeValue());
                
                setXmlConfigSuccess = true;

                
            } catch (Exception ex) {
                System.out.println("XML config file exception: "+ex.toString());
            }
                
            if(setXmlConfigSuccess){                
                // get the name of the default workspace from the DB
                ResultSet rs = null;
                Connection con = null;
                Statement st = null;
                String query = "select WorkspaceName from Workspaces";
                query += " where DefaultWorkspace=true;";
                try {
                    con = getDBConnection();
                    st = con.createStatement();
                    // get the default (open) workspaces
                    rs = st.executeQuery(query);
                    if(rs.next()){
                        myConfig.myDefaultWsName = rs.getString("WorkspaceName");
                        success = true;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(InterfaceConfig.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try{
                    if (rs != null) {
                            rs.close();
                    }
                    if (st != null) {
                        st.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                    } catch (SQLException ex) {
                        Logger.getLogger(InterfaceConfig.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                

                // need to read in the datetime info from the dbe
                //updateSelectedWorkspaceTimeStamp();
            }
        } catch (Exception ex) {
            System.out.println("exception: "+ex.toString());
        }
        if(!success){
            System.err.println("Error reading from database");
        }
        return success;
    }
    
        private boolean loadDbInfo(NodeList fileLoc){
        Element el = (Element)(fileLoc.item(0));
        myConfig.myDbURL = el.getElementsByTagName("dbURL").item(0).getFirstChild().getNodeValue();
        myConfig.myDbUser = el.getElementsByTagName("dbUser").item(0).getFirstChild().getNodeValue();
        myConfig.myDbPassword = el.getElementsByTagName("dbPassWord").item(0).getFirstChild().getNodeValue();
        return true;
    }
    
    private boolean loadMailInfo(NodeList fileLoc){
        Element el = (Element)(fileLoc.item(0));
        myConfig.myMailHost = el.getElementsByTagName("mailHost").item(0).getFirstChild().getNodeValue();
        myConfig.myMailUser = el.getElementsByTagName("mailUser").item(0).getFirstChild().getNodeValue();
        myConfig.myMailPassword = el.getElementsByTagName("mailPassword").item(0).getFirstChild().getNodeValue();
        myConfig.myMailURL = el.getElementsByTagName("mailURL").item(0).getFirstChild().getNodeValue();
        return true;
    }
}

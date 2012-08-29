/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.init;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Chuck
 */
public class UserEvent {
    private String mySessionID;
    private String myUserEmailAddress;
    private String myIpAddress;
    private String myUserAgent;
    private String myEventType;
    private String myWorkspaceName;
    private String myFileName;
    private String mySubmittedContent;
    private String myCompilerErrors;
    private String myBugReports;
    //private String myProverResults;
    private boolean myCompileSuccess;
    
    public UserEvent(HttpServletRequest request, HttpSession session){
        String submittedUserID = request.getParameter("userID");
        mySessionID = session.getId();
        myUserEmailAddress = (String)session.getAttribute("userEmailAddress");
        if(myUserEmailAddress == null && submittedUserID != null && !submittedUserID.equals("")){
            myUserEmailAddress = submittedUserID;
        }
        myIpAddress = request.getHeader("X-Forwarded-For");
        if(myIpAddress == null){
            myIpAddress = request.getRemoteAddr();
        }
        myUserAgent = request.getHeader("user-agent");
        myEventType = request.getParameter("type");
        myWorkspaceName = request.getParameter("selectedWsName");
        myFileName = request.getParameter("fileName");
        mySubmittedContent = request.getParameter("contents");
        myCompilerErrors = "";
        myBugReports = "";
        myCompileSuccess = true;
    }

    /**
     * @return the mySessionID
     */
    public String getMySessionID() {
        return mySessionID;
    }

    /**
     * @return the myUserEmailAddress
     */
    public String getMyUserEmailAddress() {
        return myUserEmailAddress;
    }

    /**
     * @return the myIpAddress
     */
    public String getMyIpAddress() {
        return myIpAddress;
    }

    /**
     * @return the myUserAgent
     */
    public String getMyUserAgent() {
        return myUserAgent;
    }

    /**
     * @return the myEventType
     */
    public String getMyEventType() {
        return myEventType;
    }

    /**
     * @return the myWorkspaceName
     */
    public String getMyWorkspaceName() {
        return myWorkspaceName;
    }

    /**
     * @return the myFileName
     */
    public String getMyFileName() {
        return myFileName;
    }

    /**
     * @return the mySubmittedContent
     */
    public String getMySubmittedContent() {
        return mySubmittedContent;
    }

    /**
     * @return the myCompilerErrors
     */
    public String getMyCompilerErrors() {
        return myCompilerErrors;
    }

    /**
     * @param myCompilerErrors the myCompilerErrors to set
     */
    public void setMyCompilerErrors(String myCompilerErrors) {
        this.myCompilerErrors = myCompilerErrors;
    }

    /**
     * @return the myBugReports
     */
    public String getMyBugReports() {
        return myBugReports;
    }

    /**
     * @param myBugReports the myBugReports to set
     */
    public void setMyBugReports(String myBugReports) {
        this.myBugReports = myBugReports;
    }

    /**
     * @return the myCompileSuccess
     */
    //public void setMyProverResults(String myProverResults) {
        //this.myProverResults = myProverResults;
    //}

    /**
     * @return the myCompileSuccess
     */
    public Boolean getMyCompileSuccess() {
        return myCompileSuccess;
    }

    /**
     * @param myCompileSuccess the myCompileSuccess to set
     */
    public void setMyCompileSuccess(boolean myCompileSuccess) {
        this.myCompileSuccess = myCompileSuccess;
    }
    
    public void setEventType(String eventType){
        myEventType = eventType;
    }
    
    public void setMySubmittedContent(String content){
        this.mySubmittedContent = content;
    }
    
    public Boolean logEvent(InterfaceConfig ic){
        Boolean logSuccess = false;
        String query = "insert into UserEvents values ( NULL, " +
                        ((mySessionID!=null)?"'" + mySessionID + "', ":
                            mySessionID + ", ") +
                        ((myUserEmailAddress!=null)?"'" + myUserEmailAddress + "', ":
                            myUserEmailAddress + ", ") +
                        ((myIpAddress!=null)?"'" + myIpAddress + "', ":
                            myIpAddress + ", ") +
                        ((myUserAgent!=null)?"'" + myUserAgent + "', ":
                            myUserAgent + ", ") +
                        ((myEventType!=null)?"'" + myEventType + "', ":
                            myEventType + ", ") +
                        ((myWorkspaceName!=null)?"'" + myWorkspaceName + "', ":
                            myWorkspaceName + ", ") +
                        ((myFileName!=null)?"'" + myFileName + "', ":
                            myFileName + ", ") +
                        ((mySubmittedContent!=null)?"'" + mySubmittedContent + "', ":
                            mySubmittedContent + ", ") +
                        ((myCompilerErrors!=null)?"'" + myCompilerErrors + "', ":
                            myCompilerErrors + ", ") +
                        ((myBugReports!=null)?"'" + myBugReports + "', ":
                            myBugReports + ", ") +
                        myCompileSuccess + ", " +
                        "NOW()" +
                        ");";
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = ic.getDBConnection();
            st = con.createStatement();
            //System.out.println(query);
            Integer querySuccess = 0;
            querySuccess = st.executeUpdate(query);
            if(querySuccess == 1){
                logSuccess = true;
            }
        } catch (Exception ex) {
            Logger.getLogger(UserEvent.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(UserEvent.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                return logSuccess;
            }
        }
    }
}

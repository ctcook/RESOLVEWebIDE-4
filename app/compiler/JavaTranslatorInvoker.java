/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import controllers.CompilerSocket;
import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.data.MetaFile;
import java.net.URLEncoder;
import java.util.HashMap;

import models.CompilerResult;
import play.mvc.Http.Outbound;
//import webui.core.UserEvent;

/**
 *
 * @author Parker
 */
public class JavaTranslatorInvoker {
    
    private String args[];
    private ResolveCompiler r;
    private Outbound myOutbound;
    
    public JavaTranslatorInvoker(ResolveCompiler c, String a[], Outbound outbound)
    {
        args = a;
        r = c;
        myOutbound = outbound;
    }
    
    public void generateJava(String job, CompilerResult result)
    {
        OutboundMessageSender outbound = new OutboundMessageSender(myOutbound);
        //Run the compiler
        try{
            r.compile(args);
            
        }
        catch(Exception ex){
            //obviously not too concerned about this situation ever happening
        }
        CompileReport cr = r.getReport();
        boolean errors = false;
        if(cr.hasErrors()){
            errors = true;
            result.error = 1;
            result.results = cr.getErrors();
            outbound.sendErrors(job, cr.getErrors());
        }
        if(cr.hasBugReports()){
            errors = true;
            result.error = 2;
            result.results = cr.getBugReports();
            outbound.sendBugs(job, cr.getBugReports());
        }
        if(!errors){
            result.error = 0;
            result.results = cr.getOutput();
            outbound.sendComplete(job, cr.getOutput());
        }

        result.save();
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import models.CompilerResult;
import play.mvc.Http;
//import org.apache.catalina.websocket.WsOutbound;
//import webui.utils.WebSocketWriter;
//import webui.core.UserEvent;

/**
 *
 * @author Parker
 */
public class VerifyInvoker {

    private ResolveCompiler r;
    private String[] args;
    private Http.Outbound myOutbound;
    
    //public VerifyInvoker(String cmpDir, int proTime, StringBuffer sb)
    public VerifyInvoker(ResolveCompiler rc, String[] a, Http.Outbound outbound)
    {
        r = rc;
        args = a;
        myOutbound = outbound;
    }

    
    public void verifyResolve(String job, CompilerResult result){
        OutboundMessageSender outbound = new OutboundMessageSender(myOutbound);
        String results = null;
        //Run the compiler
        try{
            // Pass in a listener to the prover
            WsListener listener = new WsListener(outbound);
            r.compile(args, listener);

            results = listener.getResults().toString();
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
            result.results = results;
            outbound.sendComplete(job, cr.getOutput());
        }

        result.save();
    }    
}

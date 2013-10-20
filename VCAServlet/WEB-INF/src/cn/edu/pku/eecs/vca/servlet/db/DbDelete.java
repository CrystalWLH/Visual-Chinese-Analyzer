package cn.edu.pku.eecs.vca.servlet.db;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DbDelete extends Database
{
	private static final long serialVersionUID = 9070973889003118502L;
	
	@Override
    protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	try
    	{
    		response.setContentType("application/octet-stream");  
            response.setHeader("Cache-Control", "no-cache"); 
            
	    	InputStream in = request.getInputStream();
	    	DataInputStream din = new DataInputStream(in);
	    	int nId = (int) din.readInt();
	    	din.close();
	    	in.close();
	    	
	    	boolean ret = delete(nId);
			
	        OutputStream out = response.getOutputStream();
	        DataOutputStream dout = new DataOutputStream(out);
	        dout.writeBoolean(ret);
	    	dout.flush();
	    	dout.close();
	    	out.close();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}    	
    }
}

package cn.edu.pku.eecs.vca.servlet.db;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.eecs.vca.core.Forest;


public class DbQueryById extends Database
{
	private static final long serialVersionUID = 5793883424765996747L;

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
	    	
	    	Forest ret = queryById(nId);
			
	        OutputStream out = response.getOutputStream();
	    	ObjectOutputStream oout = new ObjectOutputStream(out);
	        oout.writeObject(ret);
	    	oout.flush();
	    	oout.close();
	    	out.close();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}    	
    }
}

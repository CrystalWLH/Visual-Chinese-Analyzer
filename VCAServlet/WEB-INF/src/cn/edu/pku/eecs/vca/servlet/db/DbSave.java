package cn.edu.pku.eecs.vca.servlet.db;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.eecs.vca.core.Forest;


public class DbSave extends Database
{
	private static final long serialVersionUID = 5775251582327545699L;
	
	@Override
    protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	try
    	{
    		response.setContentType("application/octet-stream");  
            response.setHeader("Cache-Control", "no-cache"); 
            
	    	InputStream in = request.getInputStream();
	    	ObjectInputStream oin = new ObjectInputStream(in);
	    	int nId = oin.readInt();
	    	Forest forest = (Forest) oin.readObject();
	    	oin.close();
	    	in.close();
	    	
	    	nId = save(nId, forest);
	    	
	        OutputStream out = response.getOutputStream();
	        DataOutputStream dout = new DataOutputStream(out);
	        dout.writeInt(nId);
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

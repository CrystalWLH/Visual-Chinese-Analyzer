package cn.edu.pku.eecs.vca.servlet.db;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.eecs.vca.util.LibraryItem;


public class DbQueryAll extends Database
{
	private static final long serialVersionUID = 4492620244777673013L;

	@Override
    protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	try
    	{
    		response.setContentType("application/octet-stream");  
            response.setHeader("Cache-Control", "no-cache"); 
            	    	
	    	Vector<LibraryItem> ret = queryAll();
			
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
